package jp.hanatoya.ipcam.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.hanatoya.ipcam.MyApp;
import jp.hanatoya.ipcam.R;
import jp.hanatoya.ipcam.models.CamExt;
import jp.hanatoya.ipcam.utils.EvidenceImageRequest;
import jp.hanatoya.ipcam.utils.MyCamUtils;
import jp.hanatoya.ipcam.utils.MyNetworkUtils;
import jp.hanatoya.ipcam.utils.VolleySingleton;


class MainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<CamExt> camExts = new ArrayList<>();
    private MainFragment.MainFragmentListener listener;
    private  Context ctx;

    MainAdapter(MainFragment.MainFragmentListener listener, Context context
    ) {
        this.listener = listener;
        this.ctx = context;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_cam, parent, false);
        return new ViewHolderCam(convertView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolderCam) {
            final ViewHolderCam h = (ViewHolderCam) holder;
            final CamExt camExt = camExts.get(position);
            h.name.setText(camExt.getCam().getName());
            h.type.setText(camExt.getCam().getType());
//            h.status.setText(camExt.getStatus() == 0 ? context.getString(R.string.status_ok) : context.getString(R.string.status_error));
            h.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onEditClick(camExt.getCam().getId());
                }
            });
            h.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onStreamClick(camExt.getCam().getId(), camExt.getCam().getType());
                }
            });

            if (!MyCamUtils.isEvidenceCam(camExt.getCam().getType())){
                // Glide uses internal cache time so hardcode refresh is not needed
                Glide.with(this.ctx).load(MyNetworkUtils.buildGlideUrlWithAuth(camExt.getImgUrl(), camExt.getCam().getUsername(), camExt.getCam().getPassword()))
                        .error(R.drawable.bg_error)
                        .into(h.img);
            }else{
                EvidenceImageRequest evidenceImageRequest = new EvidenceImageRequest(camExt.getImgUrl(), new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        h.img.setImageBitmap(response);
                    }
                }, 0, 0, ImageView.ScaleType.CENTER, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        h.img.setImageResource(R.drawable.bg_error);
                    }
                }, camExt.getCam().getUsername(), camExt.getCam().getPassword());
                VolleySingleton.getInstance(ctx).addToRequestQueue(evidenceImageRequest);
            }
        }
    }

    @Override
    public int getItemCount() {
        return camExts.size();
    }

    static class ViewHolderCam extends RecyclerView.ViewHolder {
        @BindView(R.id.name) TextView name;
        @BindView(R.id.type) TextView type;
//        @BindView(R.id.status) TextView status;
        @BindView(R.id.edit) TextView edit;
        @BindView(R.id.img) ImageView img;


        ViewHolderCam(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    void swap(List<CamExt> newCamExts) {
        camExts.clear();
        camExts.addAll(newCamExts);
        notifyDataSetChanged();
    }
}
