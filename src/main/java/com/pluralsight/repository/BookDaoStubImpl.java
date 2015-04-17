package com.pluralsight.repository;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.pluralsight.domain.Book;
import com.pluralsight.exception.BookNotFoundException;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

/**
 * Created by KDAAU95 on 16/04/2015.
 */
public class BookDaoStubImpl implements BookDao {

    private static long idValue = 0L;
    private Map<String, Book> books;
    private ListeningExecutorService service;

    public BookDaoStubImpl() {
        books = new ConcurrentHashMap<>();
        service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));
    }

    public Collection<Book> getBooks() {
        return books.values();
    }

    public ListenableFuture<Collection<Book>> getBooksAsync() {
        ListenableFuture<Collection<Book>> future =
                service.submit(new Callable<Collection<Book>>() {
                    @Override
                    public Collection<Book> call() throws Exception {
                        return getBooks();
                    }
                });
        return future;
    }

    public Book getBook(String id) throws BookNotFoundException {
        if( !books.containsKey(id) ) {
            throw new BookNotFoundException("Book with id not found: " + id);
        }
        Book book = books.get(id);
        System.out.println("Requested book : " + book.toString());
        return books.get(id);
    }

    // todo : how can we throw an exception here???
    public ListenableFuture<Book> getBookAsync(final String id) throws BookNotFoundException {
       ListenableFuture<Book> future =
               service.submit(new Callable<Book>() {
                   @Override
                   public Book call() throws Exception {
                       return getBook(id);
                   }
               });
        return future;
    }

    public Book addBook(Book book) {
        idValue++;
        book.setId("" + idValue);
        books.put(book.getId(), book);
        return book;
    }

    public ListenableFuture<Book> addBookAsync(final Book book) {
        ListenableFuture<Book> future =
                service.submit(new Callable<Book>() {
                    public Book call() throws Exception {
                        return addBook(book);
                    }
                });
        return future;
    }

}
