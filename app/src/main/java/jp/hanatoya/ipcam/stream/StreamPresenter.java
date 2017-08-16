package jp.hanatoya.ipcam.stream;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.niqdev.mjpeg.Mjpeg;
import com.github.niqdev.mjpeg.MjpegInputStream;

import org.greenrobot.greendao.query.Query;

import java.util.HashMap;
import java.util.Map;

import jp.hanatoya.ipcam.BasePresenter;
import jp.hanatoya.ipcam.MyApp;
import jp.hanatoya.ipcam.R;
import jp.hanatoya.ipcam.main.Events;
import jp.hanatoya.ipcam.models.CamExt;
import jp.hanatoya.ipcam.repo.CamDao;
import jp.hanatoya.ipcam.repo.Switch;
import jp.hanatoya.ipcam.repo.SwitchDao;
import jp.hanatoya.ipcam.utils.VolleySingleton;
import rx.functions.Action1;


public class StreamPresenter implements StreamContract.Presenter{
    public static final int CODE_OK = 0;
    public static final int CODE_AUTH_FAIL = 403;
    public static final int CODE_ERROR_4 = 4;
    public static final int CODE_ERROR_5 = 5;

    private static final int TIMEOUT = 5;

    private CamDao camDao;
    private SwitchDao switchDao;
    private StreamContract.View view;
    private CamExt camExt;
    private boolean isError;

    public StreamPresenter(@NonNull StreamContract.View view, @NonNull CamDao camDao, @NonNull SwitchDao switchDao){
        this.camDao = camDao;
//        view.setPresenter(this);
        this.view = view;
        this.switchDao = switchDao;


    }

    @Override
    public void start() {
        Bundle bundle = view.getBundle();
        long id = bundle.getLong(BasePresenter.KEY_ID);
        this.camExt = new CamExt(camDao.load(id));
        Query query = switchDao.queryBuilder().where(SwitchDao.Properties.CamId.eq(camExt.getCam().getId())).orderAsc(SwitchDao.Properties.Id).build();
        this.camExt.getCam().setSwitches(query.list());
        loadIpCam();
    }

    @Override
    public void close() {
        MyApp.getInstance().getBus().send(new Events.RequestBack());
    }

    @Override
    public void loadIpCam() {
        if (isError){
            return;
        }
        Mjpeg.newInstance()
                .credential(camExt.getCam().getUsername(), camExt.getCam().getPassword())
                .open(camExt.getStreamUrl(), TIMEOUT)
                .subscribe(new Action1<MjpegInputStream>() {
                               @Override
                               public void call(MjpegInputStream mjpegInputStream) {
                                    view.draw(mjpegInputStream);
                               }
                           },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Log.e(getClass().getSimpleName(), "mjpeg error", throwable);
                                if(!isError){ //tiwhout this flag, this callback will be called many times
                                    isError = true;
                                    view.showError();
                                }
                            }
                        }
                );

    }

    @Override
    public void up(Context context) {
        VolleySingleton.getInstance(context).addToRequestQueue(setupReq(camExt.getUpUrl(), camExt.getCam().getUsername(), camExt.getCam().getPassword()));
    }

    @Override
    public void left(Context context) {
        VolleySingleton.getInstance(context).addToRequestQueue(setupReq(camExt.getLeftUrl(), camExt.getCam().getUsername(), camExt.getCam().getPassword()));
    }

    @Override
    public void right(Context context) {
        VolleySingleton.getInstance(context).addToRequestQueue(setupReq(camExt.getRightUrl(), camExt.getCam().getUsername(), camExt.getCam().getPassword()));
    }

    @Override
    public void down(Context context) {
        VolleySingleton.getInstance(context).addToRequestQueue(setupReq(camExt.getDownUrl(), camExt.getCam().getUsername(), camExt.getCam().getPassword()));
    }

    @Override
    public void center(Context context) {
        VolleySingleton.getInstance(context).addToRequestQueue(setupReq(camExt.getCenterUrl(), camExt.getCam().getUsername(), camExt.getCam().getPassword()));
    }

    @Override
    public void cgi(Context context, Switch s) {
        String url = camExt.buildCgiUrl(s);
        VolleySingleton.getInstance(context).addToRequestQueue(setupReq(url, camExt.getCam().getUsername(), camExt.getCam().getPassword()));
        Toast.makeText(context, context.getString(R.string.dialog_cgiclicked, s.getName() + " - " + url), Toast.LENGTH_LONG).show();
    }

    @Override
    public void openCgiDialogOrToast() {
        if (camExt.getCam().getSwitches().isEmpty()){
            view.toastNoCgi();
        }else{
            MyApp.getInstance().getBus().send(new Events.OpenCgiDialog(camExt));
        }
    }



    public static StringRequest setupReq(final String url, final String username, final String password){
        return new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Volley CGI " + url, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error", url );
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String creds = String.format("%s:%s",username, password);
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                params.put("Authorization", auth);
                return params;
            }
        };
    }
}
