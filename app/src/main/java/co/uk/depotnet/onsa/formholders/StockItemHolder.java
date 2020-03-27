package co.uk.depotnet.onsa.formholders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import co.uk.depotnet.onsa.R;

public class StockItemHolder extends RecyclerView.ViewHolder {
    public final View view;
    public final TextView txtDescription;

    public final TextView txtNumber;
    public final ImageView btnDelete;



    public StockItemHolder(@NonNull View itemView) {
        super(itemView);
        this.view = itemView;
        this.txtDescription = itemView.findViewById(R.id.txt_name);
        this.txtNumber = itemView.findViewById(R.id.txt_number);
        this.btnDelete = itemView.findViewById(R.id.btn_delete);
    }
}