package jp.hanatoya.ipcam.stream;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import org.parceler.Parcels;

import butterknife.ButterKnife;
import jp.hanatoya.ipcam.MyApp;
import jp.hanatoya.ipcam.R;
import jp.hanatoya.ipcam.evistream.EviStreamFragment;
import jp.hanatoya.ipcam.evistream.EviStreamPresenter;
import jp.hanatoya.ipcam.repo.Cam;
import jp.hanatoya.ipcam.repo.DaoSession;
import jp.hanatoya.ipcam.utils.MyCamUtils;
import rx.Subscription;


public class StreamActivity extends AppCompatActivity {
    private static final String LOAD_CAM_TYPE = "LOAD_CAM_TYPE";
    private static final String LOAD_CAM_ID = "LOAD_CAM_ID";


    private Subscription busSubscription;


    public static void launch(Context ctx, String type, long id){
        Intent intent = new Intent(ctx, StreamActivity.class);
        intent.putExtra(LOAD_CAM_TYPE, type);
        intent.putExtra(LOAD_CAM_ID, id);

        ctx.startActivity(intent);
    }


    private Fragment fragment;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        final DaoSession daoSession =  ((MyApp)getApplication()).getDaoSession();

        long id = getIntent().getLongExtra(LOAD_CAM_ID, -1);
        String type = getIntent().getStringExtra(LOAD_CAM_TYPE);



        if (!MyCamUtils.isEvidenceCam(type)){
            StreamFragment streamFragment = StreamFragment.newInstance(id);
            new StreamPresenter(streamFragment, daoSession.getCamDao(), daoSession.getSwitchDao());
            this.fragment = streamFragment;
            replaceFragment(streamFragment);
        }else{
            EviStreamFragment eviStreamFragment = EviStreamFragment.newInstance(id);
            new EviStreamPresenter(eviStreamFragment, daoSession.getCamDao(), daoSession.getSwitchDao());
            this.fragment = eviStreamFragment;
            replaceFragment(eviStreamFragment);
        }


    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.container, fragment, "stream");
        ft.commit();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.remove(this.fragment);

    }
}
