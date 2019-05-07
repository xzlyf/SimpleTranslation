package com.xz.simpletranslation.net;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.xz.simpletranslation.broadcast.NetworkChange;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.util.Arrays;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 在线获取家族软件列表
 * 获取：
 * 1.软件名字
 * 2.软件大图标
 *
 * 目前已搁置进场，暂时不使用该类转为使用写入程序式
 */
public class FamilyInfo {

    private static final String MYINFOURl = "http://xzlyf.club/Info/my_info.xml" ;
    private static final String TAG = "FamilyInfo";
    private final Context context;

    public FamilyInfo(Context context){
        this.context = context;
    }
    public void getInfo() {
        //检查网络
        if (!NetworkChange.islive()) {
            Toast.makeText(context, "当前网络异常", Toast.LENGTH_SHORT).show();
            return;
        }
        sendRequestWithOkhttp(new UpdateCallback() {
            @Override
            public void finish(String... values) {
                String[] title = formatData(values[1]);
                String[] icon = formatData(values[2]);
                Log.d(TAG, "finish: "+Arrays.toString(title));
                Log.d(TAG, "finish: "+Arrays.toString(icon));
            }

            @Override
            public void error(Exception e) {

            }
        });
    }

    /**
     * 格式化数据
     * @param values
     */
    private String[] formatData(String values) {
        return values.split("\\$");
    }

    /**
     * 请求网络响应
     * @param callback
     */
    private void sendRequestWithOkhttp(UpdateCallback callback){
        new Thread(() -> {
            try {

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(MYINFOURl)
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
     * @param data
     * @param callback
     */
    private void parseXMLWithPull(String data, UpdateCallback callback) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(data));
            int evenType = xmlPullParser.getEventType();
            String date = "";
            String title = "";
            String icon = "";
            while (evenType != XmlPullParser.END_DOCUMENT) {
                String nodeName = xmlPullParser.getName();
                switch (evenType) {
                    case XmlPullParser.START_TAG: {
                        if ("date".equals(nodeName)) {
                            date = xmlPullParser.nextText();
                        } else if ("title".equals(nodeName)) {
                            title = xmlPullParser.nextText();
                        } else if ("icon".equals(nodeName)) {
                            icon = xmlPullParser.nextText();
                        }
                        break;
                    }
                    //当解析完后的动作

                    case XmlPullParser.END_TAG: {
                        if ("Info".equals(nodeName)) {
                            //回调数据
                            callback.finish(date,title,icon);
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
