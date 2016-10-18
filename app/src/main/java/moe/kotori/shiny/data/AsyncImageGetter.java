package moe.kotori.shiny.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.widget.TextView;

import moe.kotori.shiny.R;

/**
 * Created by tedzy on 2016/10/18.
 */

public class AsyncImageGetter implements Html.ImageGetter {
    private TextView textView;
    private RequestHelper requestHelper;
    private BitmapCache bitmapCache;
    private Context context;
    private VoidListener voidListener;
    private int minHeight = 40;

    public AsyncImageGetter(Context context, TextView textView, RequestHelper requestHelper, BitmapCache bitmapCache, VoidListener voidListener) {
        this.context = context;
        this.requestHelper = requestHelper;
        this.textView = textView;
        this.bitmapCache = bitmapCache;
        this.voidListener = voidListener;
        minHeight = getFontHeight(textView.getTextSize());
    }

    private int getFontHeight(float textSize) {
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        Paint.FontMetrics fm = paint.getFontMetrics();
        return (int) Math.ceil(fm.descent - fm.ascent);
    }

    @Override
    public Drawable getDrawable(String source) {
        final String key = RequestHelper.getCacheKey(source);

        Bitmap bitmap = bitmapCache.getBitmap(key);

        if(bitmap == null){
            requestHelper.getImage(source, new CoverListener() {
                @Override
                public void OnImageGet(Bitmap image) {
                    bitmapCache.putBitmap(key, image);
                    if(voidListener != null) {
                        voidListener.onEvent();
                    }
                }
            });
            return context.getDrawable(R.mipmap.kotori);
        }else{
            Drawable drawable = new BitmapDrawable(context.getResources(), bitmap);

            double scale = 1.0;
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int maxWidth = textView.getMaxWidth();
            if(width > maxWidth){
                scale = (double) maxWidth /  (double) width;
                width = maxWidth;
                height = (int) Math.ceil(height * scale);
            }
            height = height < minHeight ? minHeight : height;
            width = width < minHeight ? minHeight : width;
            drawable.setBounds(0, 0, width, height);
            return drawable;
        }
    }
}
