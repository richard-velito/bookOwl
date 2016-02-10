package org.devel.bookowl.entity;

import java.io.Serializable;

public class PageEntity implements Serializable {

    public static final String TABLE_NAME = "PAGE";

    private long id;
    private long book_id;
    private long chapter;
    private long start_offset;
    private long end_offset;
    private long number;
    private String chapter_id;

    private CharSequence text;

    public PageEntity() {

    }

    public PageEntity(CharSequence text){
        this.text = text;
    }

    public PageEntity(long book_id, long chapter, long start_offset,
                      long end_offset, long number) {

        this.book_id = book_id;
        this.chapter = chapter;
        this.start_offset = start_offset;
        this.end_offset = end_offset;
        this.number = number;
    }

    public CharSequence getText() {
        return text;
    }

    public void setText(CharSequence text) {
        this.text = text;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getBook_id() {
        return book_id;
    }

    public void setBook_id(long book_id) {
        this.book_id = book_id;
    }

    public long getChapter() {
        return chapter;
    }

    public void setChapter(long chapter) {
        this.chapter = chapter;
    }

    public long getStart_offset() {
        return start_offset;
    }

    public void setStart_offset(long start_offset) {
        this.start_offset = start_offset;
    }

    public long getEnd_offset() {
        return end_offset;
    }

    public void setEnd_offset(long end_offset) {
        this.end_offset = end_offset;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public String getChapter_id() {
        return chapter_id;
    }

    public void setChapter_id(String chapter_id) {
        this.chapter_id = chapter_id;
    }
}
