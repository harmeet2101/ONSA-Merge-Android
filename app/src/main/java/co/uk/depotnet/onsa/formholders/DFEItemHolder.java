package co.uk.depotnet.onsa.formholders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import co.uk.depotnet.onsa.R;

public class DFEItemHolder extends RecyclerView.ViewHolder {

    public View view;
    public TextView txtTitle;
    public TextView txtValue;
    public TextView txtQuantity;
    public LinearLayout llBtnEdit;
    public LinearLayout llBtnDelete;



    public DFEItemHolder(@NonNull View itemView) {
        super(itemView);
        this.view = itemView;
        this.txtTitle = itemView.findViewById(R.id.txt_title);
        this.txtValue = itemView.findViewById(R.id.txt_value);
        this.txtQuantity = itemView.findViewById(R.id.txt_quantity);
        this.llBtnEdit = itemView.findViewById(R.id.ll_btn_edit);
        this.llBtnDelete = itemView.findViewById(R.id.ll_btn_delete);
    }
}