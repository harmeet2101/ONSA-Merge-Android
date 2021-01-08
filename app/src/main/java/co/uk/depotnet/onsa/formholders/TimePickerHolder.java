package co.uk.depotnet.onsa.formholders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import co.uk.depotnet.onsa.R;

public class TimePickerHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView txtTitle;
        public TextView txtTime;


    public TimePickerHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            this.txtTitle = itemView.findViewById(R.id.txt_title);
            this.txtTime = itemView.findViewById(R.id.txt_time);
        }
    }