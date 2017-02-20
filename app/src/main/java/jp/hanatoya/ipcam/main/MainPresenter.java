package jp.hanatoya.ipcam.main;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;



import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import jp.hanatoya.ipcam.MyApp;
import jp.hanatoya.ipcam.models.CamExt;
import jp.hanatoya.ipcam.repo.Cam;
import jp.hanatoya.ipcam.repo.CamDao;
import jp.hanatoya.ipcam.repo.SwitchDao;
import jp.hanatoya.ipcam.utils.MyFileUtils;


class MainPresenter implements MainContract.Presenter{



    @NonNull private MainContract.View view;
    @NonNull private CamDao camDao;
    @NonNull private SwitchDao switchDao;


     MainPresenter(@NonNull MainContract.View view, @NonNull CamDao camDao, @NonNull SwitchDao switchDao) {
        this.view = view;
        this.view.setPresenter(this);
        this.camDao = camDao;
         this.switchDao = switchDao;
    }


    @Override
    public void toEdit(long id) {
        MyApp.getInstance().getBus().send(new Events.SwitchToEdit(id));
    }

    @Override
    public void toStream(long id, String type) {
        MyApp.getInstance().getBus().send(new Events.SwitchToStream(id, type));
    }

    @Override
    public void addNewCam() {
        MyApp.getInstance().getBus().send(new Events.AddNewCam());
    }

    @Override
    public void exportUserSettings() {
        MyFileUtils.dumpDb(camDao, switchDao);
        view.toastDbDumped();
    }

    @Override
    public void importUserSettings() {
        if (!MyFileUtils.isExternalStorageWritable() || !MyFileUtils.isSettingsFileExist()){
            MyApp.getInstance().getBus().send(new Events.RequestFileImportInstructionDialog());
            return;
        }
        view.toastPostImport(MyFileUtils.importDb(camDao, switchDao));
    }

    @Override
    public void start() {
        List<Cam> cams = camDao.loadAll();
        List<CamExt> camExts = new ArrayList<CamExt>();
        for (Cam cam : cams){
            camExts.add(new CamExt(cam));
        }
        view.populate(camExts);
    }



}
