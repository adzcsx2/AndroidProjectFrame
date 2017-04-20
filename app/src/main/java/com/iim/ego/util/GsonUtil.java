package com.iim.ego.util;


import com.google.gson.Gson;


/**
 * Gson的辅助类
 * Created by Hoyn on 16/12/14.
 */

public class GsonUtil {
    private static Gson gson = new Gson();

    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public static <T> T fromJson(String json, Class<T> cls) {
        try {
            T obj = gson.fromJson(json, cls);
            return obj;
        } catch (Exception e) {
            L.e(e.getMessage()+"\n"+json);
            return null;
        }
    }

}
