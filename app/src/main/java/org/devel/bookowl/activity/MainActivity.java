package org.devel.bookowl.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.devel.bookowl.R;
import org.devel.bookowl.util.FileExplorerUtil;


public class MainActivity extends AppCompatActivity {

    private static String TAG = MainActivity.class.getSimpleName();
    protected static int _splashTime = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {

            // create folder if no exists
            // TODO: close if i can't create the folder
            FileExplorerUtil.createDirectory(
                    getResources().getString(R.string.folder_name));

            // splash screen
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {

                    Intent i = new Intent(MainActivity.this, BaseActivity.class);
                    startActivity(i);
                    finish();
                }
            }, _splashTime);

        } catch( Exception e) {

            Log.e(TAG, e.getMessage());
        }
    }
}
