package com.xz.simpletranslation.net;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.xz.simpletranslation.CustomLayout.UpdateDialog;
import com.xz.simpletranslation.MainActivity;
import com.xz.simpletranslation.R;
import com.xz.simpletranslation.broadcast.NetworkChange;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 更新软件
 */
public class Update {
    //检查更新服务器地址
    private final String UPDATEURL = "http://xzlyf.club/SimpleTranslation/update.xml";
    private Context context;
    private static int localVersionCode;
    private static String localVersionName;

    public Update(Context context) {
        this.context = context;

    }

    /**
     * 回到ui线程
     * handler
     */
    Handler mainHandler = new Handler(Looper.getMainLooper()) {
    };

    /**
     * 外部调用
     * 检查更新
     */
    public void checkUpdate() {

        //检查网络
        if(!NetworkChange.islive()){
            Toast.makeText(context,"当前网络异常",Toast.LENGTH_SHORT).show();
            return;
        }

        //等待数据回调
        sendRequestWithOkHttp(new UpdateCallback() {
            @Override
            public void finish(String... values) {
                //获取服务器版本信息
                /*
                String level = values[0];
                String name = values[1];
                int code = Integer.valueOf(values[2]);
                String msg = values[3];
                String link = values[4];
                */

                //获取本地版本号
                getLocalVersion();

                //比较版本
                if (compareVersion(Integer.valueOf(values[2]))) {
                    //回到主线程 - 第一种方法
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            //自定义dialog
//                            Dialog updateDialog = new AlertDialog.Builder(context).create();
//                            updateDialog.show();
////                            updateDialog.setCancelable(false);
//                            Window window = updateDialog.getWindow();
//                            window.setContentView(R.layout.dialog_update);
//                            window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                            //用自定义封装的dialog
                            UpdateDialog dialog = new UpdateDialog(context);
                            dialog.create();
                            dialog.show();
                            dialog.setVersionText(values[1]);
                            dialog.setUpdateMsg(values[3]);
                            dialog.setApkUrl(values[4]);


                        }
                    });


                } else {

                    //回到主线程-第二种方法
                    ((MainActivity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "最新版本啦！", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void error(Exception e) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context,"服务器异常，请稍后重试",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });


    }

    /**
     * 比较版本
     * 要更新true
     * 不要更新false
     */
    private boolean compareVersion(int cloudCode) {
        return localVersionCode < cloudCode;
    }

    /**
     * 获取版本号
     */
    private void getLocalVersion() {
        if (localVersionName != null && localVersionCode != 0) {
            return;
        }
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            localVersionCode = info.versionCode;
            localVersionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * http响应
     * 获取网页数据
     */
    private void sendRequestWithOkHttp(UpdateCallback callback) {
        new Thread(() -> {
            try {

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(UPDATEURL)
                        .build();
                Response response = client.newCall(request).execute();
                String data = response.body().string();
                //解析xml
                parseXMLWithPull(data, callback);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 解析xml
     *
     * @param data 网页数据
     */
    private void parseXMLWithPull(String data, UpdateCallback callback) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(data));
            int evenType = xmlPullParser.getEventType();
            String level = "";
            String name = "";
            String code = "";
            String msg = "";
            String link = "";
            while (evenType != XmlPullParser.END_DOCUMENT) {
                String nodeName = xmlPullParser.getName();
                switch (evenType) {
                    case XmlPullParser.START_TAG: {
                        if ("level".equals(nodeName)) {
                            level = xmlPullParser.nextText();
                        } else if ("name".equals(nodeName)) {
                            name = xmlPullParser.nextText();
                        } else if ("code".equals(nodeName)) {
                            code = xmlPullParser.nextText();
                        } else if ("msg".equals(nodeName)) {
                            msg = xmlPullParser.nextText();
                        } else if ("link".equals(nodeName)) {
                            link = xmlPullParser.nextText();
                        }
                        break;
                    }
                    //当解析完后的动作

                    case XmlPullParser.END_TAG: {
                        if ("ST".equals(nodeName)) {
//                            Log.d("Update", "parseXMLWithPull: "+level);
//                            Log.d("Update", "parseXMLWithPull: "+name);
//                            Log.d("Update", "parseXMLWithPull: "+code);
//                            Log.d("Update", "parseXMLWithPull: "+msg);
//                            Log.d("Update", "parseXMLWithPull: "+link);
                            //回调数据
                            callback.finish(level, name, code, msg, link);
                        }
                    }
                    default:
                        break;
                }
                evenType = xmlPullParser.next();
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
            //回调错误数据
            callback.error(e);
        } catch (IOException e) {
            e.printStackTrace();
            //回调错误数据
            callback.error(e);
        }
    }
}
