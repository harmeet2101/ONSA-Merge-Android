package co.uk.depotnet.onsa.formholders;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import co.uk.depotnet.onsa.R;

public class DayViewHolder extends RecyclerView.ViewHolder {
    public View view;
    public TextView txtTitle;


    public DayViewHolder(@NonNull View itemView) {
        super(itemView);
        this.view = itemView;
        this.txtTitle = itemView.findViewById(R.id.txt_title);
    }

    public void setColor(String status) {
        int color = view.getContext().getResources().getColor(R.color.ColorTimeSheet);

        if(!TextUtils.isEmpty(status)){
            if(status.equalsIgnoreCase("In Progress")){
                color = view.getContext().getResources().getColor(R.color.ColorTimeSheetInProgress);
            } else if (status.equalsIgnoreCase("Approved")) {
                color = view.getContext().getResources().getColor(R.color.ColorTimeSheetApproved);
            } else if (status.equalsIgnoreCase("Awaiting Approval")) {
                color = view.getContext().getResources().getColor(R.color.ColorTimeSheetPending);
            }
        }

        txtTitle.setBackgroundColor(color);
    }
}