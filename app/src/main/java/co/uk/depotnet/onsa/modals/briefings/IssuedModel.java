package co.uk.depotnet.onsa.modals.briefings;

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

public class IssuedModel implements Parcelable {
    @SerializedName("dateRead")
    @Expose
    private String dateRead;
    @SerializedName("briefings")
    @Expose
    private List<IssuedDocModal> briefings = null;
    @SerializedName("operativeNames")
    @Expose
    private List<String> operativeNames = null;

    public IssuedModel() {
    }


    protected IssuedModel(Parcel in) {
        dateRead = in.readString();
        briefings = in.createTypedArrayList(IssuedDocModal.CREATOR);
        operativeNames = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(dateRead);
        dest.writeTypedList(briefings);
        dest.writeStringList(operativeNames);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<IssuedModel> CREATOR = new Creator<IssuedModel>() {
        @Override
        public IssuedModel createFromParcel(Parcel in) {
            return new IssuedModel(in);
        }

        @Override
        public IssuedModel[] newArray(int size) {
            return new IssuedModel[size];
        }
    };

    public String getDateRead() {
        return dateRead;
    }

    public void setDateRead(String dateRead) {
        this.dateRead = dateRead;
    }

    public List<IssuedDocModal> getBriefings() {
        return briefings;
    }

    public void setBriefings(List<IssuedDocModal> briefings) {
        this.briefings = briefings;
    }

    public List<String> getOperativeNames() {
        return operativeNames;
    }

    public void setOperativeNames(List<String> operativeNames) {
        this.operativeNames = operativeNames;
    }

    public IssuedModel(Cursor cursor) {
        if (cursor != null && !cursor.isBeforeFirst() && !cursor.isAfterLast()) {
            dateRead = cursor.getString(cursor.getColumnIndex(DBTable.dateRead));
            briefings = new ArrayList<>();
            briefings.add(new IssuedDocModal(cursor));
            String operativeNames= cursor.getString(cursor.getColumnIndex(DBTable.operativeNames));
            this.operativeNames=new ArrayList<>();
            if (operativeNames != null) {
                Collections.addAll(this.operativeNames, operativeNames.split(","));
            }
        }
    }

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        if (dateRead!=null){
            cv.put(DBTable.dateRead, dateRead);
        }
        for (IssuedDocModal issuedDocModal : briefings)
        {
            cv.putAll(issuedDocModal.toContentValues());
            //cv.put(IssuedDocModal.DBTable.briefingName,issuedDocModal.getBriefingName());
        }
        String operativeNames = "";
        if (this.operativeNames != null && !this.operativeNames.isEmpty()) {
            operativeNames = TextUtils.join(",", this.operativeNames);
        }
        cv.put(DBTable.operativeNames, operativeNames);//seperated with , and pass string
        return cv;
    }
    public static class DBTable {
        public static final String NAME = "BriefingsShared";
        public static final String dateRead = "dateRead";
        public static final String operativeNames = "operativeNames";

    }
}
