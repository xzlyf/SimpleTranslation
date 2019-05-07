package com.xz.simpletranslation.CustomLayout;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xz.simpletranslation.R;

public class UserInputText extends LinearLayout implements View.OnClickListener {
    //该控件id
    private final int USERINPUTTEXT = 7543;

    private EditText inputText;
    private ImageButton deleteAll;
    private ImageButton pasteText;
    private ClipboardManager manager;
    private TextView textLength;


    private Context context;


    public UserInputText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_user_inputtext, this).setId(USERINPUTTEXT);
        this.context = context;
        inputText = findViewById(R.id.input_text);
        deleteAll = findViewById(R.id.delete_all);
        pasteText = findViewById(R.id.paste_text);
        textLength = findViewById(R.id.text_length);

        deleteAll.setOnClickListener(this);
        pasteText.setOnClickListener(this);
        manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        //监听文本框字符长度
        inputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //字数提醒颜色
                if (inputText.length()>450){
                    textLength.setTextColor(getResources().getColor(R.color.warning,null));
                }else if (inputText.length()<=450){
                    textLength.setTextColor(getResources().getColor(R.color.secondary_text,null));
                }

                textLength.setText(inputText.length() + "/500");

            }
        });

    }

    /**
     * 外界接口
     * 获取文本框内容
     *
     * @return
     */
    public String gettext() {
        return inputText.getText().toString();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.delete_all:
                //清空文本框
                inputText.setText("");
                break;
            case R.id.paste_text:
                //粘贴操作
                if (manager.getText()!=null)
                    inputText.append(manager.getText());
                break;

        }

    }
}
