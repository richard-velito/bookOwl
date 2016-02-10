package org.devel.bookowl.activity;


import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.devel.bookowl.entity.BookFile;

import java.util.List;


public class FileExplorerListAdapter extends BaseAdapter {

    private Activity activity;
    private List<BookFile> listItems;

    public FileExplorerListAdapter(Activity activity, List<BookFile> listItems){
        this.activity = activity;
        this.listItems = listItems;
    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int position) {
        return listItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FileExplorerList lstItem = new FileExplorerList(activity, listItems.get(position) );
        return lstItem;
    }
}
