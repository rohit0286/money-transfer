package com.money.transfer.config;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.money.transfer.bean.utils.BeanFactory;
import com.money.transfer.service.AccountService;
import com.money.transfer.service.exceptions.AccountServiceException;


public class ApplicationServer {

    private static final Logger log = LoggerFactory.getLogger(ApplicationServer.class);
    private static final String CONTEXT_PATH = "/*";

    private static Server getServer() {
        ApplicationConfig config = new ApplicationConfig();
        ServletHolder servlet = new ServletHolder(new ServletContainer(config));
        Integer appPort = Integer.valueOf(ApplicationConfig.properties.getProperty("app.port"));
        log.info("Application will be started at port {}", appPort);
        Server server = new Server(appPort);
        ServletContextHandler context = new ServletContextHandler(server, "/*");
        context.addServlet(servlet, CONTEXT_PATH);
        return server;
    }

    public static void startServer() {
        Server server = getServer();
        try {
            server.start();
            createTenSampleAccounts();
            server.join();
            log.info("Application has started.");
        } catch (Exception e) {
            log.error("Server exception: " + e.getClass() + " " + e.getMessage());
            System.exit(1);
        } finally {
            server.destroy();
        }
    }
    
    private static void createTenSampleAccounts() {
        log.info("Creating 10 sample accounts for transactions.");
    	AccountService accountService = (AccountService) BeanFactory.get().getBean(
    			AccountService.class);
    	IntStream.range(0, 10).forEach(i -> {
    		try {
				accountService.create(new BigDecimal(1000));
			} catch (AccountServiceException e) {
				e.printStackTrace();
			}
    	});
        log.info("Sample accounts created {}", accountService.all());

    }
}
