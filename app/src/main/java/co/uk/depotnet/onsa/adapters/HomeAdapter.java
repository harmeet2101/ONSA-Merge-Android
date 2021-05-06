package co.uk.depotnet.onsa.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.listeners.HomeJobListListener;
import co.uk.depotnet.onsa.modals.Job;
import co.uk.depotnet.onsa.modals.User;
import co.uk.depotnet.onsa.networking.Constants;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private final List<Job> jobs;

    private final HomeJobListListener listener;
    private Context context;
    private User user;


    public HomeAdapter(Context context, List<Job> jobs, HomeJobListListener listener) {
        this.context = context;
        this.jobs = jobs;
        this.listener = listener;
        user = DBHandler.getInstance().getUser();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.fragment_home, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.job = jobs.get(position);

        holder.imgHotJob.setVisibility(holder.job.isHotJob() ? View.VISIBLE : View.GONE);


        if(holder.job.isSubJob()){
            holder.txtJobEstNumber.setText(String.format("JOB ID: %s-S%s", holder.job.getestimateNumber(), holder.job.getSubJobNumber()));
        }else{
            holder.txtJobEstNumber.setText(String.format("JOB ID : %s", holder.job.getestimateNumber()));
        }

        holder.txtJobExchange.setText(holder.job.getexchange());

        if(TextUtils.isEmpty(holder.job.getpostCode())){
            holder.txtJobPinCode.setText("N/A");
        }else {
            holder.txtJobPinCode.setText(holder.job.getpostCode());
        }

        if(TextUtils.isEmpty(holder.job.getpriority())){
            holder.txtJobPriority.setVisibility(View.GONE);
        }else {
            holder.txtJobPriority.setVisibility(View.VISIBLE);
            holder.txtJobPriority.setText("Priority: "+holder.job.getpriority());
        }

        if(holder.job.getSurveyTypeId() == -1){
            holder.llBtnSurvey.setVisibility(View.GONE);
        }else{
            holder.llBtnSurvey.setVisibility(View.VISIBLE);
        }


        holder.txtWorkInfo.setText(String.format("Work Info: %s", holder.job.getworkTitle()));
        holder.txtJobStatus.setText(String.format("Job Status: %s", holder.job.getstatus()));
        holder.txtSiteCleared.setVisibility(holder.job.isSiteClear()? View.VISIBLE: View.GONE);
        holder.txtScheduled.setText(DBHandler.getInstance().getScheduledTasks(holder.job.getjobId()));
        if (!TextUtils.isEmpty(holder.job.getIcon()) &&
                holder.job.getIcon().equalsIgnoreCase("survey")) {
            holder.imgJob.setImageResource(R.drawable.ic_survey);
        } else {
            holder.imgJob.setImageResource(R.drawable.ic_poling);
        }

        holder.rlBtnViewJob.setOnClickListener(v -> {
            if (holder.viewJobPanel.getVisibility() == View.GONE) {
                if(holder.job.isHotJob()){
                    listener.showHotJobDialog(holder);
                }else {
                    holder.viewJobPanel.setVisibility(View.VISIBLE);
                    holder.imgViewJob.setImageResource(R.drawable.ic_remove_circle);
                }
            } else {
                holder.viewJobPanel.setVisibility(View.GONE);
                holder.imgViewJob.setImageResource(R.drawable.ic_add_circle);
            }
        });




        if(Constants.isStoreEnabled) {
            holder.llBtnLogStores.setVisibility(View.VISIBLE);
            holder.llBtnLogStores.setOnClickListener(view -> listener.onLogStores(holder.job));
        }else{
            holder.llBtnLogStores.setVisibility(View.GONE);
        }

        if(!TextUtils.isEmpty(user.getroleName()) && !user.getroleName().equalsIgnoreCase("Supervisor")){
            holder.llBtnRequestTask.setVisibility(View.GONE);
        }else{
            holder.llBtnRequestTask.setVisibility(View.VISIBLE);
            holder.llBtnRequestTask.setOnClickListener(view -> listener.openRequestTask(holder.job));
        }

        if(holder.job.isSubJob() || (TextUtils.isEmpty(user.getroleName()) || !user.getroleName().equalsIgnoreCase("Supervisor"))){
            holder.llBtnQualityCheck.setVisibility(View.GONE);
        }else{
            holder.llBtnQualityCheck.setVisibility(View.VISIBLE);
            holder.llBtnQualityCheck.setOnClickListener(view -> listener.onQualityCheck(holder.job));
        }






        holder.llBtnJobDeatils.setOnClickListener(view -> listener.openJobDetail(holder.job));

        holder.llBtnWorkLog.setOnClickListener(view -> listener.openWorkLog(holder.job));

        holder.llBtnJobPack.setOnClickListener(view -> listener.openJobPack(holder.job));


        holder.llBtnRiskAssessment.setOnClickListener(view -> listener.openRiskAssessment(holder.job));

        holder.llBtnVisitorAttendance.setOnClickListener(view -> listener.openVisitorAttendance(holder.job));
        holder.llBtnPhotoGallery.setOnClickListener(view -> listener.openPhotoGallery(holder.job));

        holder.llBtnSurvey.setOnClickListener(view -> listener.openSurvey(holder.job));
        holder.llBtnAddNotes.setOnClickListener(view -> listener.openAddNotes(holder.job));


        holder.llTakePhotoAndVideo.setOnClickListener(view -> listener.openTakePhotoAndVideo(holder.job));


        holder.txtJobPinCode.setOnClickListener(v -> openLocationOnMap(holder.job));

        holder.imgLocation.setOnClickListener(v -> openLocationOnMap(holder.job));

    }

    private void openLocationOnMap(Job job){
//        if(TextUtils.isEmpty(postCode)){
//            postCode =
//        }
        String postCode = job.getpostCode();
        String geoUri = "http://maps.google.com/maps?q=loc: 52.293060 , -1.779800 ( "+job.getlocationAddress()+" )";
        if(!TextUtils.isEmpty(postCode)){
            geoUri = "http://maps.google.com/maps?daddr=" + postCode;
        }
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse(geoUri));
        context.startActivity(intent);
    }


    @Override
    public int getItemCount() {
        return jobs.size();
    }


    public void update(List<Job> newJobs) {
        jobs.clear();
        jobs.addAll(newJobs);
        notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        public final View viewJobPanel;
        final RelativeLayout rlBtnViewJob;
        public final ImageView imgViewJob;
        final ImageView imgJob;
        final ImageView imgHotJob;
        final ImageView imgLocation;
        final TextView txtJobEstNumber;
        final TextView txtJobExchange;
        final TextView txtWorkInfo;
        final TextView txtJobStatus;
        final TextView txtJobPinCode;
        final TextView txtJobPriority;
        final TextView txtSiteCleared;
        final TextView txtScheduled;
        private final LinearLayout llBtnJobDeatils;
        private final LinearLayout llBtnWorkLog;
        private final LinearLayout llBtnJobPack;
        private final LinearLayout llBtnRiskAssessment;
        private final LinearLayout llBtnQualityCheck;
        private final LinearLayout llBtnLogStores;
        private final LinearLayout llBtnVisitorAttendance;
        private final LinearLayout llBtnPhotoGallery;
        private final LinearLayout llBtnSurvey;
        private final LinearLayout llBtnAddNotes;
        private final LinearLayout llTakePhotoAndVideo;
        private final LinearLayout llBtnRequestTask;
        public Job job;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            viewJobPanel = view.findViewById(R.id.job_panel);
            rlBtnViewJob = view.findViewById(R.id.rl_btn_view_job);
            imgViewJob = view.findViewById(R.id.img_view_job);
            imgLocation = view.findViewById(R.id.img_location);
            llBtnJobDeatils = view.findViewById(R.id.ll_btn_job_detail);
            llBtnWorkLog = view.findViewById(R.id.ll_btn_work_log);
            llBtnJobPack = view.findViewById(R.id.ll_btn_job_pack);
            llBtnSurvey = view.findViewById(R.id.ll_btn_survey);
            llBtnRiskAssessment = view.findViewById(R.id.ll_btn_risk_assessment);
            llBtnQualityCheck = view.findViewById(R.id.ll_btn_quality_check);
            llBtnLogStores = view.findViewById(R.id.ll_btn_log_stores);
            llBtnVisitorAttendance = view.findViewById(R.id.ll_btn_visitor_attendance);
            llBtnPhotoGallery = view.findViewById(R.id.ll_btn_photo_gallery);
            llBtnAddNotes = view.findViewById(R.id.ll_btn_add_notes);
            llTakePhotoAndVideo = view.findViewById(R.id.ll_btn_take_photo_video);
            llBtnRequestTask = view.findViewById(R.id.ll_btn_request_task);
            imgJob = view.findViewById(R.id.img_job);
            imgHotJob = view.findViewById(R.id.img_hot_job);
            txtJobEstNumber = view.findViewById(R.id.txt_job_est_number);
            txtJobExchange = view.findViewById(R.id.txt_job_exchange);
            txtJobPinCode = view.findViewById(R.id.txt_job_pin_code);
            txtWorkInfo = view.findViewById(R.id.txt_work_info);
            txtJobStatus = view.findViewById(R.id.txt_job_status);
            txtJobPriority = view.findViewById(R.id.txt_job_priority);
            txtSiteCleared = view.findViewById(R.id.txt_site_clear);
            txtScheduled = view.findViewById(R.id.txt_task_scheduled);
        }
    }
}
