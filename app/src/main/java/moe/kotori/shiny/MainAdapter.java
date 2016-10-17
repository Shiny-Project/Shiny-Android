package moe.kotori.shiny;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import moe.kotori.shiny.data.MessageItem;

/**
 * Created by tedzy on 2016/10/16.
 */

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyViewHolder> {
    private LayoutInflater inflater;
    private Context context;
    private List<MessageItem> messageItems;

    public MainAdapter(Context context){
        this.context = context;
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
        MyViewHolder holder = new MyViewHolder(inflater.inflate(R.layout.fragment_info_list_card, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MessageItem item = messageItems.get(position);
        holder.content.setText(item.getContent());
        holder.source.setText(item.getPublisher() + "#" + item.getHash());
        String[] levelChart = {
                "N",
                context.getString(R.string.NormalEvent),
                context.getString(R.string.FunEvent),
                context.getString(R.string.ImportantEvent),
                context.getString(R.string.EmergencyEvent),
                context.getString(R.string.WorldBeRuined)
        };
        holder.level.setText(levelChart[item.getLevel()]);
        holder.title.setText(item.getTitle());
    }

    @Override
    public int getItemCount() {
        return messageItems.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView level;
        TextView source;
        TextView content;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.item_data_title);
            level = (TextView) itemView.findViewById(R.id.item_data_level);
            source = (TextView) itemView.findViewById(R.id.item_data_source);
            content = (TextView) itemView.findViewById(R.id.item_data_content);
        }
    }
}
