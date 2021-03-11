package co.uk.depotnet.onsa.adapters.actions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.dialogs.LineDivider;
import co.uk.depotnet.onsa.listeners.ActionsListener;
import co.uk.depotnet.onsa.utils.Utils;

public class ActionsMainAdaptor extends RecyclerView.Adapter<ActionsMainAdaptor.MainHolder> {

    private final Context context;
    private final List<String> outstandingActions=new ArrayList<>();
    private final ActionsListener listener;
    private final String actionType;

    public ActionsMainAdaptor(Context context, String actionType, ActionsListener listener) {
        this.context = context;
        this.listener = listener;
        this.actionType = actionType;
    }

    @NonNull
    @Override
    public ActionsMainAdaptor.MainHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MainHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.actions_main_list,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ActionsMainAdaptor.MainHolder holder, int position) {
        String dueDate= outstandingActions.get(position);
        holder.action_datetime.setText(Utils.formatDate(dueDate , "yyyy-MM-dd'T'hh:mm:ss" , "dd/MM/yyyy"));
        holder.actionsAdaptor.update(dueDate , actionType);
    }

    @Override
    public int getItemCount() {
        return outstandingActions.size();
    }

    public void SetActionsList(List<String> dueDates)
    {
        outstandingActions.clear();
        outstandingActions.addAll(dueDates);
        notifyDataSetChanged();
    }

    public class MainHolder extends RecyclerView.ViewHolder {
        public ActionsAdaptor actionsAdaptor;
        private final TextView action_datetime;

        public MainHolder(@NonNull View itemView) {
            super(itemView);
            action_datetime=itemView.findViewById(R.id.action_datetime);
            RecyclerView recyclerView = itemView.findViewById(R.id.action_item_recycler);
            recyclerView.setLayoutManager(new LinearLayoutManager(context , LinearLayoutManager.VERTICAL , false));
            recyclerView.addItemDecoration(new LineDivider(recyclerView.getContext(), DividerItemDecoration.VERTICAL , ResourcesCompat.getDrawable(context.getResources() , R.drawable.line_drawable , null)));
            actionsAdaptor = new ActionsAdaptor(listener, actionType);
            recyclerView.setAdapter(actionsAdaptor);

        }
    }
}
