package co.uk.depotnet.onsa.modals.actions;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

public class Action implements Parcelable {

    private String actionId;
    private String description;
    private String raisedByUserFullName;
    private String comments;
    private String estimateNumber;
    private boolean wasRectified;
    private String cannotBeRectifiedComments;
    private String correctiveMeasure;
    private String actionType;
    private boolean isIncidentAction;
    private String dueDate;
    private String address;
    private String postcode;
    private String incidentId;
    private String incidentTitle;
    private boolean isUrgent;

    protected Action(Parcel in) {
        actionId = in.readString();
        description = in.readString();
        raisedByUserFullName = in.readString();
        comments = in.readString();
        estimateNumber = in.readString();
        wasRectified = in.readByte() != 0;
        cannotBeRectifiedComments = in.readString();
        correctiveMeasure = in.readString();
        actionType = in.readString();
        isIncidentAction = in.readByte() != 0;
        address = in.readString();
        postcode = in.readString();
        incidentId = in.readString();
        incidentTitle = in.readString();
        isUrgent = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(actionId);
        dest.writeString(description);
        dest.writeString(raisedByUserFullName);
        dest.writeString(comments);
        dest.writeString(estimateNumber);
        dest.writeByte((byte) (wasRectified ? 1 : 0));
        dest.writeString(cannotBeRectifiedComments);
        dest.writeString(correctiveMeasure);
        dest.writeString(actionType);
        dest.writeByte((byte) (isIncidentAction ? 1 : 0));
        dest.writeString(dueDate);
        dest.writeString(address);
        dest.writeString(postcode);
        dest.writeString(incidentId);
        dest.writeString(incidentTitle);
        dest.writeByte((byte) (isUrgent ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Action> CREATOR = new Creator<Action>() {
        @Override
        public Action createFromParcel(Parcel in) {
            return new Action(in);
        }

        @Override
        public Action[] newArray(int size) {
            return new Action[size];
        }
    };

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRaisedByUserFullName() {
        return raisedByUserFullName;
    }

    public void setRaisedByUserFullName(String raisedByUserFullName) {
        this.raisedByUserFullName = raisedByUserFullName;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
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

    public boolean isIncidentAction() {
        return isIncidentAction;
    }

    public void setIncidentAction(boolean incidentAction) {
        isIncidentAction = incidentAction;
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

    public Action(IncidentAction action) {
        actionId = action.getIncidentActionId();
        description = action.getActionDescription();
        raisedByUserFullName = action.getRaisedByUserFullName();
        comments = action.getActionComments();
        estimateNumber = action.getEstimateNumber();
        wasRectified = action.getWasRectified();
        cannotBeRectifiedComments = action.getCannotBeRectifiedComments();
        correctiveMeasure = action.getCloseOutComments();
        actionType = action.getActionType();
        isIncidentAction = true;
        dueDate = action.getDueDate();
        address = action.getAddress();
        postcode = action.getPostcode();
        incidentId = action.getIncidentId();
        incidentTitle = action.getIncidentTitle();
        isUrgent = action.isUrgent();
    }

    public Action(InspectionAction action) {
        actionId = action.getInspectionQuestionId();
        description = action.getQuestionText();
        raisedByUserFullName = action.getRaisedByUserFullName();
        comments = action.getDefectComments();
        estimateNumber = action.getEstimateNumber();
        wasRectified = action.getWasRectified();
        cannotBeRectifiedComments = action.getCannotBeRectifiedComments();
        correctiveMeasure = action.getCorrectiveMeasure();
        actionType = action.getActionType();
        isIncidentAction = false;
        dueDate = action.getDueDate();
    }

    public Action(Cursor cursor) {
        if (cursor != null && !cursor.isBeforeFirst() && !cursor.isAfterLast()) {
            actionId = cursor.getString(cursor.getColumnIndex(DBTable.actionId));
            description = cursor.getString(cursor.getColumnIndex(DBTable.description));
            raisedByUserFullName = cursor.getString(cursor.getColumnIndex(DBTable.raisedByUserFullName));
            comments = cursor.getString(cursor.getColumnIndex(DBTable.comments));
            estimateNumber = cursor.getString(cursor.getColumnIndex(DBTable.estimateNumber));
            wasRectified = cursor.getInt(cursor.getColumnIndex(DBTable.wasRectified)) != 0;
            cannotBeRectifiedComments = cursor.getString(cursor.getColumnIndex(DBTable.cannotBeRectifiedComments));
            correctiveMeasure = cursor.getString(cursor.getColumnIndex(DBTable.correctiveMeasure));
            actionType = cursor.getString(cursor.getColumnIndex(DBTable.actionType));
            isIncidentAction = cursor.getInt(cursor.getColumnIndex(DBTable.isIncidentAction)) != 0;
            dueDate = cursor.getString(cursor.getColumnIndex(DBTable.dueDate));
            address = cursor.getString(cursor.getColumnIndex(DBTable.address));
            postcode = cursor.getString(cursor.getColumnIndex(DBTable.postcode));
            incidentId = cursor.getString(cursor.getColumnIndex(DBTable.incidentId));
            incidentTitle = cursor.getString(cursor.getColumnIndex(DBTable.incidentTitle));
            isUrgent = cursor.getInt(cursor.getColumnIndex(DBTable.isUrgent)) != 0;
        }
    }

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        if (actionId !=null) {
            cv.put(DBTable.actionId, actionId);
        }
        cv.put(DBTable.description, description);
        cv.put(DBTable.raisedByUserFullName, raisedByUserFullName);
        cv.put(DBTable.comments, comments);
        cv.put(DBTable.estimateNumber, estimateNumber);
        cv.put(DBTable.wasRectified, wasRectified);
        cv.put(DBTable.cannotBeRectifiedComments, cannotBeRectifiedComments);
        cv.put(DBTable.correctiveMeasure, correctiveMeasure);
        cv.put(DBTable.actionType, actionType);
        cv.put(DBTable.isIncidentAction, isIncidentAction);
        cv.put(DBTable.dueDate, dueDate);
        cv.put(DBTable.address, address);
        cv.put(DBTable.postcode, postcode);
        cv.put(DBTable.incidentId, incidentId);
        cv.put(DBTable.incidentTitle, incidentTitle);
        cv.put(DBTable.isUrgent, isUrgent);

        return cv;
    }
    public static class DBTable {
        public static final String NAME = "Action";
        public static final String actionId = "actionId";
        public static final String description = "description";
        public static final String raisedByUserFullName = "raisedByUserFullName";
        public static final String comments = "comments";
        public static final String estimateNumber = "estimateNumber";
        public static final String wasRectified = "wasRectified";
        public static final String cannotBeRectifiedComments = "cannotBeRectifiedComments";
        public static final String correctiveMeasure = "correctiveMeasure";
        public static final String actionType = "actionType";
        public static final String isIncidentAction = "isIncidentAction";
        public static final String dueDate = "dueDate";
        public static final String address = "address";
        public static final String postcode = "postcode";
        public static final String incidentId = "incidentId";
        public static final String incidentTitle = "incidentTitle";
        public static final String isUrgent = "isUrgent";
    }
    
}


