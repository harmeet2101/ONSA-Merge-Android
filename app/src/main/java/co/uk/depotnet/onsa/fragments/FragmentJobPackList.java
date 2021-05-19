package co.uk.depotnet.onsa.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tonyodev.fetch2.AbstractFetchListener;
import com.tonyodev.fetch2.DefaultFetchNotificationManager;
import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.Error;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.FetchConfiguration;
import com.tonyodev.fetch2.FetchListener;
import com.tonyodev.fetch2core.Downloader;
import com.tonyodev.fetch2okhttp.OkHttpDownloader;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.adapters.JobPackAdapter;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.modals.Document;
import co.uk.depotnet.onsa.modals.Job;

public class FragmentJobPackList extends Fragment{
    private static final String ARG_JOBID = "JobId";
    private static final int UNKNOWN_REMAINING_TIME = -1;
    private static final int UNKNOWN_DOWNLOADED_BYTES_PER_SECOND = 0;
    private Context context;
    private JobPackAdapter adapter;
    private Fetch fetch;
    private Job job;
    private final FetchListener fetchListener = new AbstractFetchListener() {
        @Override
        public void onAdded(@NotNull Download download) {
            System.out.println("test navin onAdded "+download);
        }

        @Override
        public void onQueued(@NotNull Download download, boolean waitingOnNetwork) {
            System.out.println("test navin onQueued "+download);
            adapter.update(download, UNKNOWN_REMAINING_TIME, UNKNOWN_DOWNLOADED_BYTES_PER_SECOND);
        }

        @Override
        public void onCompleted(@NotNull Download download) {
            System.out.println("test navin onCompleted "+download);
            adapter.update(download, UNKNOWN_REMAINING_TIME, UNKNOWN_DOWNLOADED_BYTES_PER_SECOND);
        }

        @Override
        public void onError(@NotNull Download download, @NotNull Error error, @org.jetbrains.annotations.Nullable Throwable throwable) {
            super.onError(download, error, throwable);
            adapter.update(download, UNKNOWN_REMAINING_TIME, UNKNOWN_DOWNLOADED_BYTES_PER_SECOND);
        }

        @Override
        public void onProgress(@NotNull Download download, long etaInMilliseconds, long downloadedBytesPerSecond) {
            adapter.update(download, etaInMilliseconds, downloadedBytesPerSecond);
        }

        @Override
        public void onPaused(@NotNull Download download) {
            adapter.update(download, UNKNOWN_REMAINING_TIME, UNKNOWN_DOWNLOADED_BYTES_PER_SECOND);
        }

        @Override
        public void onResumed(@NotNull Download download) {
            adapter.update(download, UNKNOWN_REMAINING_TIME, UNKNOWN_DOWNLOADED_BYTES_PER_SECOND);
        }

        @Override
        public void onCancelled(@NotNull Download download) {
            adapter.update(download, UNKNOWN_REMAINING_TIME, UNKNOWN_DOWNLOADED_BYTES_PER_SECOND);
        }

        @Override
        public void onRemoved(@NotNull Download download) {
            adapter.update(download, UNKNOWN_REMAINING_TIME, UNKNOWN_DOWNLOADED_BYTES_PER_SECOND);
        }

        @Override
        public void onDeleted(@NotNull Download download) {
            adapter.update(download, UNKNOWN_REMAINING_TIME, UNKNOWN_DOWNLOADED_BYTES_PER_SECOND);
        }
    };


    public static FragmentJobPackList newInstance(String jobId) {
        FragmentJobPackList fragmentKitBag = new FragmentJobPackList();
        Bundle args = new Bundle();
        args.putString(ARG_JOBID, jobId);
        fragmentKitBag.setArguments(args);
        return fragmentKitBag;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        DBHandler dbHandler = DBHandler.getInstance(context);

        String jobId = args.getString(ARG_JOBID);
        job = dbHandler.getJob(jobId);
        List<Document> jobPacks = DBHandler.getInstance().getDocument(jobId);

        final FetchConfiguration fetchConfiguration = new FetchConfiguration.Builder(context)
                .setDownloadConcurrentLimit(4)
                .setHttpDownloader(new OkHttpDownloader(Downloader.FileDownloaderType.PARALLEL))
                .setNamespace("OptinonsDownloader")
                .setNotificationManager(new DefaultFetchNotificationManager(context))
                .build();
//        fetch = Fetch.Impl.getInstance(fetchConfiguration);
        fetch = Fetch.Impl.getDefaultInstance();
        fetch.addListener(fetchListener);
        adapter = new JobPackAdapter(context, jobPacks, fetch , job.isSubJob());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kit_bag, container, false);
        TextView txtToolBarTitle = view.findViewById(R.id.txt_toolbar_title);
        String title;
        if(job.isSubJob()){
            title = "Jobpack: "+job.getestimateNumber()+"-"+"S"+job.getSubJobNumber();
        }else{
            title = "Jobpack: "+job.getestimateNumber();
        }
        txtToolBarTitle.setText(title);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean b) {

            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(!fetch.isClosed()){
            fetch.removeListener(fetchListener);
            fetch.close();
        }
    }
}
