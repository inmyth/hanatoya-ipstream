package jp.hanatoya.ipcam.edit;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.widget.EditText;
import android.widget.Spinner;

import jp.hanatoya.ipcam.BasePresenter;
import jp.hanatoya.ipcam.BaseView;
import jp.hanatoya.ipcam.models.CamExt;
import jp.hanatoya.ipcam.repo.Switch;


class EditContract {

    public interface Presenter extends BasePresenter {

        void saveSwitch(Switch s);

        void deleteSwitch(Switch s);

    }


    public interface View extends BaseView<Presenter> {

        void setupViewPager();

        void handleNoCam();

        void showSwitchDialog(Switch s);

        void showDeleteSwitchDialog(Switch s);

        void dismissAddSwitchDialog();
    }

}
