package co.uk.depotnet.onsa.formholders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import co.uk.depotnet.onsa.R;

public class DateTimeHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView txtTitle;
        public TextView txtTime;
        public TextView txtDate;


    public DateTimeHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            this.txtTitle = itemView.findViewById(R.id.txt_title);
            this.txtTime = itemView.findViewById(R.id.txt_time);
            this.txtDate = itemView.findViewById(R.id.txt_date);
        }
    }