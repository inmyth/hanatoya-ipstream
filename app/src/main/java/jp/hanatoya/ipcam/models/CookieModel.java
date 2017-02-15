package jp.hanatoya.ipcam.models;

/**
 * Created by desktop on 2017/02/15.
 */

public class CookieModel {

    private String user, pass;

    public CookieModel() {
    }

    public CookieModel(String user, String pass) {
        this.user = user;
        this.pass = pass;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
