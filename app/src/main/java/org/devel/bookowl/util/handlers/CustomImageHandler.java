package org.devel.bookowl.util.handlers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;

import net.nightwhistler.htmlspanner.SpanStack;
import net.nightwhistler.htmlspanner.TagNodeHandler;

import org.htmlcleaner.TagNode;

import java.net.URLDecoder;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;

public class CustomImageHandler extends TagNodeHandler {

    private static String TAG = "CustomImageHandler";

    private Book book;

    public CustomImageHandler(Book book) {
        this.book = book;
    }

    @Override
    public void handleTagNode(TagNode node, SpannableStringBuilder builder,
                              int start, int end, SpanStack stack) {
        String src = node.getAttributeByName("src");

        builder.append("\uFFFC");

        // FIXME: check this
        Bitmap bitmap = loadBitmap(src.replace("../", ""));

        if (bitmap != null) {
            Drawable drawable = new BitmapDrawable(bitmap);
            drawable.setBounds(0, 0, bitmap.getWidth() - 1,
                    bitmap.getHeight() - 1);

            stack.pushSpan( new ImageSpan(drawable), start, builder.length() );
        }
    }

    protected Bitmap loadBitmap(String url) {

        Bitmap bitmap = null;

        try {

            Resource resource = book.getResources().getByHref(URLDecoder.decode(url));
            byte[] data = resource.getData();
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

        } catch (Exception e) {

            Log.e(TAG, e.toString());
        }

        return bitmap;
    }
}
