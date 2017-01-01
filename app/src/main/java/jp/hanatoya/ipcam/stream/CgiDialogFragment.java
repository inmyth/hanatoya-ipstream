package jp.hanatoya.ipcam.stream;


import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.hanatoya.ipcam.R;
import jp.hanatoya.ipcam.models.CamExt;
import jp.hanatoya.ipcam.repo.Switch;

public class CgiDialogFragment extends DialogFragment {
    private static final String CAMEXT = "CAMEXT";
    private CgiDialogAdapter adapter;
    private Listener listener;

    @BindView(android.R.id.list) RecyclerView list;


    public static CgiDialogFragment newInstance(CamExt camExt, Listener listener) {

        Bundle args = new Bundle();
        args.putParcelable(CAMEXT, Parcels.wrap(camExt));
        CgiDialogFragment fragment = new CgiDialogFragment();
        fragment.setListener(listener);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CamExt camExt = Parcels.unwrap(getArguments().getParcelable(CAMEXT));
        adapter = new CgiDialogAdapter(camExt, listener);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppDialogTheme);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_stream_cgi, container);
        ButterKnife.bind(this, view);
        list.setLayoutManager(new LinearLayoutManager(getActivity()));
        list.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).build());
        list.setAdapter(adapter);
        return view;
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

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {

        void cgiClick(Switch s);

    }
}
