package co.uk.depotnet.onsa.formholders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import co.uk.depotnet.onsa.R;

public class LogMuckAwayItemHolder extends RecyclerView.ViewHolder {
    public View view;
    public TextView txtMaterialType;
    public TextView txtComment;
    public CheckBox checkBox;


    public LogMuckAwayItemHolder(@NonNull View itemView) {
        super(itemView);
        this.view = itemView;
        txtMaterialType = itemView.findViewById(R.id.txt_material_type);
        txtComment = itemView.findViewById(R.id.txt_comment);
        checkBox = itemView.findViewById(R.id.checkbox);
    }
}