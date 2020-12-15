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

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.activities.MainActivity;
import co.uk.depotnet.onsa.adapters.AdapterKitBag;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.listeners.DownloadActionListener;
import co.uk.depotnet.onsa.listeners.GetFetchListener;
import co.uk.depotnet.onsa.modals.KitBagDocument;
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


public class FragmentKitBag extends Fragment implements
        DownloadActionListener {
    private static final String ARG_PARENTID = "parent_id";
    private static final int UNKNOWN_REMAINING_TIME = -1;
    private static final int UNKNOWN_DOWNLOADED_BYTES_PER_SECOND = 0;
    private Context context;
    private AdapterKitBag adapter;
    private GetFetchListener getFetchListener;
    private Fetch fetch;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        if(this.context instanceof GetFetchListener ){
            getFetchListener = (GetFetchListener)context;
        }
    }

    public static FragmentKitBag newInstance( int parentId){
        FragmentKitBag fragmentKitBag = new FragmentKitBag();
        Bundle args = new Bundle();
        args.putInt(ARG_PARENTID , parentId);
        fragmentKitBag.setArguments(args);
        return fragmentKitBag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        int parentId = args.getInt(ARG_PARENTID , 0);
        List<KitBagDocument> kitBagItems = DBHandler.getInstance().getKitBagDoc(parentId);
        fetch = getFetchListener.getFetch();
        fetch.addListener(fetchListener);
        adapter = new AdapterKitBag(context , kitBagItems , fetch , getFetchListener);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kit_bag , container , false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context , LinearLayoutManager.VERTICAL , false));
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
    public void onPauseDownload(int id) {

    }

    @Override
    public void onResumeDownload(int id) {

    }

    @Override
    public void onRemoveDownload(int id) {

    }

    @Override
    public void onRetryDownload(int id) {

    }

    private final FetchListener fetchListener = new AbstractFetchListener() {
        @Override
        public void onAdded(@NotNull Download download) {

        }

        @Override
        public void onQueued(@NotNull Download download, boolean waitingOnNetwork) {
            adapter.update(download, UNKNOWN_REMAINING_TIME, UNKNOWN_DOWNLOADED_BYTES_PER_SECOND);
        }

        @Override
        public void onCompleted(@NotNull Download download) {
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        fetch.removeListener(fetchListener);
    }
}
