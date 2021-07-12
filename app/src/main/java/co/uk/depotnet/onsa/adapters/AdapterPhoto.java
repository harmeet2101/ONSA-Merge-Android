package co.uk.depotnet.onsa.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.listeners.PhotoAdapterListener;
import co.uk.depotnet.onsa.modals.forms.Answer;
import co.uk.depotnet.onsa.modals.forms.FormItem;
import co.uk.depotnet.onsa.modals.forms.Photo;
import co.uk.depotnet.onsa.utils.MyTransformation;

import java.util.ArrayList;
import java.util.List;


public class AdapterPhoto extends RecyclerView.Adapter<AdapterPhoto.ViewHolder> {

    private Context context;
    private List<Answer> items;
    private ArrayList<Photo> photos;
    private int repeatCounter;
    private FormItem formItem;
    private RequestOptions myOptions;
    private PhotoAdapterListener listener;
    private long submissionID;

    AdapterPhoto(Context context ,long submissionID, FormItem formItem,
                 int repeatCounter,  PhotoAdapterListener listener) {
        this.context = context;
        this.formItem = formItem;
        this.listener = listener;
        this.photos = formItem.getPhotos();
        this.repeatCounter = repeatCounter;
        this.submissionID = submissionID;
        this.items = DBHandler.getInstance().getPhotos(submissionID , formItem.getPhotoId() , formItem.getTitle() , repeatCounter);
        if(!this.items.isEmpty()){
            this.items.add(new Answer());
        }

        myOptions = new RequestOptions()
                .fitCenter()
                .override(100, 100)
                .transform(new MyTransformation(context, 0));
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type){
        View view = LayoutInflater.from(context).inflate(R.layout.item_photos , viewGroup , false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        String url = items.get(position).getAnswer();
        if(url != null && !url.isEmpty()) {
            Glide.with(context).load(url).
                    apply(myOptions).into(holder.imgPhoto);
            holder.imgBtnClear.setVisibility(View.VISIBLE);
        }else{
            holder.imgPhoto.setImageResource(R.drawable.ic_camera);
            holder.imgBtnClear.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(view -> listener.openCamera(formItem, repeatCounter));
        }

        holder.imgBtnClear.setOnClickListener(view -> {
            Answer answer = items.remove(holder.getAdapterPosition());
            deletePhoto(holder.getAdapterPosition());
            DBHandler.getInstance().removeAnswer(answer);
            notifyDataSetChanged();
            if(items.size() <= 1){
                listener.onAllPhotosRemoved();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgPhoto;
        ImageView imgBtnClear;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPhoto = itemView.findViewById(R.id.img_photo);
            imgBtnClear = itemView.findViewById(R.id.img_btn_cancel);
        }
    }


    private void deletePhoto(int position){

        if(position >= photos.size()){
            return;
        }

        for(int i = position; i < photos.size()-1 ; i++){
            photos.get(i).setUrl(photos.get(i+1).getUrl());
        }

        photos.get(photos.size()-1).setUrl(null);
    }

    public int getPhotosTaken() {
        int size = items.size();
        return size >= 1 ? size-1 : 0;
    }

    public void update(FormItem formItem){
        this.items.clear();
        this.formItem = formItem;
        this.items.addAll(DBHandler.getInstance().getPhotos(submissionID , formItem.getPhotoId() , formItem.getTitle() , repeatCounter));
        if(!this.items.isEmpty()){
            for(int i = 0 ;i < items.size() ; i++){
                photos.get(i).setUrl(items.get(i).getAnswer());
            }
            this.items.add(new Answer());
        }
        notifyDataSetChanged();
    }
}
