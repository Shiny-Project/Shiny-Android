package moe.kotori.shiny;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import moe.kotori.shiny.data.AsyncImageGetter;
import moe.kotori.shiny.data.BitmapCache;
import moe.kotori.shiny.data.CoverListener;
import moe.kotori.shiny.data.MessageItem;
import moe.kotori.shiny.data.RequestHelper;

/**
 * Created by tedzy on 2016/10/16.
 */

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyViewHolder> implements OnItemTitleClickListener{
    private LayoutInflater inflater;
    private Context context;
    private RequestHelper requestHelper;
    private List<MessageItem> messageItems;
    private BitmapCache bitmapCache = new BitmapCache();

    public MainAdapter(Context context, RequestHelper requestHelper){
        this.context = context;
        this.requestHelper = requestHelper;
        messageItems = new ArrayList<>();
        MessageItem n = new MessageItem();
        n.setContent(context.getString(R.string.ShinyInitFinished));
        n.setLevel(1);
        n.setCover("");
        n.setHash("0001");
        n.setLink("");
        n.setId("0");
        n.setTitle("Shiny~");
        n.setPublisher("SHINY_ANDROID");
        messageItems.add(n);
        inflater = LayoutInflater.from(context);
    }

    public void updateMessageItems(List<MessageItem> newList){
        this.messageItems = newList;
        this.notifyDataSetChanged();
    }

    public void insertFront(MessageItem newItem){
        this.messageItems.add(0, newItem);
        this.notifyItemInserted(0);

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(inflater.inflate(R.layout.fragment_info_list_card, parent, false), this);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        //填入内容
        final MessageItem item = messageItems.get(position);
        HtmlWriter htmlWriter = new HtmlWriter(context, holder.content, requestHelper, bitmapCache);
        htmlWriter.render(item.getContent());
        //填入来源
        holder.source.setText(item.getPublisher() + "#" + item.getHash());
        //填入等级
        String[] levelChart = {
                "N",
                context.getString(R.string.NormalEvent),
                context.getString(R.string.FunEvent),
                context.getString(R.string.ImportantEvent),
                context.getString(R.string.EmergencyEvent),
                context.getString(R.string.WorldBeRuined)
        };
        holder.level.setText(levelChart[item.getLevel()]);
        //填入标题
        holder.title.setText(item.getTitle());
        //填入封面
        String coverUrl = item.getCover();
        if(coverUrl != null && !coverUrl.equals("")){
            requestHelper.getImage(coverUrl, new CoverListener() {
                @Override
                public void OnImageGet(Bitmap image) {
                    holder.cover.setImageBitmap(image);
                }
            });
        }else{
            holder.cover.setImageResource(R.mipmap.kotori);
        }

        //使标题可以点击
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String link = item.getLink();
                if(link != null && !Objects.equals(link, "")) {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(link);
                    intent.setData(content_url);
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return messageItems.size();
    }

    @Override
    public void onItemTitleClick(View view, int position) {
        Toast.makeText(context, "R " + position, Toast.LENGTH_LONG).show();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{
        TextView title;
        TextView level;
        TextView source;
        TextView content;
        ImageView cover;
        OnItemTitleClickListener titleClickListener;

        public MyViewHolder(View itemView, OnItemTitleClickListener titleClickListener) {
            super(itemView);
            this.titleClickListener = titleClickListener;
            title = (TextView) itemView.findViewById(R.id.item_data_title);
            level = (TextView) itemView.findViewById(R.id.item_data_level);
            source = (TextView) itemView.findViewById(R.id.item_data_source);
            content = (TextView) itemView.findViewById(R.id.item_data_content);
            cover = (ImageView) itemView.findViewById(R.id.item_data_cover);

            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            if(titleClickListener != null){
                titleClickListener.onItemTitleClick(v, getLayoutPosition());
            }
            return true;
        }
    }
}
