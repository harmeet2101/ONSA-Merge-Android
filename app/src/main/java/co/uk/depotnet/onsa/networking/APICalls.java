package co.uk.depotnet.onsa.networking;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import co.uk.depotnet.onsa.BuildConfig;
import co.uk.depotnet.onsa.modals.AppDFEItems;
import co.uk.depotnet.onsa.modals.Disclaimer;
import co.uk.depotnet.onsa.modals.WorkItem;
import co.uk.depotnet.onsa.modals.actions.ActionResponse;
import co.uk.depotnet.onsa.modals.briefings.BriefingsDocModal;
import co.uk.depotnet.onsa.modals.briefings.BriefingsDocument;
import co.uk.depotnet.onsa.modals.briefings.IssuedModel;
import co.uk.depotnet.onsa.modals.briefings.ReceivedModel;
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
import co.uk.depotnet.onsa.modals.timesheet.TimeSheet;
import co.uk.depotnet.onsa.modals.timesheet.TimeSheetHours;
import co.uk.depotnet.onsa.modals.timesheet.TimeSheetResponse;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;

public class APICalls {

    public static Call<User> callLogin(UserRequest user , String authToken){
        APIInterface apiInterface;
        if(!TextUtils.isEmpty(authToken)){
            apiInterface = APIClient.createService(APIInterface.class , authToken);
        }else{
            apiInterface = APIClient.getClient().create(APIInterface.class);
        }
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

    public static Call<Void> signOut(String jwtId , String authToken){
        APIInterface apiInterface = APIClient.createService(APIInterface.class , authToken);
        return apiInterface.signOut(jwtId);
    }

    public static Call<User> resetPassword(ResetPassword resetPassword){
        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        return apiInterface.resetPassword(resetPassword);
    }


    public static Call<Disclaimer> getDisclaimer(String authToken){
        APIInterface apiInterface = APIClient.createService(APIInterface.class , authToken);
        return apiInterface.getDisclaimer();
    }

    public static Call<TimeSheetHours> getTimesheetHours(String authToken , String weekCommencing){
        APIInterface apiInterface = APIClient.createService(APIInterface.class , authToken);
        return apiInterface.getTimesheetHours(weekCommencing);
    }

    public static Call<TimeSheetResponse> getTimesheets(String authToken){
        APIInterface apiInterface = APIClient.createService(APIInterface.class , authToken);
        return apiInterface.getTimeSheets();
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

    public static Call<AppDFEItems> getDfeItems(String authToken, String contractID , int raisedIssueNumber){
        APIInterface apiInterface = APIClient.createService(APIInterface.class , authToken);
        return apiInterface.getDfeItems(contractID , raisedIssueNumber);
    }

    public static Call<DataReceipts> getReceipts(String authToken){
        APIInterface apiInterface = APIClient.createService(APIInterface.class , authToken);
        return apiInterface.getReceipts();
    }

    public static Call<DataMyRequests> getMyRequests(String authToken){
        APIInterface apiInterface = APIClient.createService(APIInterface.class , authToken);
        return apiInterface.getMyRequests();
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

    public static Call<SiteActivityModel> GetSiteActivityTasks(String authToken , String jobId , int siteActivityTypeID) {
        APIInterface apiInterface = APIClient.createService(APIInterface.class , authToken);
        return apiInterface.GetSiteActivityTasks(jobId , siteActivityTypeID);
    }

    public static Call<List<Schedule>> getHseqScheduleList(String authToken){
        APIInterface apiInterface = APIClient.createService(APIInterface.class , authToken);
        return apiInterface.getHSEQScheduleInspection();
    }

    public static Call<List<BriefingsDocModal>> getBriefingsList(String authToken){
        APIInterface apiInterface = APIClient.createService(APIInterface.class , authToken);
        return apiInterface.getBriefings();
    }

    public static Call<BriefingsDocument> GetBriefingsDocData(String briefingId, String authToken){
        APIInterface apiInterface = APIClient.createService(APIInterface.class , authToken);
        return apiInterface.GetBriefingsDoc(briefingId);
    }
    public static Call<List<IssuedModel>> getBriefingsIssuedList(String authToken){
        APIInterface apiInterface = APIClient.createService(APIInterface.class , authToken);
        return apiInterface.getBriefingsIssued();
    }
    public static Call<List<ReceivedModel>> getBriefingsReceivedList(String authToken){
        APIInterface apiInterface = APIClient.createService(APIInterface.class , authToken);
        return apiInterface.getBriefingsReceived();
    }
    public static Call<List<ActionResponse>> getActionsOutstandingList(String authToken){
        APIInterface apiInterface = APIClient.createService(APIInterface.class , authToken);
        return apiInterface.getActionsOutstanding();
    }
    public static Call<List<ActionResponse>> getActionsClearedList(String authToken){
        APIInterface apiInterface = APIClient.createService(APIInterface.class , authToken);
        return apiInterface.getActionsCleared();
    }
    public static Call<ArrayList<String>> getTags(String authToken){
        APIInterface apiInterface = APIClient.createService(APIInterface.class , authToken);
        return apiInterface.methodReturnsTags();
    }
    public static Call<JobEstimate> GetJobEstimateDetail(String estimateNo, String authToken){
        APIInterface apiInterface = APIClient.createService(APIInterface.class , authToken);
        return apiInterface.GetJobEstimate(estimateNo);
    }
    public static Call<ArrayList<ToolTipModel>> GetInspectionToolTipList(String templateId, String authToken){
        APIInterface apiInterface = APIClient.createService(APIInterface.class , authToken);
        return apiInterface.GetInspectionToolTip(templateId);
    }
    public static Call<ArrayList<NotifyModel>> getNotifications(String authToken){
        APIInterface apiInterface = APIClient.createService(APIInterface.class , authToken);
        return apiInterface.GetAllNotification();
    }
    public static Call<NotifyReadPush> callNotificationMarkRead(NotifyReadPush readPush, String authToken){
        APIInterface apiInterface = APIClient.createService(APIInterface.class, authToken);
        return apiInterface.notificationRead(readPush);
    }
    public static Call<NotifyReadPush> callNotificationMarkPush(NotifyReadPush readPush,String authToken){
        APIInterface apiInterface = APIClient.createService(APIInterface.class, authToken);
        return apiInterface.notificationpush(readPush);
    }



}
