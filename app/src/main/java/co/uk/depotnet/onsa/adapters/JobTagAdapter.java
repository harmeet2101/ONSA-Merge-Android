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

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.modals.ItemType;

public class JobTagAdapter extends RecyclerView.Adapter<JobTagAdapter.ViewHolder>{

    private Context context;
    private ArrayList<ItemType> items;
    private TagRemoveListener tagRemoveListener;

    public interface TagRemoveListener{
        void onTagRemoved(int position);
    }




    public JobTagAdapter(Context context , ArrayList<ItemType> items , TagRemoveListener tagRemoveListener){
        this.context = context;
        this.items = items;
        this.tagRemoveListener = tagRemoveListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_keyword , viewGroup , false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        ItemType item = items.get(position);

        holder.txtKeyword.setText(item.getDisplayItem());
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                items.remove(position);
                tagRemoveListener.onTagRemoved(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public void add(ArrayList<ItemType> items) {
        this.items.clear();
        this.items.addAll(items);
        this.notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtKeyword;
        private ImageView btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtKeyword = itemView.findViewById(R.id.txt_keyword);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
