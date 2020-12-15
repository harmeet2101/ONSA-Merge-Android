package co.uk.depotnet.onsa.modals.briefings;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import co.uk.depotnet.onsa.database.DBHandler;

public class BriefingsDocModal implements Parcelable {
    @SerializedName("sentDate")
    @Expose
    private String sentDate;
    @SerializedName("briefings")
    @Expose
    private List<BriefingsData> briefings = null;

    public BriefingsDocModal() {
    }

    protected BriefingsDocModal(Parcel in) {
        sentDate = in.readString();
        briefings = in.createTypedArrayList(BriefingsData.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sentDate);
        dest.writeTypedList(briefings);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BriefingsDocModal> CREATOR = new Creator<BriefingsDocModal>() {
        @Override
        public BriefingsDocModal createFromParcel(Parcel in) {
            return new BriefingsDocModal(in);
        }

        @Override
        public BriefingsDocModal[] newArray(int size) {
            return new BriefingsDocModal[size];
        }
    };

    public String getSentDate() {
        return sentDate;
    }

    public void setSentDate(String sentDate) {
        this.sentDate = sentDate;
    }

    public List<BriefingsData> getBriefings() {
        return briefings;
    }

    public void setBriefings(List<BriefingsData> briefings) {
        this.briefings = briefings;
    }

    public BriefingsDocModal(Cursor cursor)
    {
        sentDate=cursor.getString(cursor.getColumnIndex(DBTable.sentDate));
        briefings = new ArrayList<>();
        briefings.add(new BriefingsData(cursor));
    }
    public void toContentValues()
    {
        for (BriefingsData briefingsData : briefings)
        {
            ContentValues cv = new ContentValues();
            cv.put(DBTable.sentDate, sentDate);
            cv.putAll(briefingsData.toContentValues());
            DBHandler.getInstance().replaceData(DBTable.NAME , cv);
        }
        return ;
    }

    public static class DBTable {
        public static final String NAME = "Briefings";
        public static final String sentDate = "sentDate";
    }

    @Override
    public String toString() {
        return "BriefingsDocModal{" +
                "sentDate='" + sentDate + '\'' +
                ", briefings=" + briefings +
                '}';
    }
}

