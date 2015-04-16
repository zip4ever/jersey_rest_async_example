package com.pluralsight.repository;

import com.pluralsight.domain.Book;

import java.util.Collection;
import java.util.HashMap;

/**
 * Created by KDAAU95 on 16/04/2015.
 */
public class BookDaoStubImpl implements BookDao {

    private HashMap<String, Book> books;

    public BookDaoStubImpl() {
        books = new HashMap<>();
        for(int i=0; i<10; i++) {
            Book book = new Book("Book " + i, "Author " + i, "1234" + i*10000 );
            book.setId("" + i);
            books.put(book.getId(), book);
        }
    }

    public Collection<Book> getBooks() {
        return books.values();
    }

    public Book getBook(String id) {
        Book book = books.get(id);
        System.out.println("Requested book : " + book.toString());
        return books.get(id);
    }
}
