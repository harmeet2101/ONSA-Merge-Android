package co.uk.depotnet.onsa.adapters.store;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.modals.store.ReceiptItems;


public class AdapterReceiptItems extends RecyclerView.Adapter<AdapterReceiptItems.ViewHolder> {

    private final ArrayList<ReceiptItems> items;
    private EditText focusedEditText;

    public AdapterReceiptItems(ArrayList<ReceiptItems> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_receipt_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = items.get(position);

        if(!TextUtils.isEmpty(holder.item.getname())){
            holder.txtName.setVisibility(View.VISIBLE);
            holder.txtName.setText(holder.item.getname());
        }else{
            holder.txtName.setVisibility(View.GONE);
        }

        if(!TextUtils.isEmpty(holder.item.getaltName())){
            holder.txtAltName.setVisibility(View.VISIBLE);
            holder.txtAltName.setText(String.format("Alt Name: %s", holder.item.getaltName()));
        }else{
            holder.txtAltName.setVisibility(View.GONE);
        }

        if(!TextUtils.isEmpty(holder.item.getunitName())){
            holder.txtUnitName.setVisibility(View.VISIBLE);
            holder.txtUnitName.setText(String.format("Unit Name: %s", holder.item.getunitName()));
        }else{
            holder.txtUnitName.setVisibility(View.GONE);
        }

        if(!TextUtils.isEmpty(holder.item.getpackagingName())){
            holder.txtPackagingName.setVisibility(View.VISIBLE);
            holder.txtPackagingName.setText(String.format("Packaging Name: %s", holder.item.getpackagingName()));
        }else{
            holder.txtPackagingName.setVisibility(View.GONE);
        }

        if(!TextUtils.isEmpty(holder.item.getreferenceNumber())){
            holder.txtReferenceNumber.setVisibility(View.VISIBLE);
            holder.txtReferenceNumber.setText(String.format("Reference: %s", holder.item.getreferenceNumber()));
        }else{
            holder.txtReferenceNumber.setVisibility(View.GONE);
        }

        holder.etNumber.setText(String.valueOf(holder.item.getquantity()));

        holder.etNumber.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                EditText et = (EditText) view;
                try {
                    holder.item.setquantity(Integer.parseInt(et.getText().toString()));
                }catch (Exception e){
                    et.setText(String.valueOf(holder.item.getquantity()));
                }
                focusedEditText = null;
            } else {
                focusedEditText = (EditText) view;
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        final TextView txtName;
        final TextView txtAltName;
        final TextView txtUnitName;
        final TextView txtPackagingName;
        final TextView txtReferenceNumber;

        final EditText etNumber;

        public ReceiptItems item;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            txtName =  view.findViewById(R.id.txt_name);
            txtAltName =  view.findViewById(R.id.txt_alt_name);
            txtUnitName =  view.findViewById(R.id.txt_unit_name);
            txtPackagingName =  view.findViewById(R.id.txt_packaging_name);
            txtReferenceNumber =  view.findViewById(R.id.txt_reference_number);
            etNumber =  view.findViewById(R.id.et_number);
        }
    }

    public void clearFocus(){
        if(focusedEditText != null){
            focusedEditText.clearFocus();
        }
    }
}
