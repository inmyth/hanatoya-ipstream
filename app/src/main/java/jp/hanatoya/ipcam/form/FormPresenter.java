package jp.hanatoya.ipcam.form;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;

import java.util.HashMap;
import java.util.Map;

import jp.hanatoya.ipcam.BasePresenter;
import jp.hanatoya.ipcam.MyApp;
import jp.hanatoya.ipcam.main.Events;
import jp.hanatoya.ipcam.models.CamExt;
import jp.hanatoya.ipcam.repo.Cam;
import jp.hanatoya.ipcam.repo.CamDao;
import jp.hanatoya.ipcam.utils.EvidenceImageRequest;
import jp.hanatoya.ipcam.utils.MyNetworkUtils;
import jp.hanatoya.ipcam.utils.VolleySingleton;


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

        VolleySingleton.getInstance(ctx).getRequestQueue().cancelAll(TAG_PING);

        if (!camExt.isEvidenceSystem()){
            ImageRequest imageRequest = new ImageRequest(camExt.getImgUrl(),

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

            imageRequest.setTag(TAG_PING);
            VolleySingleton.getInstance(ctx).addToRequestQueue(imageRequest);
        }else{
            EvidenceImageRequest evidenceImageRequest = new EvidenceImageRequest(camExt.getImgUrl(), new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    isTesting = false;
                    istestOk = true;
                    MyApp.getInstance().getBus().send(new Events.CameraPing(true));

                }
            }, 0, 0, ImageView.ScaleType.CENTER, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    isTesting = false;
                    if (!istestOk) {
                        MyApp.getInstance().getBus().send(new Events.CameraPing(false));
                    }
                }
            }, camExt.getCam().getUsername(), camExt.getCam().getPassword());

            evidenceImageRequest.setTag(TAG_PING);
            VolleySingleton.getInstance(ctx).addToRequestQueue(evidenceImageRequest);
        }



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
