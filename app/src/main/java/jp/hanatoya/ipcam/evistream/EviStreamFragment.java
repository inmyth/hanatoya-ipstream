package jp.hanatoya.ipcam.evistream;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.niqdev.mjpeg.MjpegView;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.hanatoya.ipcam.BasePresenter;
import jp.hanatoya.ipcam.MyApp;
import jp.hanatoya.ipcam.R;
import jp.hanatoya.ipcam.main.Events;
import jp.hanatoya.ipcam.stream.StreamContract;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class EviStreamFragment extends Fragment implements EviStreamContract.View {
    @BindView(R.id.coordinator) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.evistream) ImageView streamView;

    private EviStreamPresenter presenter;
    private Subscription busSubscription;

    public static EviStreamFragment newInstance(long id) {
        Bundle args = new Bundle();
        args.putLong(BasePresenter.KEY_ID, id);
        EviStreamFragment fragment = new EviStreamFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_evistream, container, false);
        ButterKnife.bind(this, view);
        presenter.start();
        presenter.loadIpCam(streamView, getActivity());
        this.busSubscription = MyApp.getInstance().getBus().toObserverable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<Object>() {
                            @Override
                            public void call(Object o) {
                                if (o instanceof Events.CgiClicked){
                                    Events.CgiClicked event = (Events.CgiClicked)o;
                                    presenter.cgi(getActivity(), event.s);
                                }
                            }
                        }
                );
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (busSubscription != null && !busSubscription.isUnsubscribed()) {
            busSubscription.unsubscribe();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.stopIpCam();
    }


    @Override
    public void onResume() {
        super.onResume();
        presenter.loadIpCam();
    }

    @Override
    public Bundle getBundle() {
        return getArguments();
    }

    @Override
    public void draw() {

    }

    @Override
    public void showError() {
        Snackbar.make(coordinatorLayout, R.string.error_stream, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.close, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        presenter.close();
                    }
                })
                .show();
    }

    @Override
    public void up() {

    }

    @Override
    public void left() {

    }

    @Override
    public void right() {

    }

    @Override
    public void down() {

    }

    @Override
    public void center() {

    }

    @Override
    public void openCgiDialog() {

    }

    @Override
    public void toastNoCgi() {

    }


    @Override
    public void setPresenter(EviStreamContract.EviStreamPresenter presenter) {
        this.presenter = (EviStreamPresenter)presenter;
    }
}
