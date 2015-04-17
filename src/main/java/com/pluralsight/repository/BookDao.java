package com.pluralsight.repository;

import com.google.common.util.concurrent.ListenableFuture;
import com.pluralsight.domain.Book;
import com.pluralsight.exception.BookNotFoundException;

import java.util.Collection;

/**
 * Created by KDAAU95 on 16/04/2015.
 */
public interface BookDao {

    Collection<Book> getBooks();

    ListenableFuture<Collection<Book>> getBooksAsync();

    public Book getBook(String id) throws BookNotFoundException;

    ListenableFuture<Book> getBookAsync(final String id) throws BookNotFoundException;

    Book addBook(Book book);

    ListenableFuture<Book> addBookAsync(final Book book);

    Book updateBook(String id, Book updates) throws BookNotFoundException;

    ListenableFuture<Book> updateBookAsync(final String id, final Book book);
}
