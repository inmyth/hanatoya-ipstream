package jp.hanatoya.ipcam.utils;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.Map;

import static jp.hanatoya.ipcam.utils.EvidenceImageRequest.setEvidenceHeaders;


public class EvidenceStringRequest extends StringRequest {
    private String user,password;


    public EvidenceStringRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener, String user, String password) {
        super(method, url, listener, errorListener);
        this.user = user;
        this.password = password;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = super.getHeaders();
        return setEvidenceHeaders(headers, user, password);
    }

}
