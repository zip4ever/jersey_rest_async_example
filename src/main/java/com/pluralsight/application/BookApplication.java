package com.pluralsight.application;

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
    }
}
