package co.uk.depotnet.onsa.formholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import co.uk.depotnet.onsa.R;

public class SiteClearHolder extends RecyclerView.ViewHolder {
    public View view;


    public TextView txtCones;
    public TextView txtBarriers;
    public TextView txtChpt8;
    public TextView txtFwBoards;
    public TextView txtBags;
    public TextView txtSand;
    public TextView txtStone;
    public ImageView imgBtnCheck;
    public LinearLayout llBtnEdit;
    public LinearLayout llBtnDelete;

    public SiteClearHolder(@NonNull View itemView) {
        super(itemView);
        this.view = itemView;

        this.txtCones = itemView.findViewById(R.id.txt_cones);
        this.txtBarriers = itemView.findViewById(R.id.txt_barriers);
        this.txtChpt8 = itemView.findViewById(R.id.txt_chpt8);
        this.txtFwBoards = itemView.findViewById(R.id.txt_fwBoards);
        this.txtBags = itemView.findViewById(R.id.txt_bags);
        this.txtSand = itemView.findViewById(R.id.txt_sand);
        this.txtStone = itemView.findViewById(R.id.txt_stone);

        this.imgBtnCheck = itemView.findViewById(R.id.img_btn_check);
        this.llBtnEdit = itemView.findViewById(R.id.ll_btn_edit);
        this.llBtnDelete = itemView.findViewById(R.id.ll_btn_delete);
    }
}