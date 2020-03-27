package co.uk.depotnet.onsa.formholders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import co.uk.depotnet.onsa.R;

public class NumberHolder extends RecyclerView.ViewHolder {
    public View view;
    public TextView txtTitle;
    public EditText editText;


    public NumberHolder(@NonNull View itemView) {
        super(itemView);
        this.view = itemView;
        this.txtTitle = itemView.findViewById(R.id.txt_title);
        this.editText = itemView.findViewById(R.id.et_number);
    }
}