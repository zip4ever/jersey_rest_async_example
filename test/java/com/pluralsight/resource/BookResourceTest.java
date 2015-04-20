package com.pluralsight.resource;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import com.pluralsight.domain.Book;
import com.pluralsight.application.BookApplication;
import com.pluralsight.repository.BookDao;
import com.pluralsight.repository.BookDaoStubImpl;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.grizzly.connector.GrizzlyConnectorProvider;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.*;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

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

    // configure for JSON to leave out non blank files out of the payload
    protected void configureClient(ClientConfig clientConfig) {
        JacksonJsonProvider jacksonJsonProvider = new JacksonJsonProvider();
        jacksonJsonProvider.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        clientConfig.register(jacksonJsonProvider);
        clientConfig.connectorProvider(new GrizzlyConnectorProvider());
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
            int count = 1;
            for(String extra : extras) {
                book.put("extra" + count, extra );
                count++;
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
        assertEquals("Books must have same publication date", response1.get("publishedData"), response2.get("publishedData"));
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

    //todo fix !!!!
    @Test
    @Ignore
    public void  testAddExtraField() throws Exception{
        // see @JsonIgnoreProperties(ignoreUnknown = true) in Book to enforce acceptance !!!
        Response response = addBook("author", "title", new Date(), "1111", "Hello World");
        assertEquals(200, response.getStatus());

        HashMap<String, Object> book = toHashMap(response);
        assertNotNull(book.get("id"));
        assertEquals("Hello World", book.get("extra1"));
    }

    @Test
    public void getBooksAsXml() {
        String output = target("books/async").request(MediaType.APPLICATION_XML_TYPE).get().readEntity(String.class);
        XML xml = new XMLDocument(output);

        assertEquals("Author 0", xml.xpath("/books/book[@id='1']/author/text()").get(0));
    }

    @Test
    public void addBookNoAuthor() {
        Response response = addBook(null, "Title", new Date(), "1234");
        assertEquals(400, response.getStatus());

        String message = response.readEntity(String.class);
        assertTrue(message.contains("author"));
    }

    @Test
         public void addBookNoTitle() {
        Response response = addBook("An author", null, new Date(), "1234");
        assertEquals(400, response.getStatus());

        String message = response.readEntity(String.class);
        assertTrue(message.contains("title"));
    }

    @Test
    public void addBookNoAuthorAndTitle() {
        Response response = addBook(null, null, new Date(), "1234");
        assertEquals(400, response.getStatus());

        String message = response.readEntity(String.class);
        assertTrue(message.contains("title") && message.contains("author"));
    }

    @Test
    public void addBookNoBook() {
        Response response = target("books/async").request().post(null);
        assertEquals(400, response.getStatus());
    }

    @Test
    public void bookNotFoundWithMessage() {
        Response response = target("books").path("NOT_EX_123").request().get();
        assertEquals(404, response.getStatus());

        String message = response.readEntity(String.class);
        assertTrue(message.contains("not found"));

    }

    @Test
    public void BookEntityTagNotModified() {
        EntityTag entityTag = target("books/async").path("1").request().get().getEntityTag();
        assertNotNull(entityTag);

        Response response = target("books/async").path("1").request().header("If-None-Match",entityTag).get();
        assertEquals(304, response.getStatus());
    }

    @Test
    public void updateBookAuthor() {
        HashMap<String, Object> updates = new HashMap<>();
        updates.put("author", "Updated_author");
        Entity<HashMap<String, Object>> updatedEntity = Entity.entity(updates, MediaType.APPLICATION_JSON_TYPE);
        Response updateResponse = target("books/async").path("1").request().build("PATCH", updatedEntity).invoke();

        assertEquals(200, updateResponse.getStatus());

        Response getResponse = target("books").path("1").request().get();
        HashMap<String, Object> getResponseMap = toHashMap(getResponse);

        assertEquals(updates.get("author"), getResponseMap.get("author"));
    }

    @Test
    public void updateBookExtra() {
        HashMap<String, Object> updates = new HashMap<>();
        updates.put("hello", "world");
        Entity<HashMap<String, Object>> updatedEntity = Entity.entity(updates, MediaType.APPLICATION_JSON_TYPE);
        Response updateResponse = target("books/async").path("1").request().build("PATCH", updatedEntity).invoke();

        assertEquals(200, updateResponse.getStatus());

        Response getResponse = target("books").path("1").request().get();
        HashMap<String, Object> getResponseMap = toHashMap(getResponse);

        assertEquals(updates.get("hello"), getResponseMap.get("hello"));
    }



}