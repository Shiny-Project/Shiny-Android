package moe.kotori.shiny.data;

import java.util.List;

/**
 * 服务器获取数据异步返回接口
 * Created by tedzy on 2016/10/16.
 */

public interface DataListener {
    public void onData(List<MessageItem> items);
}
