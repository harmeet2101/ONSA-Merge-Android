package co.uk.depotnet.onsa.networking.response;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class RiskAssessmentResponse extends BaseResponse implements Parcelable {

    private int RiskAssessmentID;
    private ArrayList<Operatives> Operatives;

    protected RiskAssessmentResponse(Parcel in) {
        super(in);
    }

    public int getRiskAssessmentID() {
        return RiskAssessmentID;
    }

    public void setRiskAssessmentID(int RiskAssessmentID) {
        this.RiskAssessmentID = RiskAssessmentID;
    }


    public ArrayList<Operatives> getOperatives() {
        return Operatives;
    }

    public void setOperatives(ArrayList<Operatives> operatives) {
        Operatives = operatives;
    }


}