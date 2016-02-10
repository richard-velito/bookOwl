package org.devel.bookowl.util.handlers;

import android.text.SpannableStringBuilder;
import android.util.Log;

import com.osbcp.cssparser.CSSParser;
import com.osbcp.cssparser.Rule;

import net.nightwhistler.htmlspanner.SpanStack;
import net.nightwhistler.htmlspanner.TagNodeHandler;
import net.nightwhistler.htmlspanner.css.CSSCompiler;
import net.nightwhistler.htmlspanner.css.CompiledRule;

import org.devel.bookowl.entity.BookEntity;
import org.htmlcleaner.TagNode;

import java.io.StringWriter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.util.IOUtil;

public class CSSLinkHandler extends TagNodeHandler {

    private static String TAG = "CSSLinkHandler";

    private BookEntity bookEntity;

    public CSSLinkHandler(BookEntity bookEntity) {
        this.bookEntity = bookEntity;
    }

    public void handleTagNode(TagNode node, SpannableStringBuilder builder, int start,
                              int end, SpanStack spanStack) {

        String type = node.getAttributeByName("type");
        String href = node.getAttributeByName("href");

        if ( type == null || ! type.equals("text/css") ) {

            //Log.e(TAG, "Ignoring link of type " + type);
        } else {

            try {

                Resource resource = bookEntity.getBook().
                        getResources().getByHref(URLDecoder.decode(href.replace("../", "")));

                StringWriter writer = new StringWriter();
                IOUtil.copy(resource.getReader(), writer);

                List<CompiledRule> rules = bookEntity.getCSSRules(href);

                if (rules==null) {

                    rules = get(CSSParser.parse(writer.toString()));
                    //TODO: save rules
                    bookEntity.setCssRules(href, rules);
                }
                for ( CompiledRule rule: rules ) {

                    spanStack.registerCompiledRule(rule);
                }

            } catch (Exception e) {

                Log.e(TAG, e.toString());
            }
        }
    }

    public List<CompiledRule> get(List<Rule> rules) {

        List<CompiledRule> result = new ArrayList<>();

        for ( Rule rule: rules ) {

            if ( rule.getSelectors().size() == 1 &&
                    rule.getSelectors().get(0).toString().equals("@font-face")) {

                //Log.e(TAG, rule.toString());

            } else {
                result.add(CSSCompiler.compile(rule, getSpanner()));
            }
        }

        return result;
    }


}
