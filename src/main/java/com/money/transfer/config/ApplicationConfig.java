package com.money.transfer.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("resources")
class ApplicationConfig extends ResourceConfig {

	public static Properties properties = new Properties();

	private Properties readProperties() {
	    InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config.properties");
	    if (inputStream != null) {
	        try {
	            properties.load(inputStream);
	        } catch (IOException e) {
	            // TODO Add your custom fail-over code here
	            e.printStackTrace();
	        }
	    }
	    return properties;
	}

    ApplicationConfig() {
        readProperties();
        packages("com.money.transfer", "com.money.transfer.service.exceptions");
        register(JacksonFeature.class);
        register(LoggingFeature.class);
    }
}
