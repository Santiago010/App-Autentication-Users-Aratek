package cn.com.aratek.demo.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs   {
    public String SHARED_URLAPI = "MYAPI";
    public String URLAPI = "URLAPI";
    public SharedPreferences storage;

    public  Prefs(Context context){
        storage = context.getSharedPreferences(SHARED_URLAPI,0);
    }

    public void saveUrl(String url){
        storage.edit().putString(URLAPI,url).apply();
    }
    public String getUrl(){
        return storage.getString(URLAPI,"https://api.escuelajs.co/api/v1/");
    }
}
