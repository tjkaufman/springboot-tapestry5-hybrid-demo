package com.tjkaufman.tutorial.tapestry.services;

import integration.springboot.tapestry.TapestryApplication;
import org.apache.tapestry5.ioc.ServiceBinder;

/**
 * Created by code8 on 12/10/15.
 */
@TapestryApplication
public class TapestryMainModule {

    public static void bind(ServiceBinder binder) {
        binder.bind(SomeInterface.class, SomeInterfaceImpl.class);
    }

}
