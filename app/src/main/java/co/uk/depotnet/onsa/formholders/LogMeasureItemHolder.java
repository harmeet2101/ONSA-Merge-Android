package co.uk.depotnet.onsa.formholders;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import co.uk.depotnet.onsa.R;

public class LogMeasureItemHolder extends RecyclerView.ViewHolder {

    public View view;
    public TextView txtValue;
    public TextView txtQuantity;
    public LinearLayout llBtnEdit;
    public LinearLayout llBtnDelete;



    public LogMeasureItemHolder(@NonNull View itemView) {
        super(itemView);
        this.view = itemView;
        this.txtValue = itemView.findViewById(R.id.txt_value);
        this.txtQuantity = itemView.findViewById(R.id.txt_quantity);
        this.llBtnEdit = itemView.findViewById(R.id.ll_btn_edit);
        this.llBtnDelete = itemView.findViewById(R.id.ll_btn_delete);
    }
}