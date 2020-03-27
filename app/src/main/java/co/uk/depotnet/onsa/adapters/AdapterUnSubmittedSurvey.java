package co.uk.depotnet.onsa.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.listeners.OnItemClickListener;
import co.uk.depotnet.onsa.modals.forms.Submission;

public class AdapterUnSubmittedSurvey extends RecyclerView.Adapter<AdapterUnSubmittedSurvey.ViewHolder> {

    private Context context;
    private ArrayList<Submission> items;
    private OnItemClickListener<Submission> listener;


    public AdapterUnSubmittedSurvey(Context context, ArrayList<Submission> items,
                                    OnItemClickListener<Submission> listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        return new ViewHolder(LayoutInflater.from(context).
                inflate(R.layout.item_un_submitted_survey, viewGroup,
                        false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Submission submission = items.get(position);
        holder.txtTitle.setText(submission.getDate());

        holder.txtTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(submission, holder.getAdapterPosition());
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                items.remove(position);
                DBHandler.getInstance().removeAnswers(submission);
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView txtTitle;
        ImageView btnDelete;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            this.txtTitle = itemView.findViewById(R.id.txt_title);
            this.btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
