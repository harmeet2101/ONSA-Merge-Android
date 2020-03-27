package co.uk.depotnet.onsa;

import android.app.Application;
import android.content.Context;

import co.uk.depotnet.onsa.utils.AppPreferences;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.FetchConfiguration;
import com.tonyodev.fetch2.HttpUrlConnectionDownloader;
import com.tonyodev.fetch2core.Downloader;


public class OnsaApp extends Application {



    @Override
    public void onCreate() {
        super.onCreate();

        AppPreferences.initAppPreferences(getApplicationContext());

        final FetchConfiguration fetchConfiguration = new FetchConfiguration.Builder(this)
                .enableRetryOnNetworkGain(true)
                .setDownloadConcurrentLimit(3)
                .setHttpDownloader(new HttpUrlConnectionDownloader(Downloader.FileDownloaderType.PARALLEL))
                // OR
                //.setHttpDownloader(getOkHttpDownloader())
                .build();
        Fetch.Impl.setDefaultInstanceConfiguration(fetchConfiguration);
    }
}
