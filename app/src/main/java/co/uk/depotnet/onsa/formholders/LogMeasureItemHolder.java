package co.uk.depotnet.onsa.formholders;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import co.uk.depotnet.onsa.R;

public class LogMeasureItemHolder extends RecyclerView.ViewHolder {

    public TextView txtWorkTitle;
    public TextView txtSurfaceValue;
    public TextView txtMaterialValue;
    public TextView txtLength;
    public TextView txtWidth;
    public TextView txtDepth;
    public TextView txtQuantity;

    public LinearLayout llBtnEdit;
    public LinearLayout llBtnDelete;
    public LinearLayout llTaskData;

    public LogMeasureItemHolder(@NonNull View itemView) {
        super(itemView);
        txtWorkTitle = itemView.findViewById(R.id.txt_work_title);
        txtSurfaceValue = itemView.findViewById(R.id.txt_surface_value);
        txtMaterialValue = itemView.findViewById(R.id.txt_material_value);
        txtLength = itemView.findViewById(R.id.txt_length);
        txtWidth = itemView.findViewById(R.id.txt_width);
        txtDepth = itemView.findViewById(R.id.txt_depth);
        txtQuantity = itemView.findViewById(R.id.txt_quantity);
        llBtnEdit = itemView.findViewById(R.id.ll_btn_edit);
        llBtnDelete = itemView.findViewById(R.id.ll_btn_delete);
        llTaskData = itemView.findViewById(R.id.ll_task_data);
    }
}