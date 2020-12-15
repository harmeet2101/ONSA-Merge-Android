package co.uk.depotnet.onsa.modals.briefings;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BriefingsDocument implements Parcelable {
    @SerializedName("briefingId")
    @Expose
    private String briefingId;
    @SerializedName("briefingName")
    @Expose
    private String briefingName;
    @SerializedName("briefingDocumentFileBytes")
    @Expose
    private String briefingDocumentFileBytes;

    BriefingsDocument(){

    }

    public BriefingsDocument getDocumentWithoutFileBytes(){
        BriefingsDocument document = new BriefingsDocument();
        document.setBriefingId(this.briefingId);
        document.setBriefingName(this.briefingName);
        return document;
    }

    protected BriefingsDocument(Parcel in) {
        briefingId = in.readString();
        briefingName = in.readString();
        briefingDocumentFileBytes = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(briefingId);
        dest.writeString(briefingName);
        dest.writeString(briefingDocumentFileBytes);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BriefingsDocument> CREATOR = new Creator<BriefingsDocument>() {
        @Override
        public BriefingsDocument createFromParcel(Parcel in) {
            return new BriefingsDocument(in);
        }

        @Override
        public BriefingsDocument[] newArray(int size) {
            return new BriefingsDocument[size];
        }
    };

    public String getBriefingId() {
        return briefingId;
    }

    public void setBriefingId(String briefingId) {
        this.briefingId = briefingId;
    }

    public String getBriefingName() {
        return briefingName;
    }

    public void setBriefingName(String briefingName) {
        this.briefingName = briefingName;
    }

    public String getBriefingDocumentFileBytes() {
        return briefingDocumentFileBytes;
    }

    public void setBriefingDocumentFileBytes(String briefingDocumentFileBytes) {
        this.briefingDocumentFileBytes = briefingDocumentFileBytes;
    }
}
