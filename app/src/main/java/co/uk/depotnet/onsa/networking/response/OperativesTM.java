package co.uk.depotnet.onsa.networking.response;

import android.os.Parcel;
import android.os.Parcelable;

public class OperativesTM implements Parcelable {
    
    private int RiskAssessmentTmOperativeID;
    private int OperativeID;

    protected OperativesTM(Parcel in) {
        RiskAssessmentTmOperativeID = in.readInt();
        OperativeID = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(RiskAssessmentTmOperativeID);
        dest.writeInt(OperativeID);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OperativesTM> CREATOR = new Creator<OperativesTM>() {
        @Override
        public OperativesTM createFromParcel(Parcel in) {
            return new OperativesTM(in);
        }

        @Override
        public OperativesTM[] newArray(int size) {
            return new OperativesTM[size];
        }
    };

    public int getRiskAssessmentOperativeID() {
        return RiskAssessmentTmOperativeID;
    }

    public void setRiskAssessmentOperativeID(int riskAssessmentOperativeID) {
        RiskAssessmentTmOperativeID = riskAssessmentOperativeID;
    }

    public int getOperativeID() {
        return OperativeID;
    }

    public void setOperativeID(int operativeID) {
        OperativeID = operativeID;
    }


    @Override
    public String toString() {
        return "RiskAssessmentTmOperativeID "+RiskAssessmentTmOperativeID;
    }
}
