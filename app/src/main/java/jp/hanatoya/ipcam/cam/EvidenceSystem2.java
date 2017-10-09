package jp.hanatoya.ipcam.cam;

import android.content.Context;
import android.net.Uri;

import com.squareup.picasso.UrlConnectionDownloader;

import java.io.IOException;
import java.net.HttpURLConnection;


public class EvidenceSystem2 implements CamAPI {

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
        return "/getLiveImage?time=" + String.valueOf(System.currentTimeMillis());
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
