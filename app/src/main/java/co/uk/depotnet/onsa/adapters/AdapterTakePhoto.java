package co.uk.depotnet.onsa.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.listeners.PhotoAdapterListener;
import co.uk.depotnet.onsa.modals.forms.Answer;
import co.uk.depotnet.onsa.modals.forms.FormItem;
import co.uk.depotnet.onsa.modals.forms.Photo;
import co.uk.depotnet.onsa.views.DropdownEditTextBottomSheet;

public class AdapterTakePhoto extends RecyclerView.Adapter<AdapterTakePhoto.ViewHolder> {

    private Context context;
    private List<Answer> items;
    private ArrayList<Photo> photos;
    private int repeatCounter;
    private FormItem formItem;

    private PhotoAdapterListener listener;
    private String title;



    public AdapterTakePhoto(Context context ,long submissionID, FormItem formItem,
                            int repeatCounter,  PhotoAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.formItem = formItem;
        this.photos = formItem.getPhotos();
        this.repeatCounter = repeatCounter;
        this.title = formItem.getTitle();
        this.items = DBHandler.getInstance().getPhotos(submissionID , formItem.getPhotoId() , formItem.getTitle() , repeatCounter);
        if(!this.items.isEmpty()){
            this.items.add(new Answer());
        }

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type){
        View view = LayoutInflater.from(context).inflate(R.layout.item_take_photos , null , false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            final Answer answer = items.get(position);

            String comment = answer.getDisplayAnswer();
            if(!TextUtils.isEmpty(comment)){
                holder.txtPhotoComment.setText(comment);
                holder.txtPhotoComment.setVisibility(View.VISIBLE);
                holder.btnAddComment.setVisibility(View.GONE);
            }else{
                holder.txtPhotoComment.setText("");
                holder.txtPhotoComment.setVisibility(View.GONE);
                holder.btnAddComment.setVisibility(View.VISIBLE);
            }

            holder.imgPhoto.setImageResource(R.drawable.ic_camera);


            holder.btnTxtEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.openCamera(formItem,  repeatCounter);
                }
            });


        holder.btnAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DropdownEditTextBottomSheet dropdownMenu =
                        new DropdownEditTextBottomSheet(context,
                                new DropdownEditTextBottomSheet.OnSubmit() {

                    @Override
                    public void onCompleted(String number) {
                        answer.setDisplayAnswer(number);
                        DBHandler.getInstance().replaceData(Answer.DBTable.NAME,
                                answer.toContentValues());
                        notifyDataSetChanged();

                    }
                });

                dropdownMenu.show();
            }
        });

        holder.txtPhotoComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Answer answer = items.remove(holder.getAdapterPosition());
                photos.get(holder.getAdapterPosition()).setUrl(null);
                DBHandler.getInstance().removeAnswer(answer);
                notifyDataSetChanged();
                if(items.size() <= 1){
                    listener.onAllPhotosRemoved();
                }
            }
        });


        holder.btnTxtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Answer answer = items.remove(holder.getAdapterPosition());
                photos.get(holder.getAdapterPosition()).setUrl(null);
                DBHandler.getInstance().removeAnswer(answer);
                notifyDataSetChanged();
                if(items.size() <= 1){
                    listener.onAllPhotosRemoved();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgPhoto;
        TextView txtPhotoComment;
        TextView btnTxtEdit;
        TextView btnTxtDelete;
        TextView btnAddComment;


        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPhoto = itemView.findViewById(R.id.img_photo);
            txtPhotoComment = itemView.findViewById(R.id.txt_photo_comment);
            btnTxtEdit = itemView.findViewById(R.id.btn_txt_edit);
            btnTxtDelete = itemView.findViewById(R.id.btn_txt_delete);
            btnAddComment = itemView.findViewById(R.id.btn_add_comment);
        }
    }
}
