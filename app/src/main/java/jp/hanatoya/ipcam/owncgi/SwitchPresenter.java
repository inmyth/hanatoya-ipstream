package jp.hanatoya.ipcam.owncgi;


import android.support.annotation.NonNull;

import java.util.List;

import jp.hanatoya.ipcam.repo.CamDao;
import jp.hanatoya.ipcam.repo.Switch;
import jp.hanatoya.ipcam.repo.SwitchDao;

public class SwitchPresenter implements SwitchContract.Presenter {

    private long camId;

    @NonNull
    private SwitchDao switchDao;

    @NonNull
    private SwitchContract.View view;

    public SwitchPresenter(@NonNull SwitchContract.View view, @NonNull SwitchDao switchDao, long camId) {
        this.view = view;
        this.view.setPresenter(this);
        this.switchDao = switchDao;
        this.camId = camId;
    }

    @Override
    public void start() {
        loadAll();
    }

    @Override
    public void loadAll() {
        List<Switch> switchList = switchDao.loadAll();
        view.populate(switchList);
    }

    public long getCamId() {
        return camId;
    }
}
