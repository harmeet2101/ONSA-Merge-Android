package co.uk.depotnet.onsa.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.activities.WorkLogActivity;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.listeners.OnItemClickListener;
import co.uk.depotnet.onsa.modals.Job;
import co.uk.depotnet.onsa.modals.WorkLog;
import co.uk.depotnet.onsa.modals.httpresponses.BaseTask;

import java.util.ArrayList;

public class AdapterWorkLog extends RecyclerView.Adapter<AdapterWorkLog.ViewHolder> {

    private WorkLogActivity context;
    private ArrayList<WorkLog> items;
    private OnItemClickListener<WorkLog> listener;
    private String jobID;
    private boolean isBookOn;
    private boolean hasRFNA;
    private boolean hasRecordReturns;
    private boolean rfnaNotRequired;
    private boolean isSubJob;
    private Job job;

    public AdapterWorkLog(WorkLogActivity context, ArrayList<WorkLog> items, OnItemClickListener<WorkLog> listener, String jobID) {
        this.context = context;
        this.items = items;
        this.listener = listener;
        this.jobID=jobID;
        job = DBHandler.getInstance().getJob(jobID);
        hasRFNA = job!=null && job.hasRFNA();
        isSubJob = job!= null && job.isSubJob();
        hasRecordReturns = job!=null && job.hasRecordReturns();
        rfnaNotRequired = job!=null && job.rfnaNotRequired();
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
        String jsonName = workLog.getJson();

        if(position == 0){
            holder.rlParent.setBackgroundColor(context.getResources().getColor(R.color.white));
        }else if(isBookOn){
            if (jsonName.contains("start_on_site.json") || isStartOnSite() ){
                holder.rlParent.setBackgroundColor(context.getResources().getColor(R.color.white));
            }else {
                holder.rlParent.setBackgroundColor(context.getResources().getColor(R.color.btn_gray));
            }
        }else{
            holder.rlParent.setBackgroundColor(context.getResources().getColor(R.color.btn_gray));
        }

        if(!isBookOn && !isStartOnSite()){
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

        if(jsonName.equalsIgnoreCase("rfna.json")) {
            if (isRFNAEnable()) {
                holder.rlParent.setBackgroundColor(context.getResources().getColor(R.color.white));
            } else {
                holder.rlParent.setBackgroundColor(context.getResources().getColor(R.color.btn_gray));
            }
        }

        if(jsonName.equalsIgnoreCase("record_return.json")) {
            if (isRecordReturnEnable()) {
                holder.rlParent.setBackgroundColor(context.getResources().getColor(R.color.white));
            } else {
                holder.rlParent.setBackgroundColor(context.getResources().getColor(R.color.btn_gray));
            }
        }

        if(jsonName.contains("eng_comp.json")) {
            if (isEngCompEnable()) {
                holder.rlParent.setBackgroundColor(context.getResources().getColor(R.color.white));
            } else {
                holder.rlParent.setBackgroundColor(context.getResources().getColor(R.color.btn_gray));
            }
        }



        holder.txtTitle.setText(workLog.getTitle());

        ArrayList<BaseTask> baseTasks = DBHandler.getInstance().getTaskItems(jobID , workLog.getTaskId() , isSubJob?1 : 0);

        if(!workLog.isIndicatorVisible()){
            holder.txtCount.setVisibility(View.GONE);
        }else if(baseTasks.isEmpty() || (!isBookOn || !isStartOnSite())){
            holder.txtCount.setVisibility(View.GONE);
            holder.rlParent.setBackgroundColor(context.getResources().getColor(R.color.btn_gray));
        }else{
            holder.txtCount.setVisibility(View.VISIBLE);
            holder.rlParent.setBackgroundColor(context.getResources().getColor(R.color.white));
            holder.txtCount.setText(String.valueOf(baseTasks.size()));
        }

        if(jsonName.contains("job_site_clear.json") || jsonName.contains("job_site_clear_unscheduled.json")){
            if(isSiteClearEnable()){
                holder.rlParent.setBackgroundColor(context.getResources().getColor(R.color.white));
            }else{
                holder.rlParent.setBackgroundColor(context.getResources().getColor(R.color.btn_gray));
            }
        }

        holder.view.setOnClickListener(view -> {



            if(holder.getAdapterPosition() != 0 && (!isBookOn || (!jsonName.contains("start_on_site.json") && !isStartOnSite())) ) {
                return;
            }

            if(jsonName.equalsIgnoreCase("rfna.json") && !isRFNAEnable()) {
                return;
            }

            if(jsonName.equalsIgnoreCase("record_return.json") && !isRecordReturnEnable()) {
                return;
            }

            if(jsonName.contains("eng_comp.json") && !isEngCompEnable()) {
                return;
            }

            if(!(jsonName.equalsIgnoreCase("job_site_clear.json") ||
                    jsonName.equalsIgnoreCase("job_site_clear_unscheduled.json")
                    || jsonName.equalsIgnoreCase("sub_job_job_site_clear.json")
                    || jsonName.equalsIgnoreCase("sub_job_job_site_clear_unscheduled.json")) && workLog.isIndicatorVisible() && baseTasks.isEmpty()){
                return;
            }

            if((jsonName.equalsIgnoreCase("job_site_clear.json") ||
                    jsonName.equalsIgnoreCase("job_site_clear_unscheduled.json")
                    || jsonName.equalsIgnoreCase("sub_job_job_site_clear.json")
                    || jsonName.equalsIgnoreCase("sub_job_job_site_clear_unscheduled.json")) && !isSiteClearEnable()){
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
        TextView txtCount;
        ImageView imgIcon;
        LinearLayout rlParent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            this.txtTitle = itemView.findViewById(R.id.txt_title);
            this.imgIcon = itemView.findViewById(R.id.img_icon);
            this.rlParent=itemView.findViewById(R.id.rlParent);
            this.txtCount = itemView.findViewById(R.id.txt_task_count);
        }
    }

    private boolean isStartOnSite(){
        return isBookOn && DBHandler.getInstance().getJobModuleStatus(jobID , "Start on Site");
    }

    private boolean isSiteClearEnable(){
        return isStartOnSite() && !DBHandler.getInstance().isScheduledTasksOtherThenSiteClear(job.getjobId());
    }

    private boolean isRFNAEnable(){
        return isStartOnSite() && (isSubJob || !(hasRFNA || rfnaNotRequired || DBHandler.getInstance().getJobModuleStatus(jobID , "Ready For Next Activity")));
    }


    private boolean isRecordReturnEnable(){
        return isStartOnSite() && !hasRecordReturns && ((isSubJob || hasRFNA || rfnaNotRequired || DBHandler.getInstance().getJobModuleStatus(jobID , "Ready For Next Activity")));
    }

    private boolean isEngCompEnable(){
        return isStartOnSite() && (isSubJob || hasRecordReturns  || DBHandler.getInstance().getJobModuleStatus(jobID , "Record Return"));
    }

}
