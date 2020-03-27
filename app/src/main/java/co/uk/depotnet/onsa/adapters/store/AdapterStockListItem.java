package co.uk.depotnet.onsa.adapters.store;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.modals.store.StockItems;

public class AdapterStockListItem extends
        RecyclerView.Adapter<AdapterStockListItem.ViewHolder> {

    private Context context;
    private ArrayList<StockItems> items;
    private HashMap<String, String> selectedList;
    private StockItems lastSelected;
    private EditText focusedEditText;

    private boolean isMultiSelection;


    public AdapterStockListItem(Context context, ArrayList<StockItems> items,
                                HashMap<String, String> selectedList,
                                boolean isMultiSelection) {
        this.context = context;
        this.items = new ArrayList<>();
        this.items.addAll(items);
        this.selectedList = selectedList;
        this.isMultiSelection = isMultiSelection;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_store_list, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(final @NonNull ViewHolder holder,
                                 final int position) {

        final StockItems stockItems = items.get(position);
        if(!TextUtils.isEmpty(stockItems.getdescription())){
            holder.txtDescription.setVisibility(View.VISIBLE);
            holder.txtDescription.setText(stockItems.getdescription());
        }else{
            holder.txtDescription.setVisibility(View.GONE);
        }
        if(!TextUtils.isEmpty(stockItems.getaltName())){
            holder.txtAltName.setVisibility(View.VISIBLE);
            holder.txtAltName.setText(String.format("Alt Name: %s", stockItems.getaltName()));
        }else{
            holder.txtAltName.setVisibility(View.GONE);
        }
        if(!TextUtils.isEmpty(stockItems.getReference())){
            holder.txtReferenceNumber.setVisibility(View.VISIBLE);
            holder.txtReferenceNumber.setText(String.format("Reference: %s", stockItems.getReference()));
        }else{
            holder.txtReferenceNumber.setVisibility(View.GONE);
        }

        holder.txtQuantity.setText(String.format("Quantity: %s", stockItems.getStockLevelUnit()));


        boolean isSelected = selectedList.containsKey(stockItems.getStaStockItemId());

        holder.imgSelected.setSelected(isSelected);
        if(isSelected){
            holder.editText.setText(selectedList.get(stockItems.getStaStockItemId()));
            holder.editText.setVisibility(View.VISIBLE);
        }else{
            holder.editText.setText("");
            holder.editText.setVisibility(View.GONE);
        }

        holder.view.setOnClickListener(v -> {

            if(focusedEditText != null){
                focusedEditText.clearFocus();
            }

            if(!isMultiSelection){
                if(lastSelected != null){
                    selectedList.remove(lastSelected.getStaStockItemId());
                    selectedList.put(stockItems.getStaStockItemId(), "0");
                }


                lastSelected = stockItems;
                notifyDataSetChanged();
            }else {
                if (selectedList.containsKey(stockItems.getStaStockItemId())) {
                    selectedList.remove(stockItems.getStaStockItemId());
                } else {
                    selectedList.put(stockItems.getStaStockItemId(), "0");
                }
                holder.imgSelected.setSelected(selectedList.containsKey(stockItems.getstockItemId()));
                notifyItemChanged(position);
            }
        });

        holder.editText.setOnFocusChangeListener((view, hasFocus) -> {
            EditText et = (EditText) view;
            if (!hasFocus) {
                selectedList.put(stockItems.getStaStockItemId() ,et.getText().toString());
                focusedEditText = null;
            } else {
                focusedEditText = et;
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public HashMap<String, String> getSelectedKeywords() {
        return selectedList;
    }



    public void update(ArrayList<StockItems> items) {
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private TextView txtDescription;
        private TextView txtAltName;
        private TextView txtReferenceNumber;
        private TextView txtQuantity;

        private ImageView imgSelected;
        private EditText editText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            txtDescription = itemView.findViewById(R.id.txt_description);
            txtAltName = itemView.findViewById(R.id.txt_alt_name);
            txtReferenceNumber = itemView.findViewById(R.id.txt_reference_number);
            txtQuantity = itemView.findViewById(R.id.txt_quantity);
            imgSelected = itemView.findViewById(R.id.img_selected);
            editText = itemView.findViewById(R.id.et_number);
            editText.setFocusableInTouchMode(true);
        }
    }

    public EditText getFocusedEditText() {
        return focusedEditText;
    }
}
