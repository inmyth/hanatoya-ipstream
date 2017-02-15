package jp.hanatoya.ipcam.test;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.UrlConnectionDownloader;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.hanatoya.ipcam.MyApp;
import jp.hanatoya.ipcam.R;
import jp.hanatoya.ipcam.main.Events;
import jp.hanatoya.ipcam.models.CookieModel;
import jp.hanatoya.ipcam.utils.MyNetworkUtils;
import jp.hanatoya.ipcam.utils.VolleySingleton;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import rx.Subscription;

public class TestActivity extends AppCompatActivity {

    private Subscription busSubscription;
    OkHttpClient client = new OkHttpClient.Builder()
            .cookieJar(new CookieJar() {
                private final HashMap<HttpUrl, List<Cookie>> cookieStore = new HashMap<>();


                @Override
                public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                    cookieStore.put(url, cookies);
                }

                @Override
                public List<Cookie> loadForRequest(HttpUrl url) {
                    List<Cookie> cookies = cookieStore.get(url);
                    return cookies != null ? cookies : new ArrayList<Cookie>();
                }
            })
            .build();


    @BindView(R.id.img) ImageView im;
    @OnClick(R.id.btn)
    public void btnClick(){
        String url = "http://evi.evidence.style/initLive.php";
        final String url2 = "http://martin:test@evi.evidence.style/initLive.php?user=martin&node=1";
        final String url3 = "http://evi.evidence.style:8801/getLiveImage?time=1487171590&node=1";
        final String userId = "martin";
        final String password = "test";
        final String node = "1";


//        StringRequest stringRequest = new StringRequest(ImageRequest.Method.GET, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.e("VRESPONSE", response);
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//// if wrong password then getMessage returns null
//                Log.e("VERROR", error.getMessage());
//
//            }
//        }) {
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> params = new HashMap<>();
//                MyNetworkUtils.addAuthToHeaderParam(params, userId, password);
//                return params;            }
//        };


//
//        StringRequest stringRequest = new StringRequest(ImageRequest.Method.POST, url2, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.e("VRESPONSE", response);
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                if (error.getMessage() != null){
//                    Log.e("VERROR", error.getMessage());
//
//
//                }else{
//                    Log.e("VERROR", "null VOlley Error response");
//
//
//                }
//
//            }
//        }) {
//
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String,String> params = new HashMap<String, String>();
//                params.put("user", userId);
//                params.put("node", node);
//                return params;
//            }
//
//        };

        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);


//        Thread thread = new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                Response response = null;
//                Request request = new Request.Builder()
//                        .url(url2)
//                        .build();
//                try {
//                    response = client.newCall(request).execute();
//                    response.body().string();
//                }catch (IOException e){
//                    e.getLocalizedMessage();
//                }
//            }
//        });
//
//        thread.start();



//        StringRequest req = new StringRequest(Request.Method.GET, url2,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        Log.i("response",response);
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.i("error",error.getMessage());
//                    }
//                }){
//
//
//            @Override
//            protected Response<String> parseNetworkResponse(NetworkResponse response) {
//                // since we don't know which of the two underlying network vehicles
//                // will Volley use, we have to handle and store session cookies manually
//                Log.i("response",response.headers.toString());
//                Map<String, String> responseHeaders = response.headers;
//                String rawCookies = responseHeaders.get("Set-Cookie");
//                Log.i("cookies",rawCookies);
//                return super.parseNetworkResponse(response);
//            }
//
//        };


        CookieImageRequest cookieImageRequest = new CookieImageRequest(url3, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                im.setImageBitmap(response);

            }
        }, 1000, 1000, ImageView.ScaleType.CENTER, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VERROR", error.getMessage() == null ? "empty string" : error.getMessage());
            }
        });
//        Picasso picasso = new Picasso.Builder(this).downloader(new CookieImageDownloader(this)).build();
//        picasso.load(Uri.parse("http://evi.evidence.style:8801/getLiveImage?time=1487151458&node=1"))
//            .into(im);


//        ImageRequest imageRequest = new ImageRequest("http://evi.evidence.style:8801/getLiveImage?time=1487151458&node=1\"",
//
//                new Response.Listener<Bitmap>() {
//
//                    @Override
//                    public void onResponse(Bitmap response) {
//
//                    }
//                }, 0, 0, null, Bitmap.Config.RGB_565,
//
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//
//                    }
//                }
//
//
//        ){
//            @Override
//            protected Response<Bitmap> parseNetworkResponse(NetworkResponse response) {
//                return super.parseNetworkResponse(response);
//            }
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> params = new HashMap<>();
//                MyNetworkUtils.addAuthToHeaderParam(params, camExt.getCam().getUsername(), camExt.getCam().getPassword());
//                return params;
//            }
//        };
//
//        imageRequest.setTag(TAG_PING);
        VolleySingleton.getInstance(this).addToRequestQueue(cookieImageRequest);


    }

    public class CookieImageRequest extends com.android.volley.toolbox.ImageRequest {
        private static final String COOKIE_INFO = "info";


        public CookieImageRequest(String url, Response.Listener<Bitmap> listener, int maxWidth, int maxHeight, ImageView.ScaleType scaleType, Bitmap.Config decodeConfig, Response.ErrorListener errorListener) {
            super(url, listener, maxWidth, maxHeight, scaleType, decodeConfig, errorListener);
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> headers = super.getHeaders();

            if (headers == null || headers.equals(Collections.emptyMap())) {
                headers = new HashMap<String, String>();
            }

                Gson gson = new Gson();
                headers.put("Cookie", "info=" + gson.toJson(new CookieModel("martin", "test")));

//            MyApp.get().addSessionCookie(headers);

            return headers;
        }
    }

    public  class CookieImageDownloader extends UrlConnectionDownloader {

        public CookieImageDownloader(Context context) {
            super(context);
        }

        @Override
        protected HttpURLConnection openConnection(Uri path) throws IOException {
            HttpURLConnection conn = super.openConnection(path);
            conn.setRequestProperty("Cookie","user" + "=" + "martin" );
            conn.setRequestProperty("Cookie","password" + "=" + "test" );
            return conn;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);


    }
}
