package co.uk.depotnet.onsa.adapters.briefing;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.listeners.BriefingsListner;
import co.uk.depotnet.onsa.modals.briefings.BriefingsData;
import co.uk.depotnet.onsa.modals.briefings.BriefingsDocModal;
import co.uk.depotnet.onsa.modals.briefings.BriefingsRecipient;
import co.uk.depotnet.onsa.utils.Utils;

public class BriefingsDocAdaptor extends
        RecyclerView.Adapter<BriefingsDocAdaptor.ViewHolder> {

    private List<BriefingsDocModal> packageList=new ArrayList<>();
    private Context context;
    private BriefingsDocSubAdaptor briefingsDocSubAdaptor;
    private final BriefingsListner listner;

    public BriefingsDocAdaptor(Context context, BriefingsListner listner) {
        this.context = context;
        this.listner = listner;
    }

    @NonNull
    @Override
    public BriefingsDocAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                    int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.briefings_doc_items, parent, false));
    }

    @Override
    public void onBindViewHolder(BriefingsDocAdaptor.ViewHolder holder,
                                 int position) {
        BriefingsDocModal model = packageList.get(position);
        try {
            holder.doc_date.setText(Utils.getSimpleDateFormat(model.getSentDate()));
        } catch (ParseException e) {
            e.printStackTrace();
            holder.doc_date.setText(model.getSentDate());
        }
        if (model.getBriefings()!=null && model.getBriefings().size()>0)
            {
                briefingsDocSubAdaptor=new BriefingsDocSubAdaptor(model.getBriefings(),context);
                holder.dateContent.setAdapter(briefingsDocSubAdaptor);
                briefingsDocSubAdaptor.notifyDataSetChanged();
            }
        String userid= DBHandler.getInstance().getUser().getuserId();
       for(BriefingsData briefingsData : model.getBriefings())
        for (BriefingsRecipient receipts: briefingsData.getRecipients()) {
            if (userid!=null)
            {
                if (userid.equals(receipts.getUserId()))
                {
                    if (receipts.getUserHasReadBriefing())
                    {
                        holder.docstatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ff33b5e5")));
                    }
                    else
                    {
                        holder.docstatus.setBackgroundTintList(context.getResources().getColorStateList(R.color.holo_red_dark));
                    }
                }
            }
        }
      holder.docstatus.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            listner.StartBriefingsRead(model);
          }
      });
    }
    public void UpdateBriefList(List<BriefingsDocModal> models){
        packageList.clear();
        packageList.addAll(models);
        notifyDataSetChanged();

    }
    @Override
    public int getItemCount() {
        return packageList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView doc_date;
        public RecyclerView dateContent;
        public MaterialButton docstatus;

        public ViewHolder(View view) {
            super(view);
            doc_date = view.findViewById(R.id.briefings_doc_date);
            dateContent = view.findViewById(R.id.briefings_doc_content);
            docstatus = view.findViewById(R.id.briefings_doc_click);
        }
    }
}
