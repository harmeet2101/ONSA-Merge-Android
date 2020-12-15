package co.uk.depotnet.onsa.modals.briefings;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BriefingsRecipient implements Parcelable {
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("userHasReadBriefing")
    @Expose
    private Boolean userHasReadBriefing;
    private String briefingId;

    public BriefingsRecipient() {
    }

    protected BriefingsRecipient(Parcel in) {
        briefingId=in.readString();
        userId = in.readString();
        userHasReadBriefing = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(briefingId);
        dest.writeString(userId);
        dest.writeByte((byte) (userHasReadBriefing ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BriefingsRecipient> CREATOR = new Creator<BriefingsRecipient>() {
        @Override
        public BriefingsRecipient createFromParcel(Parcel in) {
            return new BriefingsRecipient(in);
        }

        @Override
        public BriefingsRecipient[] newArray(int size) {
            return new BriefingsRecipient[size];
        }
    };

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Boolean getUserHasReadBriefing() {
        return userHasReadBriefing;
    }

    public void setUserHasReadBriefing(Boolean userHasReadBriefing) {
        this.userHasReadBriefing = userHasReadBriefing;
    }

    public String getBriefingId() {
        return briefingId;
    }

    public void setBriefingId(String briefingId) {
        this.briefingId = briefingId;
    }

    public BriefingsRecipient(Cursor cursor)
    {
        briefingId=cursor.getString(cursor.getColumnIndex(BriefingsData.DBTable.briefingId));
        userId=cursor.getString(cursor.getColumnIndex(DBTable.userId));
        userHasReadBriefing = cursor.getInt(cursor.getColumnIndex(DBTable.userHasReadBriefing)) != 0;
    }
    public ContentValues toContentValues()
    {
        ContentValues cv = new ContentValues();
        cv.put(BriefingsData.DBTable.briefingId , briefingId);
        cv.put(DBTable.userId,userId);
        cv.put(DBTable.userHasReadBriefing,userHasReadBriefing);
        return cv;
    }

    public static class DBTable {
        public static final String Name = "BriefingsRecipients";
        public static final String userId = "userId";
        public static final String userHasReadBriefing = "userHasReadBriefing";
    }

    @Override
    public String toString() {
        return "BriefingsRecipient{" +
                "userId='" + userId + '\'' +
                ", userHasReadBriefing=" + userHasReadBriefing +
                '}';
    }
}
