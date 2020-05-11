package co.uk.depotnet.onsa.networking;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.SparseArray;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import co.uk.depotnet.onsa.BuildConfig;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.modals.User;
import co.uk.depotnet.onsa.modals.forms.Answer;
import co.uk.depotnet.onsa.modals.forms.Submission;
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
    private Context context;
    private OkHttpClient okHttpClient;
    private Gson gson;

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
    }

    private User refreshToken(UserRequest userRequest) {

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
                    return gson.fromJson(result, User.class);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }


    public Response submitPollingSurvey(String endPoint, String photoEndPoint,
                                        Submission submission, FragmentManager fm) {


        Map<String, Object> jobData = new HashMap<>();
        Map<String, Object> fluidityTask = new HashMap<>();
        Map<String, Object> solution = new HashMap<>();
        Map<String, Object> assetSection = new HashMap<>();
        Map<String, Object> riskAssessment = new HashMap<>();


        if (!TextUtils.isEmpty(endPoint)) {
            endPoint = endPoint.replace("{jobId}", submission.getJobID());
        }

        if (!TextUtils.isEmpty(photoEndPoint)) {
            photoEndPoint = photoEndPoint.replace("{jobId}", submission.getJobID());
        }


        ArrayList<Answer> answers = DBHandler.getInstance().getAnswers(submission.getID());

        String uniqueId = UUID.randomUUID().toString();
        Map<String, Object> requestMap = new HashMap<>();
        ArrayList<Answer> photosToUpload = new ArrayList<>();
        ArrayList<Answer> signatures = new ArrayList<>();

        requestMap.put("submissionId", uniqueId);
        requestMap.put("submittedDate", submission.getDate());
        requestMap.put("dateTaken", submission.getDate());
        requestMap.put("latitude", submission.getLatitude());
        requestMap.put("longitude", submission.getLongitude());
        requestMap.put("longitude", submission.getLongitude());

        for (int c = 0; c < answers.size(); c++) {
            Answer answer = answers.get(c);
            if (answer.shouldUpload()) {
                if (answer.isPhoto() == 0) {
                    String repeatId = answer.getRepeatID();
                    String uploadId = answer.getUploadID();


                    if (!TextUtils.isEmpty(repeatId)) {  //INFO: repeat id not null
                        if(repeatId.equalsIgnoreCase("negDfeItems")){
                            repeatId = "dfeItems";
                        }
                        if (repeatId.equalsIgnoreCase("jobDataSection")) {

                            if (uploadId.equalsIgnoreCase("address")) {
                                jobData.put("surveyAddress", answer.getAnswer());
                            } else if (uploadId.equalsIgnoreCase("latitude")) {
                                jobData.put("surveyLatitude", answer.getAnswer());
                            } else if (uploadId.equalsIgnoreCase("longitude")) {
                                jobData.put("surveyLongitude", answer.getAnswer());
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
                            }
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

                                    Answer quantityAnswer = DBHandler.getInstance().getAnswer(submission.getID(), "quantity",
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


                                        /*if (!mapToAppendTo.containsKey(uploadId)) {
                                            mapToAppendTo.put(uploadId, new ArrayList<String>());
                                        }


                                        ArrayList<String> multiArr = (ArrayList<String>) mapToAppendTo.get(uploadId);
                                        String[] values = value.split(",");
                                        String[] quantity = quantities.split(",");


                                        for (int i = 0; i < values.length && i < quantity.length; i++) {
                                            multiArr.add(values[i]);
                                        }

                                        mapToAppendTo.put(uploadId, multiArr);*/
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

                    } else {
                        /*if (answer.isMultilist()) {
                            if (!requestMap.containsKey(uploadId)) {
                                requestMap.put(uploadId, new ArrayList<String>());
                            }
                            ArrayList<String> multiArr = (ArrayList<String>) requestMap.get(uploadId);
                            multiArr.add(answer.getAnswer());
                        } else {
                            requestMap.put(uploadId, answer.getAnswer());
                        }*/
                    }
                } else {
                    String signatureUrl = answer.getSignatureUrl();
                    boolean isSignature = signatureUrl != null && !signatureUrl.isEmpty();
                    if (isSignature) {

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
                if(signature.getUploadID().equalsIgnoreCase("supervisorSignatureId")){
                    riskAssessment.put(signature.getUploadID() , signatureID);
                }else {
                    requestMap.put("SignatureId", signatureID);
                }
            }
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

        String jsonSubmission = gson.toJson(requestMap);
        RequestBody body = RequestBody.create(JSON, jsonSubmission);

        if (!TextUtils.isEmpty(endPoint)) {
            String url = BuildConfig.BASE_URL + endPoint;
            Response response = performJSONNetworking(body, url);

            if (response == null || !response.isSuccessful()) {
                return response;
            }

            if (photoEndPoint != null) {
                uploadPhotos(photosToUpload, uniqueId, photoEndPoint, fm, "");
            }

            return response;
        } else if (photoEndPoint != null && !photoEndPoint.isEmpty()) {
            uploadPhotos(photosToUpload, uniqueId, photoEndPoint, fm, "");
        }

        return null;
    }


    public Response sendRfna(Submission submission){

        String url = BuildConfig.BASE_URL+"app/jobs/"+submission.getJobID()+"/sendrfna";
        return performJSONNetworking( RequestBody.create(JSON , "") , url);
    }


    public Response submitForm(String endPoint, String photoEndPoint,
                               Submission submission, FragmentManager fm) {

        if (!TextUtils.isEmpty(endPoint)) {
            endPoint = endPoint.replace("{jobId}", submission.getJobID());
        }

        if (!TextUtils.isEmpty(photoEndPoint)) {
            photoEndPoint = photoEndPoint.replace("{jobId}", submission.getJobID());
        }


        ArrayList<Answer> answers = DBHandler.getInstance().getAnswers(submission.getID());

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
            String strAns = answer.getAnswer();
            Object ans = strAns;
            if(!TextUtils.isEmpty(strAns)){
                ans = parseObject(strAns);
            }

            if (answer.shouldUpload()) {
                if (answer.isPhoto() == 0) {
                    String repeatId = answer.getRepeatID();

                    String uploadId = answer.getUploadID();
                    if (!TextUtils.isEmpty(repeatId)) {  //INFO: repeat id not null
                        if(repeatId.equalsIgnoreCase("negItems")){
                            repeatId = "items";
                        }
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
                    String signatureUrl = answer.getSignatureUrl();
                    boolean isSignature = signatureUrl != null && !signatureUrl.isEmpty();
                    if (isSignature) {
                        if(endPoint!= null && endPoint.contains("appstores")){
                            Bitmap bm = BitmapFactory.decodeFile(answer.getAnswer());
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bm.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                            byte[] byteArrayImage = baos.toByteArray();

                            String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
                            requestMap.put("SignatureImage" , encodedImage);
                        }else {
                            signatures.add(answer);
                        }
                    } else {
                        photosToUpload.add(answer);
                    }
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
            String url = BuildConfig.BASE_URL + endPoint;
            Response response ;
            if((endPoint.contains("raise-dcr/v2") || endPoint.contains("raise-dfe/v2"))){
                response = uploadMultipart(jsonSubmission, photosToUpload, url);
            }else {
                response = performJSONNetworking(body, url);

                if (response == null || !response.isSuccessful()) {
                    return response;
                }
            }

            if(photoEndPoint!= null && photoEndPoint.contains("issueId")){
                try {
                    IssueResponse json = new Gson().fromJson(response.body().string(), IssueResponse.class);
                    if(json != null) {
                        photoEndPoint = photoEndPoint.replace("{issueId}", json.getIssueId());
                        uploadPhotos(photosToUpload, uniqueId, photoEndPoint, fm, "");
                    }
                }catch (Exception e){

                }
            }else if (photoEndPoint != null) {
                uploadPhotos(photosToUpload, uniqueId, photoEndPoint, fm, "");
            }

            return response;
        } else if (photoEndPoint != null && !photoEndPoint.isEmpty()) {
            if(endPoint != null && endPoint.contains("logissue")){

            }

            Answer answer = DBHandler.getInstance().getAnswer(submission.getID(), "comment", null, 0);
            String photoComment = "";
            if (answer != null && !TextUtils.isEmpty(answer.getAnswer())) {
                photoComment = answer.getAnswer();
            }
            uploadPhotos(photosToUpload, uniqueId, photoEndPoint, fm, photoComment);
        }
        return null;
    }

    private Response uploadMultipart(String json, ArrayList<Answer> photosToUpload , String endpoint) {

        MultipartBody.Builder builder = new MultipartBody.Builder()
        .setType(MultipartBody.FORM);


        if(!photosToUpload.isEmpty()){
            Answer answer = photosToUpload.get(0);
            Bitmap bm = BitmapFactory.decodeFile(answer.getAnswer());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            byte[] byteArrayImage = baos.toByteArray();
//            String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
            String fileName = answer.getDisplayAnswer().replace(" ", "_");
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg") , byteArrayImage);
            builder = builder.addFormDataPart(fileName+".jpg" , fileName+".jpg" , requestBody);
        }
        builder = builder.addFormDataPart("request", json);
        Response response = performImageNetworking(endpoint, builder.build());
        return response;
    }

    Object parseObject(String val){
        Object obj = val;
        try{
            if(val.equalsIgnoreCase("true")
                    || val.equalsIgnoreCase("false")) {
                obj = Boolean.parseBoolean(val);
            }
        }catch (Exception e){

        }

        if(!val.contains(".")) {
            try {
                obj = Integer.parseInt(val);
            } catch (Exception e) {

            }
        }else{
            try {
                obj = Double.parseDouble(val);
            } catch (Exception e) {

            }
        }
        return obj;
    }

    public void uploadPhotos(ArrayList<Answer> photosToUpload,
                              String uniqueId, String photoUrl, FragmentManager fm, String photoComment) {
        photoUrl = BuildConfig.BASE_URL + photoUrl;
        int count = 1;
        MaterialAlertProgressDialog dialog = MaterialAlertProgressDialog.newInstance();
        dialog.setMessage("Uploading " + count + " of " + photosToUpload.size() + " photos");
        dialog.setTitle("Upload Photo");
        dialog.show(fm, MaterialAlertProgressDialog.class.getSimpleName());
        for (Answer answer : photosToUpload) {
            Bitmap bm = BitmapFactory.decodeFile(answer.getAnswer());
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
            Response responseMP = performJSONNetworking(body, photoUrl);

            if (responseMP != null && responseMP.isSuccessful()) {
                count++;
                dialog.setMessage("Uploading " + count + " of " + photosToUpload.size() + " photos");
            }

        }

        dialog.dismiss();
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

        MaterialAlertProgressDialog dialog = MaterialAlertProgressDialog.newInstance();
        dialog.setMessage("Uploading Signature");
        dialog.setTitle("Signature");
        dialog.show(fm, MaterialAlertProgressDialog.class.getSimpleName());

        if (responseMP != null && responseMP.isSuccessful()) {
            ResponseBody responseBody = responseMP.body();
            dialog.dismiss();
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
        dialog.dismiss();
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
        User user = DBHandler.getInstance().getUser();
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

        User user = DBHandler.getInstance().getUser();
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

        User user = DBHandler.getInstance().getUser();
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

            if (mainResponse.code() == 401) {
                User existUser = DBHandler.getInstance().getUser();
                if(existUser == null) {
                    return mainResponse;
                }
                UserRequest userRequest = new UserRequest(AppPreferences.getString("UserName" , ""),
                            AppPreferences.getString("UserPassword" , ""));


                User user = refreshToken(userRequest);
                if (user != null && !TextUtils.isEmpty(user.getuserId())) {
                    DBHandler.getInstance().replaceData(User.DBTable.NAME, user.toContentValues());
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
}
