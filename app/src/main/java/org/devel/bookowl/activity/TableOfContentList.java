package org.devel.bookowl.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.devel.bookowl.R;
import org.devel.bookowl.entity.IndexEntry;

public class TableOfContentList extends LinearLayout {

    private TextView lblTitle;
    private IndexEntry indexEntry;

    public TableOfContentList(Context context, IndexEntry indexEntry) {
        super(context);
        this.indexEntry = indexEntry;
        start();
    }

    private void start() {

        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater)getContext().getSystemService(infService);

        li.inflate(R.layout.list_item_toc, this, true);

        lblTitle = (TextView) findViewById(R.id.lblTitle);
        lblTitle.setText( indexEntry.getTitle() );
    }
}
