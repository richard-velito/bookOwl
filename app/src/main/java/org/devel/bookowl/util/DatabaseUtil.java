package org.devel.bookowl.util;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import org.devel.bookowl.entity.BookEntity;
import org.devel.bookowl.entity.PageEntity;

import java.util.ArrayList;
import java.util.List;


public class DatabaseUtil {

    private static String TAG = DatabaseUtil.class.getSimpleName();

    public static PageEntity getPage(SQLiteDatabase db, String number, String book_id) {

        PageEntity page= new PageEntity();

        try {

            // get book info
            String query =
                    "SELECT id, id_book, chapter, start_offset, end_offset, number, chapter_id FROM " +
                            PageEntity.TABLE_NAME + " WHERE number = ? AND id_book = ? ";
            Cursor c = db.rawQuery(query, new String[]{number, book_id});
            if (c.moveToFirst()) {
                do {
                    page.setId( c.getLong(0) );
                    page.setBook_id(c.getLong(1));
                    page.setChapter(c.getLong(2));
                    page.setStart_offset(c.getLong(3));
                    page.setEnd_offset(c.getLong(4));
                    page.setNumber(c.getLong(5));
                    page.setChapter_id(c.getString(6));

                } while(c.moveToNext());
            }

        } catch (Exception e) {

            Log.e(TAG, e.toString());
        }
        return page;
    }

    public static long getFirstPageOfChapterByChapterId(SQLiteDatabase db, String chapter_id,
                                                        String book_id) {

        long pageNumber=0;

        try {

            // get book info
            String query =
                    "SELECT id, number FROM " +
                            PageEntity.TABLE_NAME + " WHERE chapter_id = ? AND id_book = ? " +
                            " ORDER BY number ASC";
            Cursor c = db.rawQuery(query, new String[]{chapter_id, book_id});

            if (c.moveToFirst()) {
                pageNumber = c.getLong(1);
            }

        } catch (Exception e) {

            Log.e(TAG, e.toString());
        }
        return pageNumber;
    }

    public static void saveBatchPage(SQLiteDatabase db, List<PageEntity> pages,
                                     int book_id, int chapter, String chapter_id) {

        String insertSql = "INSERT INTO " + PageEntity.TABLE_NAME +
                " (id_book, chapter, start_offset, end_offset, number, chapter_id) VALUES (?,?,?,?,?,?);";
        SQLiteStatement statement = db.compileStatement(insertSql);
        db.beginTransaction();

        for (PageEntity page : pages) {

            statement.clearBindings();
                statement.bindLong(1, book_id);
                statement.bindLong(2, chapter);
                statement.bindLong(3, page.getStart_offset());
                statement.bindLong(4, page.getEnd_offset());
                statement.bindLong(5, page.getNumber());
                statement.bindString(6, chapter_id);
            statement.execute();
        }

        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public static boolean deleteByBook(SQLiteDatabase db, long book_id) {

        return db.delete(PageEntity.TABLE_NAME, "id_book=" + book_id, null) >= 0;
    }

    public static long saveBook(SQLiteDatabase db, BookEntity book) {

        ContentValues values = new ContentValues();
            values.put("name", book.getName());
            values.put("author", book.getAuthor());
            values.put("title", book.getTitle());
            values.put("path", book.getPath());
            values.put("current", book.getCurrent());
        return db.insert(BookEntity.TABLE_NAME, null, values);
    }

    public static BookEntity getBook(SQLiteDatabase db, String id) {

        BookEntity book= new BookEntity();

        try {

            // get book info
            String query =
                    "SELECT id, name, author, title, path, updated, fontsize, current, total  FROM " +
                            BookEntity.TABLE_NAME + " WHERE id = ?";
            Cursor c = db.rawQuery(query, new String[]{id});
            if (c.moveToFirst()) {
                do {
                    book.setId(c.getLong(0));
                    book.setName(c.getString(1));
                    book.setAuthor(c.getString(2));
                    book.setTitle(c.getString(3));
                    book.setPath(c.getString(4));
                    book.setUpdated(c.getLong(5));
                    if (c.getString(6)!=null) {
                        book.setFontSize(Float.parseFloat(c.getString(6)));
                    } else {
                        book.setFontSize(0);
                    }
                    book.setCurrent(c.getLong(7));
                    book.setPages(c.getLong(8));

                } while(c.moveToNext());
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }
        return book;
    }

    public static boolean existsBook(SQLiteDatabase db, String filePath) {

        boolean response = false;
        try {

            String query =
                    "SELECT id FROM " +
                            BookEntity.TABLE_NAME + " WHERE path = ?";
            Cursor c = db.rawQuery(query, new String[]{filePath});

            if (c.moveToFirst()) {
                do {
                    response=true;
                } while(c.moveToNext());
            }

        } catch (Exception e) {

            Log.e(TAG, e.toString());
        }
        return response;
    }

    public static void updateBook(SQLiteDatabase db, String k, String v, String bookId) {

        ContentValues data=new ContentValues();
            data.put(k,v);
        db.update(BookEntity.TABLE_NAME, data, "id=" + bookId, null);
    }

    public static void updateBook(SQLiteDatabase db, String k, int v, String bookId) {

        ContentValues data=new ContentValues();
            data.put(k,v);
        db.update(BookEntity.TABLE_NAME, data, "id=" + bookId, null);
    }

    public static long totalBooks(SQLiteDatabase db) {

        long qty=0;

        try {

            String query = "SELECT count(1) as c FROM " +
                            BookEntity.TABLE_NAME ;

            Cursor c = db.rawQuery(query, null);
            if (c.moveToFirst()) {
                qty = c.getLong(0);
            }

        } catch (Exception e) {

            Log.e(TAG, e.toString());
        }
        return qty;
    }

    public static List<BookEntity> catalog(SQLiteDatabase db) {

        List<BookEntity> books = new ArrayList<BookEntity>();

        try {

            String query =
                    "SELECT id, name, author, title, path FROM " +
                            BookEntity.TABLE_NAME ;
            Cursor c = db.rawQuery(query, null);
            if (c.moveToFirst()) {
                do {
                    BookEntity book = new BookEntity();
                        book.setId( c.getLong(0) );
                        book.setName(c.getString(1));
                        book.setAuthor(c.getString(2));
                        book.setTitle(c.getString(3));
                        book.setPath(c.getString(4));
                    books.add(book);
                } while(c.moveToNext());
            }

        } catch (Exception e) {

            Log.e(TAG, e.toString());
        }
        return books;
    }

    public static long getTotalPagesOfBook(SQLiteDatabase db, String bookId) {

        long total=0;

        try {

            // get book info
            String query =
                    "SELECT count(1) as total FROM " +
                            PageEntity.TABLE_NAME + " WHERE id_book = ? ";
            Cursor c = db.rawQuery(query, new String[]{bookId});

            if (c.moveToFirst()) {
                total = c.getLong(0);
            }

        } catch (Exception e) {

            Log.e(TAG, e.toString());
        }
        return total;
    }

}
