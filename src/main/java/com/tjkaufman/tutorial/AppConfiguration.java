package com.tjkaufman.tutorial;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@ComponentScan({ "com.sighlentroot.tutorial" })
public class AppConfiguration
{

//    @Bean
//    public ServletContextInitializer initializer()
//    {
//        return (ServletContext servletContext) -> {
//            servletContext.setInitParameter("tapestry.app-package", "com.sighlentroot.tutorial");
//            servletContext.setInitParameter("tapestry.development-modules", "com.sighlentroot.tutorial.services.DevelopmentModule");
//            servletContext.setInitParameter("tapestry.qa-modules", "com.foo.services.QaModule");
//            //servletContext.setInitParameter("tapestry.use-external-spring-context", "true");
//            servletContext.addFilter("app", TapestryFilter.class).addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.ERROR), false, "/*");
//            //servletContext.addFilter("app", TapestrySpringFilter.class).addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.ERROR), false, "/*");
//            servletContext.setSessionTrackingModes(EnumSet.of(SessionTrackingMode.COOKIE));
//        };
//    }
//
//    @Bean
//    public ConfigurableServletWebServerFactory webServerFactory()
//    {
//        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
//        factory.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/error404"));
//        return factory;
//    }
}
