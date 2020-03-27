package co.uk.depotnet.onsa.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.modals.Job;
import co.uk.depotnet.onsa.modals.forms.Submission;

import java.util.ArrayList;

public class OfflineQueueAdapter extends RecyclerView.Adapter<OfflineQueueAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Submission> submissions;
    private QueueListener listener;

    public void onSubmit(int position) {
        submissions.remove(position);
        notifyDataSetChanged();
    }

    public interface QueueListener{
        void onItemClick(Submission submission , int position);
    }

    public OfflineQueueAdapter(Context context ,
                               ArrayList<Submission> submissions ,
                               QueueListener listener){
        this.context = context;
        this.submissions = submissions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_offline_queue , viewGroup , false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final Submission submission = submissions.get(position);
        Job job = DBHandler.getInstance().getJob(submission.getJobID());

        if(job != null) {
            holder.txtJobID.setText(job.getjobNumber());
        }
        holder.txtSubmissionType.setText(submission.getTitle());

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(submission , position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return submissions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        View view;
        TextView txtJobID;
        TextView txtSubmissionType;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            txtJobID = itemView.findViewById(R.id.txt_job_id);
            txtSubmissionType = itemView.findViewById(R.id.txt_submission_type);
        }
    }
}
