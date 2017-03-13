package jp.hanatoya.ipcam.stream;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import jp.hanatoya.ipcam.MyApp;
import jp.hanatoya.ipcam.R;
import jp.hanatoya.ipcam.evistream.EviStreamFragment;
import jp.hanatoya.ipcam.main.Events;
import jp.hanatoya.ipcam.repo.Switch;
import jp.hanatoya.ipcam.utils.MyCamUtils;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;


public class StreamActivity extends AppCompatActivity {
    private static final String LOAD_CAM_TYPE = "LOAD_CAM_TYPE";
    private static final String LOAD_CAM_ID = "LOAD_CAM_ID";
    private Subscription busSubscription;


    public static void launch(Context ctx, String type, long id) {
        Intent intent = new Intent(ctx, StreamActivity.class);
        intent.putExtra(LOAD_CAM_TYPE, type);
        intent.putExtra(LOAD_CAM_ID, id);
        ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        long id = getIntent().getLongExtra(LOAD_CAM_ID, -1);
        String type = getIntent().getStringExtra(LOAD_CAM_TYPE);



        if (savedInstanceState == null) {
            if (!MyCamUtils.isEvidenceCam(type)) {
                StreamFragment streamFragment = StreamFragment.newInstance(id);
                replaceFragment(streamFragment);
            } else {
                EviStreamFragment eviStreamFragment = EviStreamFragment.newInstance(id);
                replaceFragment(eviStreamFragment);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.busSubscription = MyApp.getInstance().getBus().toObserverable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Object>() {

                    @Override
                    public void call(Object o) {
                        if (o instanceof Events.OpenCgiDialog) {
                            Events.OpenCgiDialog event = (Events.OpenCgiDialog) o;
                            final CgiDialogFragment cgiDialogFragment = CgiDialogFragment.newInstance(event.camExt, new CgiDialogFragment.Listener() {
                                @Override
                                public void cgiClick(Switch s) {
                                    MyApp.getInstance().getBus().send(new Events.CgiClicked(s));
                                }
                            });
                            cgiDialogFragment.show(getSupportFragmentManager(), "fragment_stream_cgi");
                        }
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.busSubscription.unsubscribe();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.container, fragment, "stream");
        ft.commit();
    }

}
