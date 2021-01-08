package co.uk.depotnet.onsa.formholders;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import co.uk.depotnet.onsa.R;

public class TimeSheetItemHolder extends RecyclerView.ViewHolder {

    public TextView txtDay;
    public TextView txtDate;
    public TextView txtTime;
    public TextView txtTimeType;
    public TextView txtNoHoursLogged;
    public RelativeLayout rlTimeContainer;
    public LinearLayout llBtnEdit;
    public LinearLayout llBtnDelete;

    public TimeSheetItemHolder(@NonNull View itemView) {
        super(itemView);
        txtDay = itemView.findViewById(R.id.txt_day);
        txtDate = itemView.findViewById(R.id.txt_date);
        txtTime = itemView.findViewById(R.id.txt_time);
        txtTimeType = itemView.findViewById(R.id.txt_time_type);
        txtNoHoursLogged = itemView.findViewById(R.id.txt_no_logged_hours);
        rlTimeContainer = itemView.findViewById(R.id.rl_time_container);
        llBtnEdit = itemView.findViewById(R.id.ll_btn_edit);
        llBtnDelete = itemView.findViewById(R.id.ll_btn_delete);
    }
}