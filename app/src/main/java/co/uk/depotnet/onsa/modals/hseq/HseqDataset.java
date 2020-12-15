package co.uk.depotnet.onsa.modals.hseq;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import co.uk.depotnet.onsa.database.DBHandler;

public class HseqDataset implements Parcelable {
    @SerializedName("inspectionTemplates")
    @Expose
    private List<InspectionTemplate> inspectionTemplates = null;
    @SerializedName("operatives")
    @Expose
    private List<OperativeTemplate> operatives = null;
    @SerializedName("inspectors")
    @Expose
    private List<InspectorTemplate> inspectors = null;
    @SerializedName("photoTypes")
    @Expose
    private List<PhotoTypes> photoTypes = null;
    public HseqDataset() {
    }

    protected HseqDataset(Parcel in) {
        inspectionTemplates = in.createTypedArrayList(InspectionTemplate.CREATOR);
        operatives = in.createTypedArrayList(OperativeTemplate.CREATOR);
        inspectors = in.createTypedArrayList(InspectorTemplate.CREATOR);
        photoTypes = in.createTypedArrayList(PhotoTypes.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(inspectionTemplates);
        dest.writeTypedList(operatives);
        dest.writeTypedList(inspectors);
        dest.writeTypedList(photoTypes);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<HseqDataset> CREATOR = new Creator<HseqDataset>() {
        @Override
        public HseqDataset createFromParcel(Parcel in) {
            return new HseqDataset(in);
        }

        @Override
        public HseqDataset[] newArray(int size) {
            return new HseqDataset[size];
        }
    };

    public List<InspectionTemplate> getInspectionTemplates() {
        return inspectionTemplates;
    }

    public void setInspectionTemplates(List<InspectionTemplate> inspectionTemplates) {
        this.inspectionTemplates = inspectionTemplates;
    }

    public List<OperativeTemplate> getOperatives() {
        return operatives;
    }

    public void setOperatives(List<OperativeTemplate> operatives) {
        this.operatives = operatives;
    }

    public List<InspectorTemplate> getInspectors() {
        return inspectors;
    }

    public void setInspectors(List<InspectorTemplate> inspectors) {
        this.inspectors = inspectors;
    }

    public List<PhotoTypes> getPhotoTypes() {
        return photoTypes;
    }

    public void setPhotoTypes(List<PhotoTypes> photoTypes) {
        this.photoTypes = photoTypes;
    }

    public ContentValues toContentValues() {

        ContentValues cv = new ContentValues();

        DBHandler dbHandler = DBHandler.getInstance();

        if (this.inspectionTemplates != null && !this.inspectionTemplates.isEmpty()) {
            for (InspectionTemplate item : this.inspectionTemplates) {
                dbHandler.replaceData(InspectionTemplate.DBTable.NAME, item.toContentValues());
            }
        }
        if (this.operatives != null && !this.operatives.isEmpty()) {
            for (OperativeTemplate item : this.operatives) {
                dbHandler.replaceData(OperativeTemplate.DBTable.NAME, item.toContentValues());
            }
        }
        if (this.inspectors != null && !this.inspectors.isEmpty()) {
            for (InspectorTemplate item : this.inspectors) {
                dbHandler.replaceData(InspectorTemplate.DBTable.NAME, item.toContentValues());
            }
        }
        if (this.photoTypes !=null && !this.photoTypes.isEmpty())
        {
            for (PhotoTypes types : this.photoTypes) {
                dbHandler.replaceData(PhotoTypes.DBTable.NAME, types.toContentValues());
            }
        }
        return cv;
    }


    public static class DBTable {
        //public static final String NAMEDataset = "HseqDatasetResponse";// for module name...
        public static final String InspectionHseq = "InspectionHseq";
        public static final String OperativesHseq = "OperativesHseq";//using depotnet for uniqueness so call in json key with same for dropdown
        public static final String InspectorsHseq = "InspectorsHseq";
        public static final String PhotoTypes = "PhotoTypes";
    }
}
