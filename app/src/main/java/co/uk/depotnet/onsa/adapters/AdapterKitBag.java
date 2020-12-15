package co.uk.depotnet.onsa.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import co.uk.depotnet.onsa.BuildConfig;
import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.activities.MainActivity;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.listeners.GetFetchListener;
import co.uk.depotnet.onsa.modals.KitBagDocument;
import co.uk.depotnet.onsa.networking.CommonUtils;
import co.uk.depotnet.onsa.utils.AppPreferences;
import co.uk.depotnet.onsa.utils.GenericFileProvider;
import co.uk.depotnet.onsa.utils.Utils;
import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.Request;

import java.io.File;
import java.util.List;

public class AdapterKitBag extends RecyclerView.Adapter<AdapterKitBag.ViewHolder> {

    private List<KitBagDocument> kitBagItems;
    private Context context;
    private Fetch fetch;
    private GetFetchListener getFetchListener;

    public AdapterKitBag(Context context, List<KitBagDocument> kitBagItems , Fetch fetch , GetFetchListener getFetchListener) {
        this.context = context;
        this.kitBagItems = kitBagItems;
        this.fetch = fetch;
        this.getFetchListener = getFetchListener;
    }

    @NonNull
    @Override
    public AdapterKitBag.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_kit_bag, viewGroup, false));
    }


    @Override
    public void onBindViewHolder(@NonNull final AdapterKitBag.ViewHolder holder, int position) {
        final KitBagDocument kitBagItem = kitBagItems.get(position);
        holder.txtDocTitle.setText(kitBagItem.getText());
        final int id = AppPreferences.getInt("KitbagDoc" + kitBagItem.getDocumentId(), -1);

        if(!kitBagItem.isDocument()){
            holder.imgIcon.setImageResource(R.drawable.ic_folder);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!kitBagItem.isDocument()){
                        getFetchListener.openKitbagFolder(kitBagItem.getId());
                    }
                }
            });
            holder.btnCancel.setVisibility(View.GONE);
            holder.progressBar.setVisibility(View.GONE);
            holder.btnDownload.setVisibility(View.GONE);
            return;
        }

        holder.imgIcon.setImageResource(R.drawable.ic_job_details);

        if (id == -1) {
            holder.btnCancel.setVisibility(View.GONE);
            holder.progressBar.setVisibility(View.GONE);
            holder.btnDownload.setVisibility(View.VISIBLE);
            holder.btnDownload.setText(R.string.download);
            holder.btnDownload.setOnClickListener(view -> {
                if(!CommonUtils.isNetworkAvailable(context)){
                    Toast.makeText(context , "Plaese enable your internet connection" , Toast.LENGTH_SHORT).show();
                    return;
                }

                String fileDir = Utils.getSaveDir(context) + "KitbagDoc/" + "KitbagDoc_" + kitBagItem.getDocumentId() +".pdf";

                String url = BuildConfig.BASE_URL+"app/kitbag-documents/"+kitBagItem.getDocumentId()+"/download";

                Request request = new Request(url, fileDir);
                request.addHeader("Authorization", "Bearer "+DBHandler.getInstance().getUser().gettoken());
//                    request.setExtras(getExtrasForRequest());

                fetch.enqueue(request, result -> {
                    notifyDataSetChanged();
                    AppPreferences.putInt("KitbagDoc" + kitBagItem.getDocumentId(), result.getId());
                }, result -> {});
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
            if(result == null){
                return;
            }
            fetch.remove(result.getId());
        }));
    }

    private void openDocument(String name) {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
        File file = new File(name);
        Uri uri = GenericFileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", file);
        String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(uri.toString());
        String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        if (extension.equalsIgnoreCase("") || mimetype == null) {
            // if there is no extension or there is no definite mimetype, still try to open the file
            intent.setDataAndType(uri, "text/*");
        } else {
            intent.setDataAndType(uri, mimetype);
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(intent, "Choose an Application:"));
    }


    @Override
    public int getItemCount() {
        return kitBagItems.size();
    }

    public void update(Download download, long unknownRemainingTime, long unknownDownloadedBytesPerSecond) {
        for (int position = 0; position < kitBagItems.size(); position++) {
            final int id = AppPreferences.getInt("KitbagDoc" + kitBagItems.get(position).getDocumentId(), -1);
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
        final ImageView imgIcon;
        final TextView txtDocTitle;
        final TextView btnDownload;
        final TextView btnCancel;
        final ProgressBar progressBar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            this.imgIcon = itemView.findViewById(R.id.img_icon);
            this.txtDocTitle = itemView.findViewById(R.id.txt_doc_title);
            this.progressBar = itemView.findViewById(R.id.progress_bar);
            this.btnDownload = itemView.findViewById(R.id.btn_download);
            this.btnCancel = itemView.findViewById(R.id.btn_cancel);
        }
    }
}
