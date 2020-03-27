package co.uk.depotnet.onsa.modals.httprequests;

import android.os.Parcel;
import android.os.Parcelable;

public class SignatureRequest implements Parcelable {

    private String submissionID;
    private String fileBytes;
    private String takenDateTime;

    public SignatureRequest() {

    }

    protected SignatureRequest(Parcel in) {
        submissionID = in.readString();
        fileBytes = in.readString();
        takenDateTime = in.readString();
    }

    public static final Creator<SignatureRequest> CREATOR = new Creator<SignatureRequest>() {
        @Override
        public SignatureRequest createFromParcel(Parcel in) {
            return new SignatureRequest(in);
        }

        @Override
        public SignatureRequest[] newArray(int size) {
            return new SignatureRequest[size];
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

    public String getTakenDateTime() {
        return takenDateTime;
    }

    public void setTakenDateTime(String takenDateTime) {
        this.takenDateTime = takenDateTime;
    }
}


