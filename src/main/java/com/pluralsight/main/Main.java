package com.pluralsight.main;

import com.pluralsight.repository.BookDao;
import com.pluralsight.repository.BookDaoStubImpl;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;

/**
 * Main class.
 *
 */
public class Main {
    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:8080/myapp/";

    public static HttpServer startServer() {

        // for usaga of h2k instead of a static variable in the BookResource class
        final BookDao bookDao = new BookDaoStubImpl();

        final ResourceConfig rc = new ResourceConfig()
                .packages("com.pluralsight")
                .register(new AbstractBinder() {
                    @Override
                    protected void configure() {
                        bind(bookDao).to(BookDao.class);
                    }
                });

        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
        System.in.read();
        server.stop();
    }
}

