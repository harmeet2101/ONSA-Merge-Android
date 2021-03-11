package co.uk.depotnet.onsa.modals.forms;


import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.modals.hseq.PhotoComments;
import co.uk.depotnet.onsa.modals.hseq.PhotoTags;


public class Answer implements Parcelable {

    private int id;
    private long submissionID;
    private int isPhoto;
    private int isMultiList;
    private int repeatCounter;
    private int arrayRepeatCounter;
    private String uploadID;
    private String answer;//to upload value ( in server post param too
    private String displayAnswer; // to show in field of form
    private String repeatID;// in case pass repeat counter too..
    private boolean shouldUpload = true;
    private int estimatedQuantity;
    private String signatureUrl;
    private double latitude;
    private double longitude;
    private String takenDateTime;
    private ArrayList<PhotoComments> comments;
    private ArrayList<PhotoTags> tags;
    public Answer(){

    }

    public Answer(Cursor cursor) {
        if (cursor != null && !cursor.isBeforeFirst() && !cursor.isAfterLast()) {
            id = cursor.getInt(cursor.getColumnIndex(DBTable.id));
            submissionID = cursor.getLong(cursor.getColumnIndex(DBTable.submissionID));
            uploadID = cursor.getString(cursor.getColumnIndex(DBTable.uploadID));
            answer = cursor.getString(cursor.getColumnIndex(DBTable.answer));
            displayAnswer = cursor.getString(cursor.getColumnIndex(DBTable.displayAnswer));
            isPhoto = cursor.getInt(cursor.getColumnIndex(DBTable.isPhoto));
            isMultiList = cursor.getInt(cursor.getColumnIndex(DBTable.isMultiList));
            repeatCounter = cursor.getInt(cursor.getColumnIndex(DBTable.repeatCounter));
            arrayRepeatCounter = cursor.getInt(cursor.getColumnIndex(DBTable.arrayRepeatCounter));
            repeatID = cursor.getString(cursor.getColumnIndex(DBTable.repeatID));
            shouldUpload = cursor.getInt(cursor.getColumnIndex(DBTable.shouldUpload)) == 1;
            estimatedQuantity = cursor.getInt(cursor.getColumnIndex(DBTable.estimatedQuantity));
            signatureUrl = cursor.getString(cursor.getColumnIndex(DBTable.signatureUrl));
            latitude = cursor.getDouble(cursor.getColumnIndex(DBTable.latitude));
            longitude = cursor.getDouble(cursor.getColumnIndex(DBTable.longitude));
            takenDateTime = cursor.getString(cursor.getColumnIndex(DBTable.takenDateTime));
            comments= DBHandler.getInstance().getPhotoComments(String.valueOf(id));
            tags= DBHandler.getInstance().getPhotoTags(id);
        }
    }

    public Answer(long submissionID, String uploadID) {
        this.submissionID = submissionID;
        this.uploadID = uploadID;
    }

    public Answer(long submissionID, String uploadID, int isPhoto) {
        this.submissionID = submissionID;
        this.uploadID = uploadID;
        this.isPhoto = isPhoto;
    }

    public Answer(long submissionID, String uploadID , String repeatID , int repeatCounter) {
        this.submissionID = submissionID;
        this.uploadID = uploadID;
        this.repeatID = repeatID;
        this.repeatCounter = repeatCounter;
    }

    public Answer(long submissionID, String uploadID , String repeatID , int repeatCounter , String answer , String displayAnswer) {
        this.submissionID = submissionID;
        this.uploadID = uploadID;
        this.repeatID = repeatID;
        this.repeatCounter = repeatCounter;
        this.answer  = answer;
        this.displayAnswer = displayAnswer;
    }

    public Answer(long submissionID, String uploadID,
                  int isPhoto, int isMultiList) {
        this.submissionID = submissionID;
        this.uploadID = uploadID;
        this.isMultiList = isMultiList;
        this.isPhoto = isPhoto;
    }

