package jp.hanatoya.ipcam.utils;

import android.util.Base64;

import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;

import java.util.HashMap;


public class MyNetworkUtils {


    public static GlideUrl buildGlideUrlWithAuth(String url, String username, String password){

        String creds = String.format("%s:%s", username, password);
        String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
        GlideUrl glideUrl = new GlideUrl(url, new LazyHeaders.Builder()
                .addHeader("Authorization", auth).build());
        return  glideUrl;
    }



    public static void addAuthToHeaderParam(HashMap<String, String> params,  String username, String password){
        String creds = String.format("%s:%s",username, password);
        String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
        params.put("Authorization", auth);
    }
}
