package e.orz.cof.util;

import android.util.Log;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ORZ on 2018/7/1.
 */

public class NetUtil {

    //宿舍
    //public static final String BASE_URL = "http://157.0.84.241:8080/cof-server";
    //教室
    public static final String BASE_URL = "http://192.168.136.1:8080/cof-server";


    public static final MediaType FORM_CONTENT_TYPE
            = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");

    private static OkHttpClient mClient = new OkHttpClient();


    public static OkHttpClient getClient() {
        return mClient;
    }


}
