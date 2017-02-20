package jp.hanatoya.ipcam.evistream;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.github.niqdev.mjpeg.MjpegInputStream;

import jp.hanatoya.ipcam.BasePresenter;
import jp.hanatoya.ipcam.BaseView;
import jp.hanatoya.ipcam.repo.Switch;
import jp.hanatoya.ipcam.stream.StreamContract;


public class EviStreamContract {

    interface EviStreamPresenter extends StreamContract.Presenter {
        void loadIpCam(ImageView streamView, Context ctx);

        void stopIpCam();
    }

    interface View extends BaseView<EviStreamContract.EviStreamPresenter> {

        Bundle getBundle();

        void draw();

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
