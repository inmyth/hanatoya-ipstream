package jp.hanatoya.ipcam.form;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.niqdev.mjpeg.Mjpeg;
import com.github.niqdev.mjpeg.MjpegInputStream;

import java.util.HashMap;
import java.util.Map;

import jp.hanatoya.ipcam.BasePresenter;
import jp.hanatoya.ipcam.MyApp;
import jp.hanatoya.ipcam.main.Events;
import jp.hanatoya.ipcam.models.CamExt;
import jp.hanatoya.ipcam.repo.Cam;
import jp.hanatoya.ipcam.repo.CamDao;
import jp.hanatoya.ipcam.utils.MyNetworkUtils;
import jp.hanatoya.ipcam.utils.VolleySingleton;
import rx.functions.Action1;


public class FormPresenter implements FormContract.Presenter {
    private static final int TIMEOUT = 5;
    private static final String TAG_PING = "TAG_PING";
    private CamExt camExt;
    private boolean isTesting;
    private boolean istestOk;


    @NonNull
    private CamDao camDao;

    @NonNull
    private FormContract.View view;


    public FormPresenter(@NonNull FormContract.View view, @NonNull CamDao camDao) {
        this.view = view;
        this.view.setPresenter(this);
        this.camDao = camDao;
    }

    @Override
    public void start() {
        Bundle bundle = view.getBundle();
        view.showProgressBar(false);
        view.showBottomTab(true);
        if (bundle != null) {
            long id = bundle.getLong(BasePresenter.KEY_ID);
            this.camExt = new CamExt(camDao.queryBuilder()
                    .where(CamDao.Properties.Id.eq(id))
                    .list()
                    .get(0));
            view.populate(this.camExt);
            return;

        }
        this.camExt = new CamExt(new Cam());
    }

    @Override
    public void testCam(Context ctx) {
        isTesting = true;
        istestOk = false;
        camExt.initAPI();


//        Mjpeg.newInstance()
//                .credential(camExt.getCam().getUsername(), camExt.getCam().getPassword())
//                .open(camExt.getStreamUrl(), TIMEOUT)
//                .subscribe(new Action1<MjpegInputStream>() {
//                               @Override
//                               public void call(MjpegInputStream mjpegInputStream) {
//                                   isTesting = false;
//                                   istestOk = true;
//                                    MyApp.getInstance().getBus().send(new Events.CameraPing(true));
//                               }
//                           },
//                        new Action1<Throwable>() {
//                            @Override
//                            public void call(Throwable throwable) {
//                                isTesting = false;
//                                if (!istestOk) {
//                                    MyApp.getInstance().getBus().send(new Events.CameraPing(false));
//                                }
//                            }
//                        }
//                );

        VolleySingleton.getInstance(ctx).getRequestQueue().cancelAll(TAG_PING);

        ImageRequest stringRequest = new ImageRequest(camExt.getImgUrl(),

                new Response.Listener<Bitmap>() {

                    @Override
                    public void onResponse(Bitmap response) {
                       isTesting = false;
                       istestOk = true;
                        MyApp.getInstance().getBus().send(new Events.CameraPing(true));
                    }
                }, 0, 0, null, Bitmap.Config.RGB_565,

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                            isTesting = false;
                            if (!istestOk) {
                                MyApp.getInstance().getBus().send(new Events.CameraPing(false));
                            }
                    }
                }


        ){
            @Override
            protected Response<Bitmap> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                MyNetworkUtils.addAuthToHeaderParam(params, camExt.getCam().getUsername(), camExt.getCam().getPassword());
                return params;
            }
        };

        stringRequest.setTag(TAG_PING);
        VolleySingleton.getInstance(ctx).addToRequestQueue(stringRequest);

    }

    @Override
    public void save() {
        if(camExt.getCam().getId() == null){
            camDao.insert(camExt.getCam());
        }else{
            camDao.insertOrReplace(camExt.getCam());
        }
    }

    @Override
    public void delCam() {
        camDao.deleteByKey(camExt.getCam().getId());
        close();
    }



    @Override
    public boolean isTesting() {
        return isTesting;
    }

    public CamExt getCamExt() {
        return camExt;
    }

    @Override
    public void close() {
        MyApp.getInstance().getBus().send(new Events.RequestBack());
    }
}
