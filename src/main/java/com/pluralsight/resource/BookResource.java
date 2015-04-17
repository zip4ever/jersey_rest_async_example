package com.pluralsight.resource;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.pluralsight.annotation.PATCH;
import com.pluralsight.domain.Book;
import com.pluralsight.exception.BookNotFoundException;
import com.pluralsight.repository.BookDao;
import org.apache.commons.codec.digest.DigestUtils;
import org.glassfish.jersey.server.ManagedAsync;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.*;
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

    @Context
    Request request;

    @GET
    // change MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML to Strings in order to add qs (weight)
    @Produces({"application/json;qs=1", "application/xml;qs=0.5"})

    //todo this breaks when requesting : org.glassfish.jersey.server.internal.process.MappableException: com.fasterxml.jackson.databind.JsonMappingException
    public Collection<Book> getBooks() {
        return bookDao.getBooks();
    }

    @Path("/async/")
    @GET
    @ManagedAsync
    // change MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML to Strings in order to add qs (weight)
    @Produces({"application/json;qs=1", "application/xml;qs=0.5"})
    public void getBooksAsync(@Suspended final AsyncResponse response) {
        ListenableFuture<Collection<Book>> booksFuture = bookDao.getBooksAsync();
        Futures.addCallback(booksFuture, new FutureCallback<Collection<Book>>() {
            @Override
            public void onSuccess(Collection<Book> books) {
                response.resume(books);
            }

            @Override
            public void onFailure(Throwable throwable) {
                response.resume(throwable);
            }
        });
    }

    @Path("/{id}")
    @GET
    // change MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML to Strings in order to add qs (weight)
    @Produces({"application/json;qs=1", "application/xml;qs=0.5"})
    public Book getBook(@PathParam("id") String id) throws BookNotFoundException {
        System.out.println("getBook with id = " + id);
        return bookDao.getBook(id);
    }

    @Path("/async/{id}")
    @GET
    @ManagedAsync
    // change MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML to Strings in order to add qs (weight)
    @Produces({"application/json;qs=1", "application/xml;qs=0.5"})
    public void getBookAsynch(@PathParam("id") String id, @Suspended final AsyncResponse response) throws BookNotFoundException {
        System.out.println("getBook with id = " + id);
        ListenableFuture<Book> bookFuture = bookDao.getBookAsync(id);
        Futures.addCallback(bookFuture, new FutureCallback<Book>() {
            @Override
            public void onSuccess(Book book) {
                // response.resume(book);
                EntityTag entityTag = generateEntityTag(book);
                Response.ResponseBuilder responseBuilder = request.evaluatePreconditions(entityTag);
                if (responseBuilder != null) {
                    response.resume(responseBuilder.build());
                } else {
                    response.resume(Response.ok().tag(entityTag).entity(book).build());
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                response.resume(throwable);
            }
        });
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Book addBook(@Valid @NotNull Book book) {
        return bookDao.addBook(book);
    }

    @POST
    @ManagedAsync
    @Path("/async")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void addBookAsynch(@Valid @NotNull Book book, @Suspended final AsyncResponse response) {
        ListenableFuture<Book> bookFuture = bookDao.addBookAsync(book);
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

    @Path("async/{id}")
    @PATCH
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ManagedAsync
    public void updateBook(@PathParam("id") String id, Book book, @Suspended final AsyncResponse response) {
        ListenableFuture<Book> bookFuture = bookDao.updateBookAsync(id, book);
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


    EntityTag generateEntityTag(Book book) {
        return new EntityTag(DigestUtils.md5Hex(book.toString()));
    }
}
