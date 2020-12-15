package co.uk.depotnet.onsa.modals.actions;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class OutstandingAction implements Parcelable {
    @SerializedName("dueDate")
    @Expose
    private String dueDate;
    private String actionType;
    @SerializedName("actions")
    @Expose
    private List<ActionsClose> actions = null;

    public OutstandingAction() {
    }

    protected OutstandingAction(Parcel in) {
        dueDate = in.readString();
        actionType = in.readString();
        actions = in.createTypedArrayList(ActionsClose.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(dueDate);
        dest.writeString(actionType);
        dest.writeTypedList(actions);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OutstandingAction> CREATOR = new Creator<OutstandingAction>() {
        @Override
        public OutstandingAction createFromParcel(Parcel in) {
            return new OutstandingAction(in);
        }

        @Override
        public OutstandingAction[] newArray(int size) {
            return new OutstandingAction[size];
        }
    };

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public List<ActionsClose> getActions() {
        return actions;
    }

    public void setActions(List<ActionsClose> actions) {
        this.actions = actions;
    }

    public OutstandingAction(Cursor cursor)
    {
        dueDate=cursor.getString(cursor.getColumnIndex(DBTable.dueDate));
        actionType=cursor.getString(cursor.getColumnIndex(DBTable.actionType));
        actions = new ArrayList<>();
        actions.add(new ActionsClose(cursor));
    }

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(DBTable.dueDate, dueDate);
        cv.put(DBTable.actionType, actionType);
        for (ActionsClose actionsClose : actions)
        {
            cv.putAll(actionsClose.toContentValues());
        }
        return cv;
    }

    public static class DBTable {
        public static final String NAME = "Actions";
        public static final String actionType = "actionType";
        public static final String dueDate = "dueDate";
    }
}
