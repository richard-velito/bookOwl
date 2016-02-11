package org.devel.bookowl.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import org.devel.bookowl.R;
import org.devel.bookowl.application.BookOwlApplication;
import org.devel.bookowl.entity.BookEntity;
import org.devel.bookowl.entity.Library;
import org.devel.bookowl.ui.HorizontalListView;
import org.devel.bookowl.util.BookUtilities;
import org.devel.bookowl.util.DatabaseUtil;
import org.devel.bookowl.util.UiUtil;

import java.util.ArrayList;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;

public class HomeFragment extends ListFragment {

    private static String TAG = HomeFragment.class.getSimpleName();

    private VerticalAdapter verListAdapter;
    private BookOwlApplication app;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        UiUtil.loadSettings(getActivity().getApplicationContext(),
                getActivity());

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {

            app = (BookOwlApplication) getActivity().getApplication();

            createBookshelf();

        } catch (Exception e) {

            Log.e(TAG, e.getMessage());
        }
    }

    public void createBookshelf() {

        try {

            Library lb = new Library();

            int i=0;
            for (BookEntity item : DatabaseUtil.catalog(app.getDatabase())) {
                lb.addBookItem(item);
                i++;
            }

            if (i>0) {

                ArrayList<ArrayList<BookEntity>> groupList =
                        new ArrayList<ArrayList<BookEntity>>();
                groupList = lb.groupbyArrayBookItem(UiUtil.itemsInBookShelf(getActivity()));

                verListAdapter = new VerticalAdapter(getContext(),
                        R.layout.row_bookshelf, groupList);
                setListAdapter(verListAdapter);

                verListAdapter.notifyDataSetChanged();
            }

        } catch (Exception e) {

            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * This class add a list of ArrayList to ListView that it include multi
     * items as bookItem.
     */
    private class VerticalAdapter extends ArrayAdapter<ArrayList<BookEntity>> {

        private int resource;

        public VerticalAdapter(Context _context, int _ResourceId,
                               ArrayList<ArrayList<BookEntity>> _items) {
            super(_context, _ResourceId, _items);
            this.resource = _ResourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;

            if (convertView == null) {

                convertView = LayoutInflater.from(getContext()).inflate(resource,
                        null);

                HorizontalListView hListView = (HorizontalListView) convertView
                        .findViewById(R.id.subListview);
                HorizontalAdapter horListAdapter = new HorizontalAdapter(
                        getContext(), R.layout.item_bookshelf, getItem(position));
                hListView.setAdapter(horListAdapter);

                holder = new ViewHolder();
                holder.horizontalListView = (HorizontalListView) convertView
                        .findViewById(R.id.subListview);
                convertView.setTag(holder);
            } else {

                holder = (ViewHolder)convertView.getTag();
            }

            return convertView;
        }
    }

    public static class ViewHolder {
        public HorizontalListView horizontalListView;
    }

    /*
     * This class add some items to Horizontal ListView this ListView include
     * several bookItem.
     */
    private class HorizontalAdapter extends ArrayAdapter<BookEntity> {

        private int resource;

        public HorizontalAdapter(Context _context, int _textViewResourceId,
                                 ArrayList<BookEntity> _items) {
            super(_context, _textViewResourceId, _items);
            this.resource = _textViewResourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View retval = LayoutInflater.from(getContext()).inflate(
                    this.resource, null);

            Book book = BookUtilities.
                    readBookFromFile(getItem(position).getPath());

            ImageView iconBook = (ImageView) retval.findViewById(R.id.icon);

            try {

                Resource rs = BookUtilities.getCover(book);

                if (rs!=null) {

                    byte[] newData = rs.getData();
                    Bitmap bmp = BitmapFactory.decodeByteArray(newData, 0, newData.length);
                    iconBook.setImageBitmap(bmp);
                    iconBook.setId((int)getItem(position).getId());
                    iconBook.setOnClickListener( new OnBookClickListener() );

                    rs = null;
                }
                book = null;
            }catch (Exception e) {

                Log.e(TAG, e.getMessage());
            }

            return retval;
        }
    }

    public class OnBookClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            try {

                BookEntity bookEntity = DatabaseUtil.getBook(app.getDatabase(),
                        String.valueOf(v.getId()));

                if (bookEntity!=null) {

                    Book book = BookUtilities.readBookFromFile(
                            bookEntity.getPath());

                    bookEntity.setBook(book);
                    ((BookOwlApplication) getActivity().getApplication()).setBookEntity(bookEntity);

                    Intent intent = new Intent(getActivity(), ReadCurlActivity.class);
                    startActivity(intent);

                } else {
                    // TODO: find the book in db or show store
                    Log.e(TAG, "bookEntity is null. WTF!!");
                }

            } catch (Exception e) {

                Log.e(TAG, e.getMessage());
            }
        }
    }
}