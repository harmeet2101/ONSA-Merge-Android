package co.uk.depotnet.onsa.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import co.uk.depotnet.onsa.BuildConfig;
import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.modals.Document;
import co.uk.depotnet.onsa.utils.AppPreferences;
import co.uk.depotnet.onsa.utils.GenericFileProvider;
import co.uk.depotnet.onsa.utils.Utils;
import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.Request;

import java.io.File;
import java.util.List;

public class JobPackAdapter extends RecyclerView.Adapter<JobPackAdapter.ViewHolder> {

    private final List<Document> jobPacks;
    private final Context context;
    private final Fetch fetch;
    private final boolean isSubJob;


    public JobPackAdapter(Context context, List<Document> jobPacks, Fetch fetch , boolean isSubJob) {
        this.context = context;
        this.jobPacks = jobPacks;
        this.fetch = fetch;
        this.isSubJob = isSubJob;
    }

    @NonNull
    @Override
    public JobPackAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_kit_bag, viewGroup, false));
    }


    @Override
    public void onBindViewHolder(@NonNull final JobPackAdapter.ViewHolder holder, int position) {
        final Document jobPack = jobPacks.get(position);

        holder.txtDocTitle.setText(jobPack.getDocumentName());
        holder.txtDocTime.setText(Utils.formatDate(jobPack.getdateTime() ,"yyyy-MM-dd'T'HH:mm:sss", "dd/MM/yyyy"));
        final int id = AppPreferences.getInt("JobPack" + jobPack.getjobDocumentId(), -1);


        if (id == -1) {
            holder.btnCancel.setVisibility(View.GONE);
            holder.progressBar.setVisibility(View.GONE);
            holder.btnDownload.setVisibility(View.VISIBLE);
            holder.btnDownload.setText("Download");
            holder.btnDownload.setOnClickListener(view -> {
                String extension = getExtension(jobPack.getDocumentName());
                if(TextUtils.isEmpty(extension)){
                    extension = "docx";
                }
                String fileDir = Utils.getSaveDir(context) + "JobPack/" + "JobPack_" + jobPack.getjobDocumentId() + "."+extension;// + jobPack.gettype().toLowerCase(Locale.ENGLISH);
                String url = BuildConfig.BASE_URL+"app/jobs/"+jobPack.getJobId()+
                        "/documents/"+jobPack.getjobDocumentId()+"/download";

                if(isSubJob){
                    url = BuildConfig.BASE_URL+"app/jobs/"+jobPack.getJobId()+
                            "/document/"+jobPack.getjobDocumentId();
                }
                Request request = new Request(url, fileDir);
                request.addHeader("Authorization", "Bearer "+DBHandler.getInstance().getUser().gettoken());
                fetch.enqueue(request, result -> {
                    notifyDataSetChanged();
                    AppPreferences.putInt("JobPack" + jobPack.getjobDocumentId(), result.getId());
                }, result -> {
                });
            });

        } else {
            fetch.getDownload(id, download -> {
                if (download == null) {
                    return;
                }

                int progress = download.getProgress();
                if (progress == -1) {
                    progress = 0;
                }
                holder.progressBar.setProgress(progress);
                switch (download.getStatus()) {
                    case COMPLETED: {
                        holder.btnDownload.setText(R.string.view);
                        holder.btnCancel.setVisibility(View.GONE);
                        holder.progressBar.setVisibility(View.GONE);
                        holder.btnDownload.setOnClickListener(view -> {
                            String fileUrl = download.getFile();
                            openDocument(fileUrl);
                        });
                        break;
                    }
                    case FAILED: {
                        holder.btnDownload.setText(R.string.retry);
                        holder.btnCancel.setVisibility(View.GONE);
                        holder.progressBar.setVisibility(View.GONE);
                        holder.btnDownload.setOnClickListener(view -> {
                            holder.btnDownload.setEnabled(false);
                            fetch.retry(download.getId());
                            notifyDataSetChanged();
                        });
                        break;
                    }
                    case PAUSED: {
                        holder.btnDownload.setText(R.string.resume);
                        holder.btnCancel.setVisibility(View.VISIBLE);
                        holder.progressBar.setVisibility(View.VISIBLE);
                        holder.btnDownload.setOnClickListener(view -> {
                            holder.btnDownload.setEnabled(false);
                            fetch.resume(download.getId());
                            notifyDataSetChanged();
                        });
                        break;
                    }
                    case DOWNLOADING:
                    case QUEUED: {
                        holder.btnDownload.setText(R.string.pause);
                        holder.btnCancel.setVisibility(View.VISIBLE);
                        holder.progressBar.setVisibility(View.VISIBLE);
                        holder.btnDownload.setOnClickListener(view -> {
                            holder.btnDownload.setEnabled(false);
                            fetch.pause(download.getId());
                            notifyDataSetChanged();
                        });
                        break;
                    }
                    case ADDED: {
                        holder.btnDownload.setText(R.string.download);
                        holder.btnCancel.setVisibility(View.GONE);
                        holder.progressBar.setVisibility(View.GONE);
                        holder.btnDownload.setOnClickListener(view -> {
                            holder.btnDownload.setEnabled(false);
                            fetch.resume(download.getId());
                            notifyDataSetChanged();
                        });
                        break;
                    }
                    default: {
                        break;
                    }
                }
            });
        }


        holder.btnCancel.setOnClickListener(view -> fetch.getDownload(id, result -> {
            if (result == null) {
                return;
            }
            fetch.remove(result.getId());
        }));
    }

    public void openDocument(String name) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        File file = new File(name);
        Uri uri = GenericFileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", file);

        String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(uri.toString());
        String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

        if (extension.equalsIgnoreCase("") || mimetype == null) {
            intent.setDataAndType(uri, "text/*");
        } else {
            intent.setDataAndType(uri, mimetype);
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(intent, "Choose an Application:"));
    }

    private String getExtension(String name){
        if(TextUtils.isEmpty(name)){
            return null;
        }
        String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(name);
        return extension;
    }


    @Override
    public int getItemCount() {
        return jobPacks.size();
    }

    public void update(Download download, long unknownRemainingTime, long unknownDownloadedBytesPerSecond) {
        for (int position = 0; position < jobPacks.size(); position++) {
            final int id = AppPreferences.getInt("JobPack" + jobPacks.get(position).getjobDocumentId(), -1);
            if (id == download.getId()) {
                switch (download.getStatus()) {
                    case REMOVED:
                    case DELETED: {
                        notifyItemRemoved(position);
                        break;
                    }
                    default: {
                        notifyItemChanged(position);
                    }
                }
                return;
            }
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        final View view;
        final TextView txtDocTitle;
        final TextView txtDocTime;
        final TextView btnDownload;
        final TextView btnCancel;
        final ProgressBar progressBar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            this.txtDocTitle = itemView.findViewById(R.id.txt_doc_title);
            this.txtDocTime = itemView.findViewById(R.id.txt_doc_time);
            this.progressBar = itemView.findViewById(R.id.progress_bar);
            this.btnDownload = itemView.findViewById(R.id.btn_download);
            this.btnCancel = itemView.findViewById(R.id.btn_cancel);
        }
    }
}
