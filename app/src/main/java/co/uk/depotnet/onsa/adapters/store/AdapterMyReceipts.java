package co.uk.depotnet.onsa.adapters.store;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.fragments.store.ReceiptItemsFragment;
import co.uk.depotnet.onsa.listeners.FragmentActionListener;
import co.uk.depotnet.onsa.modals.store.Receipts;


public class AdapterMyReceipts extends RecyclerView.Adapter<AdapterMyReceipts.ViewHolder> {

    private final ArrayList<Receipts> items;
    private FragmentActionListener listener;
    private SimpleDateFormat inDateFormat;
    private SimpleDateFormat outDateFormat;
    private SimpleDateFormat timeFormat;




    public AdapterMyReceipts(ArrayList<Receipts> items , FragmentActionListener listener) {
        this.items = items;
        this.listener = listener;
        inDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss" , Locale.UK);
        outDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
        timeFormat = new SimpleDateFormat("HH:mm", Locale.UK);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_receipts, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.item = items.get(position);
        holder.txtSentBy.setText("Sent By: "+holder.item.getsentByName());
        holder.txtDateSent.setText("Sent Date: "+getDate(holder.item.getdateSent()));
        holder.txtTimeSent.setText("Sent Time: "+getTime(holder.item.getdateSent()));
        holder.txtNoOfItems.setText("Number of Items: "+String.valueOf(holder.item.getitemCount()));
        holder.txtFrom.setText("From: "+holder.item.getsentToName());
        holder.txtBatchRef.setText("Batch ref: "+holder.item.getbatchRef());


        holder.btnView.setOnClickListener(v -> listener.addFragment(ReceiptItemsFragment.newInstance(holder.item , 0),
                false));
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
        public final TextView txtSentBy;
        public final TextView txtDateSent;
        public final TextView txtTimeSent;
        public final TextView txtNoOfItems;
        public final TextView txtFrom;
        public final TextView txtBatchRef;
        public final Button btnView;
        public Receipts item;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            txtSentBy =  view.findViewById(R.id.txt_sent_by);
            txtDateSent =  view.findViewById(R.id.txt_date_sent);
            txtTimeSent =  view.findViewById(R.id.txt_time_sent);
            txtNoOfItems =  view.findViewById(R.id.txt_number_of_items);
            txtFrom =  view.findViewById(R.id.txt_from);
            txtBatchRef =  view.findViewById(R.id.txt_batch_ref);
            btnView =  view.findViewById(R.id.btn_view);

        }
    }
}
