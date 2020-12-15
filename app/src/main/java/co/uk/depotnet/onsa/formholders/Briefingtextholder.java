package co.uk.depotnet.onsa.formholders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import co.uk.depotnet.onsa.R;

public class Briefingtextholder extends RecyclerView.ViewHolder {
    public View view;
    public TextView textView;
    public Briefingtextholder(@NonNull View itemView) {
        super(itemView);
        view=itemView;
        textView=itemView.findViewById(R.id.read_title);
    }
}
