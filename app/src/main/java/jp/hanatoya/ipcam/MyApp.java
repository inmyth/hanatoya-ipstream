package jp.hanatoya.ipcam;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import jp.hanatoya.ipcam.repo.DaoMaster;
import jp.hanatoya.ipcam.repo.DaoSession;
import jp.hanatoya.ipcam.utils.RxBus;

public class MyApp extends Application {

    private static final String SCHEMA_NAME = "jp.hanatoya.ipcam";
    private DaoSession daoSession;
    private static MyApp instance;

    private RxBus bus;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        bus = new RxBus();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, SCHEMA_NAME , null);
        SQLiteDatabase db = helper.getWritableDatabase();
        daoSession = new DaoMaster(db).newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public static MyApp getInstance() {
        return instance;
    }

    public RxBus getBus() {
        return bus;
    }

}
