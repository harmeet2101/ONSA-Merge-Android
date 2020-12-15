package co.uk.depotnet.onsa.adapters.briefing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.modals.briefings.BriefingsData;

public class BriefingsDocSubAdaptor extends RecyclerView.Adapter<BriefingsDocSubAdaptor.DocHolder> {
    private List<BriefingsData> briefingsData;
    private Context context;

    public BriefingsDocSubAdaptor(List<BriefingsData> data,Context context) {
        this.context = context;
        this.briefingsData=data;
    }

    @NonNull
    @Override
    public BriefingsDocSubAdaptor.DocHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DocHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.briefings_doc_subitem,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull BriefingsDocSubAdaptor.DocHolder holder, int position) {
        BriefingsData data=briefingsData.get(position);
        holder.docname.setText(data.getBriefingName());
        holder.docname.setChecked(briefingsData.get(position).isSelected());
        holder.docname.setTag(briefingsData.get(position));
        holder.docname.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CheckBox cb = (CheckBox) buttonView;
                if (data.isSelected()) {
                    data.setSelected(true);
                } else {
                    data.setSelected(false);
                }
                if (isChecked) {
                    data.setSelected(true);
                }
                else
                {
                    data.setSelected(false);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return briefingsData.size();
    }
    public class DocHolder extends RecyclerView.ViewHolder {
        private AppCompatCheckBox docname;
        public DocHolder(@NonNull View itemView) {
            super(itemView);
            docname=itemView.findViewById(R.id.briefings_doc_select);
        }
    }
}
