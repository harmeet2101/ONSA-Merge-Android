package co.uk.depotnet.onsa;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.modals.hseq.HseqDataset;
import co.uk.depotnet.onsa.modals.responses.DatasetResponse;
import co.uk.depotnet.onsa.modals.store.FeatureResult;
import co.uk.depotnet.onsa.modals.store.StoreDataset;
import co.uk.depotnet.onsa.networking.APICalls;
import co.uk.depotnet.onsa.networking.ConnectionHelper;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Callback;
import retrofit2.Response;

public class RefreshDatasetService extends IntentService {

    public static String PARAM_USER_TOKEN = "PARAM_USER_TOKEN";

    public RefreshDatasetService() {
        super("RefreshDatasetService");
    }


    public static void startAction(Context context, String userToken) {
        Intent intent = new Intent(context, RefreshDatasetService.class);
        intent.putExtra(PARAM_USER_TOKEN, userToken);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String userToken = intent.getStringExtra(PARAM_USER_TOKEN);
        handleAction(userToken);
    }

    private void handleAction(String token) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getDataset(token);
            }
        }).start();

        getStoreDataset(token);
    }

    private void getDataset(String token) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(150, TimeUnit.SECONDS)
                .readTimeout(150, TimeUnit.SECONDS)
                .writeTimeout(150, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build();
        Gson gson = new Gson();
        String url = BuildConfig.BASE_URL + "app/dataset";
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("Accept", "application/json")
                .build();
        Call call = okHttpClient.newCall(request);
        try {
            okhttp3.Response response = call.execute();
            if (response != null && response.isSuccessful()) {
                ResponseBody body = response.body();
                if (body != null) {
                    DatasetResponse datasetResponse = gson.fromJson(body.string(), DatasetResponse.class);
                    datasetResponse.toContentValues();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getStoreDataset(String token) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(150, TimeUnit.SECONDS)
                .readTimeout(150, TimeUnit.SECONDS)
                .writeTimeout(150, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build();
        Gson gson = new Gson();
        String url = BuildConfig.BASE_URL + "appstores/dataset";
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("Accept", "application/json")
                .build();
        Call call = okHttpClient.newCall(request);
        try {
            okhttp3.Response response = call.execute();
            if (response != null && response.isSuccessful()) {
                ResponseBody body = response.body();
                if (body != null) {
                    StoreDataset datasetResponse = gson.fromJson(body.string(), StoreDataset.class);
                    datasetResponse.toContentValues();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Request requesthseq = new Request.Builder()
                    .url(BuildConfig.BASE_URL + "apphseq/dataset")
                    .addHeader("Authorization", "Bearer " + token)
                    .addHeader("Accept", "application/json")
                    .build();
            Call callhseq = okHttpClient.newCall(requesthseq);
            okhttp3.Response responsehseq = callhseq.execute();
            if (responsehseq != null && responsehseq.isSuccessful()) {
                ResponseBody body = responsehseq.body();
                if (body != null) {
                    HseqDataset datasetResponse = gson.fromJson(body.string(), HseqDataset.class);// for depotnet dropdown
                    datasetResponse.toContentValues();
                    //Log.d("abhikr","depotnet dataset: "+datasetResponse);
                }

            }
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }

}
