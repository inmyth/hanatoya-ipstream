package jp.hanatoya.ipcam.stream;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;

import com.github.niqdev.mjpeg.MjpegInputStream;
import com.github.niqdev.mjpeg.MjpegView;

import java.util.ArrayList;

import jp.hanatoya.ipcam.BasePresenter;
import jp.hanatoya.ipcam.BaseView;
import jp.hanatoya.ipcam.repo.Switch;

interface StreamContract {

    interface Presenter extends BasePresenter {

        void loadIpCam();

        void close();

        void up(Context context);

        void left(Context context);

        void right(Context context);

        void down(Context context);

        void center(Context context);

        void cgi(Context context, Switch s);

        void openCgiDialogOrToast();

    }

    interface View extends BaseView<Presenter>{

        Bundle getBundle();

        void draw(MjpegInputStream mjpegInputStream);

        void showError();

        void up();

        void left();

        void right();

        void down();

        void center();

        void openCgiDialog();

        void toastNoCgi();

    }
}
