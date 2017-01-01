package jp.hanatoya.ipcam.main;

import java.util.List;

import jp.hanatoya.ipcam.BasePresenter;
import jp.hanatoya.ipcam.BaseView;
import jp.hanatoya.ipcam.models.CamExt;


public interface MainContract {


    public interface Presenter extends BasePresenter{

        void toEdit(long id);

        void toStream(long id);

        void addNewCam();

        void exportUserSettings();

        void importUserSettings();
    }


    public interface View extends BaseView<Presenter>{

        void populate(List<CamExt> newCamExtList);

        void addClick();

        void toastDbDumped();

        void toastPostImport(boolean isSuccess);

    }
}
