package jp.hanatoya.ipcam.stream;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.niqdev.mjpeg.DisplayMode;
import com.github.niqdev.mjpeg.MjpegInputStream;
import com.github.niqdev.mjpeg.MjpegView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.hanatoya.ipcam.BasePresenter;
import jp.hanatoya.ipcam.MyApp;
import jp.hanatoya.ipcam.R;
import jp.hanatoya.ipcam.main.Events;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;


public class StreamFragment extends Fragment implements StreamContract.View{


    @BindView(R.id.coordinator) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.stream) MjpegView streamView;
    private Subscription busSubscription;

    private StreamPresenter presenter;

    public static StreamFragment newInstance(long id) {
        Bundle args = new Bundle();
        args.putLong(BasePresenter.KEY_ID, id);
        StreamFragment fragment = new StreamFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stream, container, false);
        ButterKnife.bind(this, view);
        presenter.start();
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
    public void onPause() {
        streamView.stopPlayback();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.loadIpCam();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (busSubscription != null && !busSubscription.isUnsubscribed()) {
            busSubscription.unsubscribe();
        }
    }

    @Override
    public Bundle getBundle() {
        return getArguments();
    }

    @Override
    public void draw(MjpegInputStream mjpegInputStream) {
        streamView.setSource(mjpegInputStream);
        streamView.setDisplayMode(DisplayMode.BEST_FIT);
        streamView.showFps(true);
    }

    @Override
    public void showError(){
        streamView.stopPlayback();
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
    public void setPresenter(StreamContract.Presenter presenter) {
        this.presenter = (StreamPresenter)presenter;
    }

    @OnClick(R.id.up)
    @Override
    public void up() {
        presenter.up(getActivity());
    }

    @OnClick(R.id.left)
    @Override
    public void left() {presenter.left(getActivity());}

    @Override
    @OnClick(R.id.right)
    public void right() {
    presenter.right(getActivity());
    }

    @Override
    @OnClick(R.id.down)
    public void down() {
    presenter.down(getActivity());
    }

    @Override
    @OnClick(R.id.center)
    public void center() {
    presenter.center(getActivity());
    }

    @Override
    @OnClick(R.id.cgi)
    public void openCgiDialog() {
        presenter.openCgiDialogOrToast();
    }

    @Override
    public void toastNoCgi() {
        Toast.makeText(getActivity(), R.string.error_nocgi, Toast.LENGTH_SHORT).show();
    }
}
