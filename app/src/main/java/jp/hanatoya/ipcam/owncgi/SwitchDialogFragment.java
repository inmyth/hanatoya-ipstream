package jp.hanatoya.ipcam.owncgi;


import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.hanatoya.ipcam.R;
import jp.hanatoya.ipcam.repo.Switch;
import jp.hanatoya.ipcam.utils.MyStringUtils;

public class SwitchDialogFragment extends DialogFragment{
    private static final String SWITCH = "SWITCH";

    @BindView(R.id.name)  EditText edName;
    @BindView(R.id.cgi)  EditText edCgi;
    @BindView(R.id.port)  EditText edPort;
    @BindView(R.id.cancel) Button cancel;
    @BindView(R.id.til_name) TextInputLayout tilName;
    @BindView(R.id.til_cgi) TextInputLayout tilCgi;

    private Listener listener;
    private Switch s;

    public SwitchDialogFragment() {
    }


    public static SwitchDialogFragment newInstance(Switch s, Listener listener) {
        SwitchDialogFragment frag = new SwitchDialogFragment();
        frag.setListener(listener);
        Bundle args = new Bundle();
        args.putParcelable(SWITCH, Parcels.wrap(s));

        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Parcelable load = getArguments().getParcelable(SWITCH);
        if (load != null){
            this.s = Parcels.unwrap(load);
        }else{
            this.s = new Switch();
        }
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppDialogTheme);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_switch, container);
        ButterKnife.bind(this, view);
        if (s.getId() != null){
            edName.setText(s.getName());
            edCgi.setText(s.getCgi());
            edPort.setText(String.valueOf(s.getPort()));
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    public void onResume() {
        Window window = getDialog().getWindow();
        Point size = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        window.setLayout((int) (size.x * 0.90), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        super.onResume();
    }

    public void setListener(@NonNull Listener listener) {
        this.listener = listener;
    }

    @OnClick(R.id.cancel)
    public void cancel(){
        listener.cancel();
    }

    @OnClick(R.id.ok)
    public void ok(){
        clearError();

        String name = MyStringUtils.getString(edName);
        if (name == null){
            setError(tilName);
            return;
        }

        String cgi = MyStringUtils.getString(edCgi);
        if (cgi == null){
            setError(tilCgi);
            return;
        }

        String portS = MyStringUtils.getString(edPort);
        int port = 80;
        if (portS != null && !portS.trim().isEmpty()){
            port = Integer.parseInt(portS);
        }

        s.setName(name);
        s.setCgi(cgi);
        s.setPort(port);

        listener.ok(s);
    }

     void clearError() {
        tilName.setError(null);
        tilName.setErrorEnabled(false);
     }

    void setError(TextInputLayout textInputLayout){
        textInputLayout.setError(getString(R.string.error_required));
    }


    public interface Listener {

        void ok(Switch s);

        void cancel();


    }
}
