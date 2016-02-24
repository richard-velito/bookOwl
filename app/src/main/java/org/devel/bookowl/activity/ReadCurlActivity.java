package org.devel.bookowl.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.millennialmedia.android.MMAd;
import com.millennialmedia.android.MMInterstitial;
import com.millennialmedia.android.MMRequest;
import com.millennialmedia.android.MMSDK;
import com.millennialmedia.android.RequestListener;

import net.nightwhistler.htmlspanner.HtmlSpanner;

import org.devel.bookowl.R;
import org.devel.bookowl.activity.dialog.TableOfContentDialog;
import org.devel.bookowl.application.BookOwlApplication;
import org.devel.bookowl.entity.BookEntity;
import org.devel.bookowl.entity.PageEntity;
import org.devel.bookowl.ui.CurlPage;
import org.devel.bookowl.ui.CurlView;
import org.devel.bookowl.util.DatabaseUtil;
import org.devel.bookowl.util.PagerUtil;
import org.devel.bookowl.util.TextViewUtil;
import org.devel.bookowl.util.UiUtil;
import org.devel.bookowl.util.handlers.CSSLinkHandler;
import org.devel.bookowl.util.handlers.CustomImageHandler;

import java.util.List;

import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.SpineReference;

public class ReadCurlActivity extends AppCompatActivity {

    private static String TAG = ReadCurlActivity.class.getSimpleName();

    private HtmlSpanner spanner;

    private HandlerThread bgThread;
    private Handler mBackgroundHandler;

    private HandlerThread loaderThread;
    private Handler mLoaderHandler;

    private BookEntity bookEntity;
    private BookOwlApplication app;

    private int currentChapter = -1;
    private int position = 0;

