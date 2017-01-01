package jp.hanatoya.ipcam.cam;


public class PanasonicVLCM210 implements CamAPI {

    @Override
    public String getPath() {
        return "/nphMotionJpeg?Resolution=640x480&Quality=High";
    }

    @Override
    public String up() {
        return "/TnaviControlCamera?Action=U";
    }

    @Override
    public String left() {
        return "/TnaviControlCamera?Action=L";
    }

    @Override
    public String right() {
        return "/TnaviControlCamera?Action=R";
    }

    @Override
    public String down() {
        return "/TnaviControlCamera?Action=D";
    }

    @Override
    public String center() {
        return "/TnaviControlCamera?Action=H";
    }

    @Override
    public String img() {
        return "/SnapshotJPEG?Resolution=640x480&Quality=Clarity";
    }
}
