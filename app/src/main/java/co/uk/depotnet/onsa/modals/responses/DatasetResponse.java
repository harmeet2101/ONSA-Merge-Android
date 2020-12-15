package co.uk.depotnet.onsa.modals.responses;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.modals.ItemType;
import co.uk.depotnet.onsa.modals.KitBagDocument;
import co.uk.depotnet.onsa.modals.RiskElementType;
import co.uk.depotnet.onsa.modals.WorkItem;

public class DatasetResponse implements Parcelable {
    public static final Creator<DatasetResponse> CREATOR = new Creator<DatasetResponse>() {
        @Override
        public DatasetResponse createFromParcel(Parcel in) {
            return new DatasetResponse(in);
        }

        @Override
        public DatasetResponse[] newArray(int size) {
            return new DatasetResponse[size];
        }
    };
    private ArrayList<ItemType> aerialCables;
    private ArrayList<ItemType> photoTypes;
    private ArrayList<ItemType> bookOperatives;
    private ArrayList<WorkItem> workItems;
    private ArrayList<ItemType> dcrReasonCodes;
    private ArrayList<ItemType> roadTypes;
    private ArrayList<ItemType> engCompOutcomes;
    private ArrayList<ItemType> reinstatementTypes;
    private ArrayList<ItemType> speedLimits;
    private ArrayList<ItemType> abandonReasons;
    private ArrayList<ItemType> surfaceTypes;
    private ArrayList<WorkItem> dfeWorkItems;
    private ArrayList<ItemType> stayTypes;
    private ArrayList<KitBagDocument> kitbagFolders;
    private ArrayList<ItemType> materialTypes;
    private ArrayList<ItemType> jobCategories;
    private ArrayList<ItemType> lowestWireTypes;
    private ArrayList<ItemType> anchorTypes;
    private ArrayList<ItemType> ugCableTypes;
    private ArrayList<ItemType> trafficManagementTypes;
    private ArrayList<ItemType> poleTypes;
    private ArrayList<ItemType> blockTerminalTypes;
    private ArrayList<ItemType> jointClosureTypes;
    private ArrayList<ItemType> dacTypes;
    private ArrayList<ItemType> weatherConditions;
    private ArrayList<ItemType> pedestrianManagementTypes;
    private ArrayList<ItemType> newReplacedRecycledTypes;
    private ArrayList<RiskElementType> riskAssessmentRiskElementTypes;


