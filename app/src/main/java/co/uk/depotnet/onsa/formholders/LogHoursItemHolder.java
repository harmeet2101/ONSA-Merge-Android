package co.uk.depotnet.onsa.formholders;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import co.uk.depotnet.onsa.R;

public class LogHoursItemHolder extends RecyclerView.ViewHolder {

    public TextView txtDateWorked;
    public TextView txtTimeType;
    public TextView txtTime;

    public LinearLayout llBtnEdit;
    public LinearLayout llBtnDelete;

    public LogHoursItemHolder(@NonNull View itemView) {
        super(itemView);
        txtDateWorked = itemView.findViewById(R.id.txt_date_worked);
        txtTimeType = itemView.findViewById(R.id.txt_time_type);
        txtTime = itemView.findViewById(R.id.txt_time);
        llBtnEdit = itemView.findViewById(R.id.ll_btn_edit);
        llBtnDelete = itemView.findViewById(R.id.ll_btn_delete);
    }
}