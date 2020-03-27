package co.uk.depotnet.onsa.formholders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.views.YesNoTextView;

public class YesNoHolder extends RecyclerView.ViewHolder {
    public View view;
    public TextView txtQuestion;
    public YesNoTextView btnYes;
    public YesNoTextView btnNo;


    public YesNoHolder(@NonNull View itemView) {
        super(itemView);
        this.view = itemView;
        this.txtQuestion = itemView.findViewById(R.id.txt_question);
        this.btnYes = itemView.findViewById(R.id.txt_btn_yes);
        this.btnNo = itemView.findViewById(R.id.txt_btn_no);
    }
}