package com.pluralsight.resource;

import com.pluralsight.domain.Book;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.GenericType;

import java.util.Collection;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Created by KDAAU95 on 16/04/2015.
 */
public class BookResourceTest extends JerseyTest {

    protected Application configure() {
        // extra debug options can be enabled:
        // enable(TestProperties.LOG_TRAFFIC);
        // enable(TestProperties.RECORD_LOG_LEVEL);
        // enable(TestProperties.DUMP_ENTITY);
        return new ResourceConfig().packages("com.pluralsight");
    }

    @Test
    public void testGetBooks() throws Exception {
        Collection<Book> response = target("books")
                .request()
                .get(new GenericType<Collection<Book>>(){});
        assertEquals("Must contain 10 books", 10, response.size());
    }

    @Test
    public void testGetBook() throws Exception {
        Book response = target("books")
                .path("1")
                .request()
                .get(Book.class);
        assertNotNull("A book with id \"1\" must be found", response);
    }

    @Test
    public void testSameBookRetrieved() throws Exception {
        Book response1 = target("books")
                .path("1")
                .request()
                .get(Book.class);
        Book response2 = target("books")
                .path("1")
                .request()
                .get(Book.class);
        assertEquals("Books must have same publication date", response1.getPublishedData().getTime(), response2.getPublishedData().getTime());
    }
}