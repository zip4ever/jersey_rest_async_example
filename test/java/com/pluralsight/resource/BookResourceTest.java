package com.pluralsight.resource;

import com.pluralsight.domain.Book;
import com.pluralsight.application.BookApplication;
import com.pluralsight.repository.BookDao;
import com.pluralsight.repository.BookDaoStubImpl;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.BeforeClass;
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

    private static BookDao bookDao;

    protected Application configure() {
        // extra debug options can be enabled:
        // enable(TestProperties.LOG_TRAFFIC);
        // enable(TestProperties.RECORD_LOG_LEVEL);
        // enable(TestProperties.DUMP_ENTITY);

        // setup this way so you can easily pass another DAO for the test than the one one production
        return new BookApplication(bookDao);
    }

    @BeforeClass
    public static void setUpClass() throws Exception{
        bookDao = new BookDaoStubImpl();
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