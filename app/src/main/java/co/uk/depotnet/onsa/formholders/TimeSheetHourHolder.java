package co.uk.depotnet.onsa.formholders;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import co.uk.depotnet.onsa.R;

public class TimeSheetHourHolder extends RecyclerView.ViewHolder{

    public final View view;
    public final TextView txtNormalTime;
    public final TextView txtOverTime;
    public final LinearLayout llOverTime;
    public final TextView txtTotalHours;

    public TimeSheetHourHolder(@NonNull View itemView) {
        super(itemView);
        this.view = itemView;
        this.txtNormalTime = itemView.findViewById(R.id.txt_normal_time);
        this.txtOverTime = itemView.findViewById(R.id.txt_over_time);
        this.llOverTime = itemView.findViewById(R.id.ll_et_over_time);
        this.txtTotalHours = itemView.findViewById(R.id.txt_total_hours);
    }

}
