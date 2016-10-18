package moe.kotori.shiny;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

import moe.kotori.shiny.data.DataListener;
import moe.kotori.shiny.data.MessageItem;
import moe.kotori.shiny.data.RequestHelper;

public class MainActivity extends AppCompatActivity {
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

        //初始化数据源
        requestHelper = new RequestHelper(this);
        mainListView = (RecyclerView) findViewById(R.id.main_list_view);
        mainListView.setLayoutManager(new LinearLayoutManager(this));
        mainAdapter = new MainAdapter(this, requestHelper);
        mainListView.setAdapter(mainAdapter);
        reloadData();

        //注册广播接收
        msgReceiver = new MsgReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("moe.kotori.shiny.RECEIVER");
        registerReceiver(msgReceiver, intentFilter);

        //启动服务
        if(isServiceRunning()) {
            Toast.makeText(this, R.string.ShinyKeepAlived, Toast.LENGTH_LONG).show();
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
        if (id == R.id.action_refresh) {
            reloadData();
            Toast.makeText(this, R.string.refreshSucceed, Toast.LENGTH_LONG).show();
            return true;
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
