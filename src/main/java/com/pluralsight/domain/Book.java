package com.pluralsight.domain;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * Created by KDAAU95 on 16/04/2015.
 */
@XmlRootElement
public class Book {

    private String id;
    private String title;
    private String author;
    private String isbn;
    private Date publishedData;

    private static long idCount = 0L;

    public Book() {
        this.id = "" + idCount++;
    }

    public Book(String title, String author, String isbn) {
        this.id = "" + idCount++;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        // act as it was published now
        this.publishedData = new Date();
    }

    public Book(String title, String author, String isbn, Date publishedData) {
        this.id = "" + idCount++;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publishedData = publishedData;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Date getPublishedData() {
        return publishedData;
    }

    public void setPublishedData(Date publishedData) {
        this.publishedData = publishedData;
    }

    public String toString() {
        return "id=" + id + "\ttitle=" + title + "\tauthor=" + author + "\tisbn=" + isbn + "\tpublishedDate" + publishedData;
    }
}
