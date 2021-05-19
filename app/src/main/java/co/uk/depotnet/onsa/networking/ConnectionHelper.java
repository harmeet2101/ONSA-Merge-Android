package co.uk.depotnet.onsa.networking;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import android.text.TextUtils;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import co.uk.depotnet.onsa.BuildConfig;
import co.uk.depotnet.onsa.activities.LoginActivity;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.modals.Job;
import co.uk.depotnet.onsa.modals.User;
import co.uk.depotnet.onsa.modals.forms.Answer;
import co.uk.depotnet.onsa.modals.forms.Submission;
import co.uk.depotnet.onsa.modals.hseq.PhotoComments;
import co.uk.depotnet.onsa.modals.hseq.PhotoResponse;
import co.uk.depotnet.onsa.modals.hseq.PhotoTags;
import co.uk.depotnet.onsa.modals.httprequests.PhotoRequest;
import co.uk.depotnet.onsa.modals.httprequests.SignatureRequest;
import co.uk.depotnet.onsa.modals.httprequests.UserRequest;
import co.uk.depotnet.onsa.networking.response.IssueResponse;
import co.uk.depotnet.onsa.utils.AppPreferences;
import co.uk.depotnet.onsa.views.MaterialAlertProgressDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;

public class ConnectionHelper {

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private final Context context;
    private final OkHttpClient okHttpClient;
    private final Gson gson;
    private final DBHandler dbHandler;

