package co.uk.depotnet.onsa.modals.actions;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.List;

import co.uk.depotnet.onsa.database.DBHandler;

public class ActionResponse implements Parcelable {

    private String dueDate;
    private String closedDate;
    private List<InspectionAction> inspectionActions;
    private List<IncidentAction> incidentActions;
    private String actionType;

    protected ActionResponse(Parcel in) {
        dueDate = in.readString();
        closedDate = in.readString();
        inspectionActions = in.createTypedArrayList(InspectionAction.CREATOR);
        incidentActions = in.createTypedArrayList(IncidentAction.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(dueDate);
        dest.writeString(closedDate);
        dest.writeTypedList(inspectionActions);
        dest.writeTypedList(incidentActions);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ActionResponse> CREATOR = new Creator<ActionResponse>() {
        @Override
        public ActionResponse createFromParcel(Parcel in) {
            return new ActionResponse(in);
        }

        @Override
        public ActionResponse[] newArray(int size) {
            return new ActionResponse[size];
        }
    };

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }


    public void toContentValues() {
        if(inspectionActions != null && !inspectionActions.isEmpty()){
            for (InspectionAction action :
                    inspectionActions) {
                action.setActionType(actionType);
                if(!TextUtils.isEmpty(dueDate)) {
                    action.setDueDate(dueDate);
                }else if(!TextUtils.isEmpty(closedDate)) {
                    action.setDueDate(closedDate);
                }
                DBHandler.getInstance().replaceData(Action.DBTable.NAME , new Action(action).toContentValues());
            }
        }

        if(incidentActions != null && !incidentActions.isEmpty()){
            for (IncidentAction action : incidentActions) {
                action.setActionType(actionType);
                if(!TextUtils.isEmpty(dueDate)) {
                    action.setDueDate(dueDate);
                }else if(!TextUtils.isEmpty(closedDate)) {
                    action.setDueDate(closedDate);
                }
                DBHandler.getInstance().replaceData(Action.DBTable.NAME , new Action(action).toContentValues());
            }
        }
    }
}