    protected Answer(Parcel in) {
        id = in.readInt();
        submissionID = in.readLong();
        isPhoto = in.readInt();
        isMultiList = in.readInt();
        repeatCounter = in.readInt();
        arrayRepeatCounter = in.readInt();
        uploadID = in.readString();
        answer = in.readString();
        displayAnswer = in.readString();
        repeatID = in.readString();
        shouldUpload = in.readByte() != 0;
        estimatedQuantity = in.readInt();
        signatureUrl = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        takenDateTime = in.readString();
        comments = in.createTypedArrayList(PhotoComments.CREATOR);
        tags = in.createTypedArrayList(PhotoTags.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeLong(submissionID);
        dest.writeInt(isPhoto);
        dest.writeInt(isMultiList);
        dest.writeInt(repeatCounter);
        dest.writeInt(arrayRepeatCounter);
        dest.writeString(uploadID);
        dest.writeString(answer);
        dest.writeString(displayAnswer);
        dest.writeString(repeatID);
        dest.writeByte((byte) (shouldUpload ? 1 : 0));
        dest.writeInt(estimatedQuantity);
        dest.writeString(signatureUrl);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(takenDateTime);
        dest.writeTypedList(comments);
        dest.writeTypedList(tags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Answer> CREATOR = new Creator<Answer>() {
        @Override
        public Answer createFromParcel(Parcel in) {
            return new Answer(in);
        }

        @Override
        public Answer[] newArray(int size) {
            return new Answer[size];
        }
    };

    public int getEstimatedQuantity() {
        return estimatedQuantity;
    }

    public void setEstimatedQuantity(int estimatedQuantity) {
        this.estimatedQuantity = estimatedQuantity;
    }



    public boolean shouldUpload() {
        return shouldUpload;
    }

    public void setShouldUpload(boolean shouldUpload) {
        this.shouldUpload = shouldUpload;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getID() {
        return id;
    }

    public int isPhoto() {
        return isPhoto;
    }

    public boolean isMultilist() {
        return (isMultiList == 1);
    }

    public String getUploadID() {
        return uploadID;
    }

    public String getRepeatID() {
        return repeatID;

    }

    public String getSignatureUrl() {
        return signatureUrl;
    }

    public void setSignatureUrl(String signatureUrl) {
        this.signatureUrl = signatureUrl;
    }

    public long getSubmissionID() {
        return submissionID;
    }

    public void setRepeatID(String repeatID) {
        this.repeatID = repeatID;
    }

    public int getRepeatCount() {
        return repeatCounter;
    }

    public String getTakenDateTime() {
        return takenDateTime;
    }

    public void setTakenDateTime(String takenDateTime) {
        this.takenDateTime = takenDateTime;
    }

    public void setRepeatCount(int repeatCount) {
        this.repeatCounter = repeatCount;
    }

    public void setIsMultiList(int isMultiList) {
        this.isMultiList = isMultiList;
    }

    public void setIsPhoto(int isPhoto) {
        this.isPhoto = isPhoto;
    }

    public String getDisplayAnswer() {
        if (displayAnswer != null && displayAnswer.length() > 0)
            return displayAnswer;
        else
            return this.getAnswer();
    }

    public void setDisplayAnswer(String displayAnswer) {
        this.displayAnswer = displayAnswer;
    }

    public String getAnswer() {
        if (answer != null)
            return answer;
        else
            return "";
    }

    public int getArrayRepeatCounter() {
        return arrayRepeatCounter;
    }

    public void setArrayRepeatCounter(int arrayRepeatCounter) {
        this.arrayRepeatCounter = arrayRepeatCounter;
    }

    public void setLatitude(Double latitude){
        this.latitude=latitude;
    }
    public double getLatitude() {
        return latitude;
    } public void setLongitude(Double longitude){
        this.longitude=longitude;
    }
    public double getLongitude() {
        return longitude;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public ArrayList<PhotoComments> getComments() {
        return comments;
    }

    public void setComments(ArrayList<PhotoComments> comments) {
        this.comments = comments;
    }

    public ArrayList<PhotoTags> getTags() {
        return tags;
    }

    public void setTags(ArrayList<PhotoTags> tags) {
        this.tags = tags;
    }

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();

        if (id > 0) {
            cv.put(DBTable.id, id);
        }
       /* DBHandler dbHandler = DBHandler.getInstance();
        if (comments != null && !comments.isEmpty()) {
            for (PhotoComments n :
                    comments) {
                n.setAnswerId(id);
                dbHandler.replaceData(PhotoComments.DBTable.NAME, n.toContentValues());
            }
        }
        if (tags != null && !tags.isEmpty()) {
            for (PhotoTags n :
                    tags) {
                n.setAnswerId(id);
                dbHandler.replaceData(PhotoTags.DBTable.NAME, n.toContentValues());
            }
        }*/
        cv.put(DBTable.submissionID, submissionID);
        cv.put(DBTable.answer, answer);
        cv.put(DBTable.displayAnswer, displayAnswer);
        cv.put(DBTable.uploadID, uploadID);
        cv.put(DBTable.isPhoto, isPhoto);
        cv.put(DBTable.isMultiList, isMultiList);
        cv.put(DBTable.repeatCounter, repeatCounter);
        cv.put(DBTable.arrayRepeatCounter, arrayRepeatCounter);
        cv.put(DBTable.repeatID, repeatID);
        cv.put(DBTable.shouldUpload, shouldUpload ? 1 : 0);
        cv.put(DBTable.estimatedQuantity, estimatedQuantity);
        cv.put(DBTable.signatureUrl, signatureUrl);
        cv.put(DBTable.latitude, latitude);
        cv.put(DBTable.longitude, longitude);
        cv.put(DBTable.takenDateTime, takenDateTime);

        return cv;
    }

    public static class DBTable {
        public static final String NAME = "Answers";
        public static final String id = "id";
        public static final String submissionID = "submissionID";
        public static final String isPhoto = "isPhoto";
        public static final String isMultiList = "isMultiList";
        public static final String repeatCounter = "repeatCounter";
        public static final String arrayRepeatCounter = "arrayRepeatCounter";
        public static final String uploadID = "uploadID";
        public static final String answer = "answer";
        public static final String displayAnswer = "displayAnswer";
        public static final String repeatID = "repeatID";
        public static final String shouldUpload = "shouldUpload";
        public static final String estimatedQuantity = "estimatedQuantity";
        public static final String signatureUrl = "signatureUrl";
        public static final String latitude = "latitude";
        public static final String longitude = "longitude";
        public static final String takenDateTime = "takenDateTime";

    }

    @Override
    public String toString() {
        return "Answer{" +
                "comments=" + comments +
                ", tags=" + tags +
                '}';
    }
}
