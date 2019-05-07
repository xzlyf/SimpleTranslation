package com.xz.simpletranslation;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.xz.simpletranslation.net.FamilyInfo;

public class FamilyShare extends AppCompatActivity {
    private Button download1;
    private TextView myInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_share);
        download1 = findViewById(R.id.download_1);
        myInfo = findViewById(R.id.my_info);
        ActivityClick clickListener = new ActivityClick();
        download1.setOnClickListener(clickListener);
        myInfo.setOnClickListener(clickListener);
    }

    class ActivityClick implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.download_1: {
                    Uri uri = Uri.parse("http://xzlyf.club/DayWallpaperUpdate/apk/DayWallpaper.apk");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    break;
                }
                case R.id.my_info: {
                    Uri uri = Uri.parse("http://xzlyf.club");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    break;
                }
            }
        }
    }
}
