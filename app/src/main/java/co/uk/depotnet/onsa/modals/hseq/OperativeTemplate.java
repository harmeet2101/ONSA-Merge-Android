package co.uk.depotnet.onsa.modals.hseq;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import co.uk.depotnet.onsa.listeners.DropDownItem;

public class OperativeTemplate implements Parcelable , DropDownItem {
    @SerializedName("operativeId")
    @Expose
    private String operativeId;
    @SerializedName("operativeFullName")
    @Expose
    private String operativeFullName;
    @SerializedName("gangIds")
    @Expose
    private List<String> gangIds = null;

    public OperativeTemplate() {
    }
    public OperativeTemplate(Cursor cursor)
    {
        operativeId=cursor.getString(cursor.getColumnIndex(DBTable.operativeId));
        operativeFullName=cursor.getString(cursor.getColumnIndex(DBTable.operativeFullName));
        String gangIds= cursor.getString(cursor.getColumnIndex(DBTable.gangIds));
        this.gangIds=new ArrayList<>();
        if (gangIds != null) {
            Collections.addAll(this.gangIds, gangIds.split(","));
        }

    }
    protected OperativeTemplate(Parcel in) {
        operativeId = in.readString();
        operativeFullName = in.readString();
        gangIds = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(operativeId);
        dest.writeString(operativeFullName);
        dest.writeStringList(gangIds);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OperativeTemplate> CREATOR = new Creator<OperativeTemplate>() {
        @Override
        public OperativeTemplate createFromParcel(Parcel in) {
            return new OperativeTemplate(in);
        }

        @Override
        public OperativeTemplate[] newArray(int size) {
            return new OperativeTemplate[size];
        }
    };

    public String getOperativeId() {
        return operativeId;
    }

    public void setOperativeId(String operativeId) {
        this.operativeId = operativeId;
    }

    public String getOperativeFullName() {
        return operativeFullName;
    }

    public void setOperativeFullName(String operativeFullName) {
        this.operativeFullName = operativeFullName;
    }

    public List<String> getGangIds() {
        return gangIds;
    }

    public void setGangIds(List<String> gangIds) {
        this.gangIds = gangIds;
    }


    public ContentValues toContentValues() {

        ContentValues cv = new ContentValues();
        cv.put(DBTable.operativeId, this.operativeId);
        cv.put(DBTable.operativeFullName, this.operativeFullName);
        String gangIds = "";
        if (this.gangIds != null && !this.gangIds.isEmpty()) {
            gangIds = TextUtils.join(",", this.gangIds);
        }
        cv.put(DBTable.gangIds, gangIds);//seperated with , and pass string
        return cv;
    }

    @Override
    public String getDisplayItem() {
        return operativeFullName;
    }
    @Override
    public String getUploadValue() {
        return operativeId;
    }

    public static class DBTable {
        public static final String NAME = "OperativesHseq";
        public static final String operativeId = "operativeId";
        public static final String operativeFullName = "operativeFullName";
        public static final String gangIds = "gangIds";
    }

    @Override
    public String toString() {
        return "OperativeTemplate{" +
                "operativeId='" + operativeId + '\'' +
                ", operativeFullName='" + operativeFullName + '\'' +
                ", gangIds=" + gangIds +
                '}';
    }
}
