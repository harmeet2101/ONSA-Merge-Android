package co.uk.depotnet.onsa.adapters.actions;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.listeners.ActionsListner;
import co.uk.depotnet.onsa.modals.actions.ActionsClose;

public class ActionsAdaptor extends RecyclerView.Adapter<ActionsAdaptor.ActionsHolder> {
    private final List<ActionsClose> data=new ArrayList<>();
    private final ActionsListner listener;
    private String actionsType;
    public ActionsAdaptor(ActionsListner listener, String type) {
        this.listener = listener;
        this.actionsType=type;
    }
    @NonNull
    @Override
    public ActionsAdaptor.ActionsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.actions_item_list, parent, false);
        return new ActionsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActionsAdaptor.ActionsHolder holder, int position) {
        holder.actions = data.get(position);
        holder.tv_actions_title.setText(holder.actions.getQuestionText());
        holder.tv_raised.setText(holder.actions.getRaisedByUserFullName());
        holder.tv_ref.setText(holder.actions.getEstimateNumber());
        holder.tv_inspectionType.setText(holder.actions.getDefectComments());

        holder.tv_Corrective_Measure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.StartCorrectiveMeasure(holder.actions);
            }
        });
        holder.tv_Cannot_Rectify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.StartCannotRectify(holder.actions);
            }
        });
        if (!TextUtils.isEmpty(actionsType) && actionsType.equalsIgnoreCase("Cleared"))
        {
            holder.rl_view_schedule.setVisibility(View.GONE);
        }
        else
        {
            holder.rl_view_schedule.setVisibility(View.VISIBLE);
        }
        holder.rl_view_schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.rl_hide_schedule.getVisibility()==View.VISIBLE)
                {
                    holder.actions_expend.setImageResource(R.drawable.ic_add_circle);
                    holder.rl_hide_schedule.setVisibility(View.GONE);
                }
                else
                {
                    holder.actions_expend.setImageResource(R.drawable.ic_remove_circle);
                    holder.rl_hide_schedule.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void update(List<ActionsClose> newJobs) {
        data.clear();
        data.addAll(newJobs);
        notifyDataSetChanged();
    }


    public class ActionsHolder extends RecyclerView.ViewHolder {
        public ActionsClose actions;
        private ImageView actions_img,actions_expend;
        private TextView tv_actions_title,tv_raised,tv_ref,tv_inspectionType,tv_Corrective_Measure,tv_Cannot_Rectify;
        private RelativeLayout rl_view_schedule,rl_hide_schedule;
        public ActionsHolder(@NonNull View itemView) {
            super(itemView);
            actions_img=itemView.findViewById(R.id.actions_img);
            actions_expend=itemView.findViewById(R.id.actions_expend);
            tv_actions_title=itemView.findViewById(R.id.tv_actions_title);
            tv_raised=itemView.findViewById(R.id.tv_raised);
            tv_ref=itemView.findViewById(R.id.tv_ref);
            tv_inspectionType=itemView.findViewById(R.id.tv_inspectionType);
            tv_Corrective_Measure=itemView.findViewById(R.id.tv_Corrective_Measure);
            tv_Cannot_Rectify=itemView.findViewById(R.id.tv_Cannot_Rectify);
            rl_view_schedule=itemView.findViewById(R.id.rl_view_schedule);
            rl_hide_schedule=itemView.findViewById(R.id.rl_hide_schedule);
        }
    }
}
