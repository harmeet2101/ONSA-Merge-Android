package co.uk.depotnet.onsa.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.activities.CameraActivity;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.modals.forms.Answer;
import co.uk.depotnet.onsa.views.MaterialAlertDialog;

public class AdapterCameraPhoto extends RecyclerView.Adapter<AdapterCameraPhoto.ViewHolder> {

    private Context context;
    private List<Answer> photos;
    private RequestOptions myOptions;

    public interface OnRemoveClick{
        void onRemove(Answer photo);
    }

    public AdapterCameraPhoto(Context context, List<Answer> photos) {
        this.context = context;
        this.photos = photos;
        myOptions = new RequestOptions()
                .fitCenter()
                .override(100, 100);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cam_thumb,
                viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final Answer photo = photos.get(position);
        holder.txtTitle.setText(photo.getDisplayAnswer());
        String url = photo.getAnswer();

        if (!TextUtils.isEmpty(url)) {
            Glide.with(context).load(url).
                    apply(myOptions).into(holder.imgPhoto);
        } else {
            holder.imgPhoto.setImageDrawable(null);
        }

        holder.imgCancel.setOnClickListener(view -> {
            MaterialAlertDialog dialog = new MaterialAlertDialog.Builder(context)
                    .setTitle("Remove Photo")
                    .setMessage("Do you want to remove this photo?")
                    .setPositive("Yes", (dialog1, which) -> {
                        dialog1.dismiss();
                        DBHandler.getInstance().removeAnswer(photo);
                        this.photos.remove(holder.getAdapterPosition());
                        notifyDataSetChanged();
                    })
                    .setNegative(context.getString(android.R.string.cancel), (dialog12, which) -> dialog12.dismiss())
                    .build();
            dialog.show(((CameraActivity)context).getSupportFragmentManager(), "_CONFIRM_DIALOG");
        });
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgPhoto;
        ImageView imgCancel;
        TextView txtTitle;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPhoto = itemView.findViewById(R.id.img_photo_view);
            imgCancel = itemView.findViewById(R.id.img_btn_cancel);
            txtTitle = itemView.findViewById(R.id.txt_title);
        }
    }
}
