package co.uk.depotnet.onsa.adapters.actions;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.listeners.ActionsListner;
import co.uk.depotnet.onsa.modals.actions.OutstandingAction;
import co.uk.depotnet.onsa.utils.Utils;

public class ActionsMainAdaptor extends RecyclerView.Adapter<ActionsMainAdaptor.MainHolder> {
    private List<OutstandingAction> outstandingActions=new ArrayList<>();
    private ActionsAdaptor actionsAdaptor;
    private final ActionsListner listener;

    public ActionsMainAdaptor(ActionsListner listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ActionsMainAdaptor.MainHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MainHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.actions_main_list,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ActionsMainAdaptor.MainHolder holder, int position) {
    OutstandingAction action=outstandingActions.get(position);
        try {
            holder.action_datetime.setText(Utils.getSimpleDateFormat(action.getDueDate()));
        } catch (ParseException e) {
            e.printStackTrace();
            holder.action_datetime.setText(action.getDueDate());
        }
        actionsAdaptor=new ActionsAdaptor(listener,action.getActionType());
        holder.recyclerView.setAdapter(actionsAdaptor);
        actionsAdaptor.update(action.getActions());
        actionsAdaptor.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return outstandingActions.size();
    }
    public void SetActionsList(List<OutstandingAction> actionList)
    {
        outstandingActions.clear();
        outstandingActions.addAll(actionList);
        notifyDataSetChanged();
    }

    public class MainHolder extends RecyclerView.ViewHolder {
        private TextView action_datetime;
        private RecyclerView recyclerView;
        public MainHolder(@NonNull View itemView) {
            super(itemView);
            action_datetime=itemView.findViewById(R.id.action_datetime);
            recyclerView=itemView.findViewById(R.id.action_item_recycler);
        }
    }
}
