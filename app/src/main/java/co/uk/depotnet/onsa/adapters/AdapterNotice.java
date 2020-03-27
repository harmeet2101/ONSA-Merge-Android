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
import co.uk.depotnet.onsa.modals.Notice;

import java.util.List;

public class AdapterNotice extends RecyclerView.Adapter<AdapterNotice.ViewHolder> {

    private Context context;
    private List<Notice> jobNotices;

    public AdapterNotice(Context context , List<Notice> jobNotices){
        this.context = context;
        this.jobNotices = jobNotices;
    }

    @NonNull
    @Override
    public AdapterNotice.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_notices , viewGroup , false) );
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterNotice.ViewHolder viewHolder, int position) {
        if(position%2 == 0){
            viewHolder.view.setBackgroundColor(ContextCompat.getColor(context , R.color.white));
        }else{
            viewHolder.view.setBackgroundColor(ContextCompat.getColor(context , R.color.item_bg_light_gray));
        }
        Notice notice = jobNotices.get(position);
        viewHolder.txtName.setText(notice.getworksReference());
        viewHolder.txtAddress.setText(notice.getworksAddress());
        viewHolder.txtNoticeType.setText(notice.getnoticeType());
        viewHolder.txtComments.setText(notice.getcomment());
        viewHolder.txtStartDate.setText(notice.getstartDate());
        viewHolder.txtEndDate.setText(notice.getendDate());
    }

    @Override
    public int getItemCount() {
        return jobNotices.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        final View view;
        final TextView txtName;
        final TextView txtAddress;
        final TextView txtNoticeType;
        final TextView txtComments;
        final TextView txtStartDate;
        final TextView txtEndDate;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            this.txtName = itemView.findViewById(R.id.txt_notice_name);
            this.txtAddress = itemView.findViewById(R.id.txt_address);
            this.txtNoticeType = itemView.findViewById(R.id.txt_notice_type);
            this.txtComments = itemView.findViewById(R.id.txt_notice_comments);
            this.txtStartDate = itemView.findViewById(R.id.txt_notice_start_date);
            this.txtEndDate = itemView.findViewById(R.id.txt_notice_end_date);
        }
    }
}
