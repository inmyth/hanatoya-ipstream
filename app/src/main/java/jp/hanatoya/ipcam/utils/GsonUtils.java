package jp.hanatoya.ipcam.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;

/**
 * Created by desktop on 2016/12/27.
 */

public class GsonUtils
{
    public static <T>T toBean(String response,  Class<T> classOfT){
        Gson gson = new Gson();
        try {
            return gson.fromJson(response, classOfT);
        }catch (JsonSyntaxException e) {
            return null;
        }
    }


    public static <T>T toBean(String response, Type type){
        Gson gson = new Gson();
        try {
            return gson.fromJson(response, type);
        }catch (JsonSyntaxException e) {
            return null;
        }
    }

    public static String toJson(Object object){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(object);
    }

}