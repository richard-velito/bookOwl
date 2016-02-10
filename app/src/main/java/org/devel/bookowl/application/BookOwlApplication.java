package org.devel.bookowl.application;


import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import org.devel.bookowl.db.Repository;
import org.devel.bookowl.entity.BookEntity;

import java.util.HashMap;

public class BookOwlApplication  extends Application {

    private BookEntity bookEntity;
    private SQLiteDatabase db;
    private HashMap<Long, Boolean> threads = new HashMap<Long, Boolean>();
    private HashMap<Long, Float> threadsName = new HashMap<Long, Float>();

    public SQLiteDatabase getDatabase() {
        if (this.db == null) {
            Repository repo = Repository.getInstance(getApplicationContext());
            this.db = repo.getWritableDatabase();
        }

        return this.db;
    }

    public BookEntity getBookEntity() {
        return bookEntity;
    }

    public void setBookEntity(BookEntity bookEntity) {
        this.bookEntity = bookEntity;
    }

    public void updateThread(long bookId, Boolean state) {

        threads.put(new Long(bookId), state);
    }

    public Boolean getThreadState(long bookId) {

        try {

            Boolean value = (Boolean) threads.get( new Long(bookId) );
            if (!value.equals(null))
                return value;

        } catch (Exception e) {
            //TODO
        }
        return false;
    }

    public void updateThreadName(long bookId, float name) {

        threadsName.put(new Long(bookId), name);
    }

    public float getThreadName(long bookId) {

        try {

            Float value = (Float) threadsName.get( new Long(bookId) );
            if (!value.equals(null))
                return value;

        } catch (Exception e) {
            //TODO
        }
        return 0;
    }

    private Long getKey(Long value){
        for(Long key : threads.keySet()){
            if(threads.get(key).equals(value)){
                return key;
            }
        }
        return null;
    }
}
