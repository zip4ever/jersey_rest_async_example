package com.pluralsight.resource;

import com.pluralsight.domain.Book;
import com.pluralsight.repository.BookDao;
import com.pluralsight.repository.BookDaoStubImpl;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.awt.*;
import java.util.Collection;

/**
 * Created by KDAAU95 on 16/04/2015.
 */
@Path("books")
public class BookResource {

    private static BookDao bookDao = new BookDaoStubImpl();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Book> getBooks() {
        return bookDao.getBooks();
    }

    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Book getBook(@PathParam("id") String id) {
        System.out.println("getBook with id = " + id);
        return bookDao.getBook(id);
    }


}
