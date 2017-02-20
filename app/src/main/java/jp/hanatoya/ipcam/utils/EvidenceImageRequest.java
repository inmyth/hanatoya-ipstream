package jp.hanatoya.ipcam.utils;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.google.gson.Gson;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jp.hanatoya.ipcam.models.CookieModel;



public class EvidenceImageRequest extends com.android.volley.toolbox.ImageRequest {
    private static final String COOKIE_INFO = "info";
    private String user,password;


    public EvidenceImageRequest(String url, Response.Listener<Bitmap> listener, int maxWidth, int maxHeight, ImageView.ScaleType scaleType, Bitmap.Config decodeConfig, Response.ErrorListener errorListener,
                                String user, String password
    ) {
        super(url, listener, maxWidth, maxHeight, scaleType, decodeConfig, errorListener);
        this.user = user;
        this.password = password;
    }


    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = super.getHeaders();

        if (headers == null || headers.equals(Collections.emptyMap())) {
            headers = new HashMap<String, String>();
        }
        Gson gson = new Gson();
        headers.put("Cookie", "info=" + gson.toJson(new CookieModel(user, password)));
        return headers;
    }
}