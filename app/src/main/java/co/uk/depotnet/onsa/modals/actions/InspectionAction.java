package co.uk.depotnet.onsa.modals.actions;

import android.os.Parcel;
import android.os.Parcelable;

public class InspectionAction implements Parcelable {

    private String inspectionQuestionId;
    private String questionText;
    private String raisedByUserFullName;
    private String defectComments;
    private String estimateNumber;
    private boolean wasRectified;
    private String cannotBeRectifiedComments;
    private String correctiveMeasure;
    private String actionType;
    private String dueDate;

    protected InspectionAction(Parcel in) {
        inspectionQuestionId = in.readString();
        questionText = in.readString();
        raisedByUserFullName = in.readString();
        defectComments = in.readString();
        estimateNumber = in.readString();
        wasRectified = in.readByte() != 0;
        cannotBeRectifiedComments = in.readString();
        correctiveMeasure = in.readString();
        actionType = in.readString();
        dueDate = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(inspectionQuestionId);
        dest.writeString(questionText);
        dest.writeString(raisedByUserFullName);
        dest.writeString(defectComments);
        dest.writeString(estimateNumber);
        dest.writeByte((byte) (wasRectified ? 1 : 0));
        dest.writeString(cannotBeRectifiedComments);
        dest.writeString(correctiveMeasure);
        dest.writeString(actionType);
        dest.writeString(dueDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<InspectionAction> CREATOR = new Creator<InspectionAction>() {
        @Override
        public InspectionAction createFromParcel(Parcel in) {
            return new InspectionAction(in);
        }

        @Override
        public InspectionAction[] newArray(int size) {
            return new InspectionAction[size];
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

    public boolean getWasRectified() {
        return wasRectified;
    }

    public void setWasRectified(boolean wasRectified) {
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

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }
}


