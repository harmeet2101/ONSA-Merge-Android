package co.uk.depotnet.onsa.adapters.store;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.fragments.store.RequestItemsFragment;
import co.uk.depotnet.onsa.listeners.FragmentActionListener;
import co.uk.depotnet.onsa.modals.User;
import co.uk.depotnet.onsa.modals.store.DataMyStores;
import co.uk.depotnet.onsa.modals.store.MyRequest;
import co.uk.depotnet.onsa.networking.APICalls;
import co.uk.depotnet.onsa.networking.CommonUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AdapterMyRequests extends RecyclerView.Adapter<AdapterMyRequests.ViewHolder> {

    private final ArrayList<MyRequest> items;
    private FragmentActionListener listener;
    private User user;
    private SimpleDateFormat inDateFormat;
    private SimpleDateFormat outDateFormat;
    private SimpleDateFormat timeFormat;
    private Context context;

    public AdapterMyRequests(ArrayList<MyRequest> items ,
                             FragmentActionListener listener,Context context) {
        this.items = items;
        this.listener = listener;
        this.user = DBHandler.getInstance().getUser();
        inDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        outDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        timeFormat = new SimpleDateFormat("HH:mm");
        this.context=context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.item = items.get(position);
        String requestDate = holder.item.getcreatedDate();

        holder.txtRequestDate.setText("Request Date: "+getDate(requestDate));
        holder.txtTimeSent.setText("Sent Time: "+getTime(requestDate));
        holder.txtNoOfItems.setText("Number of Items: "+holder.item.getitemCount());
        holder.txtComment.setText("Comment: "+holder.item.getrequestComments());
        holder.txtRequestStatus.setText("Request Status: "+holder.item.getrequestStatusName());


        holder.btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.addFragment(RequestItemsFragment.newInstance(holder.item , user),
                        false);
            }
        });

        if(holder.item.getrequestStatusName().equalsIgnoreCase("rejected") ||
                holder.item.getrequestStatusName().equalsIgnoreCase("accepted")) {

            holder.imgCancel.setVisibility(View.VISIBLE);
            holder.llCheckYourReceipt.setVisibility(View.VISIBLE);
            holder.imgCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!CommonUtils.isNetworkAvailable(context)) {
                        return;
                    }

                    listener.showProgressBar();
                    APICalls.hideReequest(user.gettoken(),holder.item.getrequestId()).enqueue(new Callback<Void>() {

                        @Override
                        public void onResponse(Call<Void> call,
                                               Response<Void> response) {
                            if (response.isSuccessful()) {
                                DBHandler.getInstance().removeMyRequests(holder.item);
                                items.remove(holder.getAdapterPosition());
                                notifyItemRemoved(holder.getAdapterPosition());
                                listener.hideProgressBar();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            listener.hideProgressBar();

                        }
                    });

                }
            });

        }else{
            holder.imgCancel.setVisibility(View.GONE);
            holder.llCheckYourReceipt.setVisibility(View.GONE);
        }
    }

    private String getDate(String dateStr){
        try {
            Date date = inDateFormat.parse(dateStr);
            return outDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateStr;
    }

    private String getTime(String dateStr){
        try {
            Date date = inDateFormat.parse(dateStr);
            return timeFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateStr;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        final TextView txtRequestDate;
        final TextView txtTimeSent;
        final TextView txtNoOfItems;
        final TextView txtComment;
        final TextView txtRequestStatus;
        final ImageView imgCancel;
        final LinearLayout llCheckYourReceipt;
        final Button btnView;
        public MyRequest item;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            txtRequestDate =  view.findViewById(R.id.txt_request_date);
            txtTimeSent =  view.findViewById(R.id.txt_time_sent);
            txtNoOfItems =  view.findViewById(R.id.txt_number_of_items);
            txtComment =  view.findViewById(R.id.txt_comment);
            txtRequestStatus =  view.findViewById(R.id.txt_request_status);
            imgCancel =  view.findViewById(R.id.btn_img_cancel);
            llCheckYourReceipt =  view.findViewById(R.id.ll_check_your_receipt);
            btnView =  view.findViewById(R.id.btn_view);

        }
    }



}
