package jp.hanatoya.ipcam.form;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.niqdev.mjpeg.Mjpeg;
import com.github.niqdev.mjpeg.MjpegInputStream;

import jp.hanatoya.ipcam.BasePresenter;
import jp.hanatoya.ipcam.MyApp;
import jp.hanatoya.ipcam.main.Events;
import jp.hanatoya.ipcam.models.CamExt;
import jp.hanatoya.ipcam.repo.Cam;
import jp.hanatoya.ipcam.repo.CamDao;
import jp.hanatoya.ipcam.utils.MyNetworkUtils;
import rx.functions.Action1;


public class FormPresenter implements FormContract.Presenter {
    private static final int TIMEOUT = 5;
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

//
//        Glide.with(ctx).load(MyNetworkUtils.buildGlideUrlWithAuth(camExt.getImgUrl(), camExt.getCam().getUsername(), camExt.getCam().getPassword()))
//                .listener(new RequestListener<GlideUrl, GlideDrawable>() {
//                    @Override
//                    public boolean onException(Exception e, GlideUrl model, Target<GlideDrawable> target, boolean isFirstResource) {
//                        isTesting = false;
//                        if (!istestOk) {
//                            MyApp.getInstance().getBus().send(new Events.CameraPing(false));
//                        }
//                        return true;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(GlideDrawable resource, GlideUrl model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                        isTesting = false;
//                        istestOk = true;
//                        MyApp.getInstance().getBus().send(new Events.CameraPing(true));
//                        return true;
//                    }
//                });


        Mjpeg.newInstance()
                .credential(camExt.getCam().getUsername(), camExt.getCam().getPassword())
                .open(camExt.getStreamUrl(), TIMEOUT)
                .subscribe(new Action1<MjpegInputStream>() {
                               @Override
                               public void call(MjpegInputStream mjpegInputStream) {
                                   isTesting = false;
                                   istestOk = true;
                                    MyApp.getInstance().getBus().send(new Events.CameraPing(true));
                               }
                           },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                isTesting = false;
                                if (!istestOk) {
                                    MyApp.getInstance().getBus().send(new Events.CameraPing(false));
                                }
                            }
                        }
                );
//        queue.cancelAll(TAG_PING);
//        Log.e("Volley", "start");
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, camExt.getStreamUrl(),
//                new Response.Listener<String>() {
//
//                    @Override
//                    public void onResponse(String response) {
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e("ErrorResponse", "trig");
//            }
//        }){
//            @Override
//            protected Response<String> parseNetworkResponse(NetworkResponse response) {
//                Log.e("NetworkRes", String.valueOf(response.statusCode));
//                return super.parseNetworkResponse(response);
//            }
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> params = new HashMap<String, String>();
//                String creds = String.format("%s:%s",camExt.getUsername(),camExt.getPassword());
//                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
//                params.put("Authorization", auth);
//                return params;
//            }
//        };
//
//        stringRequest.setTag(TAG_PING);
//        queue.add(stringRequest);

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
