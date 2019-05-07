package com.xz.simpletranslation.net;

import android.content.Context;
import android.widget.Toast;

import com.xz.simpletranslation.broadcast.NetworkChange;

public class FamilyInfo {

    private final Context context;

    public FamilyInfo(Context context){
        this.context = context;
    }
    public void getInfo(){
        //检查网络
        if(!NetworkChange.islive()){
            Toast.makeText(context,"当前网络异常",Toast.LENGTH_SHORT).show();
            return;
        }
    }
}
