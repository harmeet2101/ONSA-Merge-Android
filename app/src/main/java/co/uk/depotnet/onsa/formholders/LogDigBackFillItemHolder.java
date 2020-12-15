package co.uk.depotnet.onsa.formholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import co.uk.depotnet.onsa.R;

public class LogDigBackFillItemHolder extends RecyclerView.ViewHolder {
    public View view;
    public TextView txtSurfaceValue;
    public TextView txtMaterialValue;
    public TextView txtLength;
    public TextView txtWidth;
    public TextView txtDepth;
    public TextView txtComments;
    public ImageView imgBtnCheck;
    public LinearLayout llBtnEdit;
    public LinearLayout llBtnCheck;


    public LogDigBackFillItemHolder(@NonNull View itemView) {
        super(itemView);
        this.view = itemView;
        this.txtSurfaceValue = itemView.findViewById(R.id.txt_surface_value);
        this.txtMaterialValue = itemView.findViewById(R.id.txt_material_value);
        this.txtLength = itemView.findViewById(R.id.txt_length);
        this.txtWidth = itemView.findViewById(R.id.txt_width);
        this.txtDepth = itemView.findViewById(R.id.txt_depth);
        this.txtComments = itemView.findViewById(R.id.txt_comments);
        this.imgBtnCheck = itemView.findViewById(R.id.img_btn_check);
        this.llBtnEdit = itemView.findViewById(R.id.ll_btn_edit);
        this.llBtnCheck = itemView.findViewById(R.id.ll_btn_check);
    }
}