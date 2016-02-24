package org.devel.bookowl.activity;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import org.devel.bookowl.R;
import org.devel.bookowl.application.BookOwlApplication;
import org.devel.bookowl.entity.BookEntity;
import org.devel.bookowl.entity.BookFile;
import org.devel.bookowl.util.BookUtilities;
import org.devel.bookowl.util.DatabaseUtil;
import org.devel.bookowl.util.FileExplorerUtil;
import org.devel.bookowl.util.UiUtil;

import java.io.File;
import java.util.ArrayList;

import nl.siegmann.epublib.domain.Book;

public class ImportFragment extends ListFragment {

    private static String TAG = ImportFragment.class.getSimpleName();

    private BookOwlApplication app;
    private FileExplorerListAdapter adapter;
    private ArrayList<BookFile> files;

    public ImportFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        app = (BookOwlApplication) getActivity().getApplication();

        files = FileExplorerUtil.scanFile(
                new File(Environment.getExternalStorageDirectory() + ""),
                getResources().getString(R.string.folder_name));

        if (files.size()<1) {
            // internal storage
            files.addAll(FileExplorerUtil.scanFile(
                    new File(Environment.getRootDirectory() + ""),
                    getResources().getString(R.string.folder_name)));

        }
        if ( files!=null ) {

            adapter = new FileExplorerListAdapter(getActivity(), files);
            setListAdapter(adapter);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        UiUtil.loadSettings(getActivity().getApplicationContext(),
                getActivity());

        return inflater.inflate(R.layout.fragment_import, container, false);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        super.onListItemClick(l, v, position, id);

        String toastMsg = "";

        try {

            BookFile bookFile = (BookFile)adapter.getItem(position);

            String destinyPath = FileExplorerUtil.moveToDirectory(
                    getResources().getString(R.string.folder_name), bookFile.getFilename(),
                    bookFile.getFilepath() );

            if ( destinyPath!=null ) {

                if (!DatabaseUtil.existsBook(app.getDatabase(),
                        destinyPath)) {

                    //TODO: show correct information

                    Book book = BookUtilities.readBookFromFile(
                            destinyPath);

                    BookEntity bookEntity = new BookEntity();
                    bookEntity.setPath(destinyPath);
                    bookEntity.setTitle(book.getTitle());
                    bookEntity.setName(book.getMetadata().getFirstTitle());
                    bookEntity.setCurrent(0); // first load
                    if (book.getMetadata().getAuthors().size()>0)
                        bookEntity.setAuthor(book.getMetadata().getAuthors().get(0).toString());

                    DatabaseUtil.saveBook(app.getDatabase(), bookEntity);

                    toastMsg = getResources().getString(R.string.success_adding_book) +
                            book.getTitle();
                } else {
                    toastMsg = getResources().getString(R.string.repeat_book);
                }
            }

        } catch (Exception e) {

            toastMsg = getResources().getString(R.string.error_adding_book);
            Log.e(TAG, e.getMessage());
        }

        Toast.makeText(getActivity(), toastMsg,
                Toast.LENGTH_LONG).show();

    }
}
