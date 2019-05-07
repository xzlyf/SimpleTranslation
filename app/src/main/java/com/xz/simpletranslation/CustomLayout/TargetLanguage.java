package com.xz.simpletranslation.CustomLayout;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.drm.DrmStore;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xz.simpletranslation.FullScreen;
import com.xz.simpletranslation.MainActivity;
import com.xz.simpletranslation.R;

/**
 * 目标语言界面
 */
public class TargetLanguage extends LinearLayout implements View.OnClickListener {
    private TextView targetText;
    private Button copyText;
    private Button shareText;
    private Button fullscreen;
    private LinearLayout targetLanguageLayout;
    private ClipboardManager manager ;

    private Context context;


    //控件id
    private final int TARGETLANGUAGE = 9582;

    public TargetLanguage(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //动态加载布局并指定该空间的id
        LayoutInflater.from(context).inflate(R.layout.layout_target_language, this).setId(TARGETLANGUAGE);

        this.context = context;

        targetText = findViewById(R.id.target_text);
        copyText = findViewById(R.id.copy_text);
        shareText = findViewById(R.id.share_text);
        fullscreen = findViewById(R.id.fullscreen);
        targetLanguageLayout = findViewById(R.id.target_language_layout);
        //复制到剪切板
        manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        //设置监听
        copyText.setOnClickListener(this);
        shareText.setOnClickListener(this);
        fullscreen.setOnClickListener(this);


    }


    /**
     * 对外接口
     * 向textview输出类容
     *
     * @param text 内容
     */
    public void setTargetText(String text) {
        targetText.setText(text);

    }


    /**
     * 默认进入隐藏控件的
     * 对外接口
     * 显示控件
     */
    public void showFrame() {
        if (targetLanguageLayout.getVisibility() != LinearLayout.VISIBLE) {
            targetLanguageLayout.setVisibility(LinearLayout.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.copy_text:
                //复制到剪切板
                manager.setPrimaryClip(ClipData.newPlainText("Targetlanguage", targetText.getText().toString()));
                Toast.makeText(context,"已复制",Toast.LENGTH_SHORT).show();
                break;
            case R.id.fullscreen:
                context.startActivity(new Intent(context,FullScreen.class)
                        .putExtra("msg",targetText.getText().toString()));
                break;
            case R.id.share_text:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT,"来自轻巧翻译分享");
                intent.putExtra(Intent.EXTRA_TEXT,targetText.getText().toString());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(Intent.createChooser(intent,"分享至"));
                break;
        }
    }
}
