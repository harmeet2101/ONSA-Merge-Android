package co.uk.depotnet.onsa.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.modals.responses.DatasetResponse;

public class AdapterList extends
        RecyclerView.Adapter<AdapterList.ViewHolder> {

    private Context context;
    private ArrayList<HashMap<String, String>> items;
    private HashMap<String, String> lastSelected;

    private boolean isMultiSelection;


    public AdapterList(Context context, ArrayList<HashMap<String, String>> items,
                       ArrayList<String> selectedList, boolean isMultiSelection) {

        this.context = context;
        this.items = new ArrayList<>();
        this.items.addAll(items);
        this.isMultiSelection = isMultiSelection;



        for (HashMap<String, String> item : this.items) {
            String itemValue = item.get("value");
            for (String value : selectedList) {
                if (!TextUtils.isEmpty(itemValue) && itemValue.equalsIgnoreCase(value)) {
                    item.put("selected", "true");
                    lastSelected = item;
                    break;
                }else{
                    item.remove("selected");
                }
            }
        }

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_list, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(final @NonNull ViewHolder holder,
                                 final int position) {

        final HashMap<String, String> itemType = items.get(position);
        String type = itemType.get("type");
        String text = itemType.get("value");
        if(!TextUtils.isEmpty(type) && (type.equalsIgnoreCase(DatasetResponse.DBTable.dfeWorkItems)) ) {
            holder.txtItemId.setText(text);
        }else{
            holder.txtItemId.setText("");
        }
        holder.txtItemDescription.setText(itemType.get("text"));

        holder.imgSelected.setSelected(itemType.containsKey("selected"));

        holder.view.setOnClickListener(v -> {

            if(!isMultiSelection){
                if(lastSelected != null){
                    lastSelected.remove("selected");
                }

                itemType.put("selected", "true");
                lastSelected = itemType;
                notifyDataSetChanged();
            }else {
                if (itemType.containsKey("selected")) {
                    itemType.remove("selected");
                } else {
                    itemType.put("selected", "true");
                }
                holder.imgSelected.setSelected(itemType.containsKey("selected"));
                notifyItemChanged(position);
            }




        });
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public ArrayList<HashMap<String, String>> getSelectedKeywords() {
        ArrayList<HashMap<String, String>> keywords = new ArrayList<>();
        for (HashMap<String, String> keyword : this.items) {
            if (keyword.containsKey("selected")) {
                keywords.add(keyword);
            }
        }
        return keywords;
    }

    private boolean toBool(Boolean value) {
        if (value == null) {
            return false;
        }

        return value;
    }

    public void update(ArrayList<HashMap<String, String>> keywords) {
        this.items.clear();
        this.items.addAll(keywords);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private TextView txtItemId;
        private TextView txtItemDescription;
        private ImageView imgSelected;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            txtItemId = itemView.findViewById(R.id.txt_item_id);
            txtItemDescription = itemView.findViewById(R.id.txt_item_description);
            imgSelected = itemView.findViewById(R.id.img_selected);
        }
    }
}
