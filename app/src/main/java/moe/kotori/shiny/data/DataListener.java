package moe.kotori.shiny.data;

import java.util.List;

/**
 * Created by tedzy on 2016/10/16.
 */

public interface DataListener {
    public void onData(List<MessageItem> items);
}
