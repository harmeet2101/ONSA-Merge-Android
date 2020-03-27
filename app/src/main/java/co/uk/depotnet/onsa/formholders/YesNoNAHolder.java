package co.uk.depotnet.onsa.formholders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SwitchCompat;
import android.view.View;
import android.widget.TextView;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.views.YesNoTextView;

public class YesNoNAHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView txtQuestion;
        public TextView txtRectified;
        public YesNoTextView btnYes;
        public YesNoTextView btnNo;
        public YesNoTextView btnNA;


        public YesNoNAHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            this.txtQuestion = itemView.findViewById(R.id.txt_question);
            this.txtRectified = itemView.findViewById(R.id.txt_rectified);
            this.btnYes = itemView.findViewById(R.id.txt_btn_yes);
            this.btnNo = itemView.findViewById(R.id.txt_btn_no);
            this.btnNA = itemView.findViewById(R.id.txt_btn_not_applicable);
        }
    }