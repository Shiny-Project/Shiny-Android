package moe.kotori.shiny;

import android.content.Context;
import android.text.Html;
import android.widget.TextView;

import moe.kotori.shiny.data.AsyncImageGetter;
import moe.kotori.shiny.data.BitmapCache;
import moe.kotori.shiny.data.RequestHelper;
import moe.kotori.shiny.data.VoidListener;

/**
 * Created by tedzy on 2016/10/18.
 */

public class HtmlWriter implements VoidListener{
    String htmlString;
    private Context context;
    private TextView textView;
    private RequestHelper requestHelper;
    private BitmapCache bitmapCache;


    public HtmlWriter(Context context, TextView textView, RequestHelper requestHelper, BitmapCache bitmapCache){
        this.textView = textView;
        this.context = context;
        this.requestHelper = requestHelper;
        this.bitmapCache = bitmapCache;
    }

    public void render(String html){
        this.htmlString = html;
        htmlString = htmlString.replaceAll("\\n", "<br>");
        htmlString = htmlString.replaceAll("<img\\s(.+?)>", "<br><img $1><br>");
        @SuppressWarnings("deprecation")
        CharSequence charSequence = Html.fromHtml(htmlString,
                new AsyncImageGetter(context, textView, requestHelper, bitmapCache, this),
                null);

        textView.setText(charSequence);
    }

    protected void reRender(){
        @SuppressWarnings("deprecation")
        CharSequence charSequence = Html.fromHtml(htmlString,
                new AsyncImageGetter(context, textView, requestHelper, bitmapCache, null),
                null);
        textView.setText(charSequence);
    }

    @Override
    public void onEvent() {
        reRender();
    }
}
