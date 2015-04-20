package com.pluralsight.application;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.fasterxml.jackson.jaxrs.xml.JacksonXMLProvider;
import com.pluralsight.repository.BookDao;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.filter.HttpMethodOverrideFilter;
import org.glassfish.jersey.server.filter.UriConnegFilter;

import javax.ws.rs.core.MediaType;
import java.util.HashMap;


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
        JacksonJsonProvider jacksonJsonProvider = new JacksonJsonProvider()
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        register(jacksonJsonProvider);

        // Jackson jaxrs (xml)
        JacksonXMLProvider jacksonXMLProvider = new JacksonXMLProvider()
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        register(jacksonXMLProvider);

        // URI Based content negotation
        HashMap<String, MediaType> mappings = new HashMap<>();
        mappings.put("xml", MediaType.APPLICATION_XML_TYPE);
        mappings.put("json", MediaType.APPLICATION_JSON_TYPE);
        UriConnegFilter uriConnegFilter = new UriConnegFilter(mappings, null);
        register(uriConnegFilter);

        // make sure that validation responses are sent back to the client
        property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);

        // added filter
        register(HttpMethodOverrideFilter.class);
    }
}
