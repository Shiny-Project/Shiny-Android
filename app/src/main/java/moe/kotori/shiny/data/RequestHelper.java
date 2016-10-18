package moe.kotori.shiny.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import moe.kotori.shiny.R;

/**
 * Created by tedzy on 2016/10/16.
 * 获取最近的几条消息
 */

public class RequestHelper {
    private Context context;
    private final String url = "https://shiny.kotori.moe/Data/recent";
    private RequestQueue rq;

    public RequestHelper(Context context){
        this.context = context;
        this.rq = Volley.newRequestQueue(context);
    }

    public void getRecent(final DataListener dataListener) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, this.url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                List<MessageItem> messageItems = new ArrayList<>();

                try {
                    JSONObject mainData = new JSONObject(response);
                    JSONArray dataList = mainData.getJSONArray("data");
                    for (int i = 0; i < dataList.length(); i++) {
                        JSONObject json = dataList.getJSONObject(i);
                        MessageItem mi = new MessageItem();
                        mi.parse(json);
                        messageItems.add(mi);
                    }
                } catch (JSONException e) {
                    Toast.makeText(context, "parse json error" + e.getMessage(), Toast.LENGTH_LONG).show();
                }

                dataListener.onData(messageItems);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "read json error", Toast.LENGTH_LONG).show();
            }
        });

        rq.add(stringRequest);
    }

    public void getImage(String url, final CoverListener coverListener){
        ImageRequest imageRequest = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                coverListener.OnImageGet(response);
            }
        }, 0, 0, Bitmap.Config.ARGB_4444, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                coverListener.OnImageGet(BitmapFactory.decodeResource(context.getResources(), R.mipmap.kotori));
            }
        });

        rq.add(imageRequest);
    }

    @NonNull
    public static String getCacheKey(String url, int maxWidth, int maxHeight) {
        return (new StringBuilder(url.length() + 12)).append("#W")
                .append(maxWidth)
                .append("#H")
                .append(maxHeight)
                .append(url)
                .toString();
    }

    @NonNull
    public static String getCacheKey(String url) {
        return getCacheKey(url, 0, 0);
    }
}
