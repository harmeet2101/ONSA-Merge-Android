package co.uk.depotnet.onsa.modals.httpresponses;

import android.os.Parcel;
import android.os.Parcelable;

public class SiteActivityTask extends BaseTask implements Parcelable {

    private String Address;
    private int SiteActivityTaskID;
    private String JobReferenceNumber;

    private String JobStatusName;
    private String Bags;
    private String DateCompleted;
    private String ScheduledEnd;

    private String AgentName;
    private String WhereaboutID;
    private String FwBoards;

    private String Postcode;
    private String WorkTypeID;
    private String CreatedByUserID;

    private String M2Base;
    private String AgentID;
    private String GangID;

    private String CreatedByName;

    private String M2Top;

    private String CompletedByName;

    private String ContractID;
    private String TonnageBase;
    private String AreaName;
    private String TaskDescription;
    private String ScheduledGangRef;


    private String CompletedByUserID;
    private String ScheduledStart;
    private String TonnageTop;
    private String Comments;
    private String AreaID;
    private String WorkTypeName;
    private String SiteActivityTypeName;
    private String TaskStatus;
    private String Chpt8;
    private String SiteActivityTypeID;
    private String ContractName;
    private String TaskGangType;
    private String Cones;
    private String NoticeEndDate;

    private String Barriers;


    protected SiteActivityTask(Parcel in) {
        super(in);
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public int getSiteActivityTaskID() {
        return SiteActivityTaskID;
    }

    public void setSiteActivityTaskID(int siteActivityTaskID) {
        SiteActivityTaskID = siteActivityTaskID;
    }

    public String getJobReferenceNumber() {
        return JobReferenceNumber;
    }

    public void setJobReferenceNumber(String jobReferenceNumber) {
        JobReferenceNumber = jobReferenceNumber;
    }

    public String getJobStatusName() {
        return JobStatusName;
    }

    public void setJobStatusName(String jobStatusName) {
        JobStatusName = jobStatusName;
    }

    public String getBags() {
        return Bags;
    }

    public void setBags(String bags) {
        Bags = bags;
    }

    public String getDateCompleted() {
        return DateCompleted;
    }

    public void setDateCompleted(String dateCompleted) {
        DateCompleted = dateCompleted;
    }

    public String getScheduledEnd() {
        return ScheduledEnd;
    }

    public void setScheduledEnd(String scheduledEnd) {
        ScheduledEnd = scheduledEnd;
    }

    public String getAgentName() {
        return AgentName;
    }

    public void setAgentName(String agentName) {
        AgentName = agentName;
    }

    public String getWhereaboutID() {
        return WhereaboutID;
    }

    public void setWhereaboutID(String whereaboutID) {
        WhereaboutID = whereaboutID;
    }

    public String getFwBoards() {
        return FwBoards;
    }

    public void setFwBoards(String fwBoards) {
        FwBoards = fwBoards;
    }

    public String getPostcode() {
        return Postcode;
    }

    public void setPostcode(String postcode) {
        Postcode = postcode;
    }

    public String getWorkTypeID() {
        return WorkTypeID;
    }

    public void setWorkTypeID(String workTypeID) {
        WorkTypeID = workTypeID;
    }

    public String getCreatedByUserID() {
        return CreatedByUserID;
    }

    public void setCreatedByUserID(String createdByUserID) {
        CreatedByUserID = createdByUserID;
    }

    public String getM2Base() {
        return M2Base;
    }

    public void setM2Base(String m2Base) {
        M2Base = m2Base;
    }

    public String getAgentID() {
        return AgentID;
    }

    public void setAgentID(String agentID) {
        AgentID = agentID;
    }

    public String getGangID() {
        return GangID;
    }

    public void setGangID(String gangID) {
        GangID = gangID;
    }

    public String getCreatedByName() {
        return CreatedByName;
    }

    public void setCreatedByName(String createdByName) {
        CreatedByName = createdByName;
    }

    public String getM2Top() {
        return M2Top;
    }

    public void setM2Top(String m2Top) {
        M2Top = m2Top;
    }

    public String getCompletedByName() {
        return CompletedByName;
    }

    public void setCompletedByName(String completedByName) {
        CompletedByName = completedByName;
    }

    public String getContractID() {
        return ContractID;
    }

    public void setContractID(String contractID) {
        ContractID = contractID;
    }

    public String getTonnageBase() {
        return TonnageBase;
    }

    public void setTonnageBase(String tonnageBase) {
        TonnageBase = tonnageBase;
    }

    public String getAreaName() {
        return AreaName;
    }

    public void setAreaName(String areaName) {
        AreaName = areaName;
    }

    public String getTaskDescription() {
        return TaskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        TaskDescription = taskDescription;
    }

    public String getScheduledGangRef() {
        return ScheduledGangRef;
    }

    public void setScheduledGangRef(String scheduledGangRef) {
        ScheduledGangRef = scheduledGangRef;
    }

    public String getCompletedByUserID() {
        return CompletedByUserID;
    }

    public void setCompletedByUserID(String completedByUserID) {
        CompletedByUserID = completedByUserID;
    }

    public String getScheduledStart() {
        return ScheduledStart;
    }

    public void setScheduledStart(String scheduledStart) {
        ScheduledStart = scheduledStart;
    }

    public String getTonnageTop() {
        return TonnageTop;
    }

    public void setTonnageTop(String tonnageTop) {
        TonnageTop = tonnageTop;
    }

    public String getComments() {
        return Comments;
    }

    public void setComments(String comments) {
        Comments = comments;
    }

    public String getAreaID() {
        return AreaID;
    }

    public void setAreaID(String areaID) {
        AreaID = areaID;
    }

    public String getWorkTypeName() {
        return WorkTypeName;
    }

    public void setWorkTypeName(String workTypeName) {
        WorkTypeName = workTypeName;
    }

    public String getSiteActivityTypeName() {
        return SiteActivityTypeName;
    }

    public void setSiteActivityTypeName(String siteActivityTypeName) {
        SiteActivityTypeName = siteActivityTypeName;
    }

    public String getTaskStatus() {
        return TaskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        TaskStatus = taskStatus;
    }

    public String getChpt8() {
        return Chpt8;
    }

    public void setChpt8(String chpt8) {
        Chpt8 = chpt8;
    }

    public String getSiteActivityTypeID() {
        return SiteActivityTypeID;
    }

    public void setSiteActivityTypeID(String siteActivityTypeID) {
        SiteActivityTypeID = siteActivityTypeID;
    }

    public String getContractName() {
        return ContractName;
    }

    public void setContractName(String contractName) {
        ContractName = contractName;
    }

    public String getTaskGangType() {
        return TaskGangType;
    }

    public void setTaskGangType(String taskGangType) {
        TaskGangType = taskGangType;
    }

    public String getCones() {
        return Cones;
    }

    public void setCones(String cones) {
        Cones = cones;
    }

    public String getNoticeEndDate() {
        return NoticeEndDate;
    }

    public void setNoticeEndDate(String noticeEndDate) {
        NoticeEndDate = noticeEndDate;
    }

    public String getBarriers() {
        return Barriers;
    }

    public void setBarriers(String barriers) {
        Barriers = barriers;
    }
}
			
			