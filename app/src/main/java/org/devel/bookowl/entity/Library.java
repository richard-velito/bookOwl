package org.devel.bookowl.entity;

import java.util.ArrayList;

public class Library {

    private ArrayList<BookEntity> arrayBookItem;
    public static final int AUTHOR = 1;
    public static final int TITLE = 2;
    public static final int ID = 3;
    public static final int SAME = 4;

    public Library() {
        arrayBookItem = new ArrayList<BookEntity>();
    }

    public void setColectionBookItem(ArrayList<BookEntity> _array) {
        this.arrayBookItem = _array;
    }

    public void addBookItem(BookEntity _bi) {
        this.arrayBookItem.add(_bi);
    }

    public ArrayList<ArrayList<BookEntity>> groupbyArrayBookItem(int byLine) {

        ArrayList<ArrayList<BookEntity>> groupList =
                new ArrayList<ArrayList<BookEntity>>();

        ArrayList<BookEntity> obj = new ArrayList<BookEntity>();
        for (int i = 0; i < arrayBookItem.size(); i++) {


            if ( (i%byLine)==0 ){

                obj = new ArrayList<BookEntity>();
                groupList.add(obj);
            }
            obj.add((BookEntity) arrayBookItem.get(i));

        }


        return groupList;
    }
}
