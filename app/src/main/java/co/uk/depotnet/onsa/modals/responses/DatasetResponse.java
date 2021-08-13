package co.uk.depotnet.onsa.modals.responses;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.modals.ItemType;
import co.uk.depotnet.onsa.modals.KitBagDocument;
import co.uk.depotnet.onsa.modals.RiskElementType;
import co.uk.depotnet.onsa.modals.SubJobItem;
import co.uk.depotnet.onsa.modals.WorkItem;

public class DatasetResponse implements Parcelable {
    private ArrayList<ItemType> aerialCables;
    private ArrayList<ItemType> photoTypes;
    private ArrayList<ItemType> bookOperatives;
//    private ArrayList<WorkItem> workItems;
//    private ArrayList<ItemType> dcrReasonCodes;
    private ArrayList<ItemType> roadTypes;
    private ArrayList<ItemType> engCompOutcomes;
    private ArrayList<ItemType> reinstatementTypes;
    private ArrayList<ItemType> speedLimits;
    private ArrayList<ItemType> abandonReasons;
    private ArrayList<ItemType> surfaceTypes;
//    private ArrayList<WorkItem> dfeWorkItems;
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
    private ArrayList<ItemType> subJobItems;


    protected DatasetResponse(Parcel in) {
        aerialCables = in.createTypedArrayList(ItemType.CREATOR);
        photoTypes = in.createTypedArrayList(ItemType.CREATOR);
        bookOperatives = in.createTypedArrayList(ItemType.CREATOR);
//        workItems = in.createTypedArrayList(WorkItem.CREATOR);
//        dcrReasonCodes = in.createTypedArrayList(ItemType.CREATOR);
        roadTypes = in.createTypedArrayList(ItemType.CREATOR);
        engCompOutcomes = in.createTypedArrayList(ItemType.CREATOR);
        reinstatementTypes = in.createTypedArrayList(ItemType.CREATOR);
        speedLimits = in.createTypedArrayList(ItemType.CREATOR);
        abandonReasons = in.createTypedArrayList(ItemType.CREATOR);
        surfaceTypes = in.createTypedArrayList(ItemType.CREATOR);
//        dfeWorkItems = in.createTypedArrayList(WorkItem.CREATOR);
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
        subJobItems = in.createTypedArrayList(ItemType.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(aerialCables);
        dest.writeTypedList(photoTypes);
        dest.writeTypedList(bookOperatives);
//        dest.writeTypedList(workItems);
//        dest.writeTypedList(dcrReasonCodes);
        dest.writeTypedList(roadTypes);
        dest.writeTypedList(engCompOutcomes);
        dest.writeTypedList(reinstatementTypes);
        dest.writeTypedList(speedLimits);
        dest.writeTypedList(abandonReasons);
        dest.writeTypedList(surfaceTypes);
//        dest.writeTypedList(dfeWorkItems);
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
        dest.writeTypedList(subJobItems);
    }

    @Override
    public int describeContents() {
        return 0;
    }

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

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();

        DBHandler dbHandler = DBHandler.getInstance();


        if (this.aerialCables != null && !this.aerialCables.isEmpty()) {
            dbHandler.replaceItemTypes(aerialCables, DBTable.aerialCables);
        }


        if (this.photoTypes != null && !this.photoTypes.isEmpty()) {
            dbHandler.replaceItemTypes(photoTypes, DBTable.photoTypes);
        }

        if (this.bookOperatives != null && !this.bookOperatives.isEmpty()) {
            dbHandler.replaceItemTypes(bookOperatives, DBTable.bookOperatives);
        }


//        if (this.workItems != null && !this.workItems.isEmpty()) {
//            dbHandler.replaceDataDFEWorkItems(workItems, DBTable.workItems);
//        }
//        if (this.dcrReasonCodes != null && !this.dcrReasonCodes.isEmpty()) {
//            dbHandler.replaceItemTypes(dcrReasonCodes, DBTable.dcrReasonCodes);
//        }

        if (this.roadTypes != null && !this.roadTypes.isEmpty()) {
            dbHandler.replaceItemTypes(roadTypes, DBTable.roadTypes);
        }

        if (this.engCompOutcomes != null && !this.engCompOutcomes.isEmpty()) {
            dbHandler.replaceItemTypes(engCompOutcomes, DBTable.engCompOutcomes);
        }


        if (this.reinstatementTypes != null && !this.reinstatementTypes.isEmpty()) {
            dbHandler.replaceItemTypes(reinstatementTypes, DBTable.reinstatementTypes);
        }


        if (this.speedLimits != null && !this.speedLimits.isEmpty()) {
            dbHandler.replaceItemTypes(speedLimits, DBTable.speedLimits);
        }


        if (this.abandonReasons != null && !this.abandonReasons.isEmpty()) {
            dbHandler.replaceItemTypes(abandonReasons, DBTable.abandonReasons);
        }


        if (this.surfaceTypes != null && !this.surfaceTypes.isEmpty()) {
            dbHandler.replaceItemTypes(surfaceTypes, DBTable.surfaceTypes);
        }


//        if (this.dfeWorkItems != null && !this.dfeWorkItems.isEmpty()) {
//
//            dbHandler.replaceDataDFEWorkItems(dfeWorkItems, DatasetResponse.DBTable.dfeWorkItems);
//
//        }


        if (this.stayTypes != null && !this.stayTypes.isEmpty()) {
            dbHandler.replaceItemTypes(stayTypes, DBTable.stayTypes);
        }

        dbHandler.clearTable(KitBagDocument.DBTable.NAME);
        if (this.kitbagFolders != null && !this.kitbagFolders.isEmpty()) {
            for (KitBagDocument item : this.kitbagFolders) {
                item.toContentValues();
            }
        }


        if (this.materialTypes != null && !this.materialTypes.isEmpty()) {
            dbHandler.replaceItemTypes(materialTypes, DBTable.materialTypes);
        }

        if (this.jobCategories != null && !this.jobCategories.isEmpty()) {
            dbHandler.replaceItemTypes(jobCategories, DBTable.jobCategories);
        }

        if (this.lowestWireTypes != null && !this.lowestWireTypes.isEmpty()) {
            dbHandler.replaceItemTypes(lowestWireTypes, DBTable.lowestWireTypes);
        }

        if (this.anchorTypes != null && !this.anchorTypes.isEmpty()) {
            dbHandler.replaceItemTypes(anchorTypes, DBTable.anchorTypes);
        }

        if (this.ugCableTypes != null && !this.ugCableTypes.isEmpty()) {
            dbHandler.replaceItemTypes(ugCableTypes, DBTable.ugCableTypes);
        }

        if (this.trafficManagementTypes != null && !this.trafficManagementTypes.isEmpty()) {
            dbHandler.replaceItemTypes(trafficManagementTypes, DBTable.trafficManagementTypes);
        }

        if (this.poleTypes != null && !this.poleTypes.isEmpty()) {
            dbHandler.replaceItemTypes(poleTypes, DBTable.poleTypes);
        }

        if (this.blockTerminalTypes != null && !this.blockTerminalTypes.isEmpty()) {
            dbHandler.replaceItemTypes(blockTerminalTypes, DBTable.blockTerminalTypes);
        }

        if (this.jointClosureTypes != null && !this.jointClosureTypes.isEmpty()) {
            dbHandler.replaceItemTypes(jointClosureTypes, DBTable.jointClosureTypes);
        }

        if (this.dacTypes != null && !this.dacTypes.isEmpty()) {
            dbHandler.replaceItemTypes(dacTypes, DBTable.dacTypes);
        }

        if (this.weatherConditions != null && !this.weatherConditions.isEmpty()) {
            dbHandler.replaceItemTypes(weatherConditions, DBTable.weatherConditions);
        }

        if (this.pedestrianManagementTypes != null && !this.pedestrianManagementTypes.isEmpty()) {
            dbHandler.replaceItemTypes(pedestrianManagementTypes, DBTable.pedestrianManagementTypes);
        }

        if (this.newReplacedRecycledTypes != null && !this.newReplacedRecycledTypes.isEmpty()) {
            dbHandler.replaceItemTypes(newReplacedRecycledTypes, DBTable.newReplacedRecycledTypes);
        }

        if (this.riskAssessmentRiskElementTypes != null && !this.riskAssessmentRiskElementTypes.isEmpty()) {
            for (RiskElementType item : this.riskAssessmentRiskElementTypes) {
                item.setType(DBTable.riskAssessmentRiskElementTypes);
                dbHandler.replaceData(RiskElementType.DBTable.NAME, item.toContentValues());
            }
        }

        if (this.subJobItems != null && !this.subJobItems.isEmpty()) {
            dbHandler.replaceItemTypes(subJobItems, DBTable.subJobItems);
        }
        return cv;
    }

    public static class DBTable {
        public static final String NAME = "DatasetResponse";
        public static final String aerialCables = "aerialCables";
        public static final String photoTypes = "photoTypes";
        public static final String bookOperatives = "bookOperatives";
        public static final String workItems = "workItems";
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
        public static final String subJobItems = "SubJobItems";

    }
}