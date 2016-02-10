package org.devel.bookowl.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.devel.bookowl.R;
import org.devel.bookowl.activity.dialog.AutoScanDialog;
import org.devel.bookowl.activity.dialog.TableOfContentDialog;
import org.devel.bookowl.application.BookOwlApplication;
import org.devel.bookowl.util.DatabaseUtil;
import org.devel.bookowl.util.UiUtil;

public class BaseActivity extends AppCompatActivity
        implements FragmentDrawer.FragmentDrawerListener {

    private static String TAG = BaseActivity.class.getSimpleName();

    private static String readCurlActivity = "ReadCurlActivity";

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    private BookOwlApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        app = (BookOwlApplication) getApplication();

        UiUtil.loadSettings(getApplicationContext(), this);

        // drawer
        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        displayView(0);
    }

    @Override
    protected void onResume() {

        UiUtil.loadSettings(getApplicationContext(),
                this);
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        if (menu != null) {
            if (!this.getClass().getSimpleName().equals(readCurlActivity)) {

                MenuItem item_table_of_content = menu.findItem(R.id.action_table_of_content);
                item_table_of_content.setVisible(false);
            }
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        FragmentManager fm = getSupportFragmentManager();

        int id = item.getItemId();

        switch (id) {

            case R.id.action_settings:

                Intent i1 = new Intent(BaseActivity.this,
                        SettingsActivity.class);
                startActivity(i1);
                break;

            case  R.id.action_table_of_content:

                TableOfContentDialog tocDialog = new TableOfContentDialog();
                tocDialog.show(fm, "TableOfContentDialog");
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    public void doPositiveClickInAlertDialog() {

        // show search books view
        Log.e(TAG, "Positive click!");
        displayView(1);
    }

    private void displayView(int position) {

        try {

            Fragment fragment = null;
            String title = getString(R.string.app_name);
            switch (position) {
                case 0:

                    long i = DatabaseUtil.totalBooks(app.getDatabase());

                    if (i>0) {

                        fragment = new HomeFragment();
                        title = getString(R.string.title_home);
                    } else {

                        AutoScanDialog autoScanDialog = new AutoScanDialog();
                        autoScanDialog.show(getSupportFragmentManager(),
                                "AutoScanDialog");

                        fragment = new HomeEmptyFragment();
                        title = getString(R.string.title_home);
                    }
                    break;

                case 1:
                    fragment = new ImportFragment();
                    title = getString(R.string.title_import);
                    break;

                case 2:
                    Intent i1 = new Intent(BaseActivity.this,
                            SettingsActivity.class);
                    startActivity(i1);
                    break;

                default:
                    break;
            }

            if (fragment != null) {

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, fragment);
                fragmentTransaction.commit();

                // set the toolbar title
                getSupportActionBar().setTitle(title);
            }

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }
}

