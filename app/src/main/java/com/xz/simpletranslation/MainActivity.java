package com.xz.simpletranslation;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.xz.simpletranslation.CustomLayout.ChooseLanguage;
import com.xz.simpletranslation.CustomLayout.TargetLanguage;
import com.xz.simpletranslation.CustomLayout.UserInputText;
import com.xz.simpletranslation.broadcast.NetworkChange;
import com.xz.simpletranslation.net.Translation.DateEsponseCallback;
import com.xz.simpletranslation.net.Translation.ToLanguage;
import com.xz.simpletranslation.net.Update;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    //自定义控件的id
    private final int LANGUAGE_SELECT = 9528;
    private final int TARGETLANGUAGE = 9582;
    private final int USERINPUTTEXT = 7543;
    //自定义布局对象
    private ChooseLanguage chooseLanguage;
    private TargetLanguage targetLanguage;
    private UserInputText userInputText;

    private ToLanguage toLanguage;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private NetworkChange networkChangeBroadcast;

    private Update update ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        listener();


    }

    /**
     * 监听事件
     */
    private void listener() {
        Button start_button = findViewById(R.id.start_button);
        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //判断当前网络是否通畅,不通畅提示用户然后Return
                if (!networkChangeBroadcast.islive()){
                    Toast.makeText(MainActivity.this,"请检查网络",Toast.LENGTH_SHORT).show();
                    return;
                }
                //判断from语言和to语言是否一样，一样直接复制到目标语言，防止浪费api资源
                if (chooseLanguage.isSame()){
                    targetLanguage.setTargetText(userInputText.gettext());
                    return;
                }
                //显示目标语言控件
                targetLanguage.showFrame();

                toLanguage.getString(userInputText.gettext(), chooseLanguage.LanguageExt(), new DateEsponseCallback() {
                    @Override
                    public void finish(final String toText) {
                        //向界面输出文本
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                targetLanguage.setTargetText(toText);

                            }
                        });
                    }

                    @Override
                    public void error(final int code) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                targetLanguage.setTargetText(code + "");

                            }
                        });

                    }
                });

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //关闭广播接收器
        unregisterReceiver(networkChangeBroadcast);
    }

    /**
     *初始化
     * 初始化控件
     */
    private void init() {
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        //初始化一些控件
        chooseLanguage = findViewById(LANGUAGE_SELECT);
        targetLanguage = findViewById(TARGETLANGUAGE);
        userInputText = findViewById(USERINPUTTEXT);
        toLanguage = new ToLanguage();
        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.menu);
        }
        //网络变化广播
        networkChangeBroadcast = new NetworkChange();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(networkChangeBroadcast,intentFilter);
        //检查更新对象
        update = new Update(MainActivity.this);

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_check_update:
                        //检查更新
                        update.checkUpdate();
                        break;
                    case R.id.share_family:
                        startActivity(new Intent(MainActivity.this,FamilyShare.class));

                }
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return true;
    }
}
