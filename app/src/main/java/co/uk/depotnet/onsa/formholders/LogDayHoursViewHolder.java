package co.uk.depotnet.onsa.formholders;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import co.uk.depotnet.onsa.R;


public class LogDayHoursViewHolder extends RecyclerView.ViewHolder {
    public LinearLayout llHoursContainer;
    public TextView txtNoLoggedHours;
    public TextView txtTaskType;
    public TextView txtJobReference;
    public TextView txtDate;
    public ImageView imgTimeSheetIcon;
    public ImageView imgBtnDelete;
    public TextView txtOperatives;
    public LinearLayout llTable;
    public LinearLayout llRow1;
    public LinearLayout llRow2;
    public LinearLayout llRow3;
    public LinearLayout llRow4;
    public TextView txtTimeTypeName1;
    public TextView txtNormalTime1;
    public TextView llOverTime1;
    public TextView llTotalHours1;
    public TextView txtTimeTypeName2;
    public TextView txtNormalTime2;
    public TextView llOverTime2;
    public TextView llTotalHours2;
    public TextView txtTimeTypeName3;
    public TextView txtNormalTime3;
    public TextView llOverTime3;
    public TextView llTotalHours3;
    public TextView txtTimeTypeName4;
    public TextView txtNormalTime4;
    public TextView llOverTime4;
    public TextView llTotalHours4;
    public View viewTotalLine;
    public TextView llTotalHours5;

    public LogDayHoursViewHolder(View view) {
        super(view);
        llHoursContainer = view.findViewById(R.id.ll_hour_container);
        txtNoLoggedHours = view.findViewById(R.id.txt_no_logged_hours);
        txtTaskType = view.findViewById(R.id.txt_task_type);
        txtJobReference = view.findViewById(R.id.txt_job_reference);
        txtDate = view.findViewById(R.id.txt_date);
        imgTimeSheetIcon = view.findViewById(R.id.img_timesheet_icon);
        imgBtnDelete = view.findViewById(R.id.img_btn_delete);
        txtOperatives = view.findViewById(R.id.txt_operatives);
        llTable = view.findViewById(R.id.ll_table);
        llRow1 = view.findViewById(R.id.ll_row_1);
        llRow2 = view.findViewById(R.id.ll_row_2);
        llRow3 = view.findViewById(R.id.ll_row_3);
        llRow4 = view.findViewById(R.id.ll_row_4);
        txtTimeTypeName1 = view.findViewById(R.id.txt_time_type_name1);
        txtNormalTime1 = view.findViewById(R.id.txt_normal_time1);
        llOverTime1 = view.findViewById(R.id.txt_over_time1);
        llTotalHours1 = view.findViewById(R.id.txt_total_time1);
        txtTimeTypeName2 = view.findViewById(R.id.txt_time_type_name2);
        txtNormalTime2 = view.findViewById(R.id.txt_normal_time2);
        llOverTime2 = view.findViewById(R.id.txt_over_time2);
        llTotalHours2 = view.findViewById(R.id.txt_total_time2);
        txtTimeTypeName3 = view.findViewById(R.id.txt_time_type_name3);
        txtNormalTime3 = view.findViewById(R.id.txt_normal_time3);
        llOverTime3 = view.findViewById(R.id.txt_over_time3);
        llTotalHours3 = view.findViewById(R.id.txt_total_time3);
        txtTimeTypeName4 = view.findViewById(R.id.txt_time_type_name4);
        txtNormalTime4 = view.findViewById(R.id.txt_normal_time4);
        llOverTime4 = view.findViewById(R.id.txt_over_time4);
        llTotalHours4 = view.findViewById(R.id.txt_total_time4);
        viewTotalLine = view.findViewById(R.id.view_total_line);
        llTotalHours5 = view.findViewById(R.id.txt_total_time5);
    }

    public void setColor(String status) {
        int color = itemView.getContext().getResources().getColor(R.color.ColorTimeSheet);
        if(TextUtils.isEmpty(status)){
            imgTimeSheetIcon.setImageResource(R.drawable.ic_timesheet_blue_icon);
            imgBtnDelete.setVisibility(View.GONE);
        }else if(status.equalsIgnoreCase("In Progress")){
            imgTimeSheetIcon.setImageResource(R.drawable.ic_timesheet_in_progress_icon);
            imgBtnDelete.setVisibility(View.GONE);
            color = itemView.getContext().getResources().getColor(R.color.ColorTimeSheetInProgress);
        }else if (status.equalsIgnoreCase("Approved")) {
            imgBtnDelete.setVisibility(View.GONE);
            imgTimeSheetIcon.setImageResource(R.drawable.ic_timsheet_approved_icon);
            color = itemView.getContext().getResources().getColor(R.color.ColorTimeSheetApproved);
        } else if (status.equalsIgnoreCase("Awaiting Approval")) {
            imgBtnDelete.setVisibility(View.GONE);
            imgTimeSheetIcon.setImageResource(R.drawable.ic_timesheet_pending_icon);
            color = itemView.getContext().getResources().getColor(R.color.ColorTimeSheetPending);
        } else {
            imgTimeSheetIcon.setImageResource(R.drawable.ic_timesheet_blue_icon);
            imgBtnDelete.setVisibility(View.GONE);
        }


        txtTaskType.setTextColor(color);
        txtJobReference.setTextColor(color);
        txtNormalTime1.setTextColor(color);
        llOverTime1.setTextColor(color);
        llTotalHours1.setTextColor(color);
        txtNormalTime2.setTextColor(color);
        llOverTime2.setTextColor(color);
        llTotalHours2.setTextColor(color);
        txtNormalTime3.setTextColor(color);
        llOverTime3.setTextColor(color);
        llTotalHours3.setTextColor(color);
        txtNormalTime4.setTextColor(color);
        llOverTime4.setTextColor(color);
        llTotalHours4.setTextColor(color);
        viewTotalLine.setBackgroundColor(color);
        llTotalHours5.setTextColor(color);

    }
}