    private CurlView mCurlView;
    private RelativeLayout mRelativeLayout;
    private FrameLayout mFrameLayout;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Layout
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_curl);

        // toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // curlView
        mCurlView = (CurlView) findViewById(R.id.curl);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.loading_relative_layout);
        mFrameLayout = (FrameLayout) findViewById(R.id.graphics_frameLayout1_1);

        app = (BookOwlApplication) getApplication();

        bookEntity = app.getBookEntity();

        // spanner for epub
        spanner = new HtmlSpanner();
        spanner.registerHandler("img", new CustomImageHandler(bookEntity.getBook()));
        spanner.registerHandler("link", new CSSLinkHandler(bookEntity));
        spanner.setUseColoursFromStyle(true);

        UiUtil.loadSettings(getApplicationContext(), this);

        try {

            // threads
            bgThread = new HandlerThread("background");
            bgThread.start();
            mBackgroundHandler = new Handler(bgThread.getLooper());

            loaderThread = new HandlerThread("loader");
            loaderThread.start();
            mLoaderHandler = new Handler(loaderThread.getLooper());


            if (bookEntity.getBook()!=null) {

                getSupportActionBar().setTitle(bookEntity.getTitle());

                Bundle extras = getIntent().getExtras();

                if (extras!=null) {

                    position = (int) DatabaseUtil.getFirstPageOfChapterByChapterId(
                            app.getDatabase(), extras.getString("c") ,
                            String.valueOf(bookEntity.getId()))  ;

                } else {

                    MMSDK.initialize(getApplicationContext());
                    // load millenial request
                    final MMInterstitial interstitial = new MMInterstitial(this);
                    //Set your metadata in the MMRequest object
                    MMRequest request = new MMRequest();

                    //Add the MMRequest object to your MMInterstitial.
                    interstitial.setMMRequest(request);
                    interstitial.setApid("213004");
                    interstitial.setListener(new RequestListener.RequestListenerImpl() {
                        @Override
                        public void requestCompleted(MMAd mmAd) {
                            interstitial.display(); // display the ad that was cached by fetch
                        }
                    });
                    interstitial.fetch(); // request ad to be cached locally


                    // get page from db
                    if (bookEntity.getCurrent()>0)
                        position = (int)bookEntity.getCurrent();

                }

                mCurlView.setPageProvider(new PageProvider());
                mCurlView.setSizeChangedObserver(new SizeChangedObserver());
                mCurlView.setCurrentIndex(position);

            } else {

                Log.e(TAG, "Error Loading book.");
            }
        } catch (Exception e) {

            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        FragmentManager fm = getSupportFragmentManager();

        int id = item.getItemId();

        switch (id) {

            case R.id.action_settings:

                Intent i1 = new Intent(ReadCurlActivity.this,
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
    protected void onResume() {

        UiUtil.loadSettings(getApplicationContext(), this);
        super.onResume();
        mCurlView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCurlView.onPause();
    }

    @Override
    protected void onDestroy() {

        bgThread.quit();
        loaderThread.quit();
        super.onDestroy();
    }

    private class PageProvider implements CurlView.PageProvider {

        private Spanned spannedPageContent = new SpannableStringBuilder("");

        @Override
        public void leftEnd() { }

        @Override
        public void rightEnd() { }

        @Override
        public int getPageCount() {

            if (!app.getThreadState(bookEntity.getId()) &&
                    ((bookEntity.isUpdated()!=0))) {

                if (bookEntity.getPages()<=1) {
                    return 5000;
                } else {
                    return (int)bookEntity.getPages();
                }
            } else {
                return 5000;
            }
        }

        private Bitmap loadBitmap(int width, int height, int index) {

            final TextView tv = TextViewUtil.getTextView(getApplicationContext(),
                    width, height);
            final float currentAppFontSize = PreferenceManager.getDefaultSharedPreferences(
                    getApplicationContext()).getInt(getResources().getString(
                    R.string.fontsize_preference_key), 25);

            final int index2 = index;

            if ( (bookEntity.getFontSize()!=currentAppFontSize) ) {

                // update in memory
                bookEntity.setPages(0);
                bookEntity.setUpdated(0);
                bookEntity.setFontSize(currentAppFontSize);

                // update in db
                DatabaseUtil.updateBook(app.getDatabase(), "updated",
                        0, String.valueOf(bookEntity.getId()));
                DatabaseUtil.updateBook(app.getDatabase(),"fontsize",
                        String.valueOf(currentAppFontSize), String.valueOf(bookEntity.getId()));
                DatabaseUtil.updateBook(app.getDatabase(), "total",
                        0, String.valueOf(bookEntity.getId()));

                if (app.getThreadState(bookEntity.getId())) {
                    app.updateThread(bookEntity.getId(), false);
                }
            }

            if ( (app.getBookEntity().isUpdated()==0) && !app.getThreadState(bookEntity.getId()) ) {

                // book is updating .....
                app.updateThread(bookEntity.getId(), true);
                // start thread - for the thread
                app.updateThreadName(bookEntity.getId(), currentAppFontSize);

                /**
                 * BEGIN : runner for parse page
                 */
                Runnable pager = new Runnable() {
                    @Override
                    public void run() {

                        try {

                            if ( DatabaseUtil.deleteByBook(app.getDatabase(),
                                    (int)bookEntity.getId()) ) {

                                int i = 0; int j = 0;

                                for (SpineReference bookSection :
                                        bookEntity.getBook().getSpine().getSpineReferences()) {

                                    if ((app.getThreadName(bookEntity.getId()) !=
                                            currentAppFontSize)) {
                                        break;
                                    }

                                    Log.e(TAG, bookEntity.getTitle() + " - " +
                                        currentAppFontSize + " - " +
                                        bookSection.getResource().getId());

                                    List<PageEntity> pages = PagerUtil.measure(
                                        spanner.fromHtml(bookSection.getResource().getReader()),
                                        tv, i);

                                    i = i + pages.size();

                                    if ((app.getThreadName(bookEntity.getId()) !=
                                            currentAppFontSize)) {
                                        break;
                                    }

                                    DatabaseUtil.saveBatchPage(app.getDatabase(), pages,
                                        (int) bookEntity.getId(), j, bookSection.getResource().getId());

                                    pages = null;
                                    j++;
                                }

                                if ((app.getThreadName(bookEntity.getId()) ==
                                        currentAppFontSize)) {

                                    app.getBookEntity().setUpdated(1);

                                    bookEntity.setUpdated(1);
                                    bookEntity.setPages(i);

                                    DatabaseUtil.updateBook(app.getDatabase(),"updated", 1,
                                            String.valueOf(bookEntity.getId()));
                                    DatabaseUtil.updateBook(app.getDatabase(),"total", i,
                                            String.valueOf(bookEntity.getId()));

                                    // book finish the update
                                    app.updateThread(bookEntity.getId(), false);

                                    Log.e(TAG,
                                            "End loading :" + bookEntity.getTitle());
                                }
                            }
                        } catch (Exception e) {

                            Log.e(TAG, e.getMessage());
                        }
                    }
                };
                mBackgroundHandler.post(pager);
                /**
                 * END : runner for parse page
                 */

            } else {

                PageEntity page = DatabaseUtil.getPage(app.getDatabase(),
                        String.valueOf(index), String.valueOf(bookEntity.getId()));

                if ((page != null) && (page.getChapter_id() != null)) {

                    if (currentChapter != (int) page.getChapter()) {

                        currentChapter = (int) page.getChapter();

                        try {

                            Resource res = bookEntity.getBook().getSpine().
                                    getResource( currentChapter );

                            PagerUtil.text = spanner.fromHtml(res.getReader());

                            spannedPageContent = PagerUtil.content((int)page.getStart_offset(),
                                    (int)page.getEnd_offset());

                        } catch (Exception e) {

                            Log.e(TAG, e.getMessage());
                        }

                    } else {

                        spannedPageContent = PagerUtil.content((int)page.getStart_offset(),
                                (int)page.getEnd_offset());
                    }
                }
            }

            // loading pages
            if ( (spannedPageContent.length()==0) ) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        mRelativeLayout.setVisibility(View.VISIBLE);
                        mFrameLayout.setVisibility(View.GONE);

                        Toast.makeText(ReadCurlActivity.this, getResources().getString(
                            R.string.loading_book), Toast.LENGTH_LONG).show();
                    }
                });

                Runnable eachMinute = new Runnable() {
                    @Override
                    public void run() {

                        PageEntity page = DatabaseUtil.getPage(app.getDatabase(),
                                String.valueOf(index2), String.valueOf(bookEntity.getId()));

                        if ((page != null) && (page.getChapter_id() != null)) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    mRelativeLayout.setVisibility(View.GONE);
                                    mFrameLayout.setVisibility(View.VISIBLE);
                                }
                            });

                            // found - set to index2
                            mCurlView.setCurrentIndex(index2);

                        } else if ( (bookEntity.getPages()>0) && (bookEntity.getPages()<index2) ) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    mRelativeLayout.setVisibility(View.GONE);
                                    mFrameLayout.setVisibility(View.VISIBLE);
                                }
                            });

                            // not found - set to 1
                            mCurlView.setCurrentIndex(1);

                        } else {

                            mLoaderHandler.postDelayed(this, 1000);
                        }
                    }
                };
                mLoaderHandler.postDelayed(eachMinute, 1);
            }

            // SAVE current page
            Log.e(TAG, "Continue...." +  index);
            DatabaseUtil.updateBook(app.getDatabase(),"current", index,
                    String.valueOf(bookEntity.getId()));

            // create TextView
            Bitmap txtBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(txtBitmap);
            tv.setText( spannedPageContent );
            tv.draw(c);
            c.drawBitmap(txtBitmap, 0, 0, tv.getPaint());

            spannedPageContent = new SpannableStringBuilder("");
            return txtBitmap;
        }

        @Override
        public void updatePage(CurlPage page, int width, int height, int index) {

            try {

                page.setTexture(loadBitmap(width, height, index), CurlPage.SIDE_FRONT);
                page.setColor(Color.rgb(180, 180, 180), CurlPage.SIDE_BACK);

            } catch (Exception e) {

                Log.e(TAG, e.getMessage());
            }
        }
    }

    private class SizeChangedObserver implements CurlView.SizeChangedObserver {
        @Override
        public void onSizeChanged(int w, int h) {

            mCurlView.setViewMode(CurlView.SHOW_ONE_PAGE);
            mCurlView.setMargins(.02f, .02f, .02f, .02f);
        }
    }
}
