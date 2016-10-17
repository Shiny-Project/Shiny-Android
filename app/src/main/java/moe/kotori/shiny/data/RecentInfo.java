package moe.kotori.shiny.data;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tedzy on 2016/10/16.
 * 获取最近的几条消息
 */

public class RecentInfo {
    private Context context;
    private final String url = "https://shiny.kotori.moe/Data/recent";
    private RequestQueue rq;

    public RecentInfo(Context context){
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
}
