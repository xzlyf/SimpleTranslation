package com.xz.simpletranslation.CustomLayout;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xz.simpletranslation.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URI;
import java.text.DecimalFormat;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class UpdateDialog extends Dialog implements View.OnClickListener {
    private final Context context;
    private TextView updateVersionCode;
    private TextView updateMsg;
    private Button updateCannel;
    private Button updateDownload;
    private LinearLayout downloadInfo;
    private ProgressBar downloadBar;
    private TextView downloadBarText;
    private LinearLayout g1;
    private LinearLayout g2;
    private LinearLayout g3;
    private LinearLayout g4;
    private Button openApk;
    private Button dismissButton;

    private String apkUrl;
    //apk地址
    private String dir;
    Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void create() {
        super.create();
        View view = View.inflate(context, R.layout.dialog_update, null);
        setContentView(view);
        setCancelable(false);
        init(view);


    }


    /**
     * 设置对话框一些信息
     *
     * @param text 版本代号
     */
    public void setVersionText(String text) {
        updateVersionCode.setText("V" + text);
    }

    /**
     * 设置对话框一些信息
     *
     * @param text 更新内容
     */
    public void setUpdateMsg(String text) {
        updateMsg.setText(text);
    }

    public void setApkUrl(String text) {
        this.apkUrl = text;
    }


    /**
     * 初始化
     */
    private void init(View view) {
        dir = context.getExternalFilesDir("apk") + "/apk.apk";
        updateVersionCode = findViewById(R.id.update_version_code);
        updateMsg = view.findViewById(R.id.update_msg);
        updateCannel = view.findViewById(R.id.update_cannel);
        updateDownload = view.findViewById(R.id.update_download);
        downloadInfo = findViewById(R.id.download_info);
        downloadBar = findViewById(R.id.download_bar);
        downloadBarText = findViewById(R.id.download_bar_text);
        updateCannel.setOnClickListener(this);
        updateDownload.setOnClickListener(this);
        g1 = findViewById(R.id.ground_1);
        g2 = findViewById(R.id.ground_2);
        g3 = findViewById(R.id.ground_3);
        g4 = findViewById(R.id.ground_4);
        openApk = findViewById(R.id.open_apk);
        openApk.setOnClickListener(this);
        dismissButton = findViewById(R.id.dismiss_dilog);
        dismissButton.setOnClickListener(this);

    }

    public UpdateDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    /**
     * 下载线程 下载安装包到本地
     */
    private void doDownload(String apkUrl) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //设置控件显示方式 隐藏组1 显示组2
                setViewShow(g1, false);
                setViewShow(g2, true);

                //删除已存在apk
                File file = new File(dir);
                if (file.exists()) {
                    file.delete();
                }

                InputStream is = null;
                RandomAccessFile save = null;
                Response response = null;
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder().url(apkUrl).build();

                try {
                    response = client.newCall(request).execute();
                    //文件大小
                    long l;
                    if (response.body() != null) {
                        l = response.body().contentLength();
                        is = response.body().byteStream();
                        save = new RandomAccessFile(dir, "rw");
                        byte[] b = new byte[1024];
                        int len;
                        int total = 0;
                        while ((len = is.read(b)) != -1) {
                            total += len;
                            //更新ui
                            updateValue((int) Math.ceil(total / (double) l * 100));
                            save.write(b, 0, len);
                        }
                        setViewShow(g2, false);
                        setViewShow(g3, true);

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("UpdateDialog", "run:下载失败 ");
                    setViewShow(g2,false);
                    setViewShow(g4,true);

                } finally {
                    //关闭流
                    try {
                        if (save != null) {
                            save.close();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (response.body() != null) {
                        response.body().close();
                    }

                }

            }
        }).start();
    }

    /**
     * 更新数据
     * 更新进度条
     *
     * @param i
     */
    private void updateValue(int i) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                downloadBar.setProgress(i);
                downloadBarText.setText(i + "%");
            }
        });
    }

    private void setViewShow(View view, boolean b) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (b) {
                    view.setVisibility(View.VISIBLE);
                } else {
                    view.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.update_cannel:
                dismiss();
                break;
            case R.id.update_download:
                //显示下载信息控件
                downloadInfo.setVisibility(View.VISIBLE);
                doDownload(apkUrl);
                break;
            case R.id.open_apk:
                installApk();
                dismiss();
                break;
            case R.id.dismiss_dilog:
                dismiss();
                break;
        }

    }

    /**
     * 安装Apk
     */
    private void installApk() {
        File file = new File(dir);
        if (!file.exists()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Log.d("UpdateDialog", "installApk: 执行到这");
        //安卓7.0uri问题
        if (Build.VERSION.SDK_INT >= 24) {
            Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");

        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        context.startActivity(intent);

    }
}
