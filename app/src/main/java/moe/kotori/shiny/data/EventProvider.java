package moe.kotori.shiny.data;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import moe.kotori.shiny.R;

/**
 * Created by tedzy on 2016/10/16.
 * 使用Socket.io长连接服务器获得推送消息。此功能活在后台的时候会发推送。
 */

public class EventProvider {
    private Context context;
    private Socket socket;
    final private String url = "http://websocket.shiny.kotori.moe:3737";

    public EventProvider(Context context){
        this.context = context;
    }

    public void Start(final PushEventListener pushEventListener, final InfoEventListener infoEventListener){
        try {
            socket = IO.socket(url);
        } catch (URISyntaxException e) {
            infoEventListener.onInfo(context.getString(R.string.FailedSocketServerInit));
        }
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                infoEventListener.onInfo(context.getString(R.string.SuccessConnected));
            }
        }).on("event", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                String json = (String)args[0];
                pushEventListener.onData(json);
            }
        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                infoEventListener.onInfo(context.getString(R.string.DisconnectedFromSocket));
            }
        });

        socket.connect();
    }
}
