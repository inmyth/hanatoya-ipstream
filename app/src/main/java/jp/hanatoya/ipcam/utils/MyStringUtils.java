package jp.hanatoya.ipcam.utils;

import android.text.TextUtils;
import android.widget.EditText;

/**
 * Created by desktop on 2016/12/25.
 */

public class MyStringUtils {

    public static String getString(EditText editText) {
        if (TextUtils.isEmpty(editText.getText())) {
            return null;
        }
        return editText.getText().toString();
    }}

