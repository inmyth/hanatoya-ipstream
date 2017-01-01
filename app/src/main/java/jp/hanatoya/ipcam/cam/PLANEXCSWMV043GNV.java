package jp.hanatoya.ipcam.cam;



public class PLANEXCSWMV043GNV implements CamAPI {

    @Override
    public String getPath() {
        return "/mjpg/video.mjpg";
    }

    @Override
    public String up() {
        return "/camera-cgi/com/ptz.cgi?move=up";
    }

    @Override
    public String left() {
        return "/camera-cgi/com/ptz.cgi?move=left";
    }

    @Override
    public String right() {
        return "/camera-cgi/com/ptz.cgi?move=right";
    }

    @Override
    public String down() {
        return "/camera-cgi/com/ptz.cgi?move=down";
    }

    @Override
    public String center() {
        return "/camera-cgi/com/ptz.cgi?move=home";
    }

    @Override
    public String img() {
        return "/snapshot.jpg";
    }
}
