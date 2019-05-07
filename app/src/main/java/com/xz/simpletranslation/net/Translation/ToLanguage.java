package com.xz.simpletranslation.net.Translation;

import com.xz.simpletranslation.baiduApi.TransApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 获取翻译后文本
 */
public class ToLanguage {
    private static final String APP_ID = "20190501000293266";
    private static final String SECURITY_KEY = "DxRa16dLgIZz1uog9z1Q";
    private TransApi api;

    public ToLanguage() {
        api = new TransApi(APP_ID, SECURITY_KEY);
    }

    /**
     * 获取翻译后的文本
     */
    public void getString(final String query, final String[] exr, final DateEsponseCallback callback) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                String toText = api.getTransResult(query, exr[0], exr[1], callback);
                if (toText != null) {
                    try {
                        callback.finish(parseJSON(toText));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }

    /**
     * 解析json 只获取目标语言文本
     *
     * @param result
     */
    private static String parseJSON(String result) throws JSONException {
        JSONObject obj = null;
        try {
            obj = new JSONObject(result);
            JSONArray array = obj.getJSONArray("trans_result");
            return ((JSONObject) array.get(0)).getString("dst");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "错误代码:" +obj.get("error_code");

    }
}
