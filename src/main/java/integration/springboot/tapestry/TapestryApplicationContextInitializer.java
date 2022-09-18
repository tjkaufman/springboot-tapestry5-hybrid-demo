package integration.springboot.tapestry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;

/**
 * TapestryApplicationContextInitializer is responsible for driving the Tapestry
 * IoC startup and binding with Spring's application context.
 * 
 * @author tjk46
 */
@Slf4j
public class TapestryApplicationContextInitializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {
    private TapestryBeanFactoryPostProcessor tapestryBeanFactoryPostProcessor = null;

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        if (applicationContext instanceof AnnotationConfigServletWebServerApplicationContext) {
            if (tapestryBeanFactoryPostProcessor != null) {
                throw new RuntimeException("Tapestry applicationContext already set");
            }
            tapestryBeanFactoryPostProcessor = new TapestryBeanFactoryPostProcessor(
                    (AnnotationConfigServletWebServerApplicationContext) applicationContext);
            
            // Add the TapestryBeanFactoryPostProcessor to the Spring Boot applicationContext
            // which is currently still being defined, so the complete collection of beans
            // can be registered under the spring boot applicationContext.
            applicationContext.addBeanFactoryPostProcessor(tapestryBeanFactoryPostProcessor);
        } else {
            log.warn("Tapestry-Boot: tapestry-spring-boot works only with EmbeddedWebApplicationContext (Supplied context class was"
                    + applicationContext.getClass() + ") delaying initialization");
        }
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

}
