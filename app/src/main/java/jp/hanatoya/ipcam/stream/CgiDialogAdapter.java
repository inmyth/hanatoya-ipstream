package jp.hanatoya.ipcam.stream;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.hanatoya.ipcam.R;
import jp.hanatoya.ipcam.models.CamExt;
import jp.hanatoya.ipcam.repo.Switch;


public class CgiDialogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private ArrayList<Switch> switches = new ArrayList<>();
    private CamExt camExt;
    @NonNull private CgiDialogFragment.Listener listener;


    public CgiDialogAdapter(@NonNull CamExt camExt, CgiDialogFragment.Listener listener) {
        this.switches.addAll(camExt.getCam().getSwitches());
        this.listener = listener;
        this.camExt = camExt;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_dialog_stream_cgi_dialog, parent, false);
        return new CgiDialogViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CgiDialogViewHolder) {
            CgiDialogViewHolder h = (CgiDialogViewHolder) holder;
            final Switch s = switches.get(position);
            h.name.setText(s.getName());
            h.cgi.setText(camExt.buildCgiUrl(s));
            h.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.cgiClick(s);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return switches.size();
    }


    static class CgiDialogViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.name) TextView name;
        @BindView(R.id.cgi) TextView cgi;
        @BindView(R.id.root) LinearLayout root;

        public CgiDialogViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
