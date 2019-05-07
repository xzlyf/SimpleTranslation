package com.xz.simpletranslation.CustomLayout;

import android.content.Context;
import android.media.Image;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.xz.simpletranslation.R;

import java.util.ArrayList;
import java.util.List;

/**
 * choose_language控件响应事件
 */
public class ChooseLanguage extends LinearLayout {
    private String TAG = "ChooseLanguage";
    //控件的id
    private final int LANGUAGE_SELECT = 9528;
    private Spinner from;
    private Spinner to;
    private ImageButton exc;

    //语言列表数据
    private List<String> shortlanguage;
    private String choose[] = new String[2];

    /**
     * 外界接口
     * 获取from 语言和to 语言
     * [0] - from
     * [1] - to
     */
    public String[] LanguageExt() {
        return choose;
    }

    /**
     * 外界接口
     * 判断是否选择了一样的语言
     *
     * @return
     */
    public boolean isSame() {
        if (choose[0].equals(choose[1])) {
            return true;
        }
        return false;
    }

    public ChooseLanguage(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //引用布局-动态加载-设置该布局的id
        LayoutInflater.from(context).inflate(R.layout.layout_choose_language, this).setId(LANGUAGE_SELECT);

        from = findViewById(R.id.from_language);
        to = findViewById(R.id.to_language);
        exc = findViewById(R.id.exchange);

        initdata();
        initListeners();
    }

    /**
     * 初始化数据
     */
    private void initdata() {
        shortlanguage = new ArrayList<>();
        shortlanguage.add("auto");
        shortlanguage.add("zh");
        shortlanguage.add("en");
        shortlanguage.add("yue");
        shortlanguage.add("wyw");
        shortlanguage.add("jp");
        shortlanguage.add("kor");
        shortlanguage.add("fra");
        shortlanguage.add("spa");
        shortlanguage.add("th");
        shortlanguage.add("ara");
        shortlanguage.add("ru");
        shortlanguage.add("pt");
        shortlanguage.add("de");
        shortlanguage.add("it");
        shortlanguage.add("el");
        shortlanguage.add("nl");
        shortlanguage.add("pl");
        shortlanguage.add("bul");
        shortlanguage.add("est");
        shortlanguage.add("dan");
        shortlanguage.add("fin");
        shortlanguage.add("cs");
        shortlanguage.add("rom");
        shortlanguage.add("slo");
        shortlanguage.add("swe");
        shortlanguage.add("hu");
        shortlanguage.add("cht");
        shortlanguage.add("vie");
    }

    /**
     * 监听事件
     */
    private void initListeners() {
        //互换语言
        exc.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int f = from.getSelectedItemPosition();
                int t = to.getSelectedItemPosition();
                from.setSelection(t + 1);
                to.setSelection(f - 1);
            }
        });

        from.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                choose[0] = shortlanguage.get(i);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        to.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //因为集合多了个自动检测  所以索引要加+1
                choose[1] = shortlanguage.get(i + 1);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


}
