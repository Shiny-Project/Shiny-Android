package moe.kotori.shiny;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

import moe.kotori.shiny.data.DataListener;
import moe.kotori.shiny.data.EndLessOnScrollListener;
import moe.kotori.shiny.data.MessageItem;
import moe.kotori.shiny.data.RequestHelper;
import moe.kotori.shiny.pref.SettingsActivity;

public class MainActivity extends AppCompatActivity {
    private SwipeRefreshLayout mainRefreshLayout;
    private RecyclerView mainListView;
    private MainAdapter mainAdapter;
    private RequestHelper requestHelper;
    private MsgReceiver msgReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //初始化界面
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.layout_swipe_refresh);
        mainRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.colorPrimaryDark);

        //初始化数据源
        requestHelper = new RequestHelper(this);
        mainListView = (RecyclerView) findViewById(R.id.main_list_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mainListView.setLayoutManager(linearLayoutManager);
        mainAdapter = new MainAdapter(this, requestHelper);
        mainListView.setAdapter(mainAdapter);
        reloadData();

        mainRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reloadData();
            }
        });

        mainListView.addOnScrollListener(new EndLessOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                requestHelper.getRecent(new DataListener() {
                    @Override
                    public void onData(List<MessageItem> items) {
                        mainAdapter.addMessageItemsOnTail(items);
                    }
                }, currentPage);
            }
        });

        //注册广播接收
        msgReceiver = new MsgReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("moe.kotori.shiny.RECEIVER");
        registerReceiver(msgReceiver, intentFilter);

        //启动服务
        if(isServiceRunning()) {
            //Toast.makeText(this, R.string.ShinyKeepAlived, Toast.LENGTH_LONG).show();
        }else{
            Intent mIntent = new Intent(this, SocketService.class);
            startService(mIntent);
        }
    }

    private void reloadData() {
        requestHelper.getRecent(new DataListener() {
            @Override
            public void onData(List<MessageItem> items) {
                mainAdapter.updateMessageItems(items);
                mainRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(msgReceiver);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_setting) {
            startActivity(new Intent(this, SettingsActivity.class));
        }else if (id == R.id.action_account){
            Toast.makeText(MainActivity.this, "not implementation", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("moe.kotori.shiny.SocketService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public class MsgReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            int type = intent.getIntExtra("type", 0);
            //String Info
            if(type == 1){
                String info = intent.getStringExtra("info");
                Toast.makeText(MainActivity.this, info, Toast.LENGTH_LONG).show();
            }
            //Message Info
            if(type == 2){
                String json = intent.getStringExtra("info");
                MessageItem item = new MessageItem();
                item.parseFromSocket(json);
                mainAdapter.insertFront(item);
                mainListView.smoothScrollToPosition(0);
            }
        }
    }
}
