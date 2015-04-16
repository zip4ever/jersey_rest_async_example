package com.pluralsight.repository;

import com.pluralsight.domain.Book;

import java.util.Collection;

/**
 * Created by KDAAU95 on 16/04/2015.
 */
public interface BookDao {

    Collection<Book> getBooks();

    public Book getBook(String id);

    Book addBook(Book book);
}
