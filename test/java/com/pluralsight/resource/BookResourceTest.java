package com.pluralsight.resource;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

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

    // method to force we can add extra elements
    protected Response addBook(String author, String title, Date publishedDate, String isbn, String... extras) {
        HashMap<String, Object> book = new HashMap<>();
        book.put("author", author);
        book.put("title", title);
        book.put("publishedDate", publishedDate);
        book.put("isbn", isbn);
        if( extras != null ) {
            int count = 0;
            for(String extra : extras) {
                book.put("extra " + count++, extra );
            }
        }
        Entity<HashMap<String, Object>> bookEntity = Entity.entity(book, MediaType.APPLICATION_JSON_TYPE);
        return target("books/async").request().post(bookEntity);
    }

    // convert a response to hashmap
    protected HashMap<String, Object> toHashMap(Response response) {
        return response.readEntity(new GenericType<HashMap<String, Object>>() {
        });
    }

    @Before
    // not name it setUp, cause it overrides a JerseyTest method and things will go crash kaboem kapoetski
    public void setUpTest() {
        // content was moved to beforeclass.
    }

    @Test
    public void testGetBooks() throws Exception {
        Collection<HashMap<String, Object>> response = target("books")
                .request()
                .get(new GenericType<Collection<HashMap<String, Object>>>(){});
        assertNotNull("Must contain books", response.size());
    }

    @Test
    public void testGetBooksAsync() throws Exception {
        Collection<HashMap<String, Object>> response = target("books/async")
                .request()
                .get(new GenericType<Collection<HashMap<String, Object>>>(){});
        assertNotNull("Must contain books", response.size());
    }

    @Test
    public void testGetBook() throws Exception {
        HashMap<String, Object> response = toHashMap(target("books")
                .path("1")
                .request()
                .get());
        assertNotNull("A book with id \"1\" must be found", response);
    }

    @Test
    public void testGetBookAsync() throws Exception {
        HashMap<String, Object> response = toHashMap( target("books/async")
                .path("1")
                .request()
                .get() );
        assertNotNull("A book with id \"1\" must be found", response);
    }

    @Test
    public void testSameBookRetrieved() throws Exception {
        HashMap<String, Object> response1 = toHashMap(target("books")
                .path("1")
                .request()
                .get());
        HashMap<String, Object> response2 = toHashMap(target("books")
                .path("1")
                .request()
                .get());
        assertEquals("Books must have same publication date", response1.get("publishedDate"), response2.get("publishedData"));
    }

    @Test
    public void addBook() throws Exception {
        Book book = new Book("My new Book", "Some good author", "1234567890");
        Entity<Book> bookEntity = Entity.entity(book, MediaType.APPLICATION_JSON_TYPE);
        Response response = target("books").request().post(bookEntity);
        assertEquals("Putting a book should return status ok", 200, response.getStatus());

        HashMap<String, Object> returnedBook = toHashMap(response);
        assertNotNull("Book should have an id when persisted", returnedBook.get("id"));
    }

    @Test
    public void addBookAsynch() throws Exception {
        Book book = new Book("My new Book", "Some good author", "0123456789");
        Entity<Book> bookEntity = Entity.entity(book, MediaType.APPLICATION_JSON_TYPE);
        Response response = target("books/async").request().post(bookEntity);
        assertEquals("Putting a book should return status ok", 200, response.getStatus());

        HashMap<String, Object> returnedBook = toHashMap(response);
        assertNotNull("Book should have an id when persisted", returnedBook.get("id"));
    }

    @Test
    public void  testAddExtraField() throws Exception{
        // see @JsonIgnoreProperties(ignoreUnknown = true) in Book to enforce acceptance !!!
        Response response = addBook("author", "title", new Date(), "1111", "Hello World");
        assertEquals(200, response.getStatus());

        HashMap<String, Object> book = toHashMap(response);
        assertNotNull(book.get("id"));
        assertEquals(book.get("extra1"), "Hello World");
    }

    @Test
    public void getBooksAsXml() {
        String output = target("books/async").request(MediaType.APPLICATION_XML_TYPE).get().readEntity(String.class);
        XML xml = new XMLDocument(output);

        assertEquals("Author 0", xml.xpath("/books/book[@id='1']/author/text()").get(0));
    }
}