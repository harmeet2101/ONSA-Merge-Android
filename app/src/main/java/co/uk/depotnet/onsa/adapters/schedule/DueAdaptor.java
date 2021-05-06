package co.uk.depotnet.onsa.adapters.schedule;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.listeners.ScheduleListner;
import co.uk.depotnet.onsa.modals.schedule.Schedule;
import co.uk.depotnet.onsa.utils.Utils;

public class DueAdaptor extends RecyclerView.Adapter<DueAdaptor.DueHolder>
{
    private final List<Schedule> schedules=new ArrayList<>();
    private final ScheduleListner listener;
    private Context context;


    public DueAdaptor(Context context, ScheduleListner listener) {
        this.context = context;
        this.listener = listener;
    }
    @NonNull
    @Override
    public DueHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.due_item_list, parent, false);
        return new DueHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DueHolder holder, int position) {
        holder.schedule = schedules.get(position);
        try {
            holder.tv_due_date.setText("DUE: ".concat(Utils.getSimpleDateFormat(holder.schedule.getDueDate())));
        } catch (ParseException e) {
            e.printStackTrace();
            holder.tv_due_date.setText("DUE: ".concat(holder.schedule.getDueDate()));
        }
        if (holder.schedule.getJobGangId()!=null) {
            try {
             holder.tv_auditee_text.setText(TextUtils.join(", ",DBHandler.getInstance().GetGangOperativeList(holder.schedule.getJobGangId())));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        } else
        {
            holder.tv_auditee_text.setText("");
        }
        if(TextUtils.isEmpty(holder.schedule.getJobEstimateNumber())){
            holder.tv_ref.setText((String.format("%s : %s","Estimate Number","N/A")));
        }else{
            holder.tv_ref.setText((String.format("%s : %s","Estimate Number",holder.schedule.getJobEstimateNumber())));
        }

        //holder.tv_inspectionType.setText("Inspection Type: "+holder.schedule.getInspectionTemplateName());
        holder.tv_inspectionType.setText((String.format("%s : %s","Inspection Type",holder.schedule.getInspectionTemplateName())));
        if (holder.schedule.getScheduledInspectionStatus() == 3) { //for not due
            holder.rl_view_schedule.setVisibility(View.GONE);
        }
        else {
            holder.rl_view_schedule.setVisibility(View.VISIBLE);
        }
        holder.rl_view_schedule.setOnClickListener(v -> {
            if (holder.rl_hide_schedule.getVisibility()==View.VISIBLE)
            {
                holder.due_expend.setImageResource(R.drawable.ic_add_circle);
                holder.rl_hide_schedule.setVisibility(View.GONE);
            }
            else
            {
                holder.due_expend.setImageResource(R.drawable.ic_remove_circle);
                holder.rl_hide_schedule.setVisibility(View.VISIBLE);
            }
        });
        holder.tv_start_inspection.setOnClickListener(v -> listener.StartScheduleInspection(holder.schedule));
    }

    @Override
    public int getItemCount() {
        return schedules.size();
    }

    public void update(List<Schedule> newJobs) {
        schedules.clear();
        schedules.addAll(newJobs);
        notifyDataSetChanged();
    }

    public class DueHolder extends RecyclerView.ViewHolder {
        public Schedule schedule;
        private ImageView due_img,due_expend;
        private TextView tv_due_date,tv_auditee_text,tv_ref,tv_inspectionType,tv_start_inspection;
        private RelativeLayout rl_view_schedule,rl_hide_schedule;
        public DueHolder(@NonNull View itemView) {
            super(itemView);
            due_img=itemView.findViewById(R.id.due_img);
            due_expend=itemView.findViewById(R.id.due_expend);
            tv_due_date=itemView.findViewById(R.id.tv_due_date);
            tv_auditee_text=itemView.findViewById(R.id.tv_auditee_text);
            tv_ref=itemView.findViewById(R.id.tv_ref);
            tv_inspectionType=itemView.findViewById(R.id.tv_inspectionType);
            tv_start_inspection=itemView.findViewById(R.id.tv_start_inspection);
            rl_view_schedule=itemView.findViewById(R.id.rl_view_schedule);
            rl_hide_schedule=itemView.findViewById(R.id.rl_hide_schedule);
        }
    }
}
