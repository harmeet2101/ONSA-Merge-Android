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
import java.util.Locale;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.fragments.store.RequestItemsFragment;
import co.uk.depotnet.onsa.listeners.FragmentActionListener;
import co.uk.depotnet.onsa.modals.User;
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
        inDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.UK);
        outDateFormat = new SimpleDateFormat("dd/MM/yyyy" , Locale.UK);
        timeFormat = new SimpleDateFormat("HH:mm", Locale.UK);
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

        holder.txtRequestDate.setText(String.format("Request Date: %s", getDate(requestDate)));
        holder.txtTimeSent.setText(String.format("Sent Time: %s", getTime(requestDate)));
        holder.txtNoOfItems.setText(String.format("Number of Items: %d", holder.item.getitemCount()));
        holder.txtComment.setText(String.format("Comment: %s", holder.item.getrequestComments()));
        holder.txtRequestStatus.setText(String.format("Request Status: %s", holder.item.getrequestStatusName()));


        holder.btnView.setOnClickListener(v -> listener.addFragment(RequestItemsFragment.newInstance(holder.item),
                false));

        if(holder.item.getrequestStatusName().equalsIgnoreCase("rejected") ||
                holder.item.getrequestStatusName().equalsIgnoreCase("accepted")) {

            holder.imgCancel.setVisibility(View.VISIBLE);
            holder.llCheckYourReceipt.setVisibility(View.VISIBLE);
            holder.imgCancel.setOnClickListener(v -> {

                if (!CommonUtils.isNetworkAvailable(context)) {
                    return;
                }

                if(!CommonUtils.validateToken(context)){
                    return;
                }
                listener.showProgressBar();
                APICalls.hideReequest(user.gettoken(),holder.item.getrequestId()).enqueue(new Callback<Void>() {

                    @Override
                    public void onResponse(@NonNull Call<Void> call,
                                           @NonNull Response<Void> response) {

                        if(CommonUtils.onTokenExpired(context , response.code())){
                            return;
                        }

                        if (response.isSuccessful()) {
                            DBHandler.getInstance().removeMyRequests(holder.item);
                            items.remove(holder.getAdapterPosition());
                            notifyItemRemoved(holder.getAdapterPosition());
                            listener.hideProgressBar();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        listener.hideProgressBar();

                    }
                });

            });

        }else{
            holder.imgCancel.setVisibility(View.GONE);
            holder.llCheckYourReceipt.setVisibility(View.GONE);
        }
    }

    private String getDate(String dateStr){
        Date date = null;
        try {
            date = inDateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(date != null){
            return outDateFormat.format(date);
        }
        return dateStr;
    }

    private String getTime(String dateStr){
        Date date = null;
        try {
            date = inDateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(date != null){
            return timeFormat.format(date);
        }
        return dateStr;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
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
