package com.pluralsight.application;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.pluralsight.repository.BookDao;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Created by KDAAU95 on 16/04/2015.
 */
public class BookApplication extends ResourceConfig{

    public BookApplication(final BookDao bookDao) {
        packages("com.pluralsight");
        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(bookDao).to(BookDao.class);
            }
        });

        // Jackson in favor for moxy
        // Keep the data as a String, not as a timestamp
        JacksonJsonProvider jacksonJsonProvider = new JacksonJsonProvider().configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        register(jacksonJsonProvider);
    }
}
