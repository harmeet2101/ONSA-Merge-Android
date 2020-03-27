//package co.uk.depotnet.onsa.adapters;
//
//import android.content.Context;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import co.uk.depotnet.onsa.R;
//import co.uk.depotnet.onsa.listeners.HomeTMJobListListener;
//import co.uk.depotnet.onsa.modals.Constant;
//import co.uk.depotnet.onsa.modals.TMJob;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class TMHomeAdapter extends RecyclerView.Adapter<TMHomeAdapter.ViewHolder> {
//
//    private final List<TMJob> jobs;
//    private final List<TMJob> originalJobs;
//    private final HomeTMJobListListener listener;
//    private Context context;
//    private int roleId;
//
//    public TMHomeAdapter(Context context, List<TMJob> jobs, int roleId, HomeTMJobListListener listener) {
//        this.context = context;
//        originalJobs = new ArrayList<>();
//        originalJobs.addAll(jobs);
//        this.jobs = jobs;
//        this.roleId = roleId;
//        this.listener = listener;
//    }
//
//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.fragment_tm_home, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(final ViewHolder holder, int position) {
//        holder.job = jobs.get(position);
//
//
//        holder.txtJobId.setText("JOB ID : " +holder.job.getTmJobID());
//
//
////        tmJob.getScheduledStart();
//        String startDate = holder.job.getJobStartDate();
////        tmJob.getScheduledEnd();
//        String startEndDate = holder.job.getJobEndDate();
//
//
//        holder.txtJobDate.setText(getDateRange(startDate, startEndDate));
//
//
//        holder.txtSseOverLay.setText(holder.job.getContractName() /*+ ", " + holder.job.getWorkTypeName()*/);
//        holder.txtAddress.setText(holder.job.getAreaName() + ", " + holder.job.getAddress());
//        holder.txtStatus.setText(holder.job.getTmJobStatusName() + " ");
//        holder.txtPresiteSurvey.setText("TM Survey");
//        if(holder.job.getScheduledTime() != null) {
//            holder.txtSchedule.setText(holder.job.getScheduledTime());
//        }
//
//        holder.txtTmType.setText(holder.job.getTrafficManagementTypeID());
//
//
//        int jobTypeResId = R.string.hot_job;
//        int jobTypeImgResId = R.drawable.ic_thumb_up;
//        if (holder.job.isIsEmergencyJob()) {
//            jobTypeResId = R.string.emergency_job;
//            jobTypeImgResId = R.drawable.ic_fire;
//        } else if (holder.job.isIsHotJob()) {
//            jobTypeResId = R.string.hot_job;
//            jobTypeImgResId = R.drawable.ic_warning;
//        }
//        holder.txtJobType.setText(jobTypeResId);
//        holder.imgViewJobType.setImageResource(jobTypeImgResId);
//        holder.rlBtnViewJob.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (holder.viewJobPanel.getVisibility() == View.GONE) {
//                    holder.viewJobPanel.setVisibility(View.VISIBLE);
//                    holder.imgViewJob.setImageResource(R.drawable.ic_remove_circle);
//                } else {
//                    holder.viewJobPanel.setVisibility(View.GONE);
//                    holder.imgViewJob.setImageResource(R.drawable.ic_add_circle);
//                }
//            }
//        });
//
//        if(roleId == Constant.ROLE_TM_AGENT){
//            holder.llBtnPreSite.setVisibility(View.VISIBLE) ;
//            holder.llBtnPreSite.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    listener.openPreSiteSurvey(holder.job);
//                }
//            });
//        }else{
//            holder.llBtnPreSite.setVisibility(View.GONE);
//        }
//
//        holder.llBtnJobDeatils.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                listener.openJobDetail(holder.job);
//            }
//        });
//
//        holder.llBtnWorkLog.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                listener.openWorkLog(holder.job);
//            }
//        });
//
//
//        holder.llBtnJobPack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                listener.openJobPack(holder.job);
//            }
//        });
//
//
//        holder.llBtnRiskAssessment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                listener.openRiskAssessment(holder.job);
//            }
//        });
//
//        holder.llBtnVisitorAttendance.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                listener.openVisitorAttendance(holder.job);
//            }
//        });
//
//    }
//
//    private String getDateRange(String startDate, String endDate) {
//        String date = "N/A";
//
//        if (startDate != null && !startDate.isEmpty()) {
//            String str[] = startDate.split("T");
//            if (str.length > 0) {
//                startDate = str[0].replace("-", "/");
//            }
//        }
//
//        if (endDate != null && !endDate.isEmpty()) {
//            String str[] = endDate.split("T");
//            if (str.length > 0) {
//                endDate = str[0].replace("-", "/");
//            }
//        }
//
//        date = startDate + " - " + endDate;
//
//        return date;
//    }
//
//    @Override
//    public int getItemCount() {
//        return jobs.size();
//    }
//
//    public void search(String keyword) {
//        jobs.clear();
//        if(keyword == null || keyword.isEmpty()){
//            jobs.addAll(originalJobs);
//            notifyDataSetChanged();
//            return;
//        }
//
//        for (TMJob job: originalJobs) {
////            if(job.getJobReferenceNumber().contains(keyword) ||
////                    job.getJobCategoryName().contains(keyword)){
////                jobs.add(job);
////            }
//        }
//
//        if(!jobs.isEmpty()) {
//            notifyDataSetChanged();
//            return;
//        }
//
//        jobs.addAll(originalJobs);
//        notifyDataSetChanged();
//    }
//
//    public void update(List<TMJob> jobs) {
//        this.jobs.clear();
//        this.jobs.addAll(jobs);
//        notifyDataSetChanged();
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder {
//        final View view;
//        final View viewJobPanel;
//        final RelativeLayout rlBtnViewJob;
//        final ImageView imgViewJob;
//        private final LinearLayout llBtnJobDeatils;
//        private final LinearLayout llBtnWorkLog;
//        private final LinearLayout llBtnJobPack;
//
//        private final LinearLayout llBtnRiskAssessment;
//        private final LinearLayout llBtnViewGangMeasure;
//        private final LinearLayout llBtnCreateTask;
//        private final LinearLayout llBtnVisitorAttendance;
//        final ImageView imgViewJobType;
//        final TextView txtJobId;
//        final TextView txtJobDate;
//        final TextView txtSseOverLay;
//        final TextView txtAddress;
//        final TextView txtStatus;
//        final TextView txtJobType;
//        final TextView txtSchedule;
//        final TextView txtTmType;
//
//
//        public TMJob job;
//
//        public ViewHolder(View view) {
//            super(view);
//            view = view;
//            viewJobPanel = view.findViewById(R.id.job_panel);
//            rlBtnViewJob = view.findViewById(R.id.rl_btn_view_job);
//            imgViewJob = view.findViewById(R.id.img_view_job);
//            imgViewJobType = view.findViewById(R.id.img_view_job_type);
//            llBtnJobDeatils = view.findViewById(R.id.ll_btn_job_detail);
//            llBtnWorkLog = view.findViewById(R.id.ll_btn_work_log);
//            llBtnJobPack = view.findViewById(R.id.ll_btn_job_pack);
//
//            llBtnRiskAssessment = view.findViewById(R.id.ll_btn_risk_assessment);
//            llBtnViewGangMeasure = view.findViewById(R.id.ll_btn_view_gang_mesaures);
//            llBtnCreateTask = view.findViewById(R.id.ll_btn_create_task);
//            llBtnVisitorAttendance = view.findViewById(R.id.ll_btn_visitor_attendance);
//            llBtnViewGangMeasure.setVisibility(View.GONE);
//            llBtnCreateTask.setVisibility(View.GONE);
//            txtJobId = view.findViewById(R.id.txt_job_id);
//            txtJobDate = view.findViewById(R.id.txt_job_date);
//            txtSseOverLay = view.findViewById(R.id.txt_sse_overlay);
//            txtAddress = view.findViewById(R.id.txt_address);
//            txtStatus = view.findViewById(R.id.txt_status);
//            txtJobType = view.findViewById(R.id.txt_job_type);
//
//            txtSchedule = view.findViewById(R.id.txt_schedule);
//            txtTmType = view.findViewById(R.id.txt_tm_type);
//        }
//    }
//}
