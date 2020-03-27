package co.uk.depotnet.onsa.formholders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SwitchCompat;
import android.view.View;
import android.widget.TextView;

import co.uk.depotnet.onsa.R;

public class SwitchHolder extends RecyclerView.ViewHolder {
    public View view;
    public TextView txtTitle;
    public SwitchCompat btnSwitch;


    public SwitchHolder(@NonNull View itemView) {
        super(itemView);
        this.view = itemView;
        this.txtTitle = itemView.findViewById(R.id.txt_title);
        this.btnSwitch = itemView.findViewById(R.id.btn_switch);
    }
}