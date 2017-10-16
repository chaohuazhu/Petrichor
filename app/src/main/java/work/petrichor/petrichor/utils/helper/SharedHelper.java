package work.petrichor.petrichor.utils.helper;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author mitscherlich
 * @date 2017/10/14
 **/

public class SharedHelper {

    private static final String FILE_NAME = "info-plist";

    /**
     * 保存数据
     * @param context   上下文
     * @param key       键
     * @param obj       值
     * **/
    public static void put(Context context, String key, Object obj) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (obj instanceof Boolean)
            editor.putBoolean(key, (Boolean) obj);
        else if (obj instanceof Float)
            editor.putFloat(key, (Float) obj);
        else if (obj instanceof Integer)
            editor.putInt(key, (Integer) obj);
        else if (obj instanceof Long)
            editor.putLong(key, (Long) obj);
        else
            editor.putString(key, (String) obj);

        editor.apply();
    }

    /**
     * 获取指定数据
     * @param context       上下文
     * @param key           键
     * @param defaultObj    默认值
     * **/
    public static Object get(Context context, String key, Object defaultObj) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        if (defaultObj instanceof Boolean)
            return sp.getBoolean(key, (Boolean) defaultObj);
        else if (defaultObj instanceof Float)
            return sp.getFloat(key, (Float) defaultObj);
        else if (defaultObj instanceof Integer)
            return sp.getInt(key, (Integer) defaultObj);
        else if (defaultObj instanceof Long)
            return sp.getLong(key, (Long) defaultObj);
        else if (defaultObj instanceof String)
            return sp.getString(key, (String) defaultObj);

        return null;
    }

    /**
     * 删除指定数据
     * @param context   上下文
     * @param key       键
     * **/
    public static void remove(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.apply();
    }
}
