package co.uk.depotnet.onsa.modals.httprequests;

import android.os.Parcel;
import android.os.Parcelable;

public class PhotoRequest implements Parcelable {

    private String submissionID;
    private String fileBytes;
    private String fileName;
    private String photoTypeId;
    private String comment;
    private String latitude;
    private String longitude;
    private String takenDateTime;

    public PhotoRequest() {

    }

    protected PhotoRequest(Parcel in) {
        submissionID = in.readString();
        fileBytes = in.readString();
        fileName = in.readString();
        photoTypeId = in.readString();
        comment = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        takenDateTime = in.readString();
    }

    public static final Creator<PhotoRequest> CREATOR = new Creator<PhotoRequest>() {
        @Override
        public PhotoRequest createFromParcel(Parcel in) {
            return new PhotoRequest(in);
        }

        @Override
        public PhotoRequest[] newArray(int size) {
            return new PhotoRequest[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(submissionID);
        dest.writeString(fileBytes);
        dest.writeString(fileName);
        dest.writeString(photoTypeId);
        dest.writeString(comment);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(takenDateTime);
    }

    public String getSubmissionID() {
        return submissionID;
    }

    public void setSubmissionID(String submissionID) {
        this.submissionID = submissionID;
    }

    public String getFileBytes() {
        return fileBytes;
    }

    public void setFileBytes(String fileBytes) {
        this.fileBytes = fileBytes;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPhotoType() {
        return photoTypeId;
    }

    public void setPhotoType(String photoType) {
        this.photoTypeId = photoType;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getTakenDateTime() {
        return takenDateTime;
    }

    public void setTakenDateTime(String takenDateTime) {
        this.takenDateTime = takenDateTime;
    }
}


