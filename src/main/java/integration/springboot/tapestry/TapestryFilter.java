package integration.springboot.tapestry;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.http.services.HttpServletRequestHandler;
import org.apache.tapestry5.http.services.ServletApplicationInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.stereotype.Component;

/**
 * Created by code8 on 11/8/15.
 */
@Component
public class TapestryFilter extends FilterRegistrationBean implements Filter {
    private final Logger log = LoggerFactory.getLogger(TapestryFilter.class);

    private FilterConfig config;

    private final Registry registry;

    private HttpServletRequestHandler handler;

    @Autowired
    public TapestryFilter(Registry registry) {
        this.registry = registry;
    }

    public final void init(FilterConfig filterConfig) throws ServletException {
        config = filterConfig;

        final ServletContext context = config.getServletContext();

        handler = registry.getService(HttpServletRequestHandler.class);

        ServletApplicationInitializer ai = registry.getService(ServletApplicationInitializer.class);
        ai.initializeApplication(context);
    }

    public final void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            boolean handled = handler.service((HttpServletRequest) request, (HttpServletResponse) response);

            if (!handled) {
                log.debug("Tapestry did not handle the request.. continuing with filter chain");
                chain.doFilter(request, response);
            } else {
                log.debug("Tapestry handled the request.. stop the filter chain");
            }

        } finally {
            registry.cleanupThread();
        }
    }

    @Override
    public void destroy() {
        registry.shutdown();
    }

    @Override
    public Filter getFilter() {
        return this;
    }
}
