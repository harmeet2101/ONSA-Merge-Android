package co.uk.depotnet.onsa.adapters.store;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.listeners.OnItemClickListener;
import co.uk.depotnet.onsa.modals.store.MyStore;
import co.uk.depotnet.onsa.modals.store.MyStoreFav;

public class AdapterStore extends RecyclerView.Adapter<AdapterStore.ViewHolder> {

    private Context context;
    private ArrayList<MyStore> originalItems;
    private ArrayList<MyStore> items;
    private OnItemClickListener<MyStore> listener;
    private int whiteColor;
    private int greyColor;
    private boolean isMultiSelectionEnabled;
    private CurrentStoreListener currentStoreListener;
    private HashMap<String , Object> selectedItems;
    private EditText focusedEditText;

    public void selectAll() {
        if(isMultiSelectionEnabled){
            for(MyStore store : items){
                selectedItems.put(store.getStaStockItemId() , store);
                selectedItems.put(store.getStaStockItemId()+"_qty" , selectedItems.get(store.getstockItemId()+"_qty"));
            }
            notifyDataSetChanged();
        }
    }

    public boolean isMultiSelectionEnabled() {
        return isMultiSelectionEnabled;
    }

    public void cancelMultiSelect() {

            for(MyStore store : items){
                selectedItems.remove(store.getStaStockItemId());
                selectedItems.remove(store.getStaStockItemId()+"_qty");
            }

            isMultiSelectionEnabled = false;
            currentStoreListener.onMultiSelectionEnabled(false);

            notifyDataSetChanged();

    }

    public HashMap<String, Object> getSelectedItems() {
        return selectedItems;
    }

    public void onSearch(String query) {
        if(TextUtils.isEmpty(query)){
            return;
        }

        query = query.toLowerCase();
        items.clear();
        for (int i = 0 ; i < originalItems.size() ; i++){
            MyStore store = originalItems.get(i);
            String barcode = store.getbarcode();
            String name = store.getdescription();
            String altName = store.getaltName();
            String reference = store.getBatchRef();

            if(!TextUtils.isEmpty(barcode) && barcode.toLowerCase().contains(query) ||
                    !TextUtils.isEmpty(name) && name.toLowerCase().contains(query) ||
                    !TextUtils.isEmpty(altName) && altName.toLowerCase().contains(query) ||
                    !TextUtils.isEmpty(reference) && reference.toLowerCase().contains(query)){
                items.add(store);
                break;
            }
        }

        notifyDataSetChanged();

    }

    public interface CurrentStoreListener{
        void onMultiSelectionEnabled(boolean isEnabled);
    }