    protected DatasetResponse(Parcel in) {
        aerialCables = in.createTypedArrayList(ItemType.CREATOR);
        photoTypes = in.createTypedArrayList(ItemType.CREATOR);
        bookOperatives = in.createTypedArrayList(ItemType.CREATOR);
        workItems = in.createTypedArrayList(WorkItem.CREATOR);
        dcrReasonCodes = in.createTypedArrayList(ItemType.CREATOR);
        roadTypes = in.createTypedArrayList(ItemType.CREATOR);
        engCompOutcomes = in.createTypedArrayList(ItemType.CREATOR);
        reinstatementTypes = in.createTypedArrayList(ItemType.CREATOR);
        speedLimits = in.createTypedArrayList(ItemType.CREATOR);
        abandonReasons = in.createTypedArrayList(ItemType.CREATOR);
        surfaceTypes = in.createTypedArrayList(ItemType.CREATOR);
        dfeWorkItems = in.createTypedArrayList(WorkItem.CREATOR);
        stayTypes = in.createTypedArrayList(ItemType.CREATOR);
        kitbagFolders = in.createTypedArrayList(KitBagDocument.CREATOR);
        materialTypes = in.createTypedArrayList(ItemType.CREATOR);
        jobCategories = in.createTypedArrayList(ItemType.CREATOR);
        lowestWireTypes = in.createTypedArrayList(ItemType.CREATOR);
        anchorTypes = in.createTypedArrayList(ItemType.CREATOR);
        ugCableTypes = in.createTypedArrayList(ItemType.CREATOR);
        trafficManagementTypes = in.createTypedArrayList(ItemType.CREATOR);
        poleTypes = in.createTypedArrayList(ItemType.CREATOR);
        blockTerminalTypes = in.createTypedArrayList(ItemType.CREATOR);
        jointClosureTypes = in.createTypedArrayList(ItemType.CREATOR);
        dacTypes = in.createTypedArrayList(ItemType.CREATOR);
        weatherConditions = in.createTypedArrayList(ItemType.CREATOR);
        pedestrianManagementTypes = in.createTypedArrayList(ItemType.CREATOR);
        newReplacedRecycledTypes = in.createTypedArrayList(ItemType.CREATOR);
        riskAssessmentRiskElementTypes = in.createTypedArrayList(RiskElementType.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(aerialCables);
        dest.writeTypedList(photoTypes);
        dest.writeTypedList(bookOperatives);
        dest.writeTypedList(workItems);
        dest.writeTypedList(dcrReasonCodes);
        dest.writeTypedList(roadTypes);
        dest.writeTypedList(engCompOutcomes);
        dest.writeTypedList(reinstatementTypes);
        dest.writeTypedList(speedLimits);
        dest.writeTypedList(abandonReasons);
        dest.writeTypedList(surfaceTypes);
        dest.writeTypedList(dfeWorkItems);
        dest.writeTypedList(stayTypes);
        dest.writeTypedList(kitbagFolders);
        dest.writeTypedList(materialTypes);
        dest.writeTypedList(jobCategories);
        dest.writeTypedList(lowestWireTypes);
        dest.writeTypedList(anchorTypes);
        dest.writeTypedList(ugCableTypes);
        dest.writeTypedList(trafficManagementTypes);
        dest.writeTypedList(poleTypes);
        dest.writeTypedList(blockTerminalTypes);
        dest.writeTypedList(jointClosureTypes);
        dest.writeTypedList(dacTypes);
        dest.writeTypedList(weatherConditions);
        dest.writeTypedList(pedestrianManagementTypes);
        dest.writeTypedList(newReplacedRecycledTypes);
        dest.writeTypedList(riskAssessmentRiskElementTypes);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    private void insertRecordReturn(){
//        DBHandler dbHandler = DBHandler.getInstance();
//        ItemType itemType0 = new ItemType("No Return Required" , "recordReturn", "KC40");
//        dbHandler.replaceData(ItemType.DBTable.NAME, itemType0.toContentValues());
//        ItemType itemType1 = new ItemType("Works Executed as Planned" , "recordReturn", "KC41");
//        dbHandler.replaceData(ItemType.DBTable.NAME, itemType1.toContentValues());
//        ItemType itemType2 = new ItemType("Works Executed with Changes" , "recordReturn", "KC42");
//        dbHandler.replaceData(ItemType.DBTable.NAME, itemType2.toContentValues());


    }



    public ContentValues toContentValues() {

        ContentValues cv = new ContentValues();

        DBHandler dbHandler = DBHandler.getInstance();

        insertRecordReturn();

        if (this.aerialCables != null && !this.aerialCables.isEmpty()) {
            for (ItemType item : this.aerialCables) {
                item.settype(DBTable.aerialCables);
                dbHandler.replaceData(ItemType.DBTable.NAME, item.toContentValues());
            }
        }


        if (this.photoTypes != null && !this.photoTypes.isEmpty()) {
            for (ItemType item : this.photoTypes) {
                item.settype(DBTable.photoTypes);
                dbHandler.replaceData(ItemType.DBTable.NAME, item.toContentValues());
            }
        }

        if (this.bookOperatives != null && !this.bookOperatives.isEmpty()) {
            for (ItemType item : this.bookOperatives) {
                item.settype(DBTable.bookOperatives);
                dbHandler.replaceData(ItemType.DBTable.NAME, item.toContentValues());
            }
        }


        if (this.workItems != null && !this.workItems.isEmpty()) {
            for (WorkItem item : this.workItems) {
                item.settype(DBTable.workItems);
                dbHandler.replaceData(WorkItem.DBTable.NAME, item.toContentValues());
            }
        }
        if (this.dcrReasonCodes != null && !this.dcrReasonCodes.isEmpty()) {
            for (ItemType item : this.dcrReasonCodes) {
                item.settype(DBTable.dcrReasonCodes);
                dbHandler.replaceData(ItemType.DBTable.NAME, item.toContentValues());
            }
        }

        if (this.roadTypes != null && !this.roadTypes.isEmpty()) {
            for (ItemType item : this.roadTypes) {
                item.settype(DBTable.roadTypes);
                dbHandler.replaceData(ItemType.DBTable.NAME, item.toContentValues());
            }
        }

        if (this.engCompOutcomes != null && !this.engCompOutcomes.isEmpty()) {
            for (ItemType item : this.engCompOutcomes) {
                item.settype(DBTable.engCompOutcomes);
                dbHandler.replaceData(ItemType.DBTable.NAME, item.toContentValues());
            }
        }


        if (this.reinstatementTypes != null && !this.reinstatementTypes.isEmpty()) {
            for (ItemType item : this.reinstatementTypes) {
                item.settype(DBTable.reinstatementTypes);
                dbHandler.replaceData(ItemType.DBTable.NAME, item.toContentValues());
            }
        }


        if (this.speedLimits != null && !this.speedLimits.isEmpty()) {
            for (ItemType item : this.speedLimits) {
                item.settype(DBTable.speedLimits);
                dbHandler.replaceData(ItemType.DBTable.NAME, item.toContentValues());
            }
        }


        if (this.abandonReasons != null && !this.abandonReasons.isEmpty()) {
            for (ItemType item : this.abandonReasons) {
                item.settype(DBTable.abandonReasons);
                dbHandler.replaceData(ItemType.DBTable.NAME, item.toContentValues());
            }
        }


        if (this.surfaceTypes != null && !this.surfaceTypes.isEmpty()) {
            for (ItemType item : this.surfaceTypes) {
                item.settype(DBTable.surfaceTypes);
                dbHandler.replaceData(ItemType.DBTable.NAME, item.toContentValues());
            }
        }


        if (this.dfeWorkItems != null && !this.dfeWorkItems.isEmpty()) {
            for (WorkItem item : this.dfeWorkItems) {
                item.settype(DBTable.dfeWorkItems);
                dbHandler.replaceData(WorkItem.DBTable.NAME, item.toContentValues());
            }
        }


        if (this.stayTypes != null && !this.stayTypes.isEmpty()) {
            for (ItemType item : this.stayTypes) {
                item.settype(DBTable.stayTypes);
                dbHandler.replaceData(ItemType.DBTable.NAME, item.toContentValues());
            }
        }

        dbHandler.clearTable(KitBagDocument.DBTable.NAME);
        if (this.kitbagFolders != null && !this.kitbagFolders.isEmpty()) {

            for (KitBagDocument item : this.kitbagFolders) {
                item.toContentValues();
            }
        }


        if (this.materialTypes != null && !this.materialTypes.isEmpty()) {
            for (ItemType item : this.materialTypes) {
                item.settype(DBTable.materialTypes);
                dbHandler.replaceData(ItemType.DBTable.NAME, item.toContentValues());
            }
        }

        if (this.jobCategories != null && !this.jobCategories.isEmpty()) {
            for (ItemType item : this.jobCategories) {
                item.settype(DBTable.jobCategories);
                dbHandler.replaceData(ItemType.DBTable.NAME, item.toContentValues());
            }
        }

        if (this.lowestWireTypes != null && !this.lowestWireTypes.isEmpty()) {
            for (ItemType item : this.lowestWireTypes) {
                item.settype(DBTable.lowestWireTypes);
                dbHandler.replaceData(ItemType.DBTable.NAME, item.toContentValues());
            }
        }

        if (this.anchorTypes != null && !this.anchorTypes.isEmpty()) {
            for (ItemType item : this.anchorTypes) {
                item.settype(DBTable.anchorTypes);
                dbHandler.replaceData(ItemType.DBTable.NAME, item.toContentValues());
            }
        }

        if (this.ugCableTypes != null && !this.ugCableTypes.isEmpty()) {
            for (ItemType item : this.ugCableTypes) {
                item.settype(DBTable.ugCableTypes);
                dbHandler.replaceData(ItemType.DBTable.NAME, item.toContentValues());
            }
        }

        if (this.trafficManagementTypes != null && !this.trafficManagementTypes.isEmpty()) {
            for (ItemType item : this.trafficManagementTypes) {
                item.settype(DBTable.trafficManagementTypes);
                dbHandler.replaceData(ItemType.DBTable.NAME, item.toContentValues());
            }
        }

        if (this.poleTypes != null && !this.poleTypes.isEmpty()) {
            for (ItemType item : this.poleTypes) {
                item.settype(DBTable.poleTypes);
                dbHandler.replaceData(ItemType.DBTable.NAME, item.toContentValues());
            }
        }

        if (this.blockTerminalTypes != null && !this.blockTerminalTypes.isEmpty()) {
            for (ItemType item : this.blockTerminalTypes) {
                item.settype(DBTable.blockTerminalTypes);
                dbHandler.replaceData(ItemType.DBTable.NAME, item.toContentValues());
            }
        }

        if (this.jointClosureTypes != null && !this.jointClosureTypes.isEmpty()) {
            for (ItemType item : this.jointClosureTypes) {
                item.settype(DBTable.jointClosureTypes);
                dbHandler.replaceData(ItemType.DBTable.NAME, item.toContentValues());
            }
        }

        if (this.dacTypes != null && !this.dacTypes.isEmpty()) {
            for (ItemType item : this.dacTypes) {
                item.settype(DBTable.dacTypes);
                dbHandler.replaceData(ItemType.DBTable.NAME, item.toContentValues());
            }
        }

        if (this.weatherConditions != null && !this.weatherConditions.isEmpty()) {
            for (ItemType item : this.weatherConditions) {
                item.settype(DBTable.weatherConditions);
                dbHandler.replaceData(ItemType.DBTable.NAME, item.toContentValues());
            }
        }

        if (this.pedestrianManagementTypes != null && !this.pedestrianManagementTypes.isEmpty()) {
            for (ItemType item : this.pedestrianManagementTypes) {
                item.settype(DBTable.pedestrianManagementTypes);
                dbHandler.replaceData(ItemType.DBTable.NAME, item.toContentValues());
            }
        }

        if (this.newReplacedRecycledTypes != null && !this.newReplacedRecycledTypes.isEmpty()) {
            for (ItemType item : this.newReplacedRecycledTypes) {
                item.settype(DBTable.newReplacedRecycledTypes);
                dbHandler.replaceData(ItemType.DBTable.NAME, item.toContentValues());
            }
        }

        if (this.riskAssessmentRiskElementTypes != null && !this.riskAssessmentRiskElementTypes.isEmpty()) {
            for (RiskElementType item : this.riskAssessmentRiskElementTypes) {
                item.setType(DBTable.riskAssessmentRiskElementTypes);
                dbHandler.replaceData(RiskElementType.DBTable.NAME, item.toContentValues());
            }
        }

        return cv;
    }

    public static class DBTable {
        public static final String NAME = "DatasetResponse";
        public static final String aerialCables = "aerialCables";
        public static final String photoTypes = "photoTypes";
        public static final String bookOperatives = "bookOperatives";
        public static final String workItems = "workItems";
        public static final String dcrReasonCodes = "dcrReasonCodes";
        public static final String roadTypes = "roadTypes";
        public static final String engCompOutcomes = "engCompOutcomes";
        public static final String reinstatementTypes = "reinstatementTypes";
        public static final String speedLimits = "speedLimits";
        public static final String abandonReasons = "abandonReasons";
        public static final String surfaceTypes = "surfaceTypes";
        public static final String dfeWorkItems = "dfeWorkItems";
        public static final String stayTypes = "stayTypes";
        public static final String materialTypes = "materialTypes";
        public static final String jobCategories = "jobCategories";
        public static final String lowestWireTypes = "lowestWireTypes";
        public static final String anchorTypes = "anchorTypes";
        public static final String ugCableTypes = "ugCableTypes";
        public static final String trafficManagementTypes = "trafficManagementTypes";
        public static final String poleTypes = "poleTypes";
        public static final String blockTerminalTypes = "blockTerminalTypes";
        public static final String jointClosureTypes = "jointClosureTypes";
        public static final String dacTypes = "dacTypes";
        public static final String weatherConditions = "weatherConditions";
        public static final String pedestrianManagementTypes = "pedestrianManagementTypes";
        public static final String newReplacedRecycledTypes = "newReplacedRecycledTypes";
        public static final String riskAssessmentRiskElementTypes = "riskAssessmentRiskElementTypes";

    }
}