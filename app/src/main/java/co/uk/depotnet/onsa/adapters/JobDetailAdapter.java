package co.uk.depotnet.onsa.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.modals.JobDetailItem;

public class JobDetailAdapter extends   RecyclerView.Adapter<JobDetailAdapter.ViewHolder>{
  private Context context;
  private ArrayList<JobDetailItem> arrayList;

    public JobDetailAdapter(Context context, ArrayList<JobDetailItem> arrayList) {

        this.context = context;
        this.arrayList = arrayList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.job_detail_item, viewGroup, false));
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        if(i==0 ){
            viewHolder.llParentItem.setBackgroundResource(R.color.white);
        }
        if(i%2 !=0 && i!=0){
            viewHolder.llParentItem.setBackgroundResource(R.color.item_bg_light_gray);
        }
        JobDetailItem jobDetailItem=arrayList.get(i);
        viewHolder.txtTitle.setText(jobDetailItem.getTitle());
        viewHolder.txtValue.setText(jobDetailItem.getValue());

    }


    @Override
    public int getItemCount() {
        if(arrayList!=null && arrayList.size()>0)
            return arrayList.size();
        else
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private TextView txtTitle;
        private TextView txtValue;
        private LinearLayout llParentItem;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            txtTitle = itemView.findViewById(R.id.tvHeading);
            txtValue = itemView.findViewById(R.id.txt_value);
            llParentItem=itemView.findViewById(R.id.llItem);
        }
    }
}
