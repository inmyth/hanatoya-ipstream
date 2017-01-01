package jp.hanatoya.ipcam.owncgi;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.hanatoya.ipcam.MyApp;
import jp.hanatoya.ipcam.R;
import jp.hanatoya.ipcam.edit.EditActivity;
import jp.hanatoya.ipcam.form.FormFragment;
import jp.hanatoya.ipcam.form.FormPresenter;
import jp.hanatoya.ipcam.main.Events;
import jp.hanatoya.ipcam.main.MainActivity;
import jp.hanatoya.ipcam.repo.Switch;
import jp.hanatoya.ipcam.stream.StreamFragment;
import jp.hanatoya.ipcam.stream.StreamPresenter;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;


public class SwitchFragment extends Fragment implements SwitchContract.View {

    @BindView(R.id.add) FloatingActionButton add;
    @BindView(android.R.id.list) RecyclerView list;

    private SwitchPresenter presenter;
    private Subscription busSubscription;
    private SwitchAdapter adapter;

    public static SwitchFragment newInstance() {
        Bundle args = new Bundle();
        SwitchFragment fragment = new SwitchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new SwitchAdapter(new SwitchFragmentListener() {
            @Override
            public void edit(Switch s) {
                openEditDialog(s);
            }

            @Override
            public void delete(Switch s) {
                openDeleteDialog(s);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_switch, container, false);
        ButterKnife.bind(this, view);
        this.busSubscription = MyApp.getInstance().getBus().toObserverable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<Object>() {

                            @Override
                            public void call(Object o) {
                                if (o instanceof Events.SwitchUpdated){
                                    presenter.loadAll();
                                }
                            }
                        }
                );
        list.setLayoutManager(new LinearLayoutManager(getActivity()));
        list.setAdapter(adapter);
        presenter.start();
        return  view;
    }

    @Override
    @OnClick(R.id.add)
    public void add() {
        Switch s = new Switch();
        s.setCamId(presenter.getCamId());
        openEditDialog(s);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (busSubscription != null && !busSubscription.isUnsubscribed()) {
            busSubscription.unsubscribe();
        }
    }

    @Override
    public void populate(List<Switch> switches) {
        adapter.replaceAll(switches);
    }

    @Override
    public void openDeleteDialog(Switch s) {
        MyApp.getInstance().getBus().send(new Events.DeleteSwitch(s));
    }


    @Override
    public void setPresenter(SwitchContract.Presenter presenter) {
        this.presenter = (SwitchPresenter) presenter;
    }

    @Override
    public void openEditDialog(Switch s) {
        MyApp.getInstance().getBus().send(new Events.EditSwitch(s));
    }

    interface SwitchFragmentListener {

        void edit(Switch s);

        void delete(Switch s);

    }
}
