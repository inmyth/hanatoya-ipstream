package jp.hanatoya.ipcam.cam;


import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.google.gson.Gson;
import com.squareup.picasso.UrlConnectionDownloader;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import jp.hanatoya.ipcam.models.CookieModel;

public class EvidenceSystem implements CamAPI {


    @Override
    public String getPath() {
        return null;
    }

    @Override
    public String up() {
        return null;
    }

    @Override
    public String left() {
        return null;
    }

    @Override
    public String right() {
        return null;
    }

    @Override
    public String down() {
        return null;
    }

    @Override
    public String center() {
        return null;
    }

    @Override
    public String img() {
        return "/getLiveImage?time=" + String.valueOf(System.currentTimeMillis()) +"&node=1";
    }



    public class EvidenceImageDownloader extends UrlConnectionDownloader {
        String user, password;


        public EvidenceImageDownloader(Context context, String user, String password
        ) {
            super(context);
            this.user  = user;
            this.password = password;
        }

        @Override
        protected HttpURLConnection openConnection(Uri path) throws IOException {
            HttpURLConnection conn = super.openConnection(path);
            conn.setRequestProperty("Cookie", "user=" + user);
            conn.setRequestProperty("Cookie", "password=" + password);
            return conn;
        }
    }


}
