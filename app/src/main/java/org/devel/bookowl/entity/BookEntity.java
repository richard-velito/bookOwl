package org.devel.bookowl.entity;

import net.nightwhistler.htmlspanner.FontFamily;
import net.nightwhistler.htmlspanner.css.CompiledRule;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.siegmann.epublib.domain.Book;

public class BookEntity implements Serializable {

    public static final String TABLE_NAME = "BOOK";

    private long id;
    private String name;
    private String author;
    private String title;
    private String path;
    private String same;
    private float fontSize;
    private long updated;
    private long current;
    private Book book;
    public Map<String, List<CompiledRule>> cssRules = new HashMap<>();

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long isUpdated() {
        return updated;
    }

    public void setUpdated(long updated) {
        this.updated = updated;
    }

    public float getFontSize() {
        return fontSize;
    }

    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
    }

    public String getSame() {
        return same;
    }

    public void setSame(String same) {
        this.same = same;
    }

    public long getCurrent() {
        return current;
    }

    public void setCurrent(long current) {
        this.current = current;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public List<CompiledRule> getCSSRules( String href ) {

        if ( this.cssRules.containsKey(href) ) {
            return Collections.unmodifiableList(cssRules.get(href));
        } else {
            return null;
        }
    }

    public void setCssRules(String href, List<CompiledRule> result) {

        this.cssRules.put(href, result);
    }

}
