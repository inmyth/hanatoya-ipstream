package jp.hanatoya.ipcam.main;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v13.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.afollestad.materialdialogs.MaterialDialog;

import butterknife.ButterKnife;
import jp.hanatoya.ipcam.MyApp;
import jp.hanatoya.ipcam.R;
import jp.hanatoya.ipcam.edit.EditActivity;
import jp.hanatoya.ipcam.evistream.EviStreamFragment;
import jp.hanatoya.ipcam.evistream.EviStreamPresenter;
import jp.hanatoya.ipcam.form.FormFragment;
import jp.hanatoya.ipcam.form.FormPresenter;
import jp.hanatoya.ipcam.repo.Cam;
import jp.hanatoya.ipcam.repo.DaoSession;
import jp.hanatoya.ipcam.repo.Switch;
import jp.hanatoya.ipcam.stream.CgiDialogFragment;
import jp.hanatoya.ipcam.stream.StreamActivity;
import jp.hanatoya.ipcam.stream.StreamFragment;
import jp.hanatoya.ipcam.stream.StreamPresenter;
import jp.hanatoya.ipcam.utils.MyCamUtils;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {
    private Subscription busSubscription;
    private static final String TAG = "tag";

//    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        final DaoSession daoSession =  ((MyApp)getApplication()).getDaoSession();
//        setSupportActionBar(toolbar);
//        toolbar.setTitle(R.string.app_name);


        MainFragment mainFragment = MainFragment.newInstance();
        new MainPresenter(mainFragment, daoSession.getCamDao(), daoSession.getSwitchDao());

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.container, mainFragment);
        ft.commit();
        isStoragePermissionGranted();
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        final DaoSession daoSession =  ((MyApp)getApplication()).getDaoSession();
        this.busSubscription = MyApp.getInstance().getBus().toObserverable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<Object>() {

                            @Override
                            public void call(Object o) {
                                if (o instanceof Events.SwitchToEdit){
                                    Events.SwitchToEdit event = (Events.SwitchToEdit)o;
                                    EditActivity.navigate( MainActivity.this, event.id);
                                }else if (o instanceof Events.AddNewCam){
                                    FormFragment formFragment = FormFragment.newInstance();
                                    new FormPresenter(formFragment, daoSession.getCamDao());
                                    replaceFragment(formFragment);
                                } else if (o instanceof Events.RequestBack){
                                    onBackPressed();
                                }else if (o instanceof Events.SwitchToStream){
                                    Events.SwitchToStream event = (Events.SwitchToStream)o;
                                    StreamActivity.launch(MainActivity.this, event.type, event.id);

//                                    if (!MyCamUtils.isEvidenceCam(event.type)){
//                                        StreamFragment streamFragment = StreamFragment.newInstance(event.id);
//                                        new StreamPresenter(streamFragment, daoSession.getCamDao(), daoSession.getSwitchDao());
//                                        replaceFragment(streamFragment);
//                                    }else{
//                                        EviStreamFragment eviStreamFragment = EviStreamFragment.newInstance(event.id);
//                                        new EviStreamPresenter(eviStreamFragment, daoSession.getCamDao(), daoSession.getSwitchDao());
//                                        replaceFragment(eviStreamFragment);
//                                    }

                                }else if (o instanceof  Events.OpenCgiDialog){
                                    Events.OpenCgiDialog event = (Events.OpenCgiDialog) o;
                                    final CgiDialogFragment cgiDialogFragment = CgiDialogFragment.newInstance(event.camExt, new CgiDialogFragment.Listener() {
                                        @Override
                                        public void cgiClick(Switch s) {
                                            MyApp.getInstance().getBus().send(new Events.CgiClicked(s));
                                        }
                                    });
                                    cgiDialogFragment.show(getSupportFragmentManager(), "fragment_stream_cgi");
                                }else  if (o instanceof Events.RequestFileImportInstructionDialog){
                                    new MaterialDialog.Builder(MainActivity.this)
                                            .title(R.string.dialog_howtoupload)
                                            .content(getString(R.string.dialog_uploadinstruction))
                                            .positiveText(android.R.string.ok)
                                            .build()
                                            .show();
                                }
                            }
                        }
                );
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (busSubscription != null && !busSubscription.isUnsubscribed()) {
            busSubscription.unsubscribe();
        }
    }

    @Override
    public void onBackPressed() {
        
        int count = getFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            super.onBackPressed();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.container, fragment, TAG);
        ft.addToBackStack(null);
        ft.commit();
    }
}