    public AdapterStore(Context context, ArrayList<MyStore> items,
                        OnItemClickListener<MyStore> listener,
                        CurrentStoreListener currentStoreListener) {
        this.context = context;
        this.originalItems = new ArrayList<>();
        this.originalItems.addAll(items);
        this.items = new ArrayList<>();
        this.items.addAll(originalItems);
        this.listener = listener;
        this.isMultiSelectionEnabled = false;

        this.currentStoreListener = currentStoreListener;
        this.whiteColor = ContextCompat.getColor(context, R.color.white);
        this.greyColor = ContextCompat.getColor(context, R.color.item_bg_light_gray);
        this.selectedItems = new HashMap<>();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        return new ViewHolder(LayoutInflater.from(context).
                inflate(R.layout.item_current_store, viewGroup,
                        false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final MyStore item = items.get(position);
        holder.view.setBackgroundColor(position % 2 == 0 ? whiteColor : greyColor);

        if(!TextUtils.isEmpty(item.getdescription())){
            holder.txtDescription.setVisibility(View.VISIBLE);
            holder.txtDescription.setText(item.getdescription());
        }else{
            holder.txtDescription.setVisibility(View.GONE);
        }
        if(!TextUtils.isEmpty(item.getaltName())){
            holder.txtAltName.setVisibility(View.VISIBLE);
            holder.txtAltName.setText(String.format("Alt Name: %s", item.getaltName()));
        }else{
            holder.txtAltName.setVisibility(View.GONE);
        }
        if(!TextUtils.isEmpty(item.getstockTypeName())){
            holder.txtStockTypeName.setVisibility(View.VISIBLE);
            holder.txtStockTypeName.setText(String.format("Stock Type Name: %s", item.getstockTypeName()));
        }else{
            holder.txtStockTypeName.setVisibility(View.GONE);
        }
        if(!TextUtils.isEmpty(item.getwarehouseStaName())){
            holder.txtWarehouseStaName.setVisibility(View.VISIBLE);
            holder.txtWarehouseStaName.setText(String.format("Warehouse Sta Name: %s", item.getwarehouseStaName()));
        }else{
            holder.txtWarehouseStaName.setVisibility(View.GONE);
        }
        if(!TextUtils.isEmpty(item.getunitName())){
            holder.txtUnitName.setVisibility(View.VISIBLE);
            holder.txtUnitName.setText(String.format("Unit Name: %s", item.getunitName()));
        }else{
            holder.txtUnitName.setVisibility(View.GONE);
        }

        if(!TextUtils.isEmpty(item.getpackagingName())){
            holder.txtPackageName.setVisibility(View.VISIBLE);
            holder.txtPackageName.setText(String.format("Packaging Name: %s", item.getpackagingName()));
        }else{
            holder.txtPackageName.setVisibility(View.GONE);
        }
        if(!TextUtils.isEmpty(item.getbarcode())){
            holder.txtBarcode.setVisibility(View.VISIBLE);
            holder.txtBarcode.setText(String.format("Barcode: %s", item.getbarcode()));
        }else{
            holder.txtBarcode.setVisibility(View.GONE);
        }
        holder.txtQuantity.setText(String.format("QTY: %s", String.valueOf(item.getquantity())));

        if(!TextUtils.isEmpty(item.getBatchRef())){
            holder.txtBatchRef.setVisibility(View.VISIBLE);
            holder.txtBatchRef.setText(String.format("BatchRef: %s", item.getBatchRef()));
        }else{
            holder.txtBatchRef.setVisibility(View.GONE);
        }

        if(!TextUtils.isEmpty(item.getReference())){
            holder.txtRef.setVisibility(View.VISIBLE);
            holder.txtRef.setText(String.format("Reference: %s", item.getReference()));
        }else{
            holder.txtRef.setVisibility(View.GONE);
        }

        final boolean isFavorite = DBHandler.getInstance().isMyStoreFav(item.getStaStockItemId());

        boolean isSelected = selectedItems.containsKey(item.getStaStockItemId());

        if(isSelected) {
            Integer qty = (Integer) selectedItems.get(item.getStaStockItemId() + "_qty");

            String quantity = qty == null ? null : String.valueOf(qty);
            if (!TextUtils.isEmpty(quantity)) {
                holder.etNumber.setText(quantity);
            }else{
                holder.etNumber.setText("");
            }
        }else{
            holder.etNumber.setText("");
        }

        if (isMultiSelectionEnabled) {
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setSelected(isSelected);
            holder.imgBtnFav.setVisibility(View.GONE);
            holder.etNumber.setVisibility(isSelected ? View.VISIBLE : View.GONE);
        } else {
            holder.checkBox.setVisibility(View.GONE);
            holder.etNumber.setVisibility(View.GONE);
            holder.imgBtnFav.setVisibility(View.VISIBLE);
            holder.imgBtnFav.setSelected(isFavorite);
        }


        holder.etNumber.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                EditText et = (EditText) view;

                    if(selectedItems.containsKey(item.getStaStockItemId())){
                        int value = 0;
                        try{
                            if(!TextUtils.isEmpty(et.getText().toString())) {
                                value = (Integer.parseInt(et.getText().toString()));
                            }
                        }catch (Exception e){

                        }
                        selectedItems.put(item.getStaStockItemId()+"_qty" , value);
                    }

                focusedEditText = null;
            } else {
                focusedEditText = (EditText) view;
            }
        });



        holder.view.setOnClickListener(view -> {
            if (!isMultiSelectionEnabled) {
                listener.onItemClick(item, holder.getAdapterPosition());
                return;
            }

            if(!selectedItems.containsKey(item.getStaStockItemId())) {
                holder.checkBox.setSelected(true);
                selectedItems.put(item.getStaStockItemId(), item);
            }else{
                holder.checkBox.setSelected(false);
                selectedItems.remove(item.getStaStockItemId());
            }

            if(focusedEditText != null){
                focusedEditText.clearFocus();
            }

            notifyItemChanged(holder.getAdapterPosition());

        });


