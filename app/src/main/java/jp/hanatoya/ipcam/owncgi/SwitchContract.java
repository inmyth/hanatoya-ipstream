package jp.hanatoya.ipcam.owncgi;

import android.support.design.widget.TextInputLayout;

import java.util.List;

import jp.hanatoya.ipcam.BasePresenter;
import jp.hanatoya.ipcam.BaseView;
import jp.hanatoya.ipcam.repo.Switch;


public interface SwitchContract {

    public interface Presenter extends BasePresenter {

        void loadAll();


    }

    public interface View extends BaseView<Presenter> {

        void add();

        void openEditDialog(Switch s);

        void populate(List<Switch> switches);

        void openDeleteDialog(Switch s);



    }
}
