package co.uk.depotnet.onsa.adapters.store;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import java.util.ArrayList;
import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.modals.store.ReceiptItems;
import co.uk.depotnet.onsa.modals.store.RequestItem;


public class AdapterRequestItems extends RecyclerView.Adapter<AdapterRequestItems.ViewHolder> {

    private final ArrayList<RequestItem> items;



    public AdapterRequestItems(ArrayList<RequestItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_request_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        RequestItem item = items.get(position);
        holder.txtItem.setText(item.getdescription());
        holder.txtUnit.setText(item.getunitName());
        holder.txt_number.setText(String.valueOf(item.getquantity()));


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        final TextView txtItem;
        final TextView txt_number;
        final TextView txtUnit;
        public ReceiptItems item;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            txtItem =  view.findViewById(R.id.txt_item);
            txt_number =  view.findViewById(R.id.txt_number);
            txtUnit =  view.findViewById(R.id.txt_unit);
        }
    }

    public void clearFocus(){

    }
}