    public ConnectionHelper(Context context) {
        this.context = context;
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(150, TimeUnit.SECONDS)
                .readTimeout(150, TimeUnit.SECONDS)
                .writeTimeout(150, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .addInterceptor(new AuthInterceptor())
                .build();
        gson = new Gson();
        this.dbHandler = DBHandler.getInstance();
    }

    private User refreshToken(UserRequest userRequest) {
        User user = null;
        String url = BuildConfig.BASE_URL + "signin";

        String jsonSubmission = gson.toJson(userRequest);
        RequestBody body = RequestBody.create(JSON, jsonSubmission);
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    String result = responseBody.string();
                    user = gson.fromJson(result, User.class);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return user;
    }


    public Response submitPollingSurvey(String endPoint, String photoEndPoint,
                                        Submission submission, FragmentManager fm) {

        Map<String, Object> jobData = new HashMap<>();
        Map<String, Object> fluidityTask = new HashMap<>();
        Map<String, Object> solution = new HashMap<>();
        Map<String, Object> assetSection = new HashMap<>();
        Map<String, Object> riskAssessment = new HashMap<>();
        Job job = dbHandler.getJob(submission.getJobID());
        boolean isSubJob = job!= null && job.isSubJob();

        if (!TextUtils.isEmpty(endPoint)) {
            endPoint = endPoint.replace("{jobId}", submission.getJobID());
        }

        if (!TextUtils.isEmpty(photoEndPoint)) {
            photoEndPoint = photoEndPoint.replace("{jobId}", submission.getJobID());
        }


        ArrayList<Answer> answers = dbHandler.getAnswers(submission.getID());

        String uniqueId = UUID.randomUUID().toString();
        Map<String, Object> requestMap = new HashMap<>();
        ArrayList<Answer> photosToUpload = new ArrayList<>();
        ArrayList<Answer> signatures = new ArrayList<>();

        requestMap.put("submissionId", uniqueId);
        requestMap.put("submittedDate", submission.getDate());
        requestMap.put("dateTaken", submission.getDate());
        requestMap.put("latitude", submission.getLatitude());
        requestMap.put("longitude", submission.getLongitude());


        for (int c = 0; c < answers.size(); c++) {
            Answer answer = answers.get(c);
            if (answer.shouldUpload()) {
                if (answer.isPhoto() == 0) {
                    String repeatId = answer.getRepeatID();
                    String uploadId = answer.getUploadID();



                    if (!TextUtils.isEmpty(repeatId)) {  //INFO: repeat id not null
                        if (repeatId.equalsIgnoreCase("negDfeItems")) {
                            repeatId = "dfeItems";
                        }
                        if (repeatId.equalsIgnoreCase("jobDataSection")) {

                            if (uploadId.equalsIgnoreCase("address")) {
                                if(!isSubJob) {
                                    jobData.put("surveyAddress", answer.getAnswer());
                                }else{
                                    jobData.put(uploadId, answer.getAnswer());
                                }
                            } else if (uploadId.equalsIgnoreCase("latitude")) {
                                if(!isSubJob) {
                                    jobData.put("surveyLatitude", answer.getAnswer());
                                }else{
                                    jobData.put(uploadId, answer.getAnswer());
                                }
                            } else if (uploadId.equalsIgnoreCase("longitude")) {
                                if(!isSubJob) {
                                    jobData.put("surveyLongitude", answer.getAnswer());
                                }else{
                                    jobData.put(uploadId, answer.getAnswer());
                                }
                            } else {
                                jobData.put(uploadId, answer.getAnswer());
                            }

                        } else if (repeatId.equalsIgnoreCase("fluidityTasksSection")) {
                            fluidityTask.put(uploadId, answer.getAnswer());
                        } else if (repeatId.equalsIgnoreCase("solutionSection")) {
                            solution.put(uploadId, answer.getAnswer());
                        } else if (repeatId.equalsIgnoreCase("dfeItems")) {
                            Map<String, Map<String, Object>> repeatMap =
                                    (Map<String, Map<String, Object>>) solution.get(repeatId); // getRepeatobject

                            if (repeatMap == null) {
                                repeatMap = new HashMap<>();
                                solution.put(repeatId, repeatMap);
                            }

                            String repeatCount = String.valueOf(answer.getRepeatCount());

                            if (!repeatMap.containsKey(repeatCount)) {
                                Map<String, Object> repMap = new HashMap<>();
                                repeatMap.put(repeatCount, repMap);
                            }

                            Map<String, Object> mapToAppendTo = repeatMap.get(repeatCount);
                            if (answer.isMultilist()) {

                                if (!mapToAppendTo.containsKey(uploadId)) {
                                    mapToAppendTo.put(uploadId, new ArrayList<String>());
                                }

                                ArrayList<String> multiArr = (ArrayList<String>) mapToAppendTo.get(uploadId);
                                multiArr.add(answer.getAnswer());

                            } else {
                                mapToAppendTo.put(answer.getUploadID(), answer.getAnswer());
                            }


                        } else if (repeatId.equalsIgnoreCase("assets")) {
                            if (uploadId.equalsIgnoreCase("address")) {
                                uploadId = "locateMeAddress";
                            } else if (uploadId.equalsIgnoreCase("latitude")) {
                                uploadId = "locateMeLatitude";
                            } else if (uploadId.equalsIgnoreCase("longitude")) {
                                uploadId = "locateMeLongitude";
                            }/*else if (uploadId.equalsIgnoreCase("dropWire")) {
                                uploadId = "dropDownWireCount";
                            }*/


                            Map<String, Map<String, Object>> repeatMap =
                                    (Map<String, Map<String, Object>>) assetSection.get(repeatId); // getRepeatobject

                            if (repeatMap == null) {
                                repeatMap = new HashMap<>();
                                assetSection.put(repeatId, repeatMap);
                            }

                            String repeatCount = String.valueOf(answer.getRepeatCount());
                            if (!repeatMap.containsKey(repeatCount)) {
                                Map<String, Object> repMap = new HashMap<>();
                                repeatMap.put(repeatCount, repMap);
                            }

                            Map<String, Object> mapToAppendTo = repeatMap.get(repeatCount);

                            if (uploadId.equalsIgnoreCase("poles")
                                    || uploadId.equalsIgnoreCase("blockTerminals")
                                    || uploadId.equalsIgnoreCase("anchors")
                                    || uploadId.equalsIgnoreCase("jointClosures")
                                    || uploadId.equalsIgnoreCase("aerialCables")
                                    || uploadId.equalsIgnoreCase("ugCables")
                                    || uploadId.equalsIgnoreCase("surfaceTypes")
                                    || uploadId.equalsIgnoreCase("materialTypes")
                                    || uploadId.equalsIgnoreCase("dacs")
                            ) {
                                String value = answer.getAnswer();
                                if (!TextUtils.isEmpty(value)) {

                                    Answer quantityAnswer = dbHandler.getAnswer(submission.getID(), "quantity",
                                            uploadId, answer.getRepeatCount());
                                    if (quantityAnswer != null &&
                                            !TextUtils.isEmpty(quantityAnswer.getAnswer())) {
                                        String quantities = quantityAnswer.getAnswer();

                                        if (!mapToAppendTo.containsKey(uploadId)) {
                                            mapToAppendTo.put(uploadId, new ArrayList<HashMap<String, String>>());
                                        }


                                        ArrayList<HashMap<String, String>> multiArr = (ArrayList<HashMap<String, String>>) mapToAppendTo.get(uploadId);
                                        String[] values = value.split(",");
                                        String[] quantity = quantities.split(",");


                                        for (int i = 0; i < values.length && i < quantity.length; i++) {
                                            HashMap<String, String> map = new HashMap<>();
                                            map.put("id", values[i]);
                                            map.put("quantity", quantity[i]);


                                            multiArr.add(map);
                                        }

                                        mapToAppendTo.put(uploadId, multiArr);
                                    }

                                }
                            } else {
                                if (answer.isMultilist()) {
                                    if (!mapToAppendTo.containsKey(uploadId)) {
                                        mapToAppendTo.put(uploadId, new ArrayList<String>());
                                    }

                                    ArrayList<String> multiArr = (ArrayList<String>) mapToAppendTo.get(uploadId);
                                    multiArr.add(answer.getAnswer());

                                } else {
                                    mapToAppendTo.put(uploadId, answer.getAnswer());
                                }
                            }

                        } else if (repeatId.equalsIgnoreCase("riskAssessmentSection")) {
                            riskAssessment.put(uploadId, answer.getAnswer());
                        } else if (repeatId.equalsIgnoreCase("riskElements")) {


                            Map<String, Map<String, Object>> repeatMap =
                                    (Map<String, Map<String, Object>>) riskAssessment.get(repeatId); // getRepeatobject

                            if (repeatMap == null) {
                                repeatMap = new HashMap<>();
                                riskAssessment.put(repeatId, repeatMap);
                            }

                            String repeatCount = String.valueOf(answer.getRepeatCount());

                            if (!repeatMap.containsKey(repeatCount)) {
                                Map<String, Object> repMap = new HashMap<>();
                                repeatMap.put(repeatCount, repMap);
                            }

                            Map<String, Object> mapToAppendTo = repeatMap.get(repeatCount);
                            if (answer.isMultilist()) {

                                if (!mapToAppendTo.containsKey(uploadId)) {
                                    mapToAppendTo.put(uploadId, new ArrayList<String>());
                                }

                                ArrayList<String> multiArr = (ArrayList<String>) mapToAppendTo.get(uploadId);
                                multiArr.add(answer.getAnswer());

                            } else {
                                mapToAppendTo.put(answer.getUploadID(), answer.getAnswer());
                            }
                        }

                    }
                } else {
                    String signatureUrl = answer.getSignatureUrl();
                    boolean isSignature = !TextUtils.isEmpty(signatureUrl);


//                    boolean isSignature = signatureUrl != null && !signatureUrl.isEmpty();
                    if (isSignature) {
                        if(isSubJob){
                            answer.setSignatureUrl("app/subjob/{jobId}/upload-signature");
                        }
                        signatures.add(answer);
                    } else {
                        photosToUpload.add(answer);
                    }
                }
            }
        }


        if (!signatures.isEmpty()) {
            for (Answer signature : signatures) {
                String signatureID = uploadSignature(uniqueId, signature, submission.getJobID(), fm);
                if (signature.getUploadID().equalsIgnoreCase("supervisorSignatureId")) {
                    riskAssessment.put(signature.getUploadID(), signatureID);
                } else {
                    requestMap.put("SignatureId", signatureID);
                }
            }
        }

        if(!fluidityTask.containsKey("tMMeetingRequired")){
            fluidityTask.put("tMMeetingRequired" , 2);
        }

        requestMap.put("jobDataSection", jobData);
        requestMap.put("fluidityTasksSection", fluidityTask);
        requestMap.put("solutionSection", solution);
        requestMap.put("assetsSection", assetSection);
        requestMap.put("riskAssessmentSection", riskAssessment);


        for (Map.Entry<String, Object> entry : solution.entrySet()) {
            if (entry.getValue() instanceof Map) {
                Map<String, Object> tmpMap = (Map<String, Object>) entry.getValue();
                ArrayList<Object> arrayList = new ArrayList<>();
                for (Map.Entry<String, Object> item : tmpMap.entrySet()) {
                    arrayList.add(item.getValue());
                }
                solution.put(entry.getKey(), arrayList);
            }
        }

        for (Map.Entry<String, Object> entry : riskAssessment.entrySet()) {
            if (entry.getValue() instanceof Map) {
                Map<String, Object> tmpMap = (Map<String, Object>) entry.getValue();
                ArrayList<Object> arrayList = new ArrayList<>();
                for (Map.Entry<String, Object> item : tmpMap.entrySet()) {
                    arrayList.add(item.getValue());
                }
                riskAssessment.put(entry.getKey(), arrayList);
            }
        }

        for (Map.Entry<String, Object> entry : assetSection.entrySet()) {
            if (entry.getValue() instanceof Map) {
                Map<String, Object> tmpMap = (Map<String, Object>) entry.getValue();
                ArrayList<Object> arrayList = new ArrayList<>();
                for (Map.Entry<String, Object> item : tmpMap.entrySet()) {
                    arrayList.add(item.getValue());
                }
                assetSection.put(entry.getKey(), arrayList);
            }
        }

//        ((HashMap)requestMap.get("assetsSection")).get("assets")

        if(isSubJob){
            if(requestMap.containsKey("assetsSection")){
                Object assets = requestMap.remove("assetsSection");
                if((assets instanceof  HashMap) && ((HashMap) assets).get("assets") != null) {
                    requestMap.put("assets" , ((HashMap) assets).get("assets"));
                }
            }

            if (photoEndPoint != null) {
                uploadPhotos(photosToUpload, uniqueId, photoEndPoint, fm, "");
            }
        }

        String jsonSubmission = gson.toJson(requestMap);
        RequestBody body = RequestBody.create(JSON, jsonSubmission);

        if (!TextUtils.isEmpty(endPoint)) {
            String url = BuildConfig.BASE_URL + endPoint;
            Response response = performJSONNetworking(body, url);

            if (response == null || !response.isSuccessful()) {
                return response;
            }

            if (!isSubJob && photoEndPoint != null) {
                uploadPhotos(photosToUpload, uniqueId, photoEndPoint, fm, "");
            }

            return response;
        } else if (photoEndPoint != null && !photoEndPoint.isEmpty()) {
            uploadPhotos(photosToUpload, uniqueId, photoEndPoint, fm, "");
        }

        return null;
    }


    public Response sendRfna(Submission submission) {

        String url = BuildConfig.BASE_URL + "app/jobs/" + submission.getJobID() + "/sendrfna";
        return performJSONNetworking(RequestBody.create(JSON, ""), url);
    }


    public Response submitForm(String endPoint, String photoEndPoint,
                               Submission submission, FragmentManager fm) {

        String jsonFileName = submission.getJsonFileName();

        if (!TextUtils.isEmpty(endPoint)) {
            endPoint = endPoint.replace("{jobId}", submission.getJobID());
        }

        if (!TextUtils.isEmpty(photoEndPoint)) {
            photoEndPoint = photoEndPoint.replace("{jobId}", submission.getJobID());
        }

        ArrayList<Answer> answers = dbHandler.getAnswers(submission.getID());

        String uniqueId = UUID.randomUUID().toString();
        Map<String, Object> requestMap = new HashMap<>();
        ArrayList<Answer> photosToUpload = new ArrayList<>();
        ArrayList<Answer> signatures = new ArrayList<>();

        requestMap.put("submissionId", uniqueId);

        if(!TextUtils.isEmpty(submission.getJsonFileName()) &&
                (submission.getJsonFileName().startsWith("sub_job_"))){
            requestMap.put("submittedDateTime", submission.getDate());
            if(submission.getJsonFileName().equalsIgnoreCase("sub_job_log_measure.json")){
                requestMap.put("dateLogged", submission.getDate());
            }
        }


        requestMap.put("submittedDate", submission.getDate());
        requestMap.put("dateTaken", submission.getDate());
        requestMap.put("latitude", submission.getLatitude());
        requestMap.put("longitude", submission.getLongitude());


        boolean isBookOn = !TextUtils.isEmpty(endPoint) && (endPoint.contains("book-on") || endPoint.contains("book-off"));
        boolean containsUserIds = false;

        for (int c = 0; c < answers.size(); c++) {
            Answer answer = answers.get(c);
            String strAns = answer.getAnswer();
            Object ans = strAns;
            if (!TextUtils.isEmpty(strAns)) {
                ans = parseObject(strAns);
            }

            if (answer.shouldUpload()) {
                String signatureUrl = answer.getSignatureUrl();
                boolean isSignature = !TextUtils.isEmpty(signatureUrl);
                if (answer.isPhoto() == 0 || (isSignature && signatureUrl.equalsIgnoreCase("signatureFileBytes"))) {
                    if (isSignature) {
                        Bitmap bm = BitmapFactory.decodeFile(answer.getAnswer());
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bm.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                        byte[] byteArrayImage = baos.toByteArray();
                        strAns = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
                        ans = strAns;
                    }

                    String repeatId = answer.getRepeatID();
                    String uploadId = answer.getUploadID();
                    if (!TextUtils.isEmpty(uploadId) && uploadId.equalsIgnoreCase("userIds")) {
                        containsUserIds = true;
                    }

                    if (!TextUtils.isEmpty(repeatId)) {  //INFO: repeat id not null
                        if (repeatId.equalsIgnoreCase("negItems")) {
                            repeatId = "items";
                        }
                        if (!requestMap.containsKey(repeatId)) {  // if parent not contains repeatId then create new object
                            requestMap.put(repeatId,
                                    new HashMap<String, Map<String, Object>>());
                        }

                        Map<String, Map<String, Object>> repeatMap =
                                (Map<String, Map<String, Object>>) requestMap.get(repeatId); // getRepeatobject


                        String repeatCount = String.valueOf(answer.getRepeatCount());

                        if (!repeatMap.containsKey(repeatCount)) {
                            Map<String, Object> repMap = new HashMap<>();
                            repeatMap.put(repeatCount, repMap);
                        }

                        Map<String, Object> mapToAppendTo = repeatMap.get(repeatCount);
                        if (answer.isMultilist()) {

                            if (!mapToAppendTo.containsKey(uploadId)) {
                                mapToAppendTo.put(uploadId, new ArrayList<String>());
                            }

                            ArrayList<String> multiArr = (ArrayList<String>) mapToAppendTo.get(uploadId);
                            multiArr.add(strAns);

                        } else {
                            mapToAppendTo.put(answer.getUploadID(), ans);
                        }

                    } else {
                        if (answer.isMultilist()) {
                            if (!requestMap.containsKey(uploadId)) {
                                requestMap.put(uploadId, new ArrayList<String>());
                            }
                            ArrayList<String> multiArr = (ArrayList<String>) requestMap.get(uploadId);
                            multiArr.add(strAns);
                        } else {
                            requestMap.put(uploadId, ans);
                        }
                    }
                } else if (isSignature) {
                        if (endPoint != null && endPoint.contains("appstores")) {
                            Bitmap bm = BitmapFactory.decodeFile(answer.getAnswer());
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bm.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                            byte[] byteArrayImage = baos.toByteArray();
                            String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
                            requestMap.put("SignatureImage", encodedImage);
                        } else {
                            signatures.add(answer);
                        }
                } else {
                    photosToUpload.add(answer);
                }
            }
        }

        for (Map.Entry<String, Object> entry : requestMap.entrySet()) {
            if (entry.getValue() instanceof Map) {
                Map<String, Object> tmpMap = (Map<String, Object>) entry.getValue();
                ArrayList<Object> arrayList = new ArrayList<>();
                for (Map.Entry<String, Object> item : tmpMap.entrySet()) {
                    arrayList.add(item.getValue());
                }
                requestMap.put(entry.getKey(), arrayList);
            }
        }

        boolean isSubJobPresite = false;

        if(!TextUtils.isEmpty(jsonFileName)){
            if(jsonFileName.equalsIgnoreCase("sub_job_pre_site_survey.json")) {
                requestMap = updateSubJobPresite(requestMap);
                isSubJobPresite = true;
            }if(jsonFileName.equalsIgnoreCase("good_2_go.json")) {
                requestMap = updateGoodToGoSurvey(requestMap);
            }else if(jsonFileName.equalsIgnoreCase("poling_risk_assessment.json")) {
                requestMap = updatePolingRiskAssessment(requestMap);
            }else if (jsonFileName.equalsIgnoreCase("log_measure.json")) {
                requestMap.put("submittedDateTime", submission.getDate());
            } else if(jsonFileName.equalsIgnoreCase("incident.json")){
                ArrayList<Map<String , Object>> location = (ArrayList<Map<String, Object>>) requestMap.get("location");
                User user = DBHandler.getInstance().getUser();
                if(user != null) {
                    requestMap.put("incidentOwnerUserId", user.getuserId());
                }
                if(location != null && !location.isEmpty()){
                    location.get(0).put("lat" , location.get(0).get("latitude"));
                    location.get(0).put("lng" , location.get(0).get("longitude"));
                    location.get(0).remove("longitude");
                    location.get(0).remove("latitude");
                    requestMap.put("location" , location.get(0));
                    requestMap.put("dateReported" , submission.getDate());
                    requestMap.put("customerFullName" , requestMap.get("customerForeName")+" "+requestMap.get("customerSurName"));
                }
            }else if (jsonFileName.equalsIgnoreCase("timesheet_submit_timesheet.json")) {
                if (!requestMap.containsKey("timesheetHoursIds")) {
                    requestMap.put("timesheetHoursIds", new ArrayList<String>());
                }
            }
        }

        if (isBookOn && !containsUserIds) {
            requestMap.put("userIds", new ArrayList<>());
        }

        if(TextUtils.isEmpty(photoEndPoint)){
            ArrayList<PhotoResponse> questionPhotos = new ArrayList<>();
            for (int k = 0; k < photosToUpload.size(); k++) {
                Answer answerqu = photosToUpload.get(k);
                PhotoResponse photoResponse = createPhotoRequest(answerqu);
                if (photoResponse != null) {
                    questionPhotos.add(photoResponse);
                }
            }
            requestMap.put("photos", questionPhotos);
        }

        if (!signatures.isEmpty()) {
            for (Answer signature : signatures) {
                String signatureID = uploadSignature(uniqueId, signature, submission.getJobID(), fm);
                if (signatureID != null) {
                    requestMap.put("SignatureId", signatureID);
                }
            }
        }


        String jsonSubmission = gson.toJson(requestMap);
        RequestBody body = RequestBody.create(JSON, jsonSubmission);

        if (!TextUtils.isEmpty(endPoint)) {
            if(isSubJobPresite && !TextUtils.isEmpty(photoEndPoint)){
                uploadPhotos(photosToUpload, uniqueId, photoEndPoint, fm, "");
            }
            String url = BuildConfig.BASE_URL + endPoint;
            Response response;
            if (endPoint.contains("raise-dcr/v2") || endPoint.contains("raise-dfe/v2")) {
                response = uploadMultipart(jsonSubmission, photosToUpload, url);
            } else {
                response = performJSONNetworking(body, url);
                if (response == null || !response.isSuccessful()) {
                    return response;
                }
            }

            if(!isSubJobPresite && !TextUtils.isEmpty(photoEndPoint)){
                if(photoEndPoint.contains("issueId")){
                    try {
                        IssueResponse json = new Gson().fromJson(response.body().string(), IssueResponse.class);
                        if (json != null) {
                            photoEndPoint = photoEndPoint.replace("{issueId}", json.getIssueId());
                            uploadPhotos(photosToUpload, uniqueId, photoEndPoint, fm, "");
                        }
                    } catch (Exception e) {

                    }
                }else{
                    uploadPhotos(photosToUpload, uniqueId, photoEndPoint, fm, "");
                }
            }
            return response;
        } else if (!TextUtils.isEmpty(photoEndPoint)) {
            Answer answer = dbHandler.getAnswer(submission.getID(), "comment", null, 0);
            String photoComment = "";
            if (answer != null && !TextUtils.isEmpty(answer.getAnswer())) {
                photoComment = answer.getAnswer();
            }
            return uploadPhotos(photosToUpload, uniqueId, photoEndPoint, fm, photoComment);
        }
        return null;
    }


    public Response submitActions(String endPoint,
                                  Submission submission, FragmentManager fm) {
        Gson gson = new GsonBuilder().serializeNulls().create();

        ArrayList<Answer> answers = DBHandler.getInstance().getAnswers(submission.getID());

        String uniqueId = UUID.randomUUID().toString();
        Map<String, Object> requestMap = new HashMap<>();

        List<PhotoResponse> photoResponseList = new ArrayList<>();
        int photoType = 0;

        if (submission.getJsonFileName().equalsIgnoreCase("corrective_measure.json") ||
                submission.getJsonFileName().equalsIgnoreCase("incident_corrective_measure.json")) {
            requestMap.put("cannotBeRectified", false);
            requestMap.put("cannotBeRectifiedComments", null);
            //photoResponse.setPhotoTypeId(3);
            photoType = 3;
        } else {
            requestMap.put("cannotBeRectified", true);
            requestMap.put("correctiveMeasure", null);
            //photoResponse.setPhotoTypeId(4);
            photoType = 4;
        }
        requestMap.put("submissionId", uniqueId);

        if(endPoint.equalsIgnoreCase("apphseq/actions/close-incident-action")) {
            requestMap.put("incidentActionId", submission.getJobID());
        }else{
            requestMap.put("inspectionQuestionId", submission.getJobID());
        }
        requestMap.put("submittedDate", submission.getDate());
        requestMap.put("dateTaken", submission.getDate());
        //PhotoResponseModel photoResponseModel = new PhotoResponseModel();


        for (int c = 0; c < answers.size(); c++) {
            Answer answer = answers.get(c);
            String strAns = answer.getAnswer();
            Object ans = strAns;
            if (!TextUtils.isEmpty(strAns)) {
                ans = parseObject(strAns);
            }

            if (answer.shouldUpload()) {
                if (answer.isPhoto() == 0) {
                    String repeatId = answer.getRepeatID();

                    String uploadId = answer.getUploadID();
                    if (!TextUtils.isEmpty(repeatId)) {  //INFO: repeat id not null

                        if (!requestMap.containsKey(repeatId)) {  // if parent not contains repeatId then create new object
                            requestMap.put(answer.getRepeatID(),
                                    new HashMap<String, Map<String, Object>>());
                        }

                        Map<String, Map<String, Object>> repeatMap =
                                (Map<String, Map<String, Object>>) requestMap.get(repeatId); // getRepeatobject


                        String repeatCount = String.valueOf(answer.getRepeatCount());

                        if (!repeatMap.containsKey(repeatCount)) {
                            Map<String, Object> repMap = new HashMap<>();
                            repeatMap.put(repeatCount, repMap);
                        }

                        Map<String, Object> mapToAppendTo = repeatMap.get(repeatCount);
                        if (answer.isMultilist()) {

                            if (!mapToAppendTo.containsKey(uploadId)) {
                                mapToAppendTo.put(uploadId, new ArrayList<String>());
                            }

                            ArrayList<String> multiArr = (ArrayList<String>) mapToAppendTo.get(uploadId);
                            multiArr.add(answer.getAnswer());

                        } else {

                            mapToAppendTo.put(answer.getUploadID(), ans);
                        }

                    } else {
                        if (answer.isMultilist()) {
                            if (!requestMap.containsKey(uploadId)) {
                                requestMap.put(uploadId, new ArrayList<String>());
                            }
                            ArrayList<String> multiArr = (ArrayList<String>) requestMap.get(uploadId);
                            multiArr.add(answer.getAnswer());
                        } else {
                            requestMap.put(uploadId, ans);
                        }
                    }
                } else {
                    if (!TextUtils.isEmpty(answer.getUploadID()) && answer.getUploadID().equalsIgnoreCase("FileBytes")) {
                        PhotoResponse photoResponse = createPhotoRequest(answer);
                        if (photoResponse != null) {
                            photoResponseList.add(photoResponse);
                        }
                    }
                }
            }

            requestMap.put("photos", photoResponseList);//adding photos array model
        }
        // here all object added to setlist
        for (Map.Entry<String, Object> entry : requestMap.entrySet()) {
            if (entry.getValue() instanceof Map) {
                Map<String, Object> tmpMap = (Map<String, Object>) entry.getValue();
                ArrayList<Object> arrayList = new ArrayList<>();
                for (Map.Entry<String, Object> item : tmpMap.entrySet()) {
                    arrayList.add(item.getValue());
                }
                requestMap.put(entry.getKey(), arrayList);
            }
        }

        String jsonSubmission = gson.toJson(requestMap);
        RequestBody body = RequestBody.create(JSON, jsonSubmission);

        if (!TextUtils.isEmpty(endPoint)) {
            String url = BuildConfig.BASE_URL + endPoint;
            Response response;
            response = performJSONNetworking(body, url);
            if (response == null || !response.isSuccessful()) {
                return response;
            }

            return response;
        }

        return null;
    }

    public Response submitInspections(String endPoint, String photoEndPoint,
                                      Submission submission, FragmentManager fm) {
        Gson gson = new GsonBuilder().serializeNulls().create();
        ArrayList<Answer> answersinspections = dbHandler.getAnswers(submission.getID());//Utils.newInspectionSubmission
        ArrayList<Answer> answersQuestions = dbHandler.getRepeatedQuestionAnswers(submission.getID(), null, "questions");

        String uniqueId = UUID.randomUUID().toString();
        Map<String, Object> requestMap = new HashMap<>();
        //requestMap.put("submissionId", uniqueId);
        requestMap.put("templateVersionId", submission.getJobID());
        //getting data of new/schedule inspection here
        ArrayList<String> operativedata = new ArrayList<>();
        for (int c = 0; c < answersinspections.size(); c++) {
            Answer answer = answersinspections.get(c);
            String strAns = answer.getAnswer();
            Object ans = strAns;
            if (!TextUtils.isEmpty(strAns)) {
                ans = parseObject(strAns);
            }
            if (answer.shouldUpload()) {
                if (answer.isPhoto() == 0) {
                    String repeatId = answer.getRepeatID();
                    String uploadId = answer.getUploadID();
                    if (!TextUtils.isEmpty(repeatId) && repeatId.equals("operativeIds") && uploadId.equals("operativeId")) {  //INFO: repeat id not null
                        operativedata.add(String.valueOf(ans));//adding operatives
                    } else {
                        if (uploadId.equals("scheduledInspectionId") || uploadId.equals("jobId")) {
                            requestMap.put(uploadId, ans); // only non-questions data
                        } else if (uploadId.equals("address")) {
                            requestMap.put("location", ans);
                        } else if (uploadId.equals("latitude")) {
                            requestMap.put("latitude", ans);
                        } else if (uploadId.equals("longitude")) {
                            requestMap.put("longitude", ans);
                        }else if (uploadId.equals("estimateNo")) {
                            if(ans!= null) {
                                Job job = dbHandler.getJobByEstimate(String.valueOf(ans));
                                if(job != null) {
                                    requestMap.put("jobId", job.getjobId());
                                }else{
                                    requestMap.put("jobId" , String.valueOf(ans));
                                }
                            }else{
                                requestMap.put("jobId" , String.valueOf(ans));
                            }

                        }
                    }
                } else {
                    if (!TextUtils.isEmpty(answer.getUploadID()) && answer.getUploadID().equalsIgnoreCase("signatureFileBytes")) {
                        Bitmap bm = BitmapFactory.decodeFile(answer.getAnswer());
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bm.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                        byte[] byteArrayImage = baos.toByteArray();
                        String encodedSignature = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
                        requestMap.put("signatureFileBytes", encodedSignature);
                    }
                }
            }
        }

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String date1 = sdf.format(calendar.getTime());
        requestMap.put("operativeIds", operativedata);
        //getting data of questions/signature here
        ArrayList<HashMap<String, Object>> questions = new ArrayList<>();
        for (int i = 0; i < answersQuestions.size(); i++) {
            HashMap<String, Object> questionmap = new HashMap<>();
            Answer answer = answersQuestions.get(i);
            questionmap.put("questionId", answer.getUploadID());
            questionmap.put("answerId", answer.getAnswer());
            String answerId = answer.getAnswer();// for check

            if (answerId.equalsIgnoreCase("1")) {
                questionmap.put("actionCorrectiveMeasure", null);
                questionmap.put("actionAssignedToUserId", null);
                questionmap.put("actionDueDate", date1);
                questionmap.put("actionDefectComments", null);
                questionmap.put("actionWasRectifiedOnSite", null);
            }
            if (answerId.equals("3")) {
                questionmap.put("actionCorrectiveMeasure", null);
                questionmap.put("actionAssignedToUserId", null);
                questionmap.put("actionDueDate", date1);
                questionmap.put("actionDefectComments", null);
                questionmap.put("actionWasRectifiedOnSite", null);
                questionmap.put("comments", null);
            }
            ArrayList<Answer> answers1 = dbHandler.getRepeatedQuestionAnswers(
                    submission.getID(),
                    null,
                    answer.getUploadID());
            for (int j = 0; j < answers1.size(); j++) {
                Answer answerqu = answers1.get(j);
                if (answerqu.isPhoto() == 0) {
                    Object ansObj = parseObject(answerqu.getAnswer());
                    questionmap.put(answerqu.getUploadID(), ansObj);

                    if (answerId.equals("2")) {
                        questionmap.put("comments", null);
                        if (answerqu.getUploadID().equals("actionWasRectifiedOnSite") && answerqu.getAnswer().equalsIgnoreCase("true")) {
                            questionmap.put("actionAssignedToUserId", null);
                            questionmap.put("actionDueDate", null);
                        } else if (answerqu.getUploadID().equals("actionWasRectifiedOnSite") && answerqu.getAnswer().equalsIgnoreCase("false")) {
                            questionmap.put("actionCorrectiveMeasure", null);
                        }
                    }
                }
            }
            //Slg Questions Photo
            ArrayList<PhotoResponse> questionPhotos = new ArrayList<>();
            ArrayList<Answer> questionsphotos = dbHandler.getAnswerPic(submission.getID(), answer.getUploadID(), "Slg Questions Photo");
            for (int k = 0; k < questionsphotos.size(); k++) {
                Answer answerqu = questionsphotos.get(k);
                PhotoResponse photoResponse = createPhotoRequest(answerqu);
                if (photoResponse != null) {
                    questionPhotos.add(photoResponse);
                }
            }
            //if (!questionsphotoList.isEmpty())
            questionmap.put("photos", questionPhotos);//question photo
            questions.add(questionmap);// adding all question in list
        }

        //adding all question and signature
        requestMap.put("questions", questions); //questions arraylist

        ArrayList<PhotoResponse> slgphotorespons = new ArrayList<>();
        ArrayList<Answer> answerphots = dbHandler.getAnswerPic(submission.getID(), "fileBytes", "Slg Inspection Photo");
        for (int k = 0; k < answerphots.size(); k++) {
            PhotoResponse photoResponse = createPhotoRequest(answerphots.get(k));
            if (photoResponse != null) {
                slgphotorespons.add(photoResponse);
            }
        }
        requestMap.put("photos", slgphotorespons);

        if (!requestMap.containsKey("latitude")) {
            requestMap.put("latitude", submission.getLatitude());
        }

        if (!requestMap.containsKey("longitude")) {
            requestMap.put("longitude", submission.getLongitude());
        }

        for (Map.Entry<String, Object> entry : requestMap.entrySet()) {
            if (entry.getValue() instanceof Map) {
                Map<String, Object> tmpMap = (Map<String, Object>) entry.getValue();
                ArrayList<Object> arrayList = new ArrayList<>();
                for (Map.Entry<String, Object> item : tmpMap.entrySet()) {
                    arrayList.add(item.getValue());
                }
                requestMap.put(entry.getKey(), arrayList);
            }
        }

        String jsonSubmission = gson.toJson(requestMap);
        RequestBody body = RequestBody.create(JSON, jsonSubmission);
        if (!TextUtils.isEmpty(endPoint)) {
            String url = BuildConfig.BASE_URL + endPoint;
            Response response = performJSONNetworking(body, url);
            if (response == null || !response.isSuccessful()) {
                return response;
            }

            return response;
        }
        return null;
    }

    private PhotoResponse createPhotoRequest(Answer answer) {
        if (answer == null || TextUtils.isEmpty(answer.getAnswer())) {
            return null;
        }

        PhotoResponse photoResponse = new PhotoResponse();
        photoResponse.setDateTimeTaken(answer.getTakenDateTime());
        photoResponse.setLatitude(answer.getLatitude());
        photoResponse.setLongitude(answer.getLongitude());
        photoResponse.setPhotoTypeId(1);//for inspections
        photoResponse.setFileName(FilenameUtils.getName(answer.getAnswer()));
        Bitmap bm = BitmapFactory.decodeFile(answer.getAnswer());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] byteArrayImage = baos.toByteArray();
        String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
        photoResponse.setFileBytes(encodedImage);
        List<String> tagslistslg = new ArrayList<>();
        List<String> commentlistslg = new ArrayList<>();

        if (answer.getTags() != null) {
            ArrayList<PhotoTags> photoTags = answer.getTags();
            if (photoTags.size() > 0) {
                for (PhotoTags tags : photoTags) {
                    if (answer.getID() == tags.getAnswerId()) {
                        tagslistslg.add(tags.getTagName());
                    }
                }
            }
        }

        if (answer.getComments() != null) {
            ArrayList<PhotoComments> photoComments = answer.getComments();
            if (photoComments.size() > 0) {

                for (PhotoComments comments : photoComments) {
                    if (answer.getID() == comments.getAnswerId()) {
                        commentlistslg.add(comments.getComments());
                    }
                }
            }
        }
        photoResponse.setTags(tagslistslg);
        photoResponse.setComments(commentlistslg);
        return photoResponse;
    }

