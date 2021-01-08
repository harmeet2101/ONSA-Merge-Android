package co.uk.depotnet.onsa.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.modals.User;
import co.uk.depotnet.onsa.modals.WelcomeHomeModal;
import co.uk.depotnet.onsa.networking.Constants;

public class WelcomeHomeAdapter extends RecyclerView.Adapter<WelcomeHomeAdapter.HomeHolder> {
    private OnItemClickListener listener;
    private List<WelcomeHomeModal> items;
    private Context context;
    public WelcomeHomeAdapter(Context context , OnItemClickListener listener){
        this.context = context;
        this.listener = listener;
        items = new ArrayList<>();
        User user = DBHandler.getInstance().getUser();
        items.add(new WelcomeHomeModal(1,"My Work",R.drawable.ic_my_work,R.color.ColorMyWork));

        if(Constants.isHSEQEnabled) {
            items.add(new WelcomeHomeModal(2, "HSEQ", R.drawable.ic_hseq, R.color.ColorHseq));
            items.add(new WelcomeHomeModal(4,"Briefing",R.drawable.ic_briefings,R.color.ColorBriefing));
        }
        if(Constants.isStoreEnabled) {
            items.add(new WelcomeHomeModal(3, "STORES", R.drawable.ic_stores, R.color.ColorStore));
        }
        if(user != null && user.isCompleteTimesheets()) {
            items.add(new WelcomeHomeModal(5, "TIMESHEETS", R.drawable.ic_timesheet_icon, R.color.ColorTimeSheet));
        }

    }

    @NonNull
    @Override
    public WelcomeHomeAdapter.HomeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HomeHolder(LayoutInflater.from(context).inflate(R.layout.home_hseq_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull WelcomeHomeAdapter.HomeHolder holder, int position) {
        WelcomeHomeModal welcomeHomeModal = items.get(position);
        holder.mText.setText(welcomeHomeModal.getWH_title());
        Glide.with(context)
                .load(welcomeHomeModal.getWH_icon())
                .into(holder.mImage);
        holder.mCardview.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), welcomeHomeModal.getWH_color()));
        holder.view.setOnClickListener(v -> listener.onItemClick(welcomeHomeModal.getWH_id()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class HomeHolder extends RecyclerView.ViewHolder {
        private View view;
        private MaterialCardView mCardview;
        private AppCompatImageView mImage;
        private AppCompatTextView mText;

        HomeHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            mCardview = itemView.findViewById(R.id.hseq_home_cardview);
            mImage = itemView.findViewById(R.id.hseq_home_icon);
            mText = itemView.findViewById(R.id.hseq_home_title);
        }
    }


    public interface OnItemClickListener {
        void onItemClick(int WH_id);
    }
}
