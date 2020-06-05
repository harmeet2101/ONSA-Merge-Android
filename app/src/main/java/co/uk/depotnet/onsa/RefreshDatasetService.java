package co.uk.depotnet.onsa;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import co.uk.depotnet.onsa.database.DBHandler;
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
//        getFeatures(token);
//        APICalls.getDataSet(token).enqueue(dataSetCallback);
//        APICalls.getStoreDataSet(token).enqueue(storeDataSetCallback);
new Thread(new Runnable() {
    @Override
    public void run() {
        getDataset(token);
    }
}).start();

        getStoreDataset(token);
    }

//    private void getFeatures(String token){
//        APICalls.featureResultCall(token).enqueue(new Callback<FeatureResult>() {
//            @Override
//            public void onResponse(@NonNull Call<FeatureResult> call, @NonNull Response<FeatureResult> response) {
//
//                if(response.isSuccessful()){
//                    FeatureResult featureResult = response.body();
//                    if(featureResult != null){
//                        featureResult.toContentValues();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<FeatureResult> call, @NonNull Throwable t) {
//            }
//        });
//    }

    /*private Callback<StoreDataset> storeDataSetCallback = new Callback<StoreDataset>() {
        @Override
        public void onResponse(@NonNull Call<StoreDataset> call,
                               Response<StoreDataset> response) {
            if (response.body() != null) {
                response.body().toContentValues();
            }
        }

        @Override
        public void onFailure(@NonNull Call<StoreDataset> call, @NonNull Throwable t) {
        }
    };

    private Callback<DatasetResponse> dataSetCallback = new Callback<DatasetResponse>() {
        @Override
        public void onResponse(@NonNull Call<DatasetResponse> call,
                               Response<DatasetResponse> response) {
            if (response.body() != null) {
                DBHandler.getInstance().resetDatasetTable();
                response.body().toContentValues();
            }
        }

        @Override
        public void onFailure(@NonNull Call<DatasetResponse> call, @NonNull Throwable t) {
        }
    };*/

    private void getDataset(String token){
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(150, TimeUnit.SECONDS)
                .readTimeout(150, TimeUnit.SECONDS)
                .writeTimeout(150, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build();
        Gson gson = new Gson();
        String url = BuildConfig.BASE_URL+"app/dataset";
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization" , "Bearer "+token)
                .addHeader("Accept" , "application/json")
                .build();
        Call call = okHttpClient.newCall(request);
        try {
            okhttp3.Response response = call.execute();
            if(response != null && response.isSuccessful()){
                ResponseBody body = response.body();
                if(body != null) {
                    DatasetResponse datasetResponse = gson.fromJson(body.string(), DatasetResponse.class);
                    datasetResponse.toContentValues();
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getStoreDataset(String token){
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(150, TimeUnit.SECONDS)
                .readTimeout(150, TimeUnit.SECONDS)
                .writeTimeout(150, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build();
        Gson gson = new Gson();
        String url = BuildConfig.BASE_URL+"appstores/dataset";
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization" , "Bearer "+token)
                .addHeader("Accept" , "application/json")
                .build();
        Call call = okHttpClient.newCall(request);
        try {
            okhttp3.Response response = call.execute();
            if(response != null && response.isSuccessful()){
                ResponseBody body = response.body();
                if(body != null) {
                    StoreDataset datasetResponse = gson.fromJson(body.string(), StoreDataset.class);
                    datasetResponse.toContentValues();
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