    private Response uploadMultipart(String json, ArrayList<Answer> photosToUpload, String endpoint) {

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);


        if (!photosToUpload.isEmpty()) {
            Answer answer = photosToUpload.get(0);
            Bitmap bm = BitmapFactory.decodeFile(answer.getAnswer());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            byte[] byteArrayImage = baos.toByteArray();
//            String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
            String fileName = answer.getDisplayAnswer().replace(" ", "_");
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), byteArrayImage);
            builder = builder.addFormDataPart(fileName + ".jpg", fileName + ".jpg", requestBody);
        }
        builder = builder.addFormDataPart("request", json);
        Response response = performImageNetworking(endpoint, builder.build());
        return response;
    }

    Object parseObject(String val) {
        Object obj = val;
        try {
            if (val.equalsIgnoreCase("true")
                    || val.equalsIgnoreCase("false")) {
                obj = Boolean.parseBoolean(val);
            }
        } catch (Exception e) {

        }

        if (!val.contains(".")) {
            try {
                obj = Integer.parseInt(val);
            } catch (Exception e) {

            }
        } else {
            try {
                obj = Double.parseDouble(val);
            } catch (Exception e) {

            }
        }
        return obj;
    }

    public Response uploadPhotos(ArrayList<Answer> photosToUpload,
                             String uniqueId, String photoUrl, FragmentManager fm, String photoComment) {
        Response responseMP = null;
        photoUrl = BuildConfig.BASE_URL + photoUrl;
        int count = 1;
        MaterialAlertProgressDialog dialog = null;
        if (!fm.isStateSaved()) {
            dialog = MaterialAlertProgressDialog.newInstance();
            dialog.setMessage("Uploading " + count + " of " + photosToUpload.size() + " photos");
            dialog.setTitle("Upload Photo");
            dialog.show(fm, MaterialAlertProgressDialog.class.getSimpleName());
        }
        for (Answer answer : photosToUpload) {
            Bitmap bm = BitmapFactory.decodeFile(answer.getAnswer());
            if(bm != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                byte[] byteArrayImage = baos.toByteArray();

                String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
                String fileName = answer.getDisplayAnswer();
                if (TextUtils.isEmpty(fileName)) {
                    fileName = "Photo_" + count + ".jpg";
                } else {
                    fileName = fileName.replace(" ", "_") + ".jpg";
                }

                PhotoRequest photoRequest = new PhotoRequest();

                photoRequest.setSubmissionID(uniqueId);
                photoRequest.setFileBytes(encodedImage);
                photoRequest.setFileName(fileName);
                photoRequest.setPhotoType(answer.getUploadID());
                photoRequest.setComment(photoComment);
                photoRequest.setLatitude(String.valueOf(answer.getLatitude()));
                photoRequest.setLongitude(String.valueOf(answer.getLongitude()));
                photoRequest.setTakenDateTime(answer.getTakenDateTime());

                String jsonSubmission = gson.toJson(photoRequest);
                RequestBody body = RequestBody.create(JSON, jsonSubmission);
                responseMP = performJSONNetworking(body, photoUrl);

                if (responseMP != null && responseMP.isSuccessful()) {
                    count++;
                    if (dialog != null) {
                        dialog.setMessage("Uploading " + count + " of " + photosToUpload.size() + " photos");

                    }
                }
            }

        }

        if (dialog != null) {
            dialog.dismiss();
        }

        return responseMP;
    }

    private String uploadSignature(String uniqueId, Answer signature, String jobId,
                                   FragmentManager fm) {

        String signatureUrl = signature.getSignatureUrl();
        if (signatureUrl == null || signatureUrl.isEmpty()) {
            return null;
        }

        signatureUrl = signatureUrl.replace("{jobId}", jobId);

        Bitmap bm = BitmapFactory.decodeFile(signature.getAnswer());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] byteArrayImage = baos.toByteArray();

        String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);

        String photoUrl = BuildConfig.BASE_URL + signatureUrl;

        SignatureRequest signatureRequest = new SignatureRequest();

        signatureRequest.setSubmissionID(uniqueId);
        signatureRequest.setFileBytes(encodedImage);
        signatureRequest.setTakenDateTime(signature.getTakenDateTime());

        String jsonSubmission = gson.toJson(signatureRequest);
        RequestBody body = RequestBody.create(JSON, jsonSubmission);
        Response responseMP = performJSONNetworking(body, photoUrl);
        MaterialAlertProgressDialog dialog = null;
        if (!fm.isStateSaved()) {
            dialog = MaterialAlertProgressDialog.newInstance();
            dialog.setMessage("Uploading Signature");
            dialog.setTitle("Signature");
            dialog.show(fm, MaterialAlertProgressDialog.class.getSimpleName());

        }


        if (responseMP != null && responseMP.isSuccessful()) {
            ResponseBody responseBody = responseMP.body();
            if (dialog != null) {
                dialog.dismiss();
            }
            if (responseBody != null) {
                try {
                    String data = responseBody.string();
                    if (!TextUtils.isEmpty(data)) {
                        JSONObject jsonObject = new JSONObject(data);
                        return jsonObject.optString("signatureId");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        if (dialog != null) {
            dialog.dismiss();
        }
        return null;
    }

    @Nullable
    public Response performNetworking(@NonNull String url, RequestBody requestBody) {
        RespAndCall rc = performNetworkingForCall(url, requestBody);
        if (rc != null) {
            return rc.response;
        }
        return null;
    }

    @Nullable
    public Response performJSONNetworking(@NonNull RequestBody body,
                                          @NonNull String url) {
        User user = dbHandler.getUser();
        String token = "";
        if (user != null) {
            token = "Bearer " + user.gettoken();
        }

        try {
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", token)
                    .post(body)
                    .build();

            return okHttpClient.newCall(request).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void submitData(@NonNull String url, @NonNull String userToken,
                            @NonNull RequestBody body, @NonNull Callback callback) {
        String token = "Bearer " + userToken;
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", token)
                    .post(body)
                    .build();

            okHttpClient.newCall(request).enqueue(callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    private RespAndCall performNetworkingForCall(@NonNull String url, RequestBody requestBody) {
        Request.Builder builder = new Request.Builder().url(url);

        if (requestBody != null) {
            builder.post(requestBody);
        }

        // Send up authentication token if we have it

        User user = dbHandler.getUser();
        if (user != null) {
            builder.addHeader("Authorization", "Bearer " + user.gettoken());
        }

        try {
            Request request = builder.build();

            Call call = okHttpClient.newCall(request);
            Response response = call.execute();

            return new RespAndCall(response, call);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    @Nullable
    public Response performImageNetworking(@NonNull String url, RequestBody requestBody) {
        RespAndCall rc = performImageNetworkingForCall(url, requestBody);
        if (rc != null) {
            return rc.response;
        }
        return null;
    }

    @Nullable
    private RespAndCall performImageNetworkingForCall(@NonNull String url, RequestBody requestBody) {
        Request.Builder builder = new Request.Builder().url(url);

        if (requestBody != null) {
            builder.post(requestBody);
        }

        // Send up authentication token if we have it

        User user = dbHandler.getUser();
        if (user != null) {
            builder.addHeader("Authorization", "Bearer " + user.gettoken());
        }

        try {
            Request request = builder.build();
            Call call = okHttpClient.newCall(request);
            Response response = call.execute();

            return new RespAndCall(response, call);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    class AuthInterceptor implements Interceptor {

        @NonNull
        @Override
        public Response intercept(final Chain chain) throws IOException {
            Response mainResponse = chain.proceed(chain.request());
            final Request mainRequest = chain.request();

            if (mainResponse.code() == 429) {
                Intent intent = new Intent(context, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
                return mainResponse;
            }

            if (mainResponse.code() == 401) {
                User existUser = dbHandler.getUser();
                if (existUser == null) {
                    return mainResponse;
                }
                UserRequest userRequest = new UserRequest(AppPreferences.getString("UserName", ""),
                        AppPreferences.getString("UserPassword", ""));


                User user = refreshToken(userRequest);
                if (user != null && !TextUtils.isEmpty(user.getuserId())) {
                    dbHandler.replaceData(User.DBTable.NAME, user.toContentValues());
                    Request.Builder builder = mainRequest.newBuilder().header("Authorization", "Bearer " + user.gettoken()).
                            method(mainRequest.method(), mainRequest.body());
                    mainResponse = chain.proceed(builder.build());
                }
            }

            return mainResponse;
        }
    }

    public class RespAndCall {
        public Response response;
        public Call call;

        public RespAndCall(Response response, Call call) {
            this.response = response;
            this.call = call;
        }
    }

    private Map<String , Object> updateGoodToGoSurvey(Map<String , Object> requestMap){
        Object value = requestMap.get("canOverheadGangComplete");
        if((value != null && value.toString().equalsIgnoreCase("2"))){
            return requestMap;
        }
//            first skip
        if(!requestMap.containsKey("isThePoleSolutionAManualPole")){
            requestMap.put("isThePoleSolutionAManualPole" , 3);
        }
        if(!requestMap.containsKey("isThereAccessAvailableForThePEU")){
            requestMap.put("isThereAccessAvailableForThePEU" , 3);
        }
        if(!requestMap.containsKey("reinstatementRequired")){
            requestMap.put("reinstatementRequired" , 3);
        }
        if(!requestMap.containsKey("siteContactNameNumber")){
            requestMap.put("siteContactNameNumber" , 3);
        }
        if(!requestMap.containsKey("anyOtherRestrictions")){
            requestMap.put("anyOtherRestrictions" , 3);
        }

//        second skip

        if(!requestMap.containsKey("heightOfLowestAerialCableComments")){
            requestMap.put("heightOfLowestAerialCableComments" , null);
        }

        if(!requestMap.containsKey("aerialCableTypeComments")){
            requestMap.put("aerialCableTypeComments" , null);
        }

        if(!requestMap.containsKey("noOfSpansOfAerialCableComments")){
            requestMap.put("noOfSpansOfAerialCableComments" , null);
        }

        if(!requestMap.containsKey("areDropwiresRequired")){
            requestMap.put("areDropwiresRequired" , 3);
        }

        if(!requestMap.containsKey("areDropwiresRequiredComments")){
            requestMap.put("areDropwiresRequiredComments" , null);
        }

//    third skip

        if(!requestMap.containsKey("lowestDropwireHeightComments")){
            requestMap.put("lowestDropwireHeightComments" , null);
        }

        if(!requestMap.containsKey("noOfSpansOfDropwireComments")){
            requestMap.put("noOfSpansOfDropwireComments" , null);
        }

        if(!requestMap.containsKey("whatTypeOfCustomerEndFixingsComments")){
            requestMap.put("whatTypeOfCustomerEndFixingsComments" , null);
        }

        if(!requestMap.containsKey("isThereAccessAvailableForTheHoist")){
            requestMap.put("isThereAccessAvailableForTheHoist" , 3);
        }

        if(!requestMap.containsKey("areThereHospitalsSchoolsInVicinity")){
            requestMap.put("areThereHospitalsSchoolsInVicinity" , 3);
        }

        if(!requestMap.containsKey("AdditionalPrecautionsRequiredComments")){
            requestMap.put("AdditionalPrecautionsRequiredComments" , null);
        }


        if(!requestMap.containsKey("treeCutting")){
            requestMap.put("treeCutting" , 2);
        }
        if(!requestMap.containsKey("treeSurgery")){
            requestMap.put("treeSurgery" , 2);
        }

        return requestMap;
    }

    private Map<String , Object> updatePolingRiskAssessment(Map<String , Object> requestMap){


        if(!requestMap.containsKey("AdditionalPrecautionsRequiredComments")){
            requestMap.put("AdditionalPrecautionsRequiredComments" , null);
        }

        return requestMap;
    }

    private Map<String , Object> updateSubJobPresite(Map<String , Object> requestMap){

        if(!requestMap.containsKey("locationDetailsCorrect")){
            requestMap.put("locationDetailsCorrect" , 1);
        }
        if(!requestMap.containsKey("haMeetingRequired")){
            requestMap.put("haMeetingRequired" , 2);
        }
        if(!requestMap.containsKey("trafficManagementRequired")){
            requestMap.put("trafficManagementRequired" , 2);
        }
        if(!requestMap.containsKey("tmMeetingRequired")){
            requestMap.put("tmMeetingRequired" , 2);
        }
        if(!requestMap.containsKey("wayLeavePTDRequired")){
            requestMap.put("wayLeavePTDRequired" , 2);
        }

//        second skip

        if(!requestMap.containsKey("restrictions")){
            requestMap.put("restrictions" , 2);
        }

        if(!requestMap.containsKey("dateConstraints")){
            requestMap.put("dateConstraints" , 2);
        }

        if(!requestMap.containsKey("workingTimeRestrictions")){
            requestMap.put("workingTimeRestrictions" , 2);
        }

        if(!requestMap.containsKey("outOfHoursApprovalRequired")){
            requestMap.put("outOfHoursApprovalRequired" , 2);
        }

        if(!requestMap.containsKey("workingApproval24HrRequired")){
            requestMap.put("workingApproval24HrRequired" , 2);
        }

        if(!requestMap.containsKey("parkingBaySuspensions")){
            requestMap.put("parkingBaySuspensions" , 2);
        }

        if(!requestMap.containsKey("busStopSuspensions")){
            requestMap.put("busStopSuspensions" , 2);
        }

        if(!requestMap.containsKey("jobCantStart")){
            requestMap.put("jobCantStart" , 2);
        }

        if(!requestMap.containsKey("materialsRequired")){
            requestMap.put("materialsRequired" , 2);
        }

        if(!requestMap.containsKey("pedestrianManagementRequired")){
            requestMap.put("pedestrianManagementRequired" , 2);
        }

        if(!requestMap.containsKey("proposedDateToCommenceAvailable")){
            requestMap.put("proposedDateToCommenceAvailable" , 2);
        }

        if(!requestMap.containsKey("proposedDateToCommenceDuration")){
            requestMap.put("proposedDateToCommenceDuration" , 1);
        }
        if(!requestMap.containsKey("surveyRequired")){
            requestMap.put("surveyRequired" , 2);
        }

        return requestMap;
    }


}
