package co.uk.depotnet.onsa.networking.response;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class RiskAssessmentTMResponse extends BaseResponse implements Parcelable {

    private int RiskAssessmentTmID;
    private ArrayList<OperativesTM> Operatives;

    protected RiskAssessmentTMResponse(Parcel in) {
        super(in);
    }

    public int getRiskAssessmentID() {
        return RiskAssessmentTmID;
    }

    public void setRiskAssessmentID(int RiskAssessmentID) {
        this.RiskAssessmentTmID = RiskAssessmentID;
    }


    public ArrayList<OperativesTM> getOperatives() {
        return Operatives;
    }

    public void setOperatives(ArrayList<OperativesTM> operatives) {
        Operatives = operatives;
    }


}