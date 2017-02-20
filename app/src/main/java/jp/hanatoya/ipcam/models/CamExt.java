package jp.hanatoya.ipcam.models;


import org.parceler.Parcel;
import org.parceler.Transient;

import jp.hanatoya.ipcam.cam.CamAPI;
import jp.hanatoya.ipcam.cam.EvidenceSystem;
import jp.hanatoya.ipcam.cam.PLANEXCSWMV043GNV;
import jp.hanatoya.ipcam.cam.PanasonicVLCM210;
import jp.hanatoya.ipcam.repo.Cam;
import jp.hanatoya.ipcam.repo.Switch;

@Parcel
public class CamExt {

     @Transient CamAPI camAPI;
     Cam cam;

    public CamExt(){

    }

    public CamExt(Cam cam) {
        this.cam = cam;
        if (this.cam != null && this.cam.getType() != null) {
            initAPI();
        }
    }

    public Cam getCam() {
        return cam;
    }

    public void setCam(Cam cam) {
        this.cam = cam;
    }

    public boolean isEvidenceSystem(){
        return  (camAPI != null && camAPI instanceof EvidenceSystem);
    }

    public void initAPI() { // Match these with string resource
        switch (this.cam.getType()) {
            case "Panasonic VL-CM210":
                camAPI = new PanasonicVLCM210();
                break;
            case "Planex CS-WMV043G-NV":
                camAPI = new PLANEXCSWMV043GNV();
                break;
            default:
                camAPI = new EvidenceSystem();
                break;
        }
    }

    public String getStreamUrl() {
        StringBuffer sb = buildUrl();
        sb.append(camAPI.getPath());
        return sb.toString();
    }

    public String getImgUrl(){
        StringBuffer sb = buildUrl();
        sb.append(camAPI.img());
        return sb.toString();
    }

    public String getUpUrl() {
        StringBuffer sb = buildUrl();
        sb.append(camAPI.up());
        return sb.toString();
    }

    public String getDownUrl() {
        StringBuffer sb = buildUrl();
        sb.append(camAPI.down());
        return sb.toString();
    }

    public String getLeftUrl() {
        StringBuffer sb = buildUrl();
        sb.append(camAPI.left());
        return sb.toString();
    }

    public String getRightUrl() {
        StringBuffer sb = buildUrl();
        sb.append(camAPI.right());
        return sb.toString();
    }

    public String getCenterUrl() {
        StringBuffer sb = buildUrl();
        sb.append(camAPI.center());
        return sb.toString();
    }

    private StringBuffer buildUrl() {
        StringBuffer sb = new StringBuffer(this.cam.getProtocol());
        sb.append(this.cam.getHost());
        sb.append(":");
        sb.append(this.cam.getPort());
        return sb;
    }



    public String buildCgiUrl(Switch s){
        StringBuffer sb = new StringBuffer(this.cam.getProtocol());
        sb.append(this.cam.getHost());
        sb.append(":");
        sb.append(s.getPort());
        if (!s.getCgi().startsWith("/")){
            sb.append("/");
        }
        sb.append(s.getCgi());
        return sb.toString();

    }

}
