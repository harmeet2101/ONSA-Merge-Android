package co.uk.depotnet.onsa.formholders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import co.uk.depotnet.onsa.R;

public class ForkHolder extends RecyclerView.ViewHolder {
    public View view;
    public Button btnFork;


    public ForkHolder(@NonNull View itemView) {
        super(itemView);
        this.view = itemView;
        this.btnFork = itemView.findViewById(R.id.btn_fork);
    }
}