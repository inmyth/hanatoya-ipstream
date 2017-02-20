package jp.hanatoya.ipcam.main;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.hanatoya.ipcam.R;
import jp.hanatoya.ipcam.models.CamExt;


public class MainFragment extends Fragment implements MainContract.View {

    @BindView(android.R.id.list) RecyclerView list;
    @BindView(R.id.add) FloatingActionButton add;
    @BindView(R.id.toolbar) Toolbar toolbar;

    private MainPresenter presenter;
    private MainAdapter adapter;
    private MainFragmentListener mainFragmentListener = new MainFragmentListener() {

        @Override
        public void onEditClick(long id) {
            presenter.toEdit(id);
        }

        @Override
        public void onStreamClick(long id, String type) {
            presenter.toStream(id, type);
        }
    };



     public static MainFragment newInstance() {
        Bundle args = new Bundle();
        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
     }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        adapter = new MainAdapter(mainFragmentListener, getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        list.setLayoutManager(new LinearLayoutManager(getActivity()));
        list.setAdapter(adapter);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_fragment_main, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }


    @Override
    public void onResume() {
        super.onResume();
        presenter.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.download:
                presenter.exportUserSettings();
                break;
            case R.id.upload:
                presenter.importUserSettings();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setPresenter(MainContract.Presenter presenter) {
        this.presenter = (MainPresenter)presenter;
    }

    @Override
    public void toastPostImport(boolean isSuccess) {
        int r = isSuccess ? R.string.dialog_uploadsuccess : R.string.dialog_uploaderror;
        Toast.makeText(getActivity(), r, Toast.LENGTH_LONG).show();
    }

    @Override
    public void toastDbDumped() {
        Toast.makeText(getActivity(), R.string.dialog_dbdumped, Toast.LENGTH_LONG).show();
    }

    @Override
    public void populate(List<CamExt> newCamExtList){
        adapter.swap(newCamExtList);
    }

    @OnClick(R.id.add)
    @Override
    public void addClick() {
        presenter.addNewCam();
    }


    interface MainFragmentListener {

        public void onEditClick(long id);
        public void onStreamClick(long id, String type);

    }
}
