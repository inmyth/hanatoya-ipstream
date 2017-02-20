package jp.hanatoya.ipcam.form;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.michaldrabik.tapbarmenulib.TapBarMenu;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.hanatoya.ipcam.BasePresenter;
import jp.hanatoya.ipcam.MyApp;
import jp.hanatoya.ipcam.R;
import jp.hanatoya.ipcam.main.Events;
import jp.hanatoya.ipcam.models.CamExt;
import jp.hanatoya.ipcam.utils.Debug;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class FormFragment extends Fragment implements FormContract.View {

    @BindView(R.id.coordinator)
    FrameLayout coordinatorLayout;
    @BindView(R.id.name) EditText edname;
    @BindView(R.id.host) EditText edhost;
    @BindView(R.id.port) EditText edport;
    @BindView(R.id.username) EditText edusername;
    @BindView(R.id.password) EditText edpassword;
    @BindView(R.id.model) Spinner spmodel;
    @BindView(R.id.protocol) Spinner spprotocol;
    @BindView(R.id.til_name) TextInputLayout tilName;
    @BindView(R.id.til_host) TextInputLayout tilHost;
    @BindView(R.id.ok) ImageView ok;
    @BindView(R.id.delete) ImageView delete;
    @BindView(R.id.tapBarMenu) TapBarMenu tapBarMenu;
    @BindView(android.R.id.progress) ProgressBar progressBar;
    @BindView(R.id.evPanel) LinearLayout evPanel;


    private FormPresenter presenter;
    private Subscription busSubscription;



    public static FormFragment newInstance(long id) {
        Bundle bundle = new Bundle();
        bundle.putLong(BasePresenter.KEY_ID, id);
        FormFragment formFragment = new FormFragment();
        formFragment.setArguments(bundle);
        return formFragment;
    }


    public static FormFragment newInstance() {
        return new FormFragment();
    }

    @Override
    public void setPresenter(FormContract.Presenter presenter) {
        this.presenter = (FormPresenter)presenter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_form, container, false);
        ButterKnife.bind(this, view);
        ArrayAdapter<CharSequence> adapterType = ArrayAdapter.createFromResource(getActivity(), R.array.cams, android.R.layout.simple_spinner_item);
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spmodel.setAdapter(adapterType);
        ArrayAdapter<CharSequence> adapterProtocol = ArrayAdapter.createFromResource(getActivity(), R.array.protocols, android.R.layout.simple_spinner_item);
        adapterProtocol.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spprotocol.setAdapter(adapterProtocol);
        spmodel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setEvidence(i == 0);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        busSubscription = MyApp.getInstance().getBus().toObserverable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Object>() {

                    @Override
                    public void call(Object o) {
                        if (o instanceof Events.CameraPing){
                            Events.CameraPing event = (Events.CameraPing)o;
                            showProgressBar(false);
                            if (event.isOk) {
                                presenter.getCamExt().getCam().setStatus(0);
                                presenter.save();
                                presenter.close();
                            } else {
                                presenter.getCamExt().getCam().setStatus(-1);
                                Snackbar.make(coordinatorLayout, R.string.error_verify, Snackbar.LENGTH_INDEFINITE)
                                        .setAction(android.R.string.ok, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                showBottomTab(true);
                                            }
                                        })
                                        .show();
                            }
                        }
                    }
                });
        presenter.start();
//        Debug.setCam(edname, edhost, edport, edusername, edpassword);
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (busSubscription != null && !busSubscription.isUnsubscribed()) {
            busSubscription.unsubscribe();
        }
    }

    @OnClick(R.id.ok)
    public void ok() {
        if (presenter.isTesting()) {
            return;
        }
        clearError();
        String name = getString(edname);
        if (name == null) {
            setError(tilName);
            return;
        }

        presenter.getCamExt().getCam().setName(name);
        presenter.getCamExt().getCam().setProtocol(getString(R.string.prefix_http));
        String host = getString(edhost);
        if (host.contentEquals(getString(R.string.prefix_http))) {
            setError(tilHost);
            return;
        }
        presenter.getCamExt().getCam().setHost(host);
        String portString = getString(edport);
        if (portString != null) {
            int port = Integer.parseInt(portString);
            presenter.getCamExt().getCam().setPort(port);
        }

        String username = getString(edusername);
        String password = getString(edpassword);
        String model = getModelFromSpinner(spmodel);

        presenter.getCamExt().getCam().setUsername(username);
        presenter.getCamExt().getCam().setPassword(password);
        presenter.getCamExt().getCam().setType(model);

        showProgressBar(true);
        showBottomTab(false);
        presenter.testCam(getActivity());
    }

    @OnClick(R.id.delete)
    public void delete(){
        if (presenter.getCamExt().getCam().getId() != null){
            showDeleteConfirmDialog();
        }else{
            showCancelConfirmDialog();
        }
    }

    @OnClick(R.id.tapBarMenu)
    public void tapBar(){
        tapBarMenu.toggle();
    }


    @Override
    public void showProgressBar(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
        }else{
            progressBar.setVisibility(View.GONE);
        }
    }


    @Override
    public void populate(CamExt camExt) {
        edname.setText(camExt.getCam().getName());
        edhost.setText(camExt.getCam().getHost());
        edport.setText(String.valueOf(camExt.getCam().getPort()));
        if (camExt.getCam().getUsername() != null){
            edusername.setText(camExt.getCam().getUsername());
        }
        if (camExt.getCam().getPassword() != null){
            edpassword.setText(camExt.getCam().getPassword());
        }
    }

    @Override
    public void clearError() {
        tilName.setError(null);
        tilName.setErrorEnabled(false);
        tilHost.setError(null);
        tilHost.setErrorEnabled(false);
    }

    @Override
    public void showDeleteConfirmDialog() {
        new MaterialDialog.Builder(getActivity())
                .title(R.string.dialog_confirm)
                .content(R.string.dialog_deletecam)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        presenter.delCam();
                        presenter.close();
                    }
                })
                .show();
    }

    @Override
    public void showCancelConfirmDialog() {
        new MaterialDialog.Builder(getActivity())
                .title(R.string.dialog_confirm)
                .content(R.string.dialog_cancelform)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        presenter.close();
                    }
                })
                .show();
    }

    @Override
    public void setEvidence(boolean isEvidence) {
        if (isEvidence){
            edhost.setText(getString(R.string.evidence_host));
            edport.setText("8801");
            spprotocol.setSelection(0);
            evPanel.setVisibility(View.GONE);

        }else{
            edhost.setText("");
            edport.setText("");
            evPanel.setVisibility(View.VISIBLE);

        }

    }

    @Override
    public void setError(TextInputLayout textInputLayout) {
        textInputLayout.setError(getString(R.string.error_required));
    }


    @Override
    public String getString(EditText editText) {
        if (TextUtils.isEmpty(editText.getText())) {
            return null;
        }
        return editText.getText().toString();
    }



    @Override
    public String getModelFromSpinner(Spinner spinner) {
        return spmodel.getSelectedItem().toString();
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void showBottomTab(boolean show) {
        if(show){
            tapBarMenu.setVisibility(View.VISIBLE);
        }else{
            tapBarMenu.setVisibility(View.GONE);
        }
    }




    @Override
    public Bundle getBundle() {
        return getArguments();
    }
}
