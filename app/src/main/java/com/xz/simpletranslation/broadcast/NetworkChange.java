package com.xz.simpletranslation.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * 网络变化广播
 */
public class NetworkChange extends BroadcastReceiver {
    private static boolean islive;

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        islive = info != null && info.isAvailable();
        if (!islive) {
            Toast.makeText(context, "当前网络异常", Toast.LENGTH_SHORT).show();

        }

    }

    /**
     * 返回当前网络是否通畅
     *
     * @return true 连通
     */
    public static boolean islive() {
        return islive;
    }
}
