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
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.listeners.ActionsListener;
import co.uk.depotnet.onsa.modals.actions.Action;
import co.uk.depotnet.onsa.utils.Utils;

public class ActionsAdaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<Action> data=new ArrayList<>();
    private final ActionsListener listener;
    private final String actionsType;

    public ActionsAdaptor(ActionsListener listener, String type) {
        this.listener = listener;
        this.actionsType = type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == 1){
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.actions_item_list, parent, false);
            return new ActionsHolder(view);
        }else{
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.actions_incident_item_list, parent, false);
            return new ActionsIncidentHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ActionsIncidentHolder){
            bindIncidentHolder((ActionsIncidentHolder)holder , position);
        }else{
            bindInspectionHolder((ActionsHolder)holder , position);
        }
    }

    private void bindIncidentHolder(ActionsIncidentHolder holder, int position) {
        holder.actions = data.get(position);
        if (!TextUtils.isEmpty(actionsType) && actionsType.equalsIgnoreCase("Cleared")) {
            holder.rl_view_schedule.setVisibility(View.GONE);
        } else {
            holder.rl_view_schedule.setVisibility(View.VISIBLE);
            holder.rl_view_schedule.setOnClickListener(v -> {
                if (holder.rl_hide_schedule.getVisibility()==View.VISIBLE) {
                    holder.img_expend_action.setImageResource(R.drawable.ic_add_circle);
                    holder.rl_hide_schedule.setVisibility(View.GONE);
                }
                else {
                    holder.img_expend_action.setImageResource(R.drawable.ic_remove_circle);
                    holder.rl_hide_schedule.setVisibility(View.VISIBLE);
                }
            });
        }

        holder.rl_view_action.setOnClickListener(v -> {
            if (holder.rl_action_container.getVisibility()==View.VISIBLE) {
                holder.img_expand_incident.setImageResource(R.drawable.ic_add_circle);
                holder.rl_action_container.setVisibility(View.GONE);
            }
            else {
                holder.img_expand_incident.setImageResource(R.drawable.ic_remove_circle);
                holder.rl_action_container.setVisibility(View.VISIBLE);
            }
        });

        holder.tv_incident_no.setText("Incident: "+holder.actions.getIncidentTitle());
        if(holder.actions.isUrgent()) {
            holder.actions_img.setImageResource(R.drawable.ic_error_red);
            holder.tv_urgent.setVisibility(View.VISIBLE);
        }else{
            holder.actions_img.setImageResource(R.drawable.ic_error_green);
            holder.tv_urgent.setVisibility(View.GONE);
        }
        holder.tv_due_date.setText("Due Date: "+ Utils.formatDate(holder.actions.getDueDate() , "yyyy-MM-dd'T'hh:mm:ss" , "dd/MM/yyyy"));
        holder.tv_addressed.setText("Address: "+holder.actions.getAddress());
        holder.tv_post_code.setText("Post Code: "+holder.actions.getPostcode());
        holder.tv_description.setText(holder.actions.getDescription());
        holder.tv_action_comment.setText(holder.actions.getComments());

        holder.tv_corrective_measure.setOnClickListener(v -> listener.startCorrectiveMeasure(holder.actions));
        holder.tv_cannot_rectify.setOnClickListener(v -> listener.startCannotRectify(holder.actions));
    }

    private void bindInspectionHolder(ActionsHolder holder, int position) {
        holder.actions = data.get(position);
        holder.tv_actions_title.setText(holder.actions.getDescription());
        holder.tv_raised.setText(holder.actions.getRaisedByUserFullName());
        holder.tv_ref.setText(holder.actions.getEstimateNumber());
        holder.tv_inspectionType.setText(holder.actions.getComments());

        holder.tv_Corrective_Measure.setOnClickListener(v -> listener.startCorrectiveMeasure(holder.actions));
        holder.tv_Cannot_Rectify.setOnClickListener(v -> listener.startCannotRectify(holder.actions));
        if (!TextUtils.isEmpty(actionsType) && actionsType.equalsIgnoreCase("Cleared")) {
            holder.rl_view_schedule.setVisibility(View.GONE);
        } else {
            holder.rl_view_schedule.setVisibility(View.VISIBLE);
        }
        holder.rl_view_schedule.setOnClickListener(v -> {
            if (holder.rl_hide_schedule.getVisibility()==View.VISIBLE) {
                holder.actions_expend.setImageResource(R.drawable.ic_add_circle);
                holder.rl_hide_schedule.setVisibility(View.GONE);
            } else {
                holder.actions_expend.setImageResource(R.drawable.ic_remove_circle);
                holder.rl_hide_schedule.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).isIncidentAction()? 2: 1;
    }

    public void update(String dueDate , String type) {
        List<Action> actions = DBHandler.getInstance().getActions(type , dueDate);
        data.clear();
        data.addAll(actions);
        notifyDataSetChanged();
    }


    public class ActionsHolder extends RecyclerView.ViewHolder {
        public Action actions;
        private final ImageView actions_expend;
        private final TextView tv_actions_title,tv_raised,tv_ref,tv_inspectionType,tv_Corrective_Measure,tv_Cannot_Rectify;
        private final RelativeLayout rl_view_schedule,rl_hide_schedule;
        public ActionsHolder(@NonNull View itemView) {
            super(itemView);
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

    public class ActionsIncidentHolder extends RecyclerView.ViewHolder {
        public Action actions;
        private final ImageView actions_img;
        private final ImageView img_expand_incident;
        private final ImageView img_expend_action;
        private final TextView tv_incident_no;
        private final TextView tv_urgent;
        private final TextView tv_due_date;
        private final TextView tv_addressed;
        private final TextView tv_post_code;
        private final RelativeLayout rl_view_action;
        private final RelativeLayout rl_action_container;
        private final TextView tv_description;
        private final TextView tv_action_comment;
        private final TextView tv_corrective_measure;
        private final TextView tv_cannot_rectify;
        private final RelativeLayout rl_view_schedule;
        private final RelativeLayout rl_hide_schedule;
        public ActionsIncidentHolder(@NonNull View itemView) {
            super(itemView);
            actions_img=itemView.findViewById(R.id.actions_img);
            img_expand_incident=itemView.findViewById(R.id.img_expand_incident);
            img_expend_action=itemView.findViewById(R.id.img_expend_action);
            tv_incident_no=itemView.findViewById(R.id.tv_incident_no);
            tv_urgent=itemView.findViewById(R.id.tv_urgent);
            tv_due_date=itemView.findViewById(R.id.tv_due_date);
            tv_addressed=itemView.findViewById(R.id.tv_addressed);
            tv_post_code=itemView.findViewById(R.id.tv_post_code);
            rl_view_action=itemView.findViewById(R.id.rl_view_action);
            rl_action_container=itemView.findViewById(R.id.rl_action_container);
            tv_description=itemView.findViewById(R.id.tv_description);
            tv_action_comment=itemView.findViewById(R.id.tv_action_comment);
            tv_corrective_measure=itemView.findViewById(R.id.tv_corrective_measure);
            tv_cannot_rectify=itemView.findViewById(R.id.tv_cannot_rectify);
            rl_view_schedule=itemView.findViewById(R.id.rl_view_schedule);
            rl_hide_schedule=itemView.findViewById(R.id.rl_hide_schedule);
        }
    }
}
