package co.uk.depotnet.onsa.formholders;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import co.uk.depotnet.onsa.R;

public class LogMuckAwayHolder extends RecyclerView.ViewHolder {
    public View view;
    public TextView txtMaterialName;
    public TextView txtComment;
    public CheckBox checkBox;

    public LogMuckAwayHolder(@NonNull View itemView) {
        super(itemView);
        this.view = itemView;
        this.txtMaterialName = itemView.findViewById(R.id.txt_material_type);
        this.txtComment = itemView.findViewById(R.id.txt_comment);
        this.checkBox = itemView.findViewById(R.id.checkbox);
    }
}