package co.uk.depotnet.onsa.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreferences {
    private static final String PREFERENCES_NAME = "co.uk.depotnet.onsa.onsa";
    private static SharedPreferences sharedPreferences;

    public static void initAppPreferences(Context context){

        if(sharedPreferences == null){
            sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME , Context.MODE_PRIVATE);
        }
    }

    public static void putString(String key , String value){
        sharedPreferences.edit().putString(key , value).apply();
    }

    public static String getString(String key , String defValue){
        return sharedPreferences.getString(key , defValue);
    }

    public static void putInt(String key , int value){
        sharedPreferences.edit().putInt(key , value).apply();
    }

    public static int getInt(String key , int defValue){
        return sharedPreferences.getInt(key , defValue);
    }

    public static void clear(){
        sharedPreferences.edit().clear().apply();
    }
}
