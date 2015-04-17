package com.pluralsight.domain;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.HashMap;


/**
 * Created by KDAAU95 on 16/04/2015.
 */
@XmlRootElement
@JacksonXmlRootElement(localName = "book")
// adding order for the elements
@JsonPropertyOrder({"id", "author", "title", "isbn", "publishedDate"})
// hide non null values from the resultco
@JsonInclude(JsonInclude.Include.NON_NULL)
// ignore unknown properties?
@JsonIgnoreProperties(ignoreUnknown = true)
public class Book {

    private String id;

    @NotNull(message="title is a required field")
    private String title;
    @NotNull(message="author is a required field")
    private String author;
    private String isbn;
    private Date publishedData;

    // handle extra paramaters, see also the specific getters and setters which are annotated
    private HashMap<String, Object> extras = new HashMap<>();

    public Book() {

    }

    public Book(String title, String author, String isbn) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        // act as it was published now
        this.publishedData = new Date();
    }

    public Book(String title, String author, String isbn, Date publishedData) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publishedData = publishedData;
    }

    @JacksonXmlProperty(isAttribute = true)
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
        return "id=" + id + "\ttitle=" + title + "\tauthor=" + author + "\tisbn=" + isbn + "\tpublishedDate=" + publishedData;
    }

    @JsonAnyGetter
    public HashMap<String, Object> getExtras() {
        return extras;
    }

    @JsonAnySetter
    public void set(String key, Object value) {
        this.extras.put(key, value);
    }
}
