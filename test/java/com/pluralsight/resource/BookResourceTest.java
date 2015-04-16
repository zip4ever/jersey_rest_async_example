package com.pluralsight.resource;

import com.pluralsight.domain.Book;
import com.pluralsight.application.BookApplication;
import com.pluralsight.repository.BookDao;
import com.pluralsight.repository.BookDaoStubImpl;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
        for(int i=0; i<10; i++) {
            bookDao.addBook(new Book("Title " + i, "Author " + i, "" + i*1000000));
        }
    }

    @Before
    // not name it setUp, cause it overrides a JerseyTest method and things will go crash kaboem kapoetski
    public void setUpTest() {
        // content was moved to beforeclass.
    }

    @Test
    public void testGetBooks() throws Exception {
        Collection<Book> response = target("books")
                .request()
                .get(new GenericType<Collection<Book>>(){});
        assertNotNull("Must contain books", response.size());
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
    public void testGetBookAsync() throws Exception {
        Book response = target("books/async")
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

    @Test
    public void addBook() throws Exception {
        Book book = new Book("My new Book", "Some good author", "1234567890");
        Entity<Book> bookEntity = Entity.entity(book, MediaType.APPLICATION_JSON_TYPE);
        Response response = target("books").request().post(bookEntity);
        assertEquals("Putting a book should return status ok", 200, response.getStatus());

        Book returnedBook = response.readEntity(Book.class);
        assertNotNull("Book should have an id when persisted", returnedBook.getId());
    }

    @Test
    public void addBookAsynch() throws Exception {
        Book book = new Book("My new Book", "Some good author", "0123456789");
        Entity<Book> bookEntity = Entity.entity(book, MediaType.APPLICATION_JSON_TYPE);
        Response response = target("books/async").request().post(bookEntity);
        assertEquals("Putting a book should return status ok", 200, response.getStatus());

        Book returnedBook = response.readEntity(Book.class);
        assertNotNull("Book should have an id when persisted", returnedBook.getId());
    }
}