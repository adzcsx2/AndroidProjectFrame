package com.iim.ego.net;


import com.iim.ego.base.BaseApplication;

import java.io.File;
import java.util.Map;
import java.util.Set;

import okhttp3.Cache;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;


/**
 * Created by a on 2016/5/4.
 */
public class HttpUtil {

    public static final String baseurl = "http://wthrcdn.etouch.cn";

    /**
     * 得到retrofit的请求接口
     *
     * @return
     */
    public static IRequest getApiService() {
        return getRetrofit(baseurl).create(IRequest.class);
    }

    /**
     * retrofit初始化
     *
     * @param url
     * @return
     */
    private static Retrofit getRetrofit(String url) {
        long SIZE_OF_CACHE = 10 * 1024 * 1024; // 10 MiB
        String cacheFile = BaseApplication.getContextObject().getCacheDir() + "/http";
        Cache cache = new Cache(new File(cacheFile), SIZE_OF_CACHE);
        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(CachingControlInterceptor.REWRITE_RESPONSE_INTERCEPTOR)
                .addInterceptor(CachingControlInterceptor.REWRITE_RESPONSE_INTERCEPTOR_OFFLINE)
                .cache(cache)
                .build();
        return new Retrofit.Builder().baseUrl(url)
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    /**
     * 用于上传多张图片
     *
     * @param map
     * @return RequestBody
     */
    public static RequestBody getRequestFileBody(Map<String, File> map) {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        Set<String> keyset = map.keySet();
        for (String key : keyset) {
            File file = map.get(key);
            builder.addFormDataPart(key, file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
        }
        return builder.build();
    }

    /**
     * 将String转成MultipartBody.Part用于MultipartBody上传数据
     *
     * @param key
     * @param value
     * @return
     */
    public static MultipartBody.Part postStringParams(String key, String value) {
        return MultipartBody.Part.createFormData(key, value);
    }

    /**
     * 将file转成MultipartBody用于retrofit上传图片
     *
     * @param key   imagekey
     * @param file
     * @return
     */
    public static MultipartBody.Part postFileParams(String key, File file) {
        RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file);
        return MultipartBody.Part.createFormData(key, file.getName(), fileBody);
    }
    /**
     * 将file转成MultipartBody用于retrofit上传图片(带上传进度)
     *
     * @param key     imagekey
     * @param file
     * @return
     */
    public static MultipartBody.Part postFileParamsWithProgress(String key, File file,ProgressRequestBody.UploadCallbacks uploadCallbacks) {
        ProgressRequestBody fileBody = ProgressRequestBody.createImage(MediaType.parse("multipart/form-data"), file, uploadCallbacks);
//        RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file);
        return MultipartBody.Part.createFormData(key, file.getName(), fileBody);
    }

    /**
     * 将file转成MultipartBody用于retrofit上传文件
     *
     * @param mediaType
     * @param key
     * @param file
     * @return
     */
    public static MultipartBody.Part postFileParams(String mediaType, String key, File file) {
        RequestBody fileBody = RequestBody.create(MediaType.parse(mediaType), file);
        return MultipartBody.Part.createFormData(key, file.getName(), fileBody);
    }


}