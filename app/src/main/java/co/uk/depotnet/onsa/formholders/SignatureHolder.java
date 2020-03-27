package co.uk.depotnet.onsa.formholders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import co.uk.depotnet.onsa.R;

public class SignatureHolder extends RecyclerView.ViewHolder {
    public View view;
    public TextView txtTitle;
    public ImageView imgSignature;
    public ImageView btnClear;


    public SignatureHolder(@NonNull View itemView) {
        super(itemView);
        this.view = itemView;
        this.txtTitle = itemView.findViewById(R.id.txt_title);
        this.imgSignature = itemView.findViewById(R.id.signature_pad);
        this.btnClear = itemView.findViewById(R.id.img_btn_clear);
    }
}