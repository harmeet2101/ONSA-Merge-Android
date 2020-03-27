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

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.listeners.OnItemClickListener;
import co.uk.depotnet.onsa.modals.forms.Answer;
import co.uk.depotnet.onsa.modals.forms.FormItem;
import co.uk.depotnet.onsa.modals.forms.Screen;

public class AdapterAssets extends RecyclerView.Adapter<AdapterAssets.ViewHolder> {

    private Context context;
    private ArrayList<FormItem> items;
    private OnItemClickListener<FormItem> listener;

    private long submissionID;
    private Screen screen;


    public AdapterAssets(Context context, long submissionID, ArrayList<FormItem> items,
                         OnItemClickListener<FormItem> listener , Screen screen) {
        this.context = context;
        this.items = items;
        this.listener = listener;
        this.submissionID = submissionID;
        this.screen = screen;


    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        return new ViewHolder(LayoutInflater.from(context).
                inflate(R.layout.item_work_log, viewGroup,
                        false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final FormItem formItem = items.get(position);
        holder.txtTitle.setText(formItem.getTitle());

//        holder.imgIcon.setVisibility(isValidate(position) ? View.VISIBLE : View.INVISIBLE);
        if(isValidate(position) ){
            holder.imgIcon.setImageResource(R.drawable.ic_check);
            holder.imgIcon.setBackgroundResource(R.drawable.img_bg_circle);
        }else{
            holder.imgIcon.setImageResource(R.drawable.ic_offline_queue_01);
            holder.imgIcon.setBackgroundResource(R.drawable.img_bg_cirlcle_orange);
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(formItem, holder.getAdapterPosition());
            }
        });

    }

    public boolean isValidate(int position){

        ArrayList<FormItem> items = screen.getItems();
        for(FormItem item : items){
            Answer answer = DBHandler.getInstance().getAnswer(submissionID, item.getUploadId(), item.getRepeatId(), position);
            if(!item.isOptional() && (answer == null || TextUtils.isEmpty(answer.getAnswer()))){
                return false;

            }
        }

        return true;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView txtTitle;
        ImageView imgIcon;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            this.txtTitle = itemView.findViewById(R.id.txt_title);
            this.imgIcon = itemView.findViewById(R.id.img_icon);
        }
    }
}
