package org.devel.bookowl.activity.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.devel.bookowl.R;
import org.devel.bookowl.activity.ReadCurlActivity;
import org.devel.bookowl.activity.TableOfContentListAdapter;
import org.devel.bookowl.application.BookOwlApplication;
import org.devel.bookowl.entity.IndexEntry;

import java.util.ArrayList;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.TOCReference;

public class TableOfContentDialog extends DialogFragment {

    private static String TAG = TableOfContentDialog.class.getSimpleName();

    private Book book;
    private TableOfContentListAdapter adapter;
    private ListView listToC;
    private List<IndexEntry> index = new ArrayList<IndexEntry>();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder b =  new  AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.dialog_toc_title))
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }
                );

        try {

            book = ((BookOwlApplication) getActivity().getApplication()).getBookEntity().getBook();
            LayoutInflater i = getActivity().getLayoutInflater();
            View v = i.inflate(R.layout.dialog_toc,null);


            if (book.getTableOfContents().getTocReferences()!=null) {

                // reading the book
                reader(book.getTableOfContents().getTocReferences(),1);

                adapter = new TableOfContentListAdapter(getActivity(),
                        index);

                listToC = (ListView) v.findViewById(R.id.listindex);
                listToC.setAdapter(adapter);

                adapter.notifyDataSetChanged();

                listToC.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {

                        try {

                            Intent intent = new Intent(getActivity(), ReadCurlActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                    Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("p", position);
                            intent.putExtra("c",
                                    ((IndexEntry)listToC.getItemAtPosition(position)).
                                            getId().toString());

                            getDialog().dismiss();
                            startActivity(intent);

                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                        }

                    }
                });
            }

            b.setView(v);

        } catch(Exception e) {

            Log.e(TAG, e.getMessage());
        }

        return b.create();
    }

    private void reader(List<TOCReference> refs, int i) {

        for (TOCReference ref : refs) {

            String title = "";

            int j = 0;
            for(j=0;j<=i ;j++) {
                title += "  ";
            }

            title += ref.getTitle();

            index.add(new IndexEntry(title,
                    ref.getResourceId()));

            if (ref.getResource() != null) {

                reader( ref.getChildren() ,(i+1) );
            }
        }
    }

}
