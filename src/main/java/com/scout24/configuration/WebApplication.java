package com.scout24.configuration;

import com.scout24.controller.UniversalController;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.servlets.CrossOriginFilter;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;


public class WebApplication extends Application<WebConfiguration> {
    public static void main(String[] args) throws Exception {
        new com.scout24.configuration.WebApplication().run(args);
    }

    @Override
    public String getName() {
        return "WebApplication";
    }

    @Override
    public void initialize(Bootstrap<WebConfiguration> bootstrap) {
        bootstrap.addBundle(new AssetsBundle("/assets", "/web/", "index.html"));
    }

    @Override
    public void run(WebConfiguration configuration, Environment environment) {
        final UniversalController planner = new UniversalController();
        environment.jersey().register(planner);

        // Enable CORS headers
        final FilterRegistration.Dynamic cors =
                environment.servlets().addFilter("CORS", CrossOriginFilter.class);

        // Configure CORS parameters
        cors.setInitParameter("allowedOrigins", "*");
        cors.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin");
        cors.setInitParameter("allowedMethods", "OPTIONS,GET,POST,HEAD");

        // Add URL mapping
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
    }
}
