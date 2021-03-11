package co.uk.depotnet.onsa.modals.actions;

import android.os.Parcel;
import android.os.Parcelable;

public class IncidentAction implements Parcelable {

    private String incidentActionId;
    private String actionDescription;
    private String raisedByUserFullName;
    private String actionComments;
    private String estimateNumber;
    private String address;
    private String postcode;
    private String incidentId;
    private String incidentTitle;
    private boolean isUrgent;
    private boolean wasRectified;
    private String cannotBeRectifiedComments;
    private String closeOutComments;
    private String actionType;
    private String dueDate;

    protected IncidentAction(Parcel in) {
        incidentActionId = in.readString();
        actionDescription = in.readString();
        raisedByUserFullName = in.readString();
        actionComments = in.readString();
        estimateNumber = in.readString();
        wasRectified = in.readByte() != 0;
        cannotBeRectifiedComments = in.readString();
        closeOutComments = in.readString();
        actionType = in.readString();
        dueDate = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(incidentActionId);
        dest.writeString(actionDescription);
        dest.writeString(raisedByUserFullName);
        dest.writeString(actionComments);
        dest.writeString(estimateNumber);
        dest.writeByte((byte) (wasRectified ? 1 : 0));
        dest.writeString(cannotBeRectifiedComments);
        dest.writeString(closeOutComments);
        dest.writeString(actionType);
        dest.writeString(dueDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<IncidentAction> CREATOR = new Creator<IncidentAction>() {
        @Override
        public IncidentAction createFromParcel(Parcel in) {
            return new IncidentAction(in);
        }

        @Override
        public IncidentAction[] newArray(int size) {
            return new IncidentAction[size];
        }
    };

    public String getIncidentActionId() {
        return incidentActionId;
    }

    public void setIncidentActionId(String incidentActionId) {
        this.incidentActionId = incidentActionId;
    }

    public String getActionDescription() {
        return actionDescription;
    }

    public void setActionDescription(String actionDescription) {
        this.actionDescription = actionDescription;
    }

    public String getRaisedByUserFullName() {
        return raisedByUserFullName;
    }

    public void setRaisedByUserFullName(String raisedByUserFullName) {
        this.raisedByUserFullName = raisedByUserFullName;
    }

    public String getActionComments() {
        return actionComments;
    }

    public void setActionComments(String actionComments) {
        this.actionComments = actionComments;
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

    public String getCloseOutComments() {
        return closeOutComments;
    }

    public void setCloseOutComments(String closeOutComments) {
        this.closeOutComments = closeOutComments;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getActionType() {
        return actionType;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getIncidentId() {
        return incidentId;
    }

    public void setIncidentId(String incidentId) {
        this.incidentId = incidentId;
    }

    public String getIncidentTitle() {
        return incidentTitle;
    }

    public void setIncidentTitle(String incidentTitle) {
        this.incidentTitle = incidentTitle;
    }

    public boolean isUrgent() {
        return isUrgent;
    }

    public void setUrgent(boolean urgent) {
        isUrgent = urgent;
    }
}


