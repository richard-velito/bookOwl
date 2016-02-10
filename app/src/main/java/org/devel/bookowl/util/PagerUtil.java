package org.devel.bookowl.util;

import android.graphics.Canvas;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.util.Log;
import android.widget.TextView;

import org.devel.bookowl.entity.PageEntity;

import java.util.ArrayList;
import java.util.List;

public class PagerUtil {

    private static String TAG = "PagerUtil";

    public static Spanned text;

    /**
     *
     * @param spanned Chapter content
     * @param tv
     * @return
     */
    public static List<PageEntity> measure(Spanned spanned,
                                           TextView tv, int n) {

        ArrayList<PageEntity> response = new ArrayList<PageEntity>();

        try {

            StaticLayout layout = new
                    StaticLayout(spanned, tv.getPaint(),
                    tv.getWidth() - (tv.getPaddingRight() + tv.getPaddingLeft()),
                    Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            layout.draw(new Canvas());

            int totalLines = layout.getLineCount();
            int topLineNextPage = -1;
            int pageStartOffset = 0;
            int i = 0;

            while (topLineNextPage < totalLines - 1) {

                PageEntity page = new PageEntity();

                int topLine = layout.getLineForOffset(pageStartOffset);
                topLineNextPage = layout.getLineForVertical( layout.getLineTop( topLine )
                        + tv.getHeight() - (tv.getPaddingBottom()+tv.getPaddingTop())  );

                if ( topLineNextPage == topLine ) {
                    topLineNextPage = topLine + 1;
                }

                int pageEnd = layout.getLineEnd(topLineNextPage -1);

                if (pageEnd > pageStartOffset ) {

                    if ( spanned.subSequence(pageStartOffset, pageEnd).toString().trim().length() > 0) {
                        page.setStart_offset(pageStartOffset);
                    }
                    pageStartOffset = layout.getLineStart(topLineNextPage);
                    page.setEnd_offset(pageStartOffset);
                }

                page.setNumber(n);

                response.add(i, page);
                n++;
                i++;
            }

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return response;
    }

    public static Spannable content(int start, int end) {

        CharSequence cutOff = text.subSequence(start, end);
        SpannableStringBuilder response = new SpannableStringBuilder(cutOff.toString());

        try {

            Object[] spans = text.getSpans(start, end, Object.class);

            for (int i = 0; i < spans.length; i++) {

                if (!spans[i].getClass().getSimpleName().toString().trim().equals("BorderSpan")) {
                    int st = text.getSpanStart(spans[i]);
                    int en = text.getSpanEnd(spans[i]);
                    int fl = text.getSpanFlags(spans[i]);

                    if (st < start)
                        st = start;
                    if (en > end)
                        en = end;

                    response.setSpan(spans[i], st - start, en - start,
                            fl);
                } else {
                    Log.e(TAG, spans[i].getClass().getSimpleName().toString().trim());
                }
            }

        } catch(Exception e) {

            Log.e(TAG, e.getMessage());
        }
        return response;
    }

}
