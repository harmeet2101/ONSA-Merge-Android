package co.uk.depotnet.onsa.formholders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.adapters.AdapterPhoto;

public class PhotoHolder extends RecyclerView.ViewHolder {
    public View view;
    public ImageView imgBtnCamera;
    public TextView txtTitle;
    public RecyclerView recyclerView;
    public AdapterPhoto adapterPhoto;

    public PhotoHolder(@NonNull View itemView) {
        super(itemView);
        this.view = itemView;
        this.imgBtnCamera = itemView.findViewById(R.id.img_btn_camera);
        this.txtTitle = itemView.findViewById(R.id.txt_title);
        this.recyclerView = itemView.findViewById(R.id.recycler_view);
        this.recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 3) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }

            @Override
            public boolean canScrollVertically() {
                return false;
            }


        });


    }
}