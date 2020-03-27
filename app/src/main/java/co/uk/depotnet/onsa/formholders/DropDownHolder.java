package co.uk.depotnet.onsa.formholders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import co.uk.depotnet.onsa.R;

public class DropDownHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView txtTitle;
        public TextView txtValue;


        public DropDownHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            this.txtTitle = itemView.findViewById(R.id.txt_title);
            this.txtValue = itemView.findViewById(R.id.txt_value);
        }
    }