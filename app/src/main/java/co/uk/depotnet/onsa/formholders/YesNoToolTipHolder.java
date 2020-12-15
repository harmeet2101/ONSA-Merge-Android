package co.uk.depotnet.onsa.formholders;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.views.YesNoTextView;

public class YesNoToolTipHolder extends RecyclerView.ViewHolder {
    public View view;
    public ImageButton imgBtnInfo;
    public TextView txtQuestion;
    public YesNoTextView btnYes;
    public YesNoTextView btnNo;

    public YesNoToolTipHolder(@NonNull View itemView) {
        super(itemView);
        this.view = itemView;
        this.imgBtnInfo = itemView.findViewById(R.id.imgBtnInfo);
        this.txtQuestion = itemView.findViewById(R.id.txt_question_tooltip);
        this.btnYes = itemView.findViewById(R.id.txt_btn_yes);
        this.btnNo = itemView.findViewById(R.id.txt_btn_no);
    }
}
