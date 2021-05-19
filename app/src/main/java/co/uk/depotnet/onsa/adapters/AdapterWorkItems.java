package co.uk.depotnet.onsa.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.modals.JobWorkItem;

import java.util.List;

public class AdapterWorkItems extends RecyclerView.Adapter<AdapterWorkItems.ViewHolder> {

    private Context context;
    private List<JobWorkItem> workItems;

    public AdapterWorkItems(Context context ,
                            List<JobWorkItem> workItems){
        this.context = context;
        this.workItems = workItems;
    }

    @NonNull
    @Override
    public AdapterWorkItems.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_work_item , viewGroup , false) );
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterWorkItems.ViewHolder viewHolder, int position) {
        if(position%2 == 0){
            viewHolder.view.setBackgroundColor(ContextCompat.getColor(context , R.color.white));
        }else{
            viewHolder.view.setBackgroundColor(ContextCompat.getColor(context , R.color.item_bg_light_gray));
        }
        JobWorkItem item = workItems.get(position);
        viewHolder.txtItemCode.setText("Item Code: "+item.getitemCode());
        viewHolder.txtDesc.setText("Description: "+item.getdescription());
        viewHolder.txtUnit.setText("Unit: "+String.valueOf(item.getunitType()));
        viewHolder.txtQuantity.setText("Quantity: "+String.valueOf(item.getAvailableToMeasureQuantity()));
    }

    @Override
    public int getItemCount() {
        return workItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        final View view;
        final TextView txtItemCode;
        final TextView txtDesc;
        final TextView txtUnit;
        final TextView txtQuantity;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            this.txtItemCode = itemView.findViewById(R.id.txt_item_code);
            this.txtDesc = itemView.findViewById(R.id.txt_desc);
            this.txtUnit = itemView.findViewById(R.id.txt_unit);
            this.txtQuantity = itemView.findViewById(R.id.txt_quantity);
        }
    }
}
