package co.uk.depotnet.onsa.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import co.uk.depotnet.onsa.R;

public class AdapterJobDetail extends RecyclerView.Adapter<AdapterJobDetail.ViewHolder> {

    private Context context;

    public AdapterJobDetail(Context context){
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterJobDetail.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_job_detail , viewGroup , false) );
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterJobDetail.ViewHolder viewHolder, int position) {
        if(position%2 == 0){
            viewHolder.view.setBackgroundColor(ContextCompat.getColor(context , R.color.white));
        }else{
            viewHolder.view.setBackgroundColor(ContextCompat.getColor(context , R.color.item_bg_light_gray));
        }
    }

    @Override
    public int getItemCount() {
        return 20;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        final View view;
        final TextView txtTitle;
        final TextView txtValue;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            this.txtTitle = itemView.findViewById(R.id.txt_title);
            this.txtValue = itemView.findViewById(R.id.txt_value);
        }
    }
}
