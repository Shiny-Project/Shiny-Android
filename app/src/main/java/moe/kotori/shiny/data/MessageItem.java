package moe.kotori.shiny.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tedzy on 2016/10/16.
 * 一条消息的信息模板
 */

public class MessageItem {
    private String id; //消息的内部ID，无需在意。
    private int level; //消息等级 1 - 一般事件 2 - 有趣的事件 3 - 重要事件 4 - 紧急事件 5 - 世界毁灭
    private String publisher; //消息来源
    private String hash; //消息的唯一标识值
    private String title; //消息内容标题
    private String link; //消息内容链接
    private String cover; //消息内容封面图
    private String content; //消息内容正文
    private String createdAt; //时间

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedAt(){
        return createdAt;
    }

    public void setCreatedAt(String createdAt){
        this.createdAt = createdAt;
    }

    public void parse(JSONObject json){
        try {
            this.id = json.getString("id");
        } catch (JSONException e) {
            this.id = "0";
        }
        try {
            this.level = json.getInt("level");
        } catch (JSONException e) {
            this.level = 0;
        }
        try {
            this.publisher = json.getString("publisher");
        } catch (JSONException e) {
            this.publisher = "Unknown";
        }
        try {
            this.hash = json.getString("hash");
        } catch (JSONException e) {
            this.hash = "ERR";
        }
        JSONObject contentData = null;
        try {
            contentData = json.getJSONObject("data");
        } catch (JSONException e) {
            contentData = null;
        }
        try {
            this.title = contentData.getString("title");
        } catch (JSONException e) {
            this.title = "NoTitle";
        }
        try {
            this.cover = contentData.getString("cover");
        } catch (JSONException e) {
            this.cover = "";
        }
        try {
            this.link = contentData.getString("link");
        } catch (JSONException e) {
            this.link = "";
        }
        try {
            this.content = contentData.getString("content");
        } catch (JSONException e) {
            this.content = "Content is blank.";
        }
        try{
            this.createdAt = json.getString("createdAt");
        }
        catch (JSONException e){
            this.createdAt = "未知时间";
        }
    }

    public void parseFromSocket(String json){
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(json);
        } catch (JSONException e) {
            jsonObj = null;
        }


        this.id = "0";
        try {
            this.publisher = jsonObj.getString("spiderName");
        } catch (JSONException e) {
            this.publisher = "Unknown";
        }
        try {
            this.hash = jsonObj.getString("hash");
        } catch (JSONException e) {
            this.hash = "ERR";
        }
        try {
            this.level = jsonObj.getInt("level");
        } catch (JSONException e) {
            this.level = 0;
        }

        JSONObject data = null;
        try {
            data = jsonObj.getJSONObject("data");
        } catch (JSONException e) {
            data = null;
        }

        try {
            this.title = data.getString("title");
        } catch (JSONException e) {
            this.title = "NoTitle";
        }
        try {
            this.cover = data.getString("cover");
        } catch (JSONException e) {
            this.cover = "";
        }
        try {
            this.link = data.getString("link");
        } catch (JSONException e) {
            this.link = "";
        }
        try {
            this.content = data.getString("content");
        } catch (JSONException e) {
            this.content = "Content is blank.";
        }
        this.createdAt = "刚刚";
    }
}
