package co.uk.depotnet.onsa.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.modals.ItemType;

public class ItemListAdapter extends
        RecyclerView.Adapter<ItemListAdapter.ViewHolder>{

    private Context context;
    private ArrayList<ItemType> items;
    private HashMap<String , Boolean> selectedMap;
    private boolean isMultiSelectionEnable;


    public ItemListAdapter(Context context , ArrayList<ItemType> items,
                           ArrayList<ItemType> selectedList , boolean isMultiSelectionEnable){

        this.context = context;
        this.items = new ArrayList<>();
        this.items.addAll(items);
        this.isMultiSelectionEnable = isMultiSelectionEnable;

        selectedMap = new HashMap<>();

        for(ItemType itemType : selectedList){
            selectedMap.put(itemType.getUploadValue() , true);
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_keyword_multiselector , viewGroup , false));
    }

    @Override
    public void onBindViewHolder(final @NonNull ViewHolder holder,
                                 final int position) {

        final ItemType itemType = items.get(position);

        String text = itemType.getDisplayItem();
        holder.txtKeyword.setText(text);

        if(toBool(selectedMap.get(itemType.getUploadValue()))){
            holder.imgSelected.setVisibility(View.VISIBLE);
        }else{
            holder.imgSelected.setVisibility(View.GONE);
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                boolean isSelected = toBool(selectedMap.get(itemType.getUploadValue()));


                selectedMap.put(itemType.getUploadValue() , !isSelected);
                isSelected = !isSelected;


                if(isSelected){
                    holder.imgSelected.setVisibility(View.VISIBLE);
                }else{
                    holder.imgSelected.setVisibility(View.GONE);
                }
                notifyItemChanged(position);

            }
        });
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private TextView txtKeyword;
        private ImageView imgSelected;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            txtKeyword = itemView.findViewById(R.id.txt_keyword);
            imgSelected = itemView.findViewById(R.id.img_selected);
        }
    }

    public ArrayList<ItemType> getSelectedKeywords(){
        ArrayList<ItemType> keywords = new ArrayList<>();
        for (ItemType keyword : this.items){
            if(toBool(selectedMap.get(keyword.getUploadValue()))) {
                keywords.add(keyword);
            }
        }

        return keywords;
    }


    private boolean toBool(Boolean value){
        if(value == null){
            return false;
        }

        return value;
    }

    public void update(ArrayList<ItemType> keywords){
        this.items.clear();
        this.items.addAll(keywords);
        notifyDataSetChanged();
    }
}
