package co.uk.depotnet.onsa.formholders;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import co.uk.depotnet.onsa.R;

public class DigMeasureItemHolder extends RecyclerView.ViewHolder {


        public TextView txtSurfaceValue;
        public TextView txtMaterialValue;
        public TextView txtLength;
        public TextView txtWidth;
        public TextView txtDepth;
        public LinearLayout llBtnEdit;
        public LinearLayout llBtnDelete;

        public DigMeasureItemHolder(@NonNull View itemView) {
            super(itemView);
            txtSurfaceValue = itemView.findViewById(R.id.txt_surface_value);
            txtMaterialValue = itemView.findViewById(R.id.txt_material_value);
            txtLength = itemView.findViewById(R.id.txt_length);
            txtWidth = itemView.findViewById(R.id.txt_width);
            txtDepth = itemView.findViewById(R.id.txt_depth);
            llBtnEdit = itemView.findViewById(R.id.ll_btn_edit);
            llBtnDelete = itemView.findViewById(R.id.ll_btn_delete);

        }
    }