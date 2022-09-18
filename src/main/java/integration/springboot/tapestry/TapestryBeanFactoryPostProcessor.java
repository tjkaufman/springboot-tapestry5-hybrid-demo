package integration.springboot.tapestry;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.http.internal.SingleKeySymbolProvider;
import org.apache.tapestry5.http.internal.TapestryAppInitializer;
import org.apache.tapestry5.http.internal.TapestryHttpInternalConstants;
import org.apache.tapestry5.http.internal.util.DelegatingSymbolProvider;
import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.internal.services.SystemPropertiesSymbolProvider;
import org.apache.tapestry5.ioc.services.ServiceActivityScoreboard;
import org.apache.tapestry5.ioc.services.SymbolProvider;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.StringUtils;

/**
 * TapestryBeanFactoryPostProcessor is responsible for binding the Tapestry IoC
 * to Spring's ApplicationContext, by manually building Tapestry's registry and
 * passing each service (and it's wired dependencies) to Spring's DI Container.
 *
 * #NOTE: This class has undergone modifications from code8's original design to
 * account for the changes in Tapestry 5.7+ versions. The migration of some
 * packages and services within the Tapestry modules forced some Tapestry
 * package import changes.
 *
 * ##IMPORTANT: Also noted with the changes in Tapestry 5.7+, this set of
 * packages now seems to fail to auto-load the TapestryModule.class class, so
 * the tapestry "AppModule" class must now have the following annotation above
 * the class declaration:
 *
 * @ImportModule(TapestryModule.class)
 *
 * @author kaufmant
 * @author code8 (Original)
 */
