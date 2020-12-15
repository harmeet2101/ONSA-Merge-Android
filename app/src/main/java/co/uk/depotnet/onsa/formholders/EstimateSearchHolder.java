package co.uk.depotnet.onsa.formholders;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import co.uk.depotnet.onsa.R;

public class EstimateSearchHolder extends RecyclerView.ViewHolder {
    public View view;
    public TextView txtTitle;
    public EditText editText;
    public ImageView btn_estimate_search;

    public EstimateSearchHolder(@NonNull View itemView) {
        super(itemView);
        this.view = itemView;
        this.txtTitle = itemView.findViewById(R.id.txt_estimate_title);
        this.editText = itemView.findViewById(R.id.et_estimate);
        this.btn_estimate_search = itemView.findViewById(R.id.btn_estimate_search);
    }
}
