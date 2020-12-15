package co.uk.depotnet.onsa.formholders;

import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import co.uk.depotnet.onsa.R;

public class RFNAToggleHolder extends RecyclerView.ViewHolder {
    public View view;
    public RadioGroup radioGroup;
    public RadioButton radioButtonRFNA1;
    public RadioButton radioButtonRFNA2;


    public RFNAToggleHolder(@NonNull View itemView) {
        super(itemView);
        this.view = itemView;
        this.radioGroup = itemView.findViewById(R.id.radio_group);
        this.radioButtonRFNA1 = itemView.findViewById(R.id.radio_button_rfna1);
        this.radioButtonRFNA2 = itemView.findViewById(R.id.radio_button_rfna2);
    }
}