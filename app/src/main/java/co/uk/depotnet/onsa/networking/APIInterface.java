package co.uk.depotnet.onsa.networking;

import co.uk.depotnet.onsa.modals.Disclaimer;
import co.uk.depotnet.onsa.modals.actions.ActionResponse;
import co.uk.depotnet.onsa.modals.briefings.BriefingsDocModal;
import co.uk.depotnet.onsa.modals.briefings.BriefingsDocument;
import co.uk.depotnet.onsa.modals.briefings.IssuedModel;
import co.uk.depotnet.onsa.modals.briefings.ReceivedModel;
import co.uk.depotnet.onsa.modals.hseq.HseqDataset;
import co.uk.depotnet.onsa.modals.hseq.ToolTipModel;
import co.uk.depotnet.onsa.modals.httprequests.ActiveMfa;
import co.uk.depotnet.onsa.modals.httprequests.ResetPassword;
import co.uk.depotnet.onsa.modals.httprequests.VerificationRequest;
import co.uk.depotnet.onsa.modals.httpresponses.SiteActivityModel;
import co.uk.depotnet.onsa.modals.httpresponses.VerificationResult;
import co.uk.depotnet.onsa.modals.notify.NotifyModel;
import co.uk.depotnet.onsa.modals.notify.NotifyReadPush;
import co.uk.depotnet.onsa.modals.responses.DatasetResponse;
import co.uk.depotnet.onsa.modals.responses.JobResponse;
import co.uk.depotnet.onsa.modals.User;
import co.uk.depotnet.onsa.modals.httprequests.UserRequest;

import java.util.ArrayList;
import java.util.List;

import co.uk.depotnet.onsa.modals.responses.MeasureItemResponse;
import co.uk.depotnet.onsa.modals.responses.MenSplitResponse;
import co.uk.depotnet.onsa.modals.schedule.JobEstimate;
import co.uk.depotnet.onsa.modals.schedule.Schedule;
import co.uk.depotnet.onsa.modals.store.DataMyRequests;
import co.uk.depotnet.onsa.modals.store.DataMyStores;
import co.uk.depotnet.onsa.modals.store.DataReceipts;
import co.uk.depotnet.onsa.modals.store.FeatureResult;
import co.uk.depotnet.onsa.modals.store.StockItems;
import co.uk.depotnet.onsa.modals.store.StockLevel;
import co.uk.depotnet.onsa.modals.store.StoreDataset;
import co.uk.depotnet.onsa.modals.timesheet.TimeSheetHours;
import co.uk.depotnet.onsa.modals.timesheet.TimeSheetResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIInterface {

    @POST("signin")
    Call<User> login(@Body UserRequest user);

    @POST("your-account/verifyCode")
    Call<VerificationResult> verifyCode(@Body VerificationRequest verificationRequest);

    @POST("verify2FAChallenge")
    Call<User> verify2FAChallenge(@Body VerificationRequest verificationRequest);

    @PUT("your-account/activate-mfa")
    Call<ActiveMfa> activeMFA();

    @POST("reset-password")
    Call<User> resetPassword(@Body ResetPassword resetPassword);

    @GET("app/disclaimer")
    Call<Disclaimer> getDisclaimer();

    @GET("app/jobs")
    Call<JobResponse> getJobList();

    @GET("app/dataset")
    Call<DatasetResponse> getDataSet();

    @GET("appstores/dataset")
    Call<StoreDataset> getStoreDataSet();

    @GET("appstores/getreceipts")
    Call<DataReceipts> getReceipts();

    @GET("appstores/getrequests")
    Call<DataMyRequests> getMyRequests();

    @GET("appstores/getitem")
    Call<StockItems> getItem(@Query("Barcode") String barcode , @Query("StaId") String StaId);

    @GET("appstores/mystores")
    Call<DataMyStores> getMyStore();

    @GET("appstores/getstocklevel")
    Call<StockLevel> getStockLevel(@Query("StaId") String StaId , @Query("StockItemId") String StockItemId);

    @PUT("appstores/hiderequest/{requestId}")
    Call<Void> hideReequest(@Path("requestId") String requestId);

    @POST("app/jobs/{jobId}/sendrfna")
    Call<Void> sendrfna(@Path("jobId") String jobId);

    @GET("app/features")
    Call<FeatureResult> getFeatures();

    @GET("app/getmensplits")
    Call<MenSplitResponse> getMenSplits();

    @GET("app/getmeasureitems")
    Call<MeasureItemResponse> getMeasureItems();

    @GET("app/jobs/{jobId}/get-site-activity-tasks/{siteActivityTypeId}")
    Call<SiteActivityModel> GetSiteActivityTasks(@Path("jobId") String JobID , @Path("siteActivityTypeId") int SiteActivityTypeID );

    @GET("apphseq/dataset")
    Call<HseqDataset> getHSEQDataSet();

    @GET("apphseq/scheduledinspections")
    Call<List<Schedule>> getHSEQScheduleInspection();

    @GET("apphseq/briefings")
    Call<List<BriefingsDocModal>> getBriefings();

    @GET("apphseq/briefings/{briefingId}")
    Call<BriefingsDocument> GetBriefingsDoc(@Path("briefingId") String briefingId);

    @GET("apphseq/briefings/sharedbyme")
    Call<List<IssuedModel>> getBriefingsIssued();

    @GET("apphseq/briefings/readbyme")
    Call<List<ReceivedModel>> getBriefingsReceived();

    @GET("apphseq/actions/open")
    Call<List<ActionResponse>> getActionsOutstanding();

    @GET("apphseq/actions/closed")
    Call<List<ActionResponse>> getActionsCleared();

    @GET("apphseq/tags")
    Call<ArrayList<String>> methodReturnsTags();

    @GET("apphseq/jobs/{estimateNumber}")
    Call<JobEstimate> GetJobEstimate(@Path("estimateNumber") String estimateNo);

    @GET("apphseq/inspections/template-versions/{inspectionTemplateVersionId}/questions")
    Call<ArrayList<ToolTipModel>> GetInspectionToolTip(@Path("inspectionTemplateVersionId") String templateid);

    @GET("apphseq/notifications")
    Call<ArrayList<NotifyModel>> GetAllNotification();

    @POST("apphseq/notifications/mark-as-read")
    Call<NotifyReadPush> notificationRead(@Body NotifyReadPush readPush);

    @POST("apphseq/notifications/mark-as-pushed")
    Call<NotifyReadPush> notificationpush(@Body NotifyReadPush readPush);

    @GET("app/timesheets/timesheet-hours")
    Call<TimeSheetHours> getTimesheetHours(@Query("weekCommencing") String weekCommencing);

    @GET("app/timesheets")
    Call<TimeSheetResponse> getTimeSheets();
}
