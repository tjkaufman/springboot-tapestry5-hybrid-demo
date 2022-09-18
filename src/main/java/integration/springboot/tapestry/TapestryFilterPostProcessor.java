/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package integration.springboot.tapestry;

import lombok.extern.slf4j.Slf4j;
import org.apache.tapestry5.http.internal.TapestryAppInitializer;
import org.apache.tapestry5.ioc.Registry;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * Extracted private class from TapestryBeanFactoryPostProcessor to clarify what
 * the classes are actually doing during the join DI Container initialization
 * and linking.
 *
 * @author tjk46
 */
@Slf4j
public class TapestryFilterPostProcessor implements BeanPostProcessor {

    private final Registry registry;

    private final TapestryAppInitializer appInitializer;

    public TapestryFilterPostProcessor(Registry registry, TapestryAppInitializer appInitializer) {
        this.registry = registry;
        this.appInitializer = appInitializer;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass() == TapestryFilter.class) {
            log.info("Tapestry-Boot: About to start TapestryFilter, begin Registry initialization");
            registry.performRegistryStartup();
            registry.cleanupThread();
            appInitializer.announceStartup();
            log.info("Tapestry-Boot: About to start TapestryFilter, Registry initialization complete");
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
