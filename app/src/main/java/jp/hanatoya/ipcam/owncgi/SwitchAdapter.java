package jp.hanatoya.ipcam.owncgi;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.hanatoya.ipcam.R;
import jp.hanatoya.ipcam.repo.Switch;


public class SwitchAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    ArrayList<Switch> switches = new ArrayList<>();
    @NonNull SwitchFragment.SwitchFragmentListener listener;

    public SwitchAdapter(@NonNull SwitchFragment.SwitchFragmentListener listener) {
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_switch, parent, false);
        return new ViewHolderSwitch(convertView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolderSwitch){
            ViewHolderSwitch h = (ViewHolderSwitch) holder;
            final Switch s = switches.get(position);
            h.name.setText(s.getName());
            h.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.edit(s);
                }
            });
            h.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.delete(s);
                }
            });

        }
    }

    public void replaceAll(List<Switch> switchList){
        switches.clear();
        switches.addAll(switchList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return switches.size();
    }


     static class ViewHolderSwitch extends RecyclerView.ViewHolder {

         @BindView(R.id.name) TextView name;
         @BindView(R.id.card_view) CardView root;
         @BindView(R.id.delete) ImageView delete;

        public ViewHolderSwitch(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }
}
