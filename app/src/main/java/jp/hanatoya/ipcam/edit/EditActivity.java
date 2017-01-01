package jp.hanatoya.ipcam.edit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.hanatoya.ipcam.MyApp;
import jp.hanatoya.ipcam.R;
import jp.hanatoya.ipcam.form.FormFragment;
import jp.hanatoya.ipcam.form.FormPresenter;
import jp.hanatoya.ipcam.main.Events;
import jp.hanatoya.ipcam.main.MainActivity;
import jp.hanatoya.ipcam.owncgi.SwitchDialogFragment;
import jp.hanatoya.ipcam.owncgi.SwitchFragment;
import jp.hanatoya.ipcam.owncgi.SwitchPresenter;
import jp.hanatoya.ipcam.repo.DaoSession;
import jp.hanatoya.ipcam.repo.Switch;
import jp.hanatoya.ipcam.stream.StreamFragment;
import jp.hanatoya.ipcam.stream.StreamPresenter;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;


public class EditActivity extends AppCompatActivity implements EditContract.View {
    private static final String CAMID =  "CAMID";
    private Subscription busSubscription;


    @BindView(R.id.tabs)  TabLayout tabLayout;
    @BindView(R.id.viewpager)  ViewPager viewPager;

    private EditPresenter presenter;
    private DaoSession daoSession;
    private SwitchDialogFragment switchDialogFragment;
    private MaterialDialog deleteDialog;

    public static void navigate(Context ctx, long camId){
        Intent i = new Intent(ctx, EditActivity.class);
        i.putExtra(CAMID, camId);
        ctx.startActivity(i);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ButterKnife.bind(this);
        daoSession =  ((MyApp)getApplication()).getDaoSession();

        this.busSubscription = MyApp.getInstance().getBus().toObserverable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<Object>() {

                            @Override
                            public void call(Object o) {
                                 if(o instanceof  Events.EditSwitch){
                                    Events.EditSwitch event = (Events.EditSwitch)o;
                                    showSwitchDialog(event.s);
                                }else if (o instanceof  Events.DeleteSwitch){
                                    Events.DeleteSwitch event = (Events.DeleteSwitch) o;
                                     showDeleteSwitchDialog(event.s);
                                }
                            }
                        }
                );


        new EditPresenter(this, daoSession.getSwitchDao(), daoSession.getCamDao(), getIntent().getLongExtra(CAMID, -1));


        setupViewPager();
        tabLayout.setupWithViewPager(viewPager);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (busSubscription != null && !busSubscription.isUnsubscribed()) {
            busSubscription.unsubscribe();
        }
    }

    @Override
    public void setPresenter(EditContract.Presenter presenter) {
        this.presenter = (EditPresenter)presenter;
    }

    @Override
    public void setupViewPager() {
        long camId = getIntent().getLongExtra(CAMID, -1);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());
        FormFragment formFragment = FormFragment.newInstance(camId);
        SwitchFragment switchFragment = SwitchFragment.newInstance();
        adapter.addFragment(formFragment, getString(R.string.fragment_edit));
        adapter.addFragment(switchFragment, getString(R.string.fragment_switch));
        viewPager.setAdapter(adapter);
        new FormPresenter(formFragment, daoSession.getCamDao());
        new SwitchPresenter(switchFragment, daoSession.getSwitchDao(), camId);
    }

    @Override
    public void handleNoCam() {
        finish();
    }



    @Override
    public void dismissAddSwitchDialog() {
        if (switchDialogFragment != null){switchDialogFragment.dismiss();}
    }

    @Override
    public void showSwitchDialog(Switch s){
        this.switchDialogFragment = SwitchDialogFragment.newInstance(s, new SwitchDialogFragment.Listener() {
            @Override
            public void ok(Switch s) {
                presenter.saveSwitch(s);
                dismissAddSwitchDialog();
                MyApp.getInstance().getBus().send(new Events.SwitchUpdated());
            }

            @Override
            public void cancel() {
                dismissAddSwitchDialog();
            }
        });
        this.switchDialogFragment.show(getSupportFragmentManager(), "fragment_add_switch");
    }

    @Override
    public void showDeleteSwitchDialog(final Switch s) {
       this.deleteDialog =  new MaterialDialog.Builder(this)
                .title(R.string.dialog_confirm)
                .content(getString(R.string.dialog_deletecgi, s.getName()))
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        presenter.deleteSwitch(s);
                        if (deleteDialog != null){
                                deleteDialog.dismiss();
                            }
                        MyApp.getInstance().getBus().send(new Events.SwitchUpdated());

                    }
                })
                .build();

        this.deleteDialog.show();
    }
}
