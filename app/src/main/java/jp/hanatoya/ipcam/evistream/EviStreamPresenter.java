package jp.hanatoya.ipcam.evistream;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import jp.hanatoya.ipcam.BasePresenter;
import jp.hanatoya.ipcam.models.CamExt;
import jp.hanatoya.ipcam.repo.CamDao;
import jp.hanatoya.ipcam.repo.Switch;
import jp.hanatoya.ipcam.repo.SwitchDao;
import jp.hanatoya.ipcam.utils.EvidenceImageRequest;
import jp.hanatoya.ipcam.utils.VolleySingleton;


public class EviStreamPresenter implements EviStreamContract.EviStreamPresenter {
    private static final long INTERVAL_MS = 500l;

    private CamDao camDao;
    private SwitchDao switchDao;
    private EviStreamContract.View view;

    private CamExt camExt;
    private Runnable stream;
    private Handler handler = new Handler();


    public EviStreamPresenter( EviStreamContract.View view, CamDao camDao, SwitchDao switchDao) {
        this.view = view;
        view.setPresenter(this);
        this.camDao = camDao;
        this.switchDao = switchDao;
    }

    @Override
    public void start() {
        Bundle bundle = view.getBundle();
        long id = bundle.getLong(BasePresenter.KEY_ID);
        this.camExt = new CamExt(camDao.load(id));
    }

    @Override
    public void loadIpCam(final ImageView streamView, final Context ctx) {
        if (stream != null){
            stopIpCam();
        }
        stream = new Runnable() {

            @Override
            public void run() {
                try{

                    EvidenceImageRequest evidenceImageRequest = new EvidenceImageRequest(camExt.getImgUrl(), new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap response) {
                            streamView.setImageBitmap(response);
                        }
                    }, 0, 0, ImageView.ScaleType.CENTER, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            stopIpCam();
                            view.showError();
                        }
                    }, camExt.getCam().getUsername(), camExt.getCam().getPassword());
                    VolleySingleton.getInstance(ctx).addToRequestQueue(evidenceImageRequest);
                }finally {
                    handler.postDelayed(this, INTERVAL_MS);
                }

            }
        };

        stream.run();



    }
    @Override
    public void stopIpCam(){
        if (stream != null)
        this.handler.removeCallbacks(stream);
    }

    @Override
    public void loadIpCam() {
        // unused
    }

    @Override
    public void close() {

    }

    @Override
    public void up(Context context) {

    }

    @Override
    public void left(Context context) {

    }

    @Override
    public void right(Context context) {

    }

    @Override
    public void down(Context context) {

    }

    @Override
    public void center(Context context) {

    }

    @Override
    public void cgi(Context context, Switch s) {

    }

    @Override
    public void openCgiDialogOrToast() {

    }


}
