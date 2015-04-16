package com.pluralsight.resource;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.pluralsight.domain.Book;
import com.pluralsight.repository.BookDao;
import com.pluralsight.repository.BookDaoStubImpl;
import org.glassfish.jersey.server.ManagedAsync;

import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.awt.*;
import java.util.Collection;

/**
 * Created by KDAAU95 on 16/04/2015.
 */
@Path("books")
public class BookResource {

    // removed in favor of h2k
    // private static BookDao bookDao = new BookDaoStubImpl();

    @Context
    BookDao bookDao;

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

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Book addBook(Book book) {
        return bookDao.addBook(book);
    }

    @POST
    @ManagedAsync
    @Path("/async")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void addBookAsynch(Book book, @Suspended final AsyncResponse response) {
        ListenableFuture<Book> bookFuture = bookDao.addBookAsynch(book);
        Futures.addCallback(bookFuture, new FutureCallback<Book>() {
            @Override
            public void onSuccess(Book book) {
                response.resume(book);
            }

            @Override
            public void onFailure(Throwable throwable) {
                response.resume(throwable);
            }
        });
    }
}
