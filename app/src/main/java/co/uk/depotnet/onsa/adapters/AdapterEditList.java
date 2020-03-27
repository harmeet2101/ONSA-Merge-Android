package co.uk.depotnet.onsa.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import co.uk.depotnet.onsa.R;

public class AdapterEditList extends
        RecyclerView.Adapter<AdapterEditList.ViewHolder> {

    private Context context;
    private ArrayList<HashMap<String, String>> items;
    private HashMap<String, String> lastSelected;
    private EditText focusedEditText;

    private boolean isMultiSelection;


    public AdapterEditList(Context context, ArrayList<HashMap<String, String>> items,
                           HashMap<String, String> selectedList,
                           boolean isMultiSelection) {

        this.context = context;
        this.items = new ArrayList<>();
        this.items.addAll(items);
        this.isMultiSelection = isMultiSelection;

        for (HashMap<String, String> item : this.items) {
            String itemValue = item.get("value");
            if(selectedList.containsKey(itemValue)){
                item.put("selected", "true");
                lastSelected = item;
            }else{
                item.remove("selected");
            }
        }

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_list_edit, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(final @NonNull ViewHolder holder,
                                 final int position) {

        final HashMap<String, String> itemType = items.get(position);
        String type = itemType.get("type");
        String text = itemType.get("value");

        holder.txtItemId.setText("");

        holder.txtItemDescription.setText(itemType.get("text"));

        holder.imgSelected.setSelected(itemType.containsKey("selected"));
        if(itemType.containsKey("selected")){
            holder.editText.setText(itemType.get("quantity"));
            holder.editText.setVisibility(View.VISIBLE);
        }else{
            holder.editText.setText("");
            holder.editText.setVisibility(View.GONE);
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(focusedEditText != null){
                    focusedEditText.clearFocus();
                }

                if(!isMultiSelection){
                    if(lastSelected != null){
                        lastSelected.remove("selected");
                        itemType.remove("quantity");
                    }

                    itemType.put("selected", "true");
                    lastSelected = itemType;
                    notifyDataSetChanged();
                }else {
                    if (itemType.containsKey("selected")) {
                        itemType.remove("selected");
                        itemType.remove("quantity");
                    } else {
                        itemType.put("selected", "true");
                    }
                    holder.imgSelected.setSelected(itemType.containsKey("selected"));
                    notifyItemChanged(position);
                }
            }
        });

        holder.editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                EditText et = (EditText) view;
                if (!hasFocus) {
                    itemType.put("quantity" ,et.getText().toString());
                    focusedEditText = null;
                } else {
                    focusedEditText = et;
                }
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
        private EditText editText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            txtItemId = itemView.findViewById(R.id.txt_item_id);
            txtItemDescription = itemView.findViewById(R.id.txt_item_description);
            imgSelected = itemView.findViewById(R.id.img_selected);
            editText = itemView.findViewById(R.id.et_number);
            editText.setFocusableInTouchMode(true);
        }
    }

    public EditText getFocusedEditText() {
        return focusedEditText;
    }
}
