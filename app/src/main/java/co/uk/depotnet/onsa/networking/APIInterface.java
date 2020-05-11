package co.uk.depotnet.onsa.networking;

import co.uk.depotnet.onsa.modals.Disclaimer;
import co.uk.depotnet.onsa.modals.Driver;
import co.uk.depotnet.onsa.modals.Feature;
import co.uk.depotnet.onsa.modals.Job;
import co.uk.depotnet.onsa.modals.httprequests.ActiveMfa;
import co.uk.depotnet.onsa.modals.httprequests.ResetPassword;
import co.uk.depotnet.onsa.modals.httprequests.VerificationRequest;
import co.uk.depotnet.onsa.modals.httpresponses.VerificationResult;
import co.uk.depotnet.onsa.modals.responses.DatasetResponse;
import co.uk.depotnet.onsa.modals.responses.JobResponse;
import co.uk.depotnet.onsa.modals.KitBagDocument;
import co.uk.depotnet.onsa.modals.MaterialType;
import co.uk.depotnet.onsa.modals.SurfaceType;
import co.uk.depotnet.onsa.modals.User;
import co.uk.depotnet.onsa.modals.Vehicle;
import co.uk.depotnet.onsa.modals.httprequests.UserRequest;
import co.uk.depotnet.onsa.modals.httpresponses.BaseTask;

import java.util.List;

import co.uk.depotnet.onsa.modals.store.DataMyRequests;
import co.uk.depotnet.onsa.modals.store.DataMyStores;
import co.uk.depotnet.onsa.modals.store.DataReceipts;
import co.uk.depotnet.onsa.modals.store.FeatureResult;
import co.uk.depotnet.onsa.modals.store.MyStore;
import co.uk.depotnet.onsa.modals.store.StockItems;
import co.uk.depotnet.onsa.modals.store.StockLevel;
import co.uk.depotnet.onsa.modals.store.StoreDataset;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIInterface {

    @POST("/signin")
    Call<User> login(@Body UserRequest user);

    @POST("/your-account/verifyCode")
    Call<VerificationResult> verifyCode(@Body VerificationRequest verificationRequest);

    @POST("/verify2FAChallenge")
    Call<User> verify2FAChallenge(@Body VerificationRequest verificationRequest);

    @PUT("/your-account/activate-mfa")
    Call<ActiveMfa> activeMFA();

    @POST("/reset-password")
    Call<User> resetPassword(@Body ResetPassword resetPassword);

    @GET("/app/disclaimer")
    Call<Disclaimer> getDisclaimer();

    @GET("/app/jobs")
    Call<JobResponse> getJobList();

    @GET("/app/dataset")
    Call<DatasetResponse> getDataSet();

    @GET("/appstores/dataset")
    Call<StoreDataset> getStoreDataSet();

    @GET("/appstores/getreceipts")
    Call<DataReceipts> getReceipts();

    @GET("/appstores/getrequests")
    Call<DataMyRequests> getMyRequests();

    @GET("/appstores/getitem")
    Call<StockItems> getItem(@Query("Barcode") String barcode , @Query("StaId") String StaId);

    @GET("/appstores/mystores")
    Call<DataMyStores> getMyStore();

    @GET("/appstores/getstocklevel")
    Call<StockLevel> getStockLevel(@Query("StaId") String StaId , @Query("StockItemId") String StockItemId);

    @PUT("/appstores/hiderequest/{requestId}")
    Call<Void> hideReequest(@Path("requestId") String requestId);

    @POST("/app/jobs/{jobId}/sendrfna")
    Call<Void> sendrfna(@Path("jobId") String jobId);

    @GET("/app/features")
    Call<FeatureResult> getFeatures();

}
