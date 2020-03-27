package co.uk.depotnet.onsa.networking.response;

import android.os.Parcel;
import android.os.Parcelable;

public class Operatives implements Parcelable {

    private int RiskAssessmentOperativeID;
    private int OperativeID;

    protected Operatives(Parcel in) {
        RiskAssessmentOperativeID = in.readInt();
        OperativeID = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(RiskAssessmentOperativeID);
        dest.writeInt(OperativeID);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Operatives> CREATOR = new Creator<Operatives>() {
        @Override
        public Operatives createFromParcel(Parcel in) {
            return new Operatives(in);
        }

        @Override
        public Operatives[] newArray(int size) {
            return new Operatives[size];
        }
    };

    public int getRiskAssessmentOperativeID() {
        return RiskAssessmentOperativeID;
    }

    public void setRiskAssessmentOperativeID(int riskAssessmentOperativeID) {
        RiskAssessmentOperativeID = riskAssessmentOperativeID;
    }

    public int getOperativeID() {
        return OperativeID;
    }

    public void setOperativeID(int operativeID) {
        OperativeID = operativeID;
    }
}
