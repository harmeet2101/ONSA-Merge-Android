package co.uk.depotnet.onsa.networking;

import java.util.concurrent.TimeUnit;

import co.uk.depotnet.onsa.BuildConfig;
import co.uk.depotnet.onsa.modals.Disclaimer;
import co.uk.depotnet.onsa.modals.MeasureItems;
import co.uk.depotnet.onsa.modals.MenSplit;
import co.uk.depotnet.onsa.modals.httprequests.ActiveMfa;
import co.uk.depotnet.onsa.modals.httprequests.ResetPassword;
import co.uk.depotnet.onsa.modals.httprequests.VerificationRequest;
import co.uk.depotnet.onsa.modals.httpresponses.VerificationResult;
import co.uk.depotnet.onsa.modals.responses.DatasetResponse;
import co.uk.depotnet.onsa.modals.responses.JobResponse;
import co.uk.depotnet.onsa.modals.User;
import co.uk.depotnet.onsa.modals.httprequests.UserRequest;


import co.uk.depotnet.onsa.modals.responses.MeasureItemResponse;
import co.uk.depotnet.onsa.modals.responses.MenSplitResponse;
import co.uk.depotnet.onsa.modals.store.DataMyRequests;
import co.uk.depotnet.onsa.modals.store.DataMyStores;
import co.uk.depotnet.onsa.modals.store.DataReceipts;
import co.uk.depotnet.onsa.modals.store.FeatureResult;
import co.uk.depotnet.onsa.modals.store.StockItems;
import co.uk.depotnet.onsa.modals.store.StockLevel;
import co.uk.depotnet.onsa.modals.store.StoreDataset;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;

public class APICalls {

    public static Call<User> callLogin(UserRequest user){
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        return apiInterface.login(user);
    }

    public static Call<VerificationResult> verifyCode(VerificationRequest verificationRequest , String authToken){
        APIInterface apiInterface = APIClient.createService(APIInterface.class , authToken);
        return apiInterface.verifyCode(verificationRequest);
    }

    public static Call<User> verify2FAChallenge(VerificationRequest verificationRequest , String authToken){
        APIInterface apiInterface = APIClient.createService(APIInterface.class , authToken);
        return apiInterface.verify2FAChallenge(verificationRequest);
    }

    public static Call<ActiveMfa> activeMFA(String authToken){
        APIInterface apiInterface = APIClient.createService(APIInterface.class , authToken);
        return apiInterface.activeMFA();
    }

    public static Call<User> resetPassword(ResetPassword resetPassword){
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        return apiInterface.resetPassword(resetPassword);
    }


    public static Call<Disclaimer> getDisclaimer(String authToken){
        APIInterface apiInterface = APIClient.createService(APIInterface.class , authToken);
        return apiInterface.getDisclaimer();
    }
    public static void getDisclaimerLogo(String authToken , Callback callback){
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(150, TimeUnit.SECONDS)
                .readTimeout(150, TimeUnit.SECONDS)
                .writeTimeout(150, TimeUnit.SECONDS)
                .addInterceptor(logging).build();

        Request request = new Request.Builder()
                .url(BuildConfig.BASE_URL+"app/disclaimer/logo")
                .addHeader("Authorization" , "Bearer " +authToken)
                .build();
        client.newCall(request).enqueue(callback);

//        APIInterface apiInterface = APIClient.getClient(APIInterface.class);
//        return apiInterface. getDisclaimerLogo();
    }



    public static Call<JobResponse> getJobList(String authToken){
        APIInterface apiInterface = APIClient.createService(APIInterface.class , authToken);
        return apiInterface.getJobList();
    }

    public static Call<DatasetResponse> getDataSet(String authToken){
        APIInterface apiInterface = APIClient.createService(APIInterface.class , authToken);
        return apiInterface.getDataSet();
    }

    public static Call<StoreDataset> getStoreDataSet(String authToken){
        APIInterface apiInterface = APIClient.createService(APIInterface.class , authToken);
        return apiInterface.getStoreDataSet();
    }

    public static Call<DataReceipts> getReceipts(String authToken){
        APIInterface apiInterface = APIClient.createService(APIInterface.class , authToken);
        return apiInterface.getReceipts();
    }

    public static Call<DataMyRequests> getMyRequests(String authToken){
        APIInterface apiInterface = APIClient.createService(APIInterface.class , authToken);
        return apiInterface.getMyRequests();
    }

    public static Call<StockLevel> getStockLevel(String authToken , String staId , String stockItemId){
        APIInterface apiInterface = APIClient.createService(APIInterface.class , authToken);
        return apiInterface.getStockLevel(staId , stockItemId);
    }

    public static Call<StockItems> getItem(String authToken , String barcode, String staId){
        APIInterface apiInterface = APIClient.createService(APIInterface.class , authToken);
        return apiInterface.getItem(barcode ,staId);
    }

    public static Call<DataMyStores> getMyStore(String authToken){
        APIInterface apiInterface = APIClient.createService(APIInterface.class , authToken);
        return apiInterface.getMyStore();
    }

    public static Call<Void> hideReequest(String authToken,String requestId){
        APIInterface apiInterface = APIClient.createService(APIInterface.class , authToken);
        return apiInterface.hideReequest(requestId);
    }

    public static Call<Void> sendRfna(String jobId, String authToken){
        APIInterface apiInterface = APIClient.createService(APIInterface.class , authToken);
        return apiInterface.sendrfna(jobId);
    }

    public static Call<FeatureResult> featureResultCall(String authToken){
        APIInterface apiInterface = APIClient.createService(APIInterface.class , authToken);
        return apiInterface.getFeatures();
    }

    public static Call<MenSplitResponse> getMenSplits(String authToken){
        APIInterface apiInterface = APIClient.createService(APIInterface.class , authToken);
        return apiInterface.getMenSplits();
    }

    public static Call<MeasureItemResponse> getMeasureItems(String authToken){
        APIInterface apiInterface = APIClient.createService(APIInterface.class , authToken);
        return apiInterface.getMeasureItems();
    }

}
