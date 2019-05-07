package com.xz.simpletranslation;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

public class FullScreen extends AppCompatActivity {
    private TextView fullScreenText;
    private FrameLayout layout;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fullScreenText = null;
        layout = null;
        Log.d("Full", "onDestroy: 执行");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);

        Intent intent = getIntent();
        fullScreenText = findViewById(R.id.fullscreen_text);
        layout = findViewById(R.id.layout_frame);
        fullScreenText.setText(intent.getStringExtra("msg"));

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
