package org.devel.bookowl.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.devel.bookowl.R;
import org.devel.bookowl.entity.BookFile;


public class FileExplorerList extends LinearLayout {

    private TextView lblFileName, lblFilePath;
    private BookFile bookFile;

    public FileExplorerList(Context context, BookFile bookFile) {
        super(context);
        this.bookFile = bookFile;
        start();
    }

    private void start() {

        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater)getContext().getSystemService(infService);

        li.inflate(R.layout.row_list_explorer, this, true);

        lblFileName = (TextView) findViewById(R.id.lblFileName);
        lblFileName.setText( bookFile.getFilename() );

        lblFilePath = (TextView) findViewById(R.id.lblFilePath);
        lblFilePath.setText( bookFile.getFilepath() );
    }
}
