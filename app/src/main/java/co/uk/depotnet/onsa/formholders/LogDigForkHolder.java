package co.uk.depotnet.onsa.formholders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.utils.VerticalSpaceItemDecoration;

public class LogDigForkHolder extends RecyclerView.ViewHolder {
    public View view;
    public TextView txtTitle;
    public LinearLayout llRecycleContainer;
    public LinearLayout llAddContainer;
    public Button btnAddItem;
    public RecyclerView recyclerView;


    public LogDigForkHolder(@NonNull View itemView) {
        super(itemView);
        this.view = itemView;
        this.txtTitle = itemView.findViewById(R.id.txt_title);
        this.llRecycleContainer = itemView.findViewById(R.id.ll_recycle_container);
        this.llAddContainer = itemView.findViewById(R.id.ll_add_container);
        this.btnAddItem = itemView.findViewById(R.id.btn_add_item);
        this.recyclerView = itemView.findViewById(R.id.recycler_view);
        VerticalSpaceItemDecoration itemDecoration = new VerticalSpaceItemDecoration(20);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }

            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        });
    }
}