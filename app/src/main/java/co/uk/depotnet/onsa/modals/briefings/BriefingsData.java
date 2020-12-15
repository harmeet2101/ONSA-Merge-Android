package co.uk.depotnet.onsa.modals.briefings;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import co.uk.depotnet.onsa.database.DBHandler;

public class BriefingsData implements Parcelable {
    @SerializedName("briefingId")
    @Expose
    private String briefingId;
    @SerializedName("briefingName")
    @Expose
    private String briefingName;
    @SerializedName("recipients")
    @Expose
    private List<BriefingsRecipient> recipients = null;
    private boolean isSelected = false;
    public BriefingsData() {
    }

    protected BriefingsData(Parcel in) {
        briefingId = in.readString();
        briefingName = in.readString();
        recipients = in.createTypedArrayList(BriefingsRecipient.CREATOR);
        isSelected = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(briefingId);
        dest.writeString(briefingName);
        dest.writeTypedList(recipients);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BriefingsData> CREATOR = new Creator<BriefingsData>() {
        @Override
        public BriefingsData createFromParcel(Parcel in) {
            return new BriefingsData(in);
        }

        @Override
        public BriefingsData[] newArray(int size) {
            return new BriefingsData[size];
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

    public List<BriefingsRecipient> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<BriefingsRecipient> recipients) {
        this.recipients = recipients;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
    }
    public BriefingsData(Cursor cursor)
    {
        briefingId=cursor.getString(cursor.getColumnIndex(DBTable.briefingId));
        briefingName=cursor.getString(cursor.getColumnIndex(DBTable.briefingName));
        recipients = DBHandler.getInstance().getBriefingsRecipient(briefingId);
        //recipients.add(new BriefingsRecipient(cursor));
    }
    public ContentValues toContentValues()
    {
        ContentValues cv = new ContentValues();
        DBHandler dbHandler = DBHandler.getInstance();
        cv.put(DBTable.briefingId ,  briefingId);
        cv.put(DBTable.briefingName, briefingName);

        if (this.recipients != null && !this.recipients.isEmpty()) {
            for (BriefingsRecipient item : this.recipients) {
                item.setBriefingId(briefingId);
                dbHandler.replaceData(BriefingsRecipient.DBTable.Name, item.toContentValues());
            }
        }
        return cv;
    }

    public static class DBTable {
        public static final String briefingId = "briefingId";
        public static final String briefingName = "briefingName";
    }

    @Override
    public String toString() {
        return "BriefingsData{" +
                "briefingId='" + briefingId + '\'' +
                ", briefingName='" + briefingName + '\'' +
                ", recipients=" + recipients +
                '}';
    }
}
