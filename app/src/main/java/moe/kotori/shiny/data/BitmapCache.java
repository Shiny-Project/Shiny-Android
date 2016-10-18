package moe.kotori.shiny.data;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by tedzy on 2016/10/18.
 */

public class BitmapCache implements ImageLoader.ImageCache {
    private LruCache<String, Bitmap> cache;

    public BitmapCache(){
        cache = new LruCache<String, Bitmap>(25*1024*1024){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };
    }

    @Override
    public Bitmap getBitmap(String key) {
        return cache.get(key);
    }

    @Override
    public void putBitmap(String key, Bitmap bitmap) {
        cache.put(key, bitmap);
    }
}
