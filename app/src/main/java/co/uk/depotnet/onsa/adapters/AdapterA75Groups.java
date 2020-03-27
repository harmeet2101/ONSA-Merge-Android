package co.uk.depotnet.onsa.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.modals.A75Groups;

public class AdapterA75Groups extends RecyclerView.Adapter<AdapterA75Groups.ViewHolder> {

    private Context context;
    private List<A75Groups> a75Groups;

    public AdapterA75Groups(Context context , List<A75Groups> a75Groups){
        this.context = context;
        this.a75Groups = a75Groups;
    }

    @NonNull
    @Override
    public AdapterA75Groups.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_a75_groups , viewGroup , false) );
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterA75Groups.ViewHolder viewHolder, int position) {
        if(position%2 == 0){
            viewHolder.view.setBackgroundColor(ContextCompat.getColor(context , R.color.white));
        }else{
            viewHolder.view.setBackgroundColor(ContextCompat.getColor(context , R.color.item_bg_light_gray));
        }
        A75Groups item = a75Groups.get(position);
        viewHolder.txtDp.setText(item.getDpNo());
        viewHolder.txtPoleInfo.setText(item.getPoleInfo());
    }

    @Override
    public int getItemCount() {
        return a75Groups.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        final View view;
        final TextView txtDp;
        final TextView txtPoleInfo;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            this.txtDp = itemView.findViewById(R.id.txt_dp_value);
            this.txtPoleInfo = itemView.findViewById(R.id.txt_pole_info_value);
        }
    }
}
