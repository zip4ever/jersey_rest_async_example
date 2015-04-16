package com.pluralsight.repository;

import com.pluralsight.domain.Book;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by KDAAU95 on 16/04/2015.
 */
public class BookDaoStubImpl implements BookDao {

    private static long idValue = 0L;

    private Map<String, Book> books;

    public BookDaoStubImpl() {
        books = new ConcurrentHashMap<>();
    }

    public Collection<Book> getBooks() {
        return books.values();
    }

    public Book getBook(String id) {
        Book book = books.get(id);
        System.out.println("Requested book : " + book.toString());
        return books.get(id);
    }

    public Book addBook(Book book) {
        idValue++;
        book.setId("" + idValue);
        books.put(book.getId(), book);
        return book;
    }

}
