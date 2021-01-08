package co.uk.depotnet.onsa.formholders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import co.uk.depotnet.onsa.R;

public class TimeTotalWorkedHoursHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView txtTime;


    public TimeTotalWorkedHoursHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            this.txtTime = itemView.findViewById(R.id.txt_time);
        }
    }