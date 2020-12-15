package co.uk.depotnet.onsa.modals.actions;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ActionsClose implements Parcelable {

    @SerializedName("inspectionQuestionId")
    @Expose
    private String inspectionQuestionId;
    @SerializedName("questionText")
    @Expose
    private String questionText;
    @SerializedName("raisedByUserFullName")
    @Expose
    private String raisedByUserFullName;
    @SerializedName("defectComments")
    @Expose
    private String defectComments;
    @SerializedName("estimateNumber")
    @Expose
    private String estimateNumber;
    @SerializedName("wasRectified")
    @Expose
    private Boolean wasRectified;
    @SerializedName("cannotBeRectifiedComments")
    @Expose
    private String cannotBeRectifiedComments;
    @SerializedName("correctiveMeasure")
    @Expose
    private String correctiveMeasure;

    public ActionsClose() {
    }

    protected ActionsClose(Parcel in) {
        inspectionQuestionId = in.readString();
        questionText = in.readString();
        raisedByUserFullName = in.readString();
        defectComments = in.readString();
        estimateNumber = in.readString();
        byte tmpWasRectified = in.readByte();
        wasRectified = tmpWasRectified == 0 ? null : tmpWasRectified == 1;
        correctiveMeasure = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(inspectionQuestionId);
        dest.writeString(questionText);
        dest.writeString(raisedByUserFullName);
        dest.writeString(defectComments);
        dest.writeString(estimateNumber);
        dest.writeByte((byte) (wasRectified == null ? 0 : wasRectified ? 1 : 2));
        dest.writeString(correctiveMeasure);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ActionsClose> CREATOR = new Creator<ActionsClose>() {
        @Override
        public ActionsClose createFromParcel(Parcel in) {
            return new ActionsClose(in);
        }

        @Override
        public ActionsClose[] newArray(int size) {
            return new ActionsClose[size];
        }
    };

    public String getInspectionQuestionId() {
        return inspectionQuestionId;
    }

    public void setInspectionQuestionId(String inspectionQuestionId) {
        this.inspectionQuestionId = inspectionQuestionId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getRaisedByUserFullName() {
        return raisedByUserFullName;
    }

    public void setRaisedByUserFullName(String raisedByUserFullName) {
        this.raisedByUserFullName = raisedByUserFullName;
    }

    public String getDefectComments() {
        return defectComments;
    }

    public void setDefectComments(String defectComments) {
        this.defectComments = defectComments;
    }

    public String getEstimateNumber() {
        return estimateNumber;
    }

    public void setEstimateNumber(String estimateNumber) {
        this.estimateNumber = estimateNumber;
    }

    public Boolean getWasRectified() {
        return wasRectified;
    }

    public void setWasRectified(Boolean wasRectified) {
        this.wasRectified = wasRectified;
    }

    public String getCannotBeRectifiedComments() {
        return cannotBeRectifiedComments;
    }

    public void setCannotBeRectifiedComments(String cannotBeRectifiedComments) {
        this.cannotBeRectifiedComments = cannotBeRectifiedComments;
    }

    public String getCorrectiveMeasure() {
        return correctiveMeasure;
    }

    public void setCorrectiveMeasure(String correctiveMeasure) {
        this.correctiveMeasure = correctiveMeasure;
    }
    public ActionsClose(Cursor cursor) {
        if (cursor != null && !cursor.isBeforeFirst() && !cursor.isAfterLast()) {
            inspectionQuestionId = cursor.getString(cursor.getColumnIndex(DBTable.inspectionQuestionId));
            questionText = cursor.getString(cursor.getColumnIndex(DBTable.questionText));
            raisedByUserFullName = cursor.getString(cursor.getColumnIndex(DBTable.raisedByUserFullName));
            defectComments = cursor.getString(cursor.getColumnIndex(DBTable.defectComments));
            estimateNumber = cursor.getString(cursor.getColumnIndex(DBTable.estimateNumber));
            wasRectified = cursor.getInt(cursor.getColumnIndex(DBTable.wasRectified)) != 0;
            cannotBeRectifiedComments = cursor.getString(cursor.getColumnIndex(DBTable.cannotBeRectifiedComments));
            correctiveMeasure = cursor.getString(cursor.getColumnIndex(DBTable.correctiveMeasure));
        }
    }
    
    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        if (inspectionQuestionId!=null) {
            cv.put(DBTable.inspectionQuestionId, inspectionQuestionId);
        }
        cv.put(DBTable.questionText, questionText);
        cv.put(DBTable.raisedByUserFullName, raisedByUserFullName);
        cv.put(DBTable.defectComments, defectComments);
        cv.put(DBTable.estimateNumber, estimateNumber);
        cv.put(DBTable.wasRectified, wasRectified);
        cv.put(DBTable.cannotBeRectifiedComments, cannotBeRectifiedComments);
        cv.put(DBTable.correctiveMeasure, correctiveMeasure);

        return cv;
    }
    public static class DBTable {
        public static final String inspectionQuestionId = "inspectionQuestionId";
        public static final String questionText = "questionText";
        public static final String raisedByUserFullName = "raisedByUserFullName";
        public static final String defectComments = "defectComments";
        public static final String estimateNumber = "estimateNumber";
        public static final String wasRectified = "wasRectified";
        public static final String cannotBeRectifiedComments = "cannotBeRectifiedComments";
        public static final String correctiveMeasure = "correctiveMeasure";
    }
}