        holder.view.setOnLongClickListener(v -> {
            if(!isMultiSelectionEnabled) {
                isMultiSelectionEnabled = true;
                currentStoreListener.onMultiSelectionEnabled(true);
                notifyDataSetChanged();
            }
            return false;
        });

        holder.imgBtnFav.setOnClickListener(v -> {
            if(isFavorite) {
                DBHandler.getInstance().deleteMyStoreFav(item.getStaStockItemId());
            }else{
                DBHandler.getInstance().insertData(MyStoreFav.DBTable.NAME , new MyStoreFav(item.getStaStockItemId() , true).toContentValues());
            }
            notifyItemChanged(holder.getAdapterPosition());

        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final View view;
        private final TextView txtDescription;
        private final TextView txtAltName;
        private final TextView txtStockTypeName;
        private final TextView txtWarehouseStaName;
        private final TextView txtPackageName;
        private final TextView txtUnitName;
        private final TextView txtBarcode;
        private final TextView txtQuantity;
        private final TextView txtBatchRef;
        private final TextView txtRef;
        private final ImageView checkBox;
        private final EditText etNumber;
        private final ImageView imgBtnFav;


        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            this.txtDescription = itemView.findViewById(R.id.txt_description);
            this.txtAltName = itemView.findViewById(R.id.txt_alt_name);
            this.txtStockTypeName = itemView.findViewById(R.id.txt_stock_type_name);
            this.txtWarehouseStaName = itemView.findViewById(R.id.txt_warehouse_sta_name);
            this.txtPackageName = itemView.findViewById(R.id.txt_package_name);
            this.txtUnitName = itemView.findViewById(R.id.txt_unit_name);
            this.txtBarcode = itemView.findViewById(R.id.txt_barcode);
            this.txtQuantity = itemView.findViewById(R.id.txt_quantity);
            this.txtBatchRef = itemView.findViewById(R.id.txt_batch_ref);
            this.txtRef = itemView.findViewById(R.id.txt_ref);
            this.checkBox = itemView.findViewById(R.id.img_btn_check);
            this.imgBtnFav = itemView.findViewById(R.id.img_btn_fav);
            this.etNumber = itemView.findViewById(R.id.et_number);
        }
    }

    public void clearFocus(){
        if(focusedEditText != null){
            focusedEditText.clearFocus();
        }
    }

    public void filterByFav(){
        this.items.clear();
        for(MyStore store : originalItems){
            boolean isFavorite = DBHandler.getInstance().isMyStoreFav(store.getStaStockItemId());
            if(isFavorite) {
                this.items.add(store);
            }
        }

        if(items.isEmpty()){
            Toast.makeText(context , "You have no favorite item." , Toast.LENGTH_SHORT).show();
//            items.addAll(originalItems);
        }

        notifyDataSetChanged();
    }

    public void filterBySta(String staId){
        if(TextUtils.isEmpty(staId)){
            return;
        }
        this.items.clear();
        for(MyStore store : originalItems){
            if(!TextUtils.isEmpty(store.getstaId()) &&
                    store.getstaId().equalsIgnoreCase(staId)) {
                this.items.add(store);
            }
        }

        if(items.isEmpty()){
            Toast.makeText(context , "No Store found with this batch." , Toast.LENGTH_SHORT).show();
            items.addAll(originalItems);
        }

        notifyDataSetChanged();
    }

    public void filterByBatch(String batchRef){
        if(TextUtils.isEmpty(batchRef)){
            return;
        }
        this.items.clear();
        for(MyStore store : originalItems){
            if(!TextUtils.isEmpty(store.getBatchRef()) &&
                    store.getBatchRef().equalsIgnoreCase(batchRef)) {
                this.items.add(store);
            }
        }

        if(items.isEmpty()){
            Toast.makeText(context , "No Store found with this batch." , Toast.LENGTH_SHORT).show();
            items.addAll(originalItems);
        }

        notifyDataSetChanged();
    }

    public void resetSearch(){
        this.items.clear();
        this.items.addAll(originalItems);
        notifyDataSetChanged();
    }

    public void reset(ArrayList<MyStore> stores){
        this.items.clear();
        this.originalItems.clear();
        this.originalItems.addAll(stores);
        this.items.addAll(originalItems);
        notifyDataSetChanged();
    }
}
