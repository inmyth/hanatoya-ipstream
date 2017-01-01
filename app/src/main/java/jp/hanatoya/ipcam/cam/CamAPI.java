package jp.hanatoya.ipcam.cam;

/**
 * Created by martin on 12/16/2016.
 */

public interface CamAPI {


    public  String getPath();

    public String up();

    public String left();

    public String right();

    public String down();

    public String center();

    public String img();


}
