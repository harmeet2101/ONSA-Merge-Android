package co.uk.depotnet.onsa.formholders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import co.uk.depotnet.onsa.R;

public class CurrentStoreHolder extends RecyclerView.ViewHolder {
    public final View view;
    public final TextView txtDescription;
    public final TextView txtUnit;
    public final TextView txtQuantity;
    public final TextView txtNumber;



    public CurrentStoreHolder(@NonNull View itemView) {
        super(itemView);
        this.view = itemView;
        this.txtDescription = itemView.findViewById(R.id.txt_description);
        this.txtUnit = itemView.findViewById(R.id.txt_unit);
        this.txtQuantity = itemView.findViewById(R.id.txt_quantity);
        this.txtNumber = itemView.findViewById(R.id.txt_number);

    }
}