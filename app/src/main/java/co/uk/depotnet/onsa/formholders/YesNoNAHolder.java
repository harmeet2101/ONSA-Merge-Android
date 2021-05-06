package co.uk.depotnet.onsa.formholders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SwitchCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.views.YesNoTextView;

public class YesNoNAHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView txtQuestion;
        public TextView txtRectified;
        public LinearLayout llYes;
        public LinearLayout llNo;
        public LinearLayout llNA;
        public YesNoTextView btnYes;
        public YesNoTextView btnNo;
        public YesNoTextView btnNA;


        public YesNoNAHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            this.txtQuestion = itemView.findViewById(R.id.txt_question);
            this.txtRectified = itemView.findViewById(R.id.txt_rectified);
            this.llYes = itemView.findViewById(R.id.ll_yes);
            this.llNo = itemView.findViewById(R.id.ll_no);
            this.llNA = itemView.findViewById(R.id.ll_na);
            this.btnYes = itemView.findViewById(R.id.txt_btn_yes);
            this.btnNo = itemView.findViewById(R.id.txt_btn_no);
            this.btnNA = itemView.findViewById(R.id.txt_btn_not_applicable);
        }
    }