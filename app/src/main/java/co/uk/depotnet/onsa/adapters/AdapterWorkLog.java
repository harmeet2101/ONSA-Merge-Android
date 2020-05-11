package co.uk.depotnet.onsa.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.listeners.OnItemClickListener;
import co.uk.depotnet.onsa.modals.Job;
import co.uk.depotnet.onsa.modals.WorkLog;

import java.util.ArrayList;

public class AdapterWorkLog extends RecyclerView.Adapter<AdapterWorkLog.ViewHolder> {

    private Context context;
    private ArrayList<WorkLog> items;
    private OnItemClickListener<WorkLog> listener;
    private String jobID;
    private boolean isBookOn;
    private boolean hasRFNA;

    public AdapterWorkLog(Context context, ArrayList<WorkLog> items, OnItemClickListener<WorkLog> listener,String jobID) {
        this.context = context;
        this.items = items;
        this.listener = listener;
        this.jobID=jobID;
        Job job = DBHandler.getInstance().getJob(jobID);
        hasRFNA = job!=null && job.hasRFNA();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        return new ViewHolder(LayoutInflater.from(context).
                inflate(R.layout.item_work_log, viewGroup,
                        false));
    }

    public void setBookOn(boolean isBookOn){
        this.isBookOn = isBookOn;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final WorkLog workLog = items.get(position);



        if(position == 0 ){
            holder.rlParent.setBackgroundColor(context.getResources().getColor(R.color.white));
        }else if(isBookOn){
            holder.rlParent.setBackgroundColor(context.getResources().getColor(R.color.white));
        }else{
            holder.rlParent.setBackgroundColor(context.getResources().getColor(R.color.btn_gray));
        }

        if(!isBookOn ){
            holder.imgIcon.setImageResource(R.drawable.ic_offline_queue_01);
            holder.imgIcon.setBackgroundResource(R.drawable.img_bg_cirlcle_orange);
        }else if(position == 0){
            holder.imgIcon.setImageResource(R.drawable.ic_check);
            holder.imgIcon.setBackgroundResource(R.drawable.img_bg_circle);
        }else{
            holder.imgIcon.setImageResource(workLog.isStatus() ?
                    R.drawable.ic_check : R.drawable.ic_offline_queue_01);
            holder.imgIcon.setBackgroundResource(workLog.isStatus() ?
                    R.drawable.img_bg_circle : R.drawable.img_bg_cirlcle_orange);
        }
        if(position == 5 ) {
            if (isRFNAEnable()) {
                holder.rlParent.setBackgroundColor(context.getResources().getColor(R.color.white));
            } else {
                holder.rlParent.setBackgroundColor(context.getResources().getColor(R.color.btn_gray));
            }
        }

        if(position == 6 ) {
            if (isEngCompEnable()) {
                holder.rlParent.setBackgroundColor(context.getResources().getColor(R.color.white));
            } else {
                holder.rlParent.setBackgroundColor(context.getResources().getColor(R.color.btn_gray));
            }
        }

        holder.txtTitle.setText(workLog.getTitle());

        holder.view.setOnClickListener(view -> {

            if(holder.getAdapterPosition() == 5 && !isRFNAEnable()) {
                return;
            }

            if(holder.getAdapterPosition() == 6 && !isEngCompEnable()) {
                return;
            }
            listener.onItemClick(workLog, holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView txtTitle;
        ImageView imgIcon;
        RelativeLayout rlParent;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            this.txtTitle = itemView.findViewById(R.id.txt_title);
            this.imgIcon = itemView.findViewById(R.id.img_icon);
            this.rlParent=itemView.findViewById(R.id.rlParent);
        }
    }

    private boolean isRFNAEnable(){
        boolean status =
                !hasRFNA &&
                DBHandler.getInstance().getJobModuleStatus(jobID , "Start on Site") &&
                        !DBHandler.getInstance().getJobModuleStatus(jobID , "Eng Comp");

        return status;
    }

    private boolean isEngCompEnable(){
        boolean status =
                hasRFNA || DBHandler.getInstance().getJobModuleStatus(jobID , "Ready For Next Activity");

        return status;
    }
}
