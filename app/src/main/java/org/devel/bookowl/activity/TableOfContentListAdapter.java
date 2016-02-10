package org.devel.bookowl.activity;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.devel.bookowl.entity.IndexEntry;

import java.util.List;

public class TableOfContentListAdapter extends BaseAdapter {

    private Activity activity;
    private List<IndexEntry> indexEntries;

    public TableOfContentListAdapter(Activity activity, List<IndexEntry> indexEntries){
        this.activity = activity;
        this.indexEntries = indexEntries;
    }

    @Override
    public int getCount() {
        return indexEntries.size();
    }

    @Override
    public Object getItem(int position) {
        return indexEntries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TableOfContentList lstItem = new TableOfContentList(activity, indexEntries.get(position) );
        return lstItem;
    }
}