@Slf4j
public class TapestryBeanFactoryPostProcessor
        implements BeanFactoryPostProcessor, Ordered {

    private static final String SPRING_CONTEXT_PATH = "server.servlet.context-path";
    private static final String PROPERTY_TAPESTRY_RELEASE_VERSION = "tapestry.release.version";
    private static final String PROPERTY_TAPESTRY_PRODUCTION_MODE = "tapestry.production.mode";

    protected final AnnotationConfigServletWebServerApplicationContext applicationContext;
    private Registry registry = null;
    private TapestryAppInitializer appInitializer = null;

    public TapestryBeanFactoryPostProcessor(AnnotationConfigServletWebServerApplicationContext applicationContext) {
        super();
        this.applicationContext = applicationContext;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        Collection<String> packagesToScan = findPackagesToScan(applicationContext);
        String appModuleClass = findAppModule(packagesToScan, applicationContext.getEnvironment());

        String filterName = appModuleClass.substring(appModuleClass.lastIndexOf('.') + 1).replace("Module", "");
        SymbolProvider combinedProvider = setupTapestryContext(appModuleClass, filterName);
        String executionMode = combinedProvider.valueForSymbol(SymbolConstants.EXECUTION_MODE);
        log.info("Tapestry-Boot: About to start Tapestry app module: {}, filterName: {}, executionMode: {} ", appModuleClass, filterName, executionMode);
        appInitializer = new TapestryAppInitializer(log, combinedProvider, filterName, executionMode);
        appInitializer.addModules(new SpringModuleDef(applicationContext));
        appInitializer.addModules(AssetSourceModule.class);
        log.info("Tapestry-Boot: creating tapestry registry");
        registry = appInitializer.createRegistry();

        beanFactory.addBeanPostProcessor(new TapestryFilterPostProcessor(registry, appInitializer));

        registerTapestryServices(applicationContext.getBeanFactory(),
                combinedProvider.valueForSymbol(TapestryHttpInternalConstants.TAPESTRY_APP_PACKAGE_PARAM) + ".services",
                registry);

        // This will scan and find TapestryFilter which in turn will be post
        // processed be TapestryFilterPostProcessor completing tapestry initialisation
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner((BeanDefinitionRegistry) applicationContext);
        scanner.scan(TapestryBeanFactoryPostProcessor.class.getPackage().getName());

    }

    /**
     * Defines the Tapestry Servlet init parameters needed to startup the Tapestry
     * service inside of the Spring Boot container. These properties are linked
     * to the Spring Boot application.properties.
     * 
     * @param appModuleClass
     * @param filterName
     * @return 
     */
    private SymbolProvider setupTapestryContext(String appModuleClass, String filterName) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        Map<String, Object> tapestryContext = new HashMap<>();

        tapestryContext.put("tapestry.filter-name", filterName);

        String servletContextPath = environment.getProperty(SymbolConstants.CONTEXT_PATH, environment.getProperty(SPRING_CONTEXT_PATH, ""));
        tapestryContext.put(SymbolConstants.CONTEXT_PATH, servletContextPath);

        String executionMode = environment.getProperty(SymbolConstants.EXECUTION_MODE, "production");
        tapestryContext.put(SymbolConstants.EXECUTION_MODE, executionMode);

        String rootPackageName = appModuleClass.substring(0, appModuleClass.lastIndexOf('.')).replace(".services", "");
        tapestryContext.put(TapestryHttpInternalConstants.TAPESTRY_APP_PACKAGE_PARAM, rootPackageName);

        String tapestryReleaseVersion = loadTapestryValueFromProperties(PROPERTY_TAPESTRY_RELEASE_VERSION, applicationContext.getEnvironment());
        tapestryContext.put(SymbolConstants.TAPESTRY_VERSION, tapestryReleaseVersion);

        String productionMode = loadTapestryValueFromProperties(PROPERTY_TAPESTRY_PRODUCTION_MODE, applicationContext.getEnvironment());
        tapestryContext.put(SymbolConstants.PRODUCTION_MODE, productionMode);

        environment.getPropertySources().addFirst(new MapPropertySource("tapestry-context", tapestryContext));

        return new DelegatingSymbolProvider(
                new SystemPropertiesSymbolProvider(),
                new SingleKeySymbolProvider(SymbolConstants.CONTEXT_PATH, servletContextPath),
                new SingleKeySymbolProvider(TapestryHttpInternalConstants.TAPESTRY_APP_PACKAGE_PARAM, rootPackageName),
                new SingleKeySymbolProvider(SymbolConstants.EXECUTION_MODE, executionMode),
                new SingleKeySymbolProvider(SymbolConstants.PRODUCTION_MODE, productionMode),
                new SingleKeySymbolProvider(SymbolConstants.TAPESTRY_VERSION, tapestryReleaseVersion));
    }

    /**
     * Convenience method to source environment variables from
     * application.properties for defining the Tapestry context
     *
     * @param propertyKey
     * @param environment
     * @return
     */
    private String loadTapestryValueFromProperties(String propertyKey, Environment environment) {
        String tapestryPropertyValue = environment.getProperty(propertyKey, "");

        if (StringUtils.isEmpty(tapestryPropertyValue)) {
            throw new IllegalStateException("Tapestry property: " + propertyKey + " not found. Set the property.");
        }

        return tapestryPropertyValue;
    }

    private Collection<String> findPackagesToScan(ConfigurableApplicationContext applicationContext) {
        Set<String> packages = new HashSet<>();
        Object springApplication = applicationContext.getBeansWithAnnotation(SpringBootApplication.class).values().iterator()
                .next();
        packages.add(springApplication.getClass().getPackage().getName());
        return packages;
    }

    /**
     * Detects the "AppModule" defined in the project. This expects that the
     * class serving as the Tapestry AppModule(s) in your project have been
     * annotated with the @TapestryApplication annotation, defined in this
     * package.
     *
     * @param packages
     * @param environment
     * @return
     */
    private String findAppModule(Collection<String> packages, Environment environment) {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false, environment);
        scanner.addIncludeFilter(new AnnotationTypeFilter(TapestryApplication.class));
        for (String pack : packages) {
            Set<BeanDefinition> definitions = scanner.findCandidateComponents(pack);
            if (!definitions.isEmpty()) {
                return definitions.iterator().next().getBeanClassName();
            }
        }
        throw new RuntimeException("TapestryApplication not found. Use @TapestryApplication to mark module.");
    }

    /**
     * Using Tapestry's ServiceActivityScoreboard (which provides run-time
     * details about a Tapestry service), the service details are provided to
     * the spring context while it is still initializing.
     *
     * @param beanFactory
     * @param servicesPackage
     * @param registry
     */
    private void registerTapestryServices(ConfigurableListableBeanFactory beanFactory, String servicesPackage,
            Registry registry) {
        ServiceActivityScoreboard scoreboard = registry.getService(ServiceActivityScoreboard.class);
        scoreboard.getServiceActivity().forEach(service -> {
            if (service.getServiceInterface().getPackage().getName().startsWith(servicesPackage)
                    || !service.getMarkers().isEmpty() || service.getServiceInterface().getName().contains("tapestry5")) {
                Object proxy = registry.getService(service.getServiceId(), (Class<?>) service.getServiceInterface());
                beanFactory.registerResolvableDependency(service.getServiceInterface(), proxy);
                log.debug("Tapestry-Boot: tapestry service {} exposed to spring", service.getServiceId());
            }
        });
        beanFactory.registerResolvableDependency(Registry.class, registry);
        log.info("Tapestry-Boot: tapestry Registry registered with spring (Still pending initialization)");
    }
}
