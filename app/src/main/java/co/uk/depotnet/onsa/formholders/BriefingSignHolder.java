package co.uk.depotnet.onsa.formholders;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import co.uk.depotnet.onsa.R;


public class BriefingSignHolder extends RecyclerView.ViewHolder {
    public View view;
    public TextView txtTitle;
    public ImageButton imgButton;
    public BriefingSignHolder(@NonNull View itemView) {
        super(itemView);
        view=itemView;
        txtTitle=view.findViewById(R.id.briefing_signs_operative);
        imgButton=view.findViewById(R.id.briefing_signs_delete);
    }
}
