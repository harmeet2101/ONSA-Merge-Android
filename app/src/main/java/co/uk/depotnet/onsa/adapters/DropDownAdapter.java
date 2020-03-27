package co.uk.depotnet.onsa.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.listeners.DropDownItem;
import co.uk.depotnet.onsa.listeners.OnItemClickListener;

import java.util.ArrayList;

public class DropDownAdapter extends RecyclerView.Adapter<DropDownAdapter.ViewHolder> {

    private Context context;
    private ArrayList<DropDownItem> items;
    private OnItemClickListener<DropDownItem> listener;
    private boolean isFilterBatch;


    public interface OnItemSelectedListener{
        void onItemSelected(int position);
    }


    public DropDownAdapter(Context context , ArrayList<DropDownItem> items ,
                           OnItemClickListener<DropDownItem> listener){
        this.context = context;
        this.items = items;
        this.listener = listener;

    }

    public void setFilterBatch(boolean filterBatch) {
        isFilterBatch = filterBatch;
    }

    public boolean isFilterBatch() {
        return isFilterBatch;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_popup_dropdown , viewGroup , false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final @NonNull ViewHolder viewHolder, int position) {
        DropDownItem dropDownItem = items.get(position);
        viewHolder.txtValue.setText(dropDownItem.getDisplayItem());
        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DropDownItem dropDownItem = items.get(viewHolder.getAdapterPosition());
                listener.onItemClick(dropDownItem , viewHolder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView txtValue;
        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            this.txtValue = itemView.findViewById(R.id.txt_value);
        }
    }

    public void updateData(ArrayList<DropDownItem> items){
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();;
    }
}
