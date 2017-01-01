package jp.hanatoya.ipcam.edit;

import android.support.annotation.NonNull;

import org.greenrobot.greendao.query.Query;

import jp.hanatoya.ipcam.BasePresenter;
import jp.hanatoya.ipcam.repo.Cam;
import jp.hanatoya.ipcam.repo.CamDao;
import jp.hanatoya.ipcam.repo.Switch;
import jp.hanatoya.ipcam.repo.SwitchDao;


class EditPresenter implements EditContract.Presenter {


    private EditContract.View view;
    private SwitchDao switchDao;
    private Cam cam;

    public EditPresenter(@NonNull EditContract.View view, @NonNull SwitchDao switchDao, @NonNull CamDao camDao, long camId) {
        this.view = view;
        this.view.setPresenter(this);
        this.switchDao = switchDao;
        this.cam = camDao.load(camId);

        if (cam == null){
            view.handleNoCam();
        }
    }

    @Override
    public void start() {

    }

    @Override
    public void saveSwitch(@NonNull Switch s) {
        s.setCamId(cam.getId());
        try {
            if (s.getId() == null) { // new switch

                switchDao.insert(s);
            } else {
                switchDao.insertOrReplace(s);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void deleteSwitch(Switch s) {
        switchDao.deleteByKey(s.getId());
    }
}
