package co.uk.depotnet.onsa.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.os.Parcel;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import co.uk.depotnet.onsa.listeners.DropDownItem;
import co.uk.depotnet.onsa.modals.A75Groups;
import co.uk.depotnet.onsa.modals.DCRReasons;
import co.uk.depotnet.onsa.modals.Document;
import co.uk.depotnet.onsa.modals.Feature;
import co.uk.depotnet.onsa.modals.ItemType;
import co.uk.depotnet.onsa.modals.Job;
import co.uk.depotnet.onsa.modals.JobModuleStatus;
import co.uk.depotnet.onsa.modals.JobWorkItem;
import co.uk.depotnet.onsa.modals.KitBagDocument;
import co.uk.depotnet.onsa.modals.MeasureItems;
import co.uk.depotnet.onsa.modals.MenSplit;
import co.uk.depotnet.onsa.modals.Note;
import co.uk.depotnet.onsa.modals.Notice;
import co.uk.depotnet.onsa.modals.RecordReturnReason;
import co.uk.depotnet.onsa.modals.RiskElementType;
import co.uk.depotnet.onsa.modals.User;
import co.uk.depotnet.onsa.modals.WorkItem;
import co.uk.depotnet.onsa.modals.actions.Action;
import co.uk.depotnet.onsa.modals.briefings.BriefingsData;
import co.uk.depotnet.onsa.modals.briefings.BriefingsDocModal;
import co.uk.depotnet.onsa.modals.briefings.BriefingsRecipient;
import co.uk.depotnet.onsa.modals.briefings.IssuedModel;
import co.uk.depotnet.onsa.modals.briefings.ReceivedModel;
import co.uk.depotnet.onsa.modals.forms.Answer;
import co.uk.depotnet.onsa.modals.forms.Submission;
import co.uk.depotnet.onsa.modals.hseq.InspectionTemplate;
import co.uk.depotnet.onsa.modals.hseq.InspectorTemplate;
import co.uk.depotnet.onsa.modals.hseq.OperativeTemplate;
import co.uk.depotnet.onsa.modals.hseq.PhotoComments;
import co.uk.depotnet.onsa.modals.hseq.PhotoTags;
import co.uk.depotnet.onsa.modals.httpresponses.BaseTask;
import co.uk.depotnet.onsa.modals.incident.IncidentCategory;
import co.uk.depotnet.onsa.modals.incident.IncidentSeverity;
import co.uk.depotnet.onsa.modals.incident.IncidentSource;
import co.uk.depotnet.onsa.modals.incident.IncidentSubCategory;
import co.uk.depotnet.onsa.modals.incident.UniqueIncident;
import co.uk.depotnet.onsa.modals.notify.NotifyModel;
import co.uk.depotnet.onsa.modals.schedule.Schedule;
import co.uk.depotnet.onsa.modals.store.MyRequest;
import co.uk.depotnet.onsa.modals.store.MyStore;
import co.uk.depotnet.onsa.modals.store.MyStoreFav;
import co.uk.depotnet.onsa.modals.store.ReceiptItems;
import co.uk.depotnet.onsa.modals.store.Receipts;
import co.uk.depotnet.onsa.modals.store.RequestItem;
import co.uk.depotnet.onsa.modals.store.StockItems;
import co.uk.depotnet.onsa.modals.timesheet.LogHourItem;
import co.uk.depotnet.onsa.modals.timesheet.LogHourTime;
import co.uk.depotnet.onsa.modals.timesheet.TimeSheet;
import co.uk.depotnet.onsa.modals.timesheet.TimeSheetHour;
import co.uk.depotnet.onsa.modals.timesheet.TimeTypeActivity;
import co.uk.depotnet.onsa.modals.timesheet.TimesheetOperative;
import co.uk.depotnet.onsa.networking.Constants;

public class DBHandler {

    private static DBHandler dbHandler;
    private SQLiteDatabase db;
    private DBHelper dbHelper;

    private DBHandler() {
    }

    public static DBHandler getInstance() {
//        Log.d("Database" , "dbHandler =  "+dbHandler);
        if (dbHandler == null) {
            synchronized (DBHandler.class) {
                if (dbHandler == null) {
                    dbHandler = new DBHandler();
                }
            }
        }
//        Log.d("Database" , "dbHandler.db =  "+dbHandler.db);
        if (dbHandler.db == null) {
            dbHandler.openDatabase();
        }
        return dbHandler;
    }

    public static DBHandler getInstance(Context context) {
//        Log.d("Database" , "dbHandler =  "+dbHandler);
        if (dbHandler == null) {
            synchronized (DBHandler.class) {
                if (dbHandler == null) {
                    dbHandler = new DBHandler();
                }
            }
        }
//        Log.d("Database" , "dbHandler.db =  "+dbHandler.db);
        if (dbHandler.db == null) {
            dbHandler.openDatabase(context);
        }
        return dbHandler;
    }

    public void init(Context context) {
        dbHelper = DBHelper.init(context);
        openDatabase();
    }

    private void openDatabase() {
        Log.d("Database" , "openDatabase dbHelper =  "+dbHelper);
        if (dbHelper == null) {
            return;
        }
        try {
            dbHelper.openDatabase();
            Log.d("Database" , "openDatabase dbHelper db =  "+db);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openDatabase(Context context) {
        Log.d("Database" , "openDatabase dbHelper =  "+dbHelper);
        if (dbHelper == null) {
            init(context);
        }
        try {
            dbHelper.openDatabase();
            Log.d("Database" , "openDatabase dbHelper db =  "+db);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long insertData(String tableName, ContentValues values) {
        return db.insert(tableName, null, values);
    }

    public long replaceData(String tableName, ContentValues values) {
        return db.replace(tableName, null, values);
    }

    public void replaceDataDFEWorkItems(ArrayList<WorkItem> workItems , String type) {
        db.beginTransaction();
        for(int i = 0 ; i < workItems.size() ; i++) {
            WorkItem item = workItems.get(i);
            item.settype(type);
            db.replace(WorkItem.DBTable.NAME, null, item.toContentValues());
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void replaceItemTypes(ArrayList<ItemType> itemTypes , String type) {
        db.beginTransaction();
        for(int i = 0 ; i < itemTypes.size() ; i++) {
            ItemType item = itemTypes.get(i);
            item.settype(type);
            db.replace(ItemType.DBTable.NAME, null, item.toContentValues());
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }


    public User getUser() {
        User user = null;
        String query = "SELECT * FROM " + User.DBTable.NAME;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            user = new User(cursor);
        }

        if (cursor != null) {
            cursor.close();
        }
        return user;
    }

    public List<Job> getJobs() {
        ArrayList<Job> jobs = new ArrayList<>();
        String query = "SELECT * FROM " + Job.DBTable.NAME;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Job job = new Job(cursor);
                jobs.add(job);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return jobs;
    }

    public Job getJob(String jobId) {
        String whereClause = Job.DBTable.jobId + " = ? ";
        String[] whereArgs = new String[]{jobId};

        Cursor cursor = db.query(Job.DBTable.NAME, null, whereClause,
                whereArgs, null, null, null);
        if (cursor.moveToFirst()) {
            return new Job(cursor);
        }

        cursor.close();
        return null;
    }

    public Job getJobByEstimate(String estimateNo) {
        String whereClause = Job.DBTable.jobNumber + " = ? ";
        String[] whereArgs = new String[]{estimateNo};

        Cursor cursor = db.query(Job.DBTable.NAME, null, whereClause,
                whereArgs, null, null, null);
        if (cursor.moveToFirst()) {
            return new Job(cursor);
        }

        cursor.close();
        return null;
    }


    public List<KitBagDocument> getKitBagDoc(int parentId) {
        ArrayList<KitBagDocument> kitBagDocuments = new ArrayList<>();

        String whereClause = KitBagDocument.DBTable.parentId + " = ? ";
        String[] whereArgs = new String[]{String.valueOf(parentId)};

        Cursor cursor = db.query(KitBagDocument.DBTable.NAME, null, whereClause,
                whereArgs, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                KitBagDocument kitBagDocument = new KitBagDocument(cursor);
                kitBagDocuments.add(kitBagDocument);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return kitBagDocuments;
    }

    public void resetDatabase() {
        try {
            String[] tables = {
                    User.DBTable.NAME,
                    Job.DBTable.NAME,
                    JobWorkItem.DBTable.NAME,
                    Note.DBTable.NAME,
                    Notice.DBTable.NAME,
                    Document.DBTable.NAME,
                    KitBagDocument.DBTable.NAME,
                    JobModuleStatus.DBTable.NAME,
                    WorkItem.DBTable.NAME,
                    ItemType.DBTable.NAME,
                    Document.DBTable.NAME,
                    RiskElementType.DBTable.NAME,
                    MyStore.DBTable.NAME,
                    OperativeTemplate.DBTable.NAME,
                    InspectionTemplate.DBTable.NAME,
                    InspectorTemplate.DBTable.NAME,
                    PhotoTags.DBTable.NAME,
                    NotifyModel.DBTable.NAME,
                    ReceivedModel.DBTable.NAME,
                    IssuedModel.DBTable.NAME,
                    BriefingsDocModal.DBTable.NAME,
                    BriefingsRecipient.DBTable.Name,
                    Action.DBTable.NAME,
                    IncidentCategory.DBTable.NAME,
                    IncidentSeverity.DBTable.NAME,
                    IncidentSource.DBTable.NAME,
                    IncidentSubCategory.DBTable.NAME,
                    UniqueIncident.DBTable.NAME,
                    Schedule.DBTable.NAME,
                    PhotoComments.DBTable.NAME,
                    TimeSheet.DBTable.NAME,
                    TimeSheetHour.DBTable.NAME,
                    TimeTypeActivity.DBTable.NAME,
                    Submission.DBTable.NAME,
                    Answer.DBTable.NAME,
                    Feature.DBTable.NAME

            };

            db.beginTransaction();

            for (String table : tables) {
                db.delete(table, null, null);
            }

            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void resetJobs() {
        try {
            String[] tables = {
                    Job.DBTable.NAME,
                    JobWorkItem.DBTable.NAME,
                    Note.DBTable.NAME,
                    Notice.DBTable.NAME,
                    Document.DBTable.NAME,
                    A75Groups.DBTable.NAME
            };

            db.beginTransaction();

            for (String table : tables) {
                db.delete(table, null, null);
            }

            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resetReceipts() {
        try {
            String[] tables = {
                    Receipts.DBTable.NAME,
                    ReceiptItems.DBTable.NAME
            };

            db.beginTransaction();

            for (String table : tables) {
                db.delete(table, null, null);
            }

            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resetMyRequest() {
        try {
            String[] tables = {
                    MyRequest.DBTable.NAME,
                    RequestItem.DBTable.NAME
            };

            db.beginTransaction();

            for (String table : tables) {
                db.delete(table, null, null);
            }

            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearTable(String tableName) {
        db.execSQL("delete from " + tableName);
    }


    @Nullable
    public Answer getAnswer(long submissionID, String uploadID, int repeatID, String picTitle) {
        Answer ans = null;

        String whereClause = Answer.DBTable.submissionID + " = ? AND " + Answer.DBTable.uploadID + " = ? AND " + Answer.DBTable.repeatCounter + " = ? AND " + Answer.DBTable.displayAnswer + " = ?";
        String[] whereArgs = new String[]{String.valueOf(submissionID), uploadID, String.valueOf(repeatID), picTitle};

        Cursor cursor = db.query(Answer.DBTable.NAME, null, whereClause, whereArgs, null, null, Answer.DBTable.id + " ASC", "1");
        if (cursor != null && cursor.moveToFirst()) {
            ans = new Answer(cursor);
        }

        if (cursor != null) {
            cursor.close();
        }
        return ans;
    }

    @Nullable
    public Answer getAnswer(long submissionID, String uploadID, String repeatID, int repeatCount) {
        Answer out = null;
        String whereClause;
        String[] whereArgs;

        if (repeatID != null && !repeatID.isEmpty()) {
            whereClause = Answer.DBTable.submissionID + " = ? AND " + Answer.DBTable.uploadID + " = ? AND " + Answer.DBTable.repeatCounter + " = ? AND " + Answer.DBTable.repeatID + " = ?";
            whereArgs = new String[]{String.valueOf(submissionID), uploadID, String.valueOf(repeatCount), repeatID};
        } else {
            whereClause = Answer.DBTable.submissionID + " = ? AND " + Answer.DBTable.uploadID + " = ? AND " + Answer.DBTable.repeatCounter + " = ?";
            whereArgs = new String[]{String.valueOf(submissionID), uploadID, String.valueOf(repeatCount)};
        }

        Cursor cursor = db.query(Answer.DBTable.NAME, null,
                whereClause, whereArgs, null, null,
                Answer.DBTable.id + " ASC", "1");
        if (cursor.moveToFirst()) {
            out = new Answer(cursor);
        }

        cursor.close();
        return out;
    }

    @Nullable
    public Answer getAnswer(long submissionID, String uploadID, String repeatID, int repeatCount , int arrayRepeatCounter) {
        Answer out = null;
        String whereClause;
        String[] whereArgs;

        if(!TextUtils.isEmpty(repeatID)){
            whereClause = Answer.DBTable.submissionID + " = ? AND " +
                    Answer.DBTable.uploadID + " = ? AND " +
                    Answer.DBTable.repeatID + " = ? AND " +
                    Answer.DBTable.repeatCounter + " = ? AND " +
                    Answer.DBTable.arrayRepeatCounter + " = ?";
            whereArgs = new String[]{String.valueOf(submissionID), uploadID, repeatID,
                    String.valueOf(repeatCount), String.valueOf(arrayRepeatCounter)};
        }else{
            whereClause = Answer.DBTable.submissionID + " = ? AND " +
                    Answer.DBTable.uploadID + " = ? AND " +
                    Answer.DBTable.repeatCounter + " = ? " +
                    Answer.DBTable.arrayRepeatCounter + " = ?";
            whereArgs = new String[]{String.valueOf(submissionID), uploadID,
                    String.valueOf(repeatCount), String.valueOf(arrayRepeatCounter)};
        }

        Cursor cursor = db.query(Answer.DBTable.NAME, null,
                whereClause, whereArgs, null, null,
                Answer.DBTable.id + " ASC", "1");
        if (cursor.moveToFirst()) {
            out = new Answer(cursor);
        }

        cursor.close();
        return out;
    }

    public boolean removeAnswer(Answer answer) {
        String whereClause = Answer.DBTable.id + " = ?";
        String[] whereArgs = {String.valueOf(answer.getID())};
        int affectedRows = db.delete(Answer.DBTable.NAME, whereClause, whereArgs);
        return affectedRows > 0;
    }

    public boolean removeMyRequests(MyRequest requestItem) {
        String whereClause = MyRequest.DBTable.requestId + " = ?";
        String[] whereArgs = {String.valueOf(requestItem.getrequestId())};
        int affectedRows = db.delete(MyRequest.DBTable.NAME, whereClause, whereArgs);

        String whereClause1 = RequestItem.DBTable.requestId + " = ?";
        String[] whereArgs1 = {String.valueOf(requestItem.getrequestId())};
        int affectedRows1 = db.delete(RequestItem.DBTable.NAME, whereClause1, whereArgs1);
        return affectedRows > 0;
    }


    public ArrayList<Answer> getAnswers(long submissionId) {
        String whereClause = Answer.DBTable.submissionID + " = ? ";
        String[] whereArgs = new String[]{String.valueOf(submissionId)};


        Cursor cursor = db.query(Answer.DBTable.NAME, null, whereClause, whereArgs, null, null, Answer.DBTable.repeatCounter + " ASC");
        ArrayList<Answer> answers = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                answers.add(new Answer(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return answers;
    }

    public ArrayList<Answer> getRepeatedAnswers(long submissionId,
                                                String uploadID, String repeatID) {

        String whereClause;
        String[] whereArgs;

        if (TextUtils.isEmpty(repeatID)) {
            whereClause = Answer.DBTable.submissionID + " = ? AND " +
                    Answer.DBTable.uploadID + " = ?";
            whereArgs = new String[]{String.valueOf(submissionId),
                    uploadID};
        } else {
            whereClause = Answer.DBTable.submissionID + " = ? AND " +
                    Answer.DBTable.uploadID + " = ? AND " +
                    Answer.DBTable.repeatID + " = ?";
            whereArgs = new String[]{String.valueOf(submissionId),
                    uploadID, repeatID};
        }


        Cursor cursor = db.query(Answer.DBTable.NAME, null, whereClause, whereArgs, null, null, Answer.DBTable.repeatCounter + " ASC");
        ArrayList<Answer> answers = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                answers.add(new Answer(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return answers;
    }

    public ArrayList<Answer> getRepeatedMultiArrayAnswers(long submissionId,
                                                String uploadID, String repeatID , int repeatCounter) {

        String whereClause;
        String[] whereArgs;

        if (TextUtils.isEmpty(repeatID)) {
            whereClause = Answer.DBTable.submissionID + " = ? AND " +
                    Answer.DBTable.uploadID + " = ?";
            whereArgs = new String[]{String.valueOf(submissionId),
                    uploadID};
        } else {
            whereClause = Answer.DBTable.submissionID + " = ? AND " +
                    Answer.DBTable.uploadID + " = ? AND " +
                    Answer.DBTable.repeatID + " = ? AND " +
                    Answer.DBTable.repeatCounter + " = ?";
            whereArgs = new String[]{String.valueOf(submissionId),
                    uploadID, repeatID, String.valueOf(repeatCounter)};
        }


        Cursor cursor = db.query(Answer.DBTable.NAME, null, whereClause, whereArgs, null, null, Answer.DBTable.repeatCounter + " ASC");
        ArrayList<Answer> answers = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                answers.add(new Answer(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return answers;
    }

    public ArrayList<Answer> getPhotos(long submissionId,
                                       String photoID, String title, int repeatCount) {
        String whereClause = Answer.DBTable.submissionID + " = ? AND " +
                Answer.DBTable.uploadID + " = ? AND " +
                Answer.DBTable.displayAnswer + " like ? AND " +
                Answer.DBTable.repeatCounter + " = ?";
        String[] whereArgs = new String[]{String.valueOf(submissionId),
                photoID, title + "%", String.valueOf(repeatCount)};


        Cursor cursor = db.query(Answer.DBTable.NAME, null, whereClause, whereArgs, null, null, Answer.DBTable.repeatID + " ASC");
        ArrayList<Answer> answers = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                answers.add(new Answer(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return answers;
    }

    @Nullable
    public Answer getPhoto(long submissionID, String uploadID, int repeatID, String picTitle) {
        Answer ans = null;

        String whereClause = Answer.DBTable.submissionID + " = ? AND " + Answer.DBTable.uploadID + " = ? AND " + Answer.DBTable.repeatCounter + " = ? AND " + Answer.DBTable.displayAnswer + " = ?";
        String[] whereArgs = new String[]{String.valueOf(submissionID), uploadID, String.valueOf(repeatID), picTitle};

        Cursor cursor = db.query(Answer.DBTable.NAME, null, whereClause, whereArgs, null, null, Answer.DBTable.id + " ASC", "1");
        if (cursor != null && cursor.moveToFirst()) {
            ans = new Answer(cursor);
        }

        if (cursor != null) {
            cursor.close();
        }
        return ans;
    }


    public ArrayList<Note> getNotes(String jobId) {

        String selection = Note.DBTable.jobId + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(jobId)};

        Cursor cursor = db.query(Note.DBTable.NAME,
                null, selection, selectionArgs,
                null, null, Note.DBTable.dateTime+" DESC");
        ArrayList<Note> notes = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                notes.add(new Note(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return notes;
    }

    public ArrayList<ReceiptItems> getReceiptItems(String batchReference) {

        String selection = ReceiptItems.DBTable.batchRef + " = ?";
        String[] selectionArgs = new String[]{batchReference};

        Cursor cursor = db.query(ReceiptItems.DBTable.NAME,
                null, selection, selectionArgs,
                null, null, null);


        ArrayList<ReceiptItems> receiptItems = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                receiptItems.add(new ReceiptItems(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return receiptItems;
    }

    public ArrayList<Receipts> getReceipts() {

        String selection = null;
        String[] selectionArgs = null;

        Cursor cursor = db.query(Receipts.DBTable.NAME,
                null, selection, selectionArgs,
                null, null, null);

        ArrayList<Receipts> receipts = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                receipts.add(new Receipts(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return receipts;
    }

    public Receipts getReceipt(String batchReference) {

        String selection = Receipts.DBTable.batchRef + " = ?";
        String[] selectionArgs = new String[]{batchReference};

        Cursor cursor = db.query(Receipts.DBTable.NAME,
                null, selection, selectionArgs,
                null, null, null);

        if (cursor.moveToFirst()) {
            return new Receipts(cursor);
        }
        cursor.close();
        return null;
    }

    public ArrayList<MyRequest> getMyRequest() {

        String selection = null;
        String[] selectionArgs = null;

        Cursor cursor = db.query(MyRequest.DBTable.NAME,
                null, selection, selectionArgs,
                null, null, null);

        ArrayList<MyRequest> requests = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                requests.add(new MyRequest(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return requests;
    }

    public ArrayList<JobWorkItem> getJobWorkItem(String jobId) {

        String selection = JobWorkItem.DBTable.jobId + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(jobId)};

        Cursor cursor = db.query(JobWorkItem.DBTable.NAME,
                null, selection, selectionArgs,
                null, null,
                JobWorkItem.DBTable.itemCode + " ASC");
        ArrayList<JobWorkItem> workItems = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                workItems.add(new JobWorkItem(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return workItems;
    }

    public JobWorkItem getJobWorkItem(String jobId, String itemId) {

        String selection = JobWorkItem.DBTable.jobId + " = ? AND " + JobWorkItem.DBTable.itemId + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(jobId), itemId};
        JobWorkItem workItem = null;
        Cursor cursor = db.query(JobWorkItem.DBTable.NAME,
                null, selection, selectionArgs,
                null, null,
                JobWorkItem.DBTable.itemCode + " ASC");


        if (cursor.moveToFirst()) {
            workItem = new JobWorkItem(cursor);

        }
        cursor.close();
        return workItem;
    }

    public JobWorkItem getJobWorkItemByItemCode(String jobId, String itemCode) {

        String selection = JobWorkItem.DBTable.jobId + " = ? AND " + JobWorkItem.DBTable.itemCode + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(jobId), itemCode};
        JobWorkItem workItem = null;
        Cursor cursor = db.query(JobWorkItem.DBTable.NAME,
                null, selection, selectionArgs,
                null, null,
                JobWorkItem.DBTable.itemCode + " ASC");


        if (cursor.moveToFirst()) {
            workItem = new JobWorkItem(cursor);
        }
        cursor.close();
        return workItem;
    }

    public ArrayList<WorkItem> getWorkItem(String type, String orderBy) {

        String selection = WorkItem.DBTable.type + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(type)};

        Cursor cursor = db.query(WorkItem.DBTable.NAME,
                null, selection, selectionArgs,
                null, null, orderBy + " ASC");
        ArrayList<WorkItem> workItems = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                workItems.add(new WorkItem(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return workItems;
    }

    public ArrayList<WorkItem> getWorkItem(String type, String contractNumber, int revisionNo, String orderBy) {

        String selection = WorkItem.DBTable.type + " = ? AND " + WorkItem.DBTable.contractNumber + " = ? AND " + WorkItem.DBTable.revisionNo + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(type), contractNumber , String.valueOf(revisionNo)};

        Cursor cursor = db.query(WorkItem.DBTable.NAME,
                null, selection, selectionArgs,
                null, null, orderBy + " ASC");
        ArrayList<WorkItem> workItems = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                workItems.add(new WorkItem(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return workItems;
    }


    public ArrayList<Document> getDocument(String jobId) {

        String selection = Note.DBTable.jobId + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(jobId)};

        Cursor cursor = db.query(Document.DBTable.NAME,
                null, selection, selectionArgs,
                null, null, Document.DBTable.jobDocumentId + " ASC");
        ArrayList<Document> documents = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                documents.add(new Document(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return documents;
    }

    public ArrayList<Notice> getNotices(String jobId) {

        String selection = Notice.DBTable.jobId + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(jobId)};

        Cursor cursor = db.query(Notice.DBTable.NAME,
                null, selection, selectionArgs,
                null, null, Notice.DBTable.startDate + " DESC");
        ArrayList<Notice> notices = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                notices.add(new Notice(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return notices;
    }


    public boolean removeAnswers(Submission submission) {
        String whereClause = Submission.DBTable.id + " = ?";
        String[] whereArgs = {String.valueOf(submission.getID())};
        submission.setQueued(0);
        replaceData(Submission.DBTable.NAME, submission.toContentValues());
//        int affectedRows = db.delete(Submission.DBTable.NAME, whereClause, whereArgs);

        whereClause = Answer.DBTable.submissionID + " = ? AND " + Answer.DBTable.isPhoto + " = ?";
        whereArgs = new String[]{String.valueOf(submission.getID()), String.valueOf(0)};
        int affectedRows = db.delete(Answer.DBTable.NAME, whereClause, whereArgs);
        return affectedRows > 0;
    }

    public boolean removeMultiAnswersByUploadID(long submissionID , String uploadID) {
        String whereClause = Submission.DBTable.id + " = ?";
        String[] whereArgs = {String.valueOf(submissionID)};


        whereClause = Answer.DBTable.submissionID + " = ? AND " + Answer.DBTable.uploadID + " = ?";
        whereArgs = new String[]{String.valueOf(submissionID), uploadID};
        int affectedRows = db.delete(Answer.DBTable.NAME, whereClause, whereArgs);
        return affectedRows > 0;
    }

    public boolean setSubmissionQueued(Submission submission) {
        submission.setQueued(1);
        String whereClause = Submission.DBTable.id + " = ?";
        String[] whereArgs = {String.valueOf(submission.getID())};
        int affectedRows = db.update(Submission.DBTable.NAME, submission.toContentValues(), whereClause,
                whereArgs);
        return affectedRows > 0;
    }

    public boolean setSubmissionByFileName(Submission submission) {
        submission.setQueued(1);
        String whereClause = Submission.DBTable.id + " = ?";
        String[] whereArgs = {String.valueOf(submission.getID())};
        int affectedRows = db.update(Submission.DBTable.NAME, submission.toContentValues(), whereClause,
                whereArgs);
        return affectedRows > 0;
    }

    public boolean getJobModuleStatus(String jobId, String moduleName) {
        String selection = JobModuleStatus.DBTable.JobId + " = ? AND "
                + JobModuleStatus.DBTable.ModuleName + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(jobId), moduleName};

        Cursor cursor = db.query(JobModuleStatus.DBTable.NAME,
                null, selection, selectionArgs,
                null, null, JobModuleStatus.DBTable.JobId + " ASC");

        if (cursor == null) {
            return false;
        }

        if (!cursor.moveToFirst()) {
            cursor.close();
            return false;
        }


        JobModuleStatus jobModuleStatus = new JobModuleStatus(cursor);
        cursor.close();
        return jobModuleStatus.isStatus();
    }


    public boolean getJobModuleStatus(String jobId, String moduleName, String date) {
        String selection = JobModuleStatus.DBTable.JobId + " = ? AND "
                + JobModuleStatus.DBTable.ModuleName + " = ? AND "
                + JobModuleStatus.DBTable.selectedDate + " = ?";
        String[] selectionArgs = new String[]{jobId, moduleName , date};

        Cursor cursor = db.query(JobModuleStatus.DBTable.NAME,
                null, selection, selectionArgs,
                null, null, JobModuleStatus.DBTable.JobId + " ASC");

        if (cursor == null) {
            return false;
        }

        if (!cursor.moveToFirst()) {
            cursor.close();
            return false;
        }

        JobModuleStatus jobModuleStatus = new JobModuleStatus(cursor);
        cursor.close();
        return jobModuleStatus.isStatus();
    }

    public boolean getJobModuleStatus(String jobId, String moduleName, long submissionId) {
        String selection = JobModuleStatus.DBTable.JobId + " = ? AND "
                + JobModuleStatus.DBTable.ModuleName + " = ? AND "
                + JobModuleStatus.DBTable.submissionId + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(jobId), moduleName, String.valueOf(submissionId)};

        Cursor cursor = db.query(JobModuleStatus.DBTable.NAME,
                null, selection, selectionArgs,
                null, null,
                JobModuleStatus.DBTable.JobId + " ASC");

        if (cursor == null) {
            return false;
        }

        if (!cursor.moveToFirst()) {
            cursor.close();
            return false;
        }

        JobModuleStatus jobModuleStatus = new JobModuleStatus(cursor);
        cursor.close();
        return jobModuleStatus.isStatus();
    }

    @NonNull
    public Submission getSubmission(String submissionId) {

        Submission submission = null;
        String whereClause = Submission.DBTable.id + " = ? ";
        String[] whereArgs = new String[]{submissionId};

        Cursor cursor = db.query(Submission.DBTable.NAME, null, whereClause, whereArgs, null, null, Submission.DBTable.id + " ASC");
        if (cursor != null && cursor.moveToFirst()) {
            submission = new Submission(cursor);
        }

        if (cursor != null) {
            cursor.close();
        }
        return submission;
    }

    @NonNull
    public boolean isFeatureActive(String featureName) {

        boolean isActive = false;
        String whereClause = Feature.DBTable.featureName + " = ? ";
        String[] whereArgs = new String[]{featureName};

        Cursor cursor = db.query(Feature.DBTable.NAME, null, whereClause, whereArgs, null, null, Feature.DBTable.featureName + " ASC");
        if (cursor != null && cursor.moveToFirst()) {
            Feature feature = new Feature(cursor);
            isActive = feature.isActive();
        }

        if (cursor != null) {
            cursor.close();
        }
        return isActive;
    }


    public ArrayList<Submission> getSubmissionsByJobAndTitle(String title, String jobId) {

        ArrayList<Submission> submissions = new ArrayList<>();
        String whereClause = Submission.DBTable.title + " = ? AND " +
                Submission.DBTable.jobID + " = ?";
        String[] whereArgs = new String[]{title, jobId};


        Cursor cursor = db.query(Submission.DBTable.NAME, null,
                whereClause, whereArgs, null, null,
                Submission.DBTable.id + " DESC");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                submissions.add(new Submission(cursor));
            } while (cursor.moveToNext());

        }

        if (cursor != null) {
            cursor.close();
        }
        return submissions;
    }

    public Submission getSubmissionByJsonName(String jsonName) {

        Submission submission = null;
        String whereClause = Submission.DBTable.jsonFile + " = ? AND " + Submission.DBTable.queued + " != ?";
        String[] whereArgs = new String[]{jsonName, String.valueOf(0)};

        Cursor cursor = db.query(Submission.DBTable.NAME, null, whereClause, whereArgs, null, null, Submission.DBTable.id + " DESC");
        if (cursor != null && cursor.moveToFirst()) {
            submission = new Submission(cursor);
        }

        if (cursor != null) {
            cursor.close();
        }
        return submission;
    }


    @NonNull
    public ArrayList<Submission> getQueuedSubmissions() {

        ArrayList<Submission> submissions = new ArrayList<>();
        String whereClause = Submission.DBTable.queued + " = ? ";
        String[] whereArgs = new String[]{String.valueOf(1)};

        Cursor cursor = db.query(Submission.DBTable.NAME, null, whereClause, whereArgs, null, null, Submission.DBTable.id + " ASC");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                submissions.add(new Submission(cursor));
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        return submissions;
    }


    public void deleteItemTypes(String type) {

        String whereClause = ItemType.DBTable.type + " = ?";
        String[] whereArgs = {type};
        db.delete(ItemType.DBTable.NAME,
                whereClause, whereArgs);
    }

    public ArrayList<ItemType> getItemTypes(String type) {

        ArrayList<ItemType> itemTypes = new ArrayList<>();
        String whereClause = ItemType.DBTable.type + " = ?";
        String[] whereArgs = {type};
        Cursor cursor = db.query(ItemType.DBTable.NAME, null,
                whereClause, whereArgs, null, null,
                ItemType.DBTable.id + " ASC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                itemTypes.add(new ItemType(cursor));
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        return itemTypes;

    }

    public ItemType getItemType(String type, String value) {


        String whereClause = ItemType.DBTable.type + " = ? AND " + ItemType.DBTable.value + " = ?";
        String[] whereArgs = {type, value};
        Cursor cursor = db.query(ItemType.DBTable.NAME, null,
                whereClause, whereArgs, null, null,
                ItemType.DBTable.id + " ASC");

        if (cursor != null && cursor.moveToFirst()) {

            return new ItemType(cursor);

        }

        if (cursor != null) {
            cursor.close();
        }

        return null;

    }

    public ArrayList<RiskElementType> getRiskElementType(String type) {

        ArrayList<RiskElementType> itemTypes = new ArrayList<>();
        String whereClause = RiskElementType.DBTable.type + " = ?";
        String[] whereArgs = {type};
        Cursor cursor = db.query(RiskElementType.DBTable.NAME, null,
                whereClause, whereArgs, null, null,
                RiskElementType.DBTable.text + " ASC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                itemTypes.add(new RiskElementType(cursor));
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        return itemTypes;

    }

    public boolean removeJob(String jobId) {
        String whereClause = Job.DBTable.jobId + " = ?";
        String[] whereArgs = {String.valueOf(jobId)};
        int affectedRows = db.delete(Job.DBTable.NAME, whereClause, whereArgs);
        return affectedRows > 0;
    }

    public ArrayList<A75Groups> getA75Groups(String jobId) {
        ArrayList<A75Groups> a75Groups = new ArrayList<>();
        String selection = A75Groups.DBTable.jobId + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(jobId)};

        Cursor cursor = db.query(A75Groups.DBTable.NAME,
                null, selection, selectionArgs,
                null, null, A75Groups.DBTable.dpNo + " ASC");


        if (cursor.moveToFirst()) {
            do {
                a75Groups.add(new A75Groups(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return a75Groups;
    }
    public ArrayList<DCRReasons> getDCRReason(String jobId) {
        ArrayList<DCRReasons> dcrReasons = new ArrayList<>();
        String selection = DCRReasons.DBTable.jobId + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(jobId)};

        Cursor cursor = db.query(DCRReasons.DBTable.NAME,
                null, selection, selectionArgs,
                null, null, DCRReasons.DBTable.value + " ASC");


        if (cursor.moveToFirst()) {
            do {
                dcrReasons.add(new DCRReasons(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return dcrReasons;
    }
    public ArrayList<RecordReturnReason> getRecordReturnReasons(String jobId) {
        ArrayList<RecordReturnReason> recordReturnReasons = new ArrayList<>();
        String selection = RecordReturnReason.DBTable.jobId + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(jobId)};

        Cursor cursor = db.query(RecordReturnReason.DBTable.NAME,
                null, selection, selectionArgs,
                null, null, RecordReturnReason.DBTable.value + " ASC");


        if (cursor.moveToFirst()) {
            do {
                recordReturnReasons.add(new RecordReturnReason(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return recordReturnReasons;
    }

    public void removeRecordReturnReasons(String jobId) {
        String selection = RecordReturnReason.DBTable.jobId + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(jobId)};

        db.delete(RecordReturnReason.DBTable.NAME, selection, selectionArgs);

    }


    public void resetMyStores() {
        try {
            String[] tables = {
                    MyStore.DBTable.NAME
            };

            db.beginTransaction();

            for (String table : tables) {
                db.delete(table, null, null);
            }

            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<MyStore> getMyStores() {
        ArrayList<MyStore> myStores = new ArrayList<>();
        String selection = null;
        String[] selectionArgs = null;

        Cursor cursor = db.query(MyStore.DBTable.NAME,
                null, selection, selectionArgs,
                null, null, null);


        if (cursor.moveToFirst()) {
            do {
                myStores.add(new MyStore(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return myStores;
    }

    public MyStore getMyStores(String staStockItemId) {
        MyStore myStore = null;
        String selection = MyStore.DBTable.staStockItemId + " = ?";
        String[] selectionArgs = new String[]{staStockItemId};

        Cursor cursor = db.query(MyStore.DBTable.NAME,
                null, selection, selectionArgs,
                null, null, null);


        if (cursor.moveToFirst()) {
            myStore = new MyStore(cursor);
        }
        cursor.close();
        return myStore;
    }

    public ArrayList<MyStore> getMyStoresByStaId(String staId) {
        ArrayList<MyStore> myStores = new ArrayList<>();
        String selection = MyStore.DBTable.staId + " = ?";
        String[] selectionArgs = new String[]{staId};

        Cursor cursor = db.query(MyStore.DBTable.NAME,
                null, selection, selectionArgs,
                null, null, null);


        if (cursor.moveToFirst()) {
            do {
                myStores.add(new MyStore(cursor));
            } while (cursor.moveToNext());

        }
        cursor.close();
        return myStores;
    }


    public ArrayList<MyStore> getMyStoresByBatchRef(String batchRef) {
        ArrayList<MyStore> myStores = new ArrayList<>();
        String selection = MyStore.DBTable.batchRef + " = ?";
        String[] selectionArgs = new String[]{batchRef};

        Cursor cursor = db.query(MyStore.DBTable.NAME,
                null, selection, selectionArgs,
                null, null, null);


        if (cursor.moveToFirst()) {
            do {
                myStores.add(new MyStore(cursor));
            } while (cursor.moveToNext());

        }
        cursor.close();
        return myStores;
    }

    public ArrayList<DropDownItem> getUniqueBatchrefBy() {
        ArrayList<DropDownItem> myStores = new ArrayList<>();

        String[] columns = new String[]{MyStore.DBTable.batchRef};

        Cursor cursor = db.query(true, MyStore.DBTable.NAME, columns,
                null, null, null,
                null, null, null);


        if (cursor.moveToFirst()) {
            do {

                String tempVal = cursor.getString(0);
                if (tempVal == null || tempVal.isEmpty()) {
                    tempVal = "NA";
                }
                String value = tempVal;
                myStores.add(new DropDownItem() {
                    @Override
                    public String getDisplayItem() {
                        return value;
                    }

                    @Override
                    public String getUploadValue() {
                        return value;
                    }

                    @Override
                    public int describeContents() {
                        return 0;
                    }

                    @Override
                    public void writeToParcel(Parcel dest, int flags) {

                    }
                });
            } while (cursor.moveToNext());

        }
        cursor.close();
        return myStores;
    }


    public ArrayList<StockItems> getStockItems() {
        ArrayList<StockItems> stockItems = new ArrayList<>();
        String selection = null;
        String[] selectionArgs = null;

        Cursor cursor = db.query(StockItems.DBTable.NAME,
                null, selection, selectionArgs,
                null, null, null);


        if (cursor.moveToFirst()) {
            do {
                stockItems.add(new StockItems(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return stockItems;
    }

    public ArrayList<StockItems> getStockItemsByStaId(String staId) {
        ArrayList<StockItems> stockItems = new ArrayList<>();
        String selection = StockItems.DBTable.staId + " = ?";
        String[] selectionArgs = new String[]{staId};

        Cursor cursor = db.query(StockItems.DBTable.NAME,
                null, selection, selectionArgs,
                null, null, null);


        if (cursor.moveToFirst()) {
            do {
                stockItems.add(new StockItems(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return stockItems;
    }


    public StockItems getStockItems(String staStockItemId) {

        String selection = StockItems.DBTable.staStockItemId + " = ?";
        String[] selectionArgs = new String[]{staStockItemId};

        Cursor cursor = db.query(StockItems.DBTable.NAME,
                null, selection, selectionArgs,
                null, null, null);


        if (cursor.moveToFirst()) {

            return new StockItems(cursor);

        }
        cursor.close();
        return null;
    }

    public ArrayList<RequestItem> getRequestItems(String requestId) {

        ArrayList<RequestItem> requestItems = new ArrayList<>();
        String selection = RequestItem.DBTable.requestId + " = ?";
        String[] selectionArgs = new String[]{requestId};

        Cursor cursor = db.query(RequestItem.DBTable.NAME,
                null, selection, selectionArgs,
                null, null, null);


        if (cursor.moveToFirst()) {
            do {
                requestItems.add(new RequestItem(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return requestItems;
    }

    public boolean isMyStoreFav(String getstockItemId) {

        String[] columns = null;
        String selection = MyStoreFav.DBTable.stockItemId + " = ?";
        String[] selectionArgs = new String[]{getstockItemId};

        Cursor cursor = db.query(MyStoreFav.DBTable.NAME,
                columns, selection, selectionArgs,
                null, null, null);


        if (cursor.moveToFirst()) {
            return new MyStoreFav(cursor).isFavorite;
        }
        cursor.close();
        return false;
    }

    public boolean deleteMyStoreFav(String getstockItemId) {

        String selection = MyStoreFav.DBTable.stockItemId + " = ?";
        String[] selectionArgs = new String[]{getstockItemId};

        int affectedRows = db.delete(MyStoreFav.DBTable.NAME, selection, selectionArgs);
        return affectedRows > 0;
    }


    public ArrayList<Submission> getSubmissionsByJobId(String jobId) {
        String whereClause = Submission.DBTable.jobID + " = ?";
        String[] whereArgs = new String[]{jobId};

        Cursor cursor = db.query(Submission.DBTable.NAME, null, whereClause, whereArgs, null, null, Submission.DBTable.id + " DESC");
        ArrayList<Submission> submissions = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                submissions.add(new Submission(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return submissions;

    }

    public ArrayList<Answer> getRecentPhotos(String jobId) {

        ArrayList<Submission> submissions = getSubmissionsByJobId(jobId);
        ArrayList<Answer> answers = new ArrayList<>();
        int coount = 0;
        for (Submission s :
                submissions) {
            String whereClause = Answer.DBTable.isPhoto + " = ? AND " + Answer.DBTable.submissionID + " = ?";
            String[] whereArgs = new String[]{String.valueOf(1), String.valueOf(s.getID())};


            Cursor cursor = db.query(Answer.DBTable.NAME, null, whereClause, whereArgs, null, null, Answer.DBTable.repeatCounter + " DESC", "5");

            if (cursor.moveToFirst()) {
                do {
                    Answer answer = new Answer(cursor);
                    if (answer.getUploadID() != null && !answer.getUploadID().equalsIgnoreCase("SignatureId")) {
                        answers.add(new Answer(cursor));
                        coount++;
                        if (coount == 5) {
                            break;
                        }
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }


        return answers;
    }

    public ArrayList<MenSplit> getMenSplit() {
        ArrayList<MenSplit> menSplits = new ArrayList<>();
        String whereClause = null;
        String[] whereArgs = null;
        Cursor cursor = db.query(MenSplit.DBTable.NAME, null, whereClause, whereArgs, null, null, MenSplit.DBTable.menSplitId + " DESC");
        if (cursor.moveToFirst()) {
            do {
                MenSplit menSplit = new MenSplit(cursor);
                menSplits.add(menSplit);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return menSplits;
    }

    public ArrayList<MeasureItems> getMeasures() {
        ArrayList<MeasureItems> measureItems = new ArrayList<>();
        String whereClause = null;
        String[] whereArgs = null;
        Cursor cursor = db.query(MeasureItems.DBTable.NAME, null, whereClause, whereArgs, null, null, MeasureItems.DBTable.subcontractorRateId + " DESC");
        if (cursor.moveToFirst()) {
            do {
                MeasureItems mi =new MeasureItems(cursor);
                measureItems.add(mi);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return measureItems;
    }

    public void removeWorkItems(String jobId) {
        String whereClause = JobWorkItem.DBTable.jobId + " = ?";
        String[] whereArgs = {String.valueOf(jobId)};
        int affectedRows = db.delete(JobWorkItem.DBTable.NAME, whereClause, whereArgs);
        return;
    }

    public ArrayList<BaseTask> getTaskItems(String jobID, int taskId , int isSubJobTask) {
        ArrayList<BaseTask> tasks = new ArrayList<>();
        String whereClause = null;
        String[] whereArgs = null;

        whereClause = BaseTask.DBTable.jobId + " = ? AND " + BaseTask.DBTable.siteActivityTypeId + " = ? AND " + BaseTask.DBTable.isSubJobTask + " = ?";
        whereArgs = new String[]{jobID, String.valueOf(taskId), String.valueOf(isSubJobTask)};

        Cursor cursor = db.query(BaseTask.DBTable.NAME , null , whereClause , whereArgs , null , null , BaseTask.DBTable.siteActivityTaskId+" DESC" );
        if (cursor.moveToFirst()) {
            do {
                tasks.add(new BaseTask(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return tasks;
    }

    public ArrayList<BaseTask> getTaskItems(String jobID , int isSubJobTask) {
        ArrayList<BaseTask> tasks = new ArrayList<>();
        String whereClause = null;
        String[] whereArgs = null;

        whereClause = BaseTask.DBTable.jobId + " = ? AND "+BaseTask.DBTable.isSubJobTask +" = ?";
        whereArgs = new String[]{jobID , String.valueOf(isSubJobTask)};

        Cursor cursor = db.query(BaseTask.DBTable.NAME , null , whereClause , whereArgs , null , null , BaseTask.DBTable.siteActivityTaskId+" DESC" );
        if (cursor.moveToFirst()) {
            do {
                tasks.add(new BaseTask(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return tasks;
    }

    public String getScheduledTasks(String jobID){

        String whereClause = null;
        String[] whereArgs = null;
        String[] columns = new String[]{BaseTask.DBTable.siteActivityTypeId};

        whereClause = BaseTask.DBTable.jobId + " = ?";
        whereArgs = new String[]{jobID};

        String value = "Task Scheduled: ";

        Cursor cursor = db.query(true, BaseTask.DBTable.NAME , columns , whereClause , whereArgs , null,null , BaseTask.DBTable.siteActivityTypeId+" ASC", "100");
        if (cursor.moveToFirst()) {
            do {
                int type = cursor.getInt(cursor.getColumnIndex(BaseTask.DBTable.siteActivityTypeId));
                if(type == Constants.TYPE_ID_BACKFILL){
                    value += "Backfill, ";
                }else if(type == Constants.TYPE_ID_MUCKAWAY){
                    value += "MuckAway, ";
                }else if(type == Constants.TYPE_ID_SERVICE_MATERAL){
                    value += "Service Material Drop Off, ";
                }else if(type == Constants.TYPE_ID_SITE_CLEAR){
                    value += "Site Clear, ";
                }else if(type == Constants.TYPE_ID_REINSTATEMENT){
                    value += "Reinstatement, ";
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return value;
    }

    public boolean isScheduledTasksOtherThenSiteClear(String jobID){

        String whereClause = null;
        String[] whereArgs = null;
        String[] columns = new String[]{BaseTask.DBTable.siteActivityTypeId};

        whereClause = BaseTask.DBTable.jobId + " = ?";
        whereArgs = new String[]{jobID};
        boolean isSiteClearAvail = false;

        Cursor cursor = db.query(true, BaseTask.DBTable.NAME , columns , whereClause , whereArgs , null,null , BaseTask.DBTable.siteActivityTypeId+" ASC", "100");
        if (cursor.moveToFirst()) {
            do {
                int type = cursor.getInt(cursor.getColumnIndex(BaseTask.DBTable.siteActivityTypeId));
                if(type == Constants.TYPE_ID_BACKFILL){
                    return true;
                }else if(type == Constants.TYPE_ID_MUCKAWAY){
                    return true;
                }else if(type == Constants.TYPE_ID_SERVICE_MATERAL){
                    return true;
                }else if(type == Constants.TYPE_ID_REINSTATEMENT){
                    return true;
                }else if(type == Constants.TYPE_ID_SITE_CLEAR){
                    isSiteClearAvail = true;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        return false;
    }

    public void deleteBaseTasks(String jobID, int taskId , int isSubJob) {
        String whereClause = null;
        String[] whereArgs = null;

        whereClause = BaseTask.DBTable.jobId + " = ? AND " + BaseTask.DBTable.siteActivityTypeId + " = ? AND " + BaseTask.DBTable.isSubJobTask + " = ?";
        whereArgs = new String[]{jobID, String.valueOf(taskId) , String.valueOf(isSubJob)};

        int affectedRows = db.delete(BaseTask.DBTable.NAME, whereClause, whereArgs);
    }

    public void deleteBaseTasks(String jobID, int isSubJob) {
        String whereClause = null;
        String[] whereArgs = null;

        whereClause = BaseTask.DBTable.jobId + " = ? AND " + BaseTask.DBTable.isSubJobTask + " = ?";
        whereArgs = new String[]{jobID, String.valueOf(isSubJob)};

        int affectedRows = db.delete(BaseTask.DBTable.NAME, whereClause, whereArgs);
    }

    public ArrayList<Answer> getMultiAnswers(long submissionId,
                                             String uploadID, int repeatCounter) {
        String whereClause = Answer.DBTable.submissionID + " = ? AND "
                + Answer.DBTable.uploadID + " = ? AND "
                + Answer.DBTable.repeatCounter + " = ?";
        String[] whereArgs = new String[]{String.valueOf(submissionId),
                uploadID, String.valueOf(repeatCounter)};


        Cursor cursor = db.query(Answer.DBTable.NAME, null, whereClause, whereArgs, null, null, Answer.DBTable.repeatID + " ASC");
        ArrayList<Answer> answers = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                answers.add(new Answer(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return answers;
    }

    @Nullable
    public boolean deleteMultiAnswer(long submissionID, String uploadID, int repeatID) {

        String whereClause = null;
        String[] whereArgs = null;

        whereClause = Answer.DBTable.submissionID + " = ? AND " + Answer.DBTable.uploadID + " = ? AND " + Answer.DBTable.repeatCounter + " = ?";
        whereArgs = new String[]{String.valueOf(submissionID), uploadID, String.valueOf(repeatID)};

        int affectedRows = db.delete(Answer.DBTable.NAME, whereClause, whereArgs);

        return affectedRows > 0;
    }

    @Nullable
    public boolean deleteAnswers(long submissionID, String repeatID, int repeatCount) {

        String whereClause = null;
        String[] whereArgs = null;

        whereClause = Answer.DBTable.submissionID + " = ? AND " + Answer.DBTable.repeatID + " = ? AND " + Answer.DBTable.repeatCounter + " = ?";
        whereArgs = new String[]{String.valueOf(submissionID), repeatID, String.valueOf(repeatCount)};

        int affectedRows = db.delete(Answer.DBTable.NAME, whereClause, whereArgs);

        return affectedRows > 0;
    }

    public ArrayList<OperativeTemplate> getOperativeHseq() {
        ArrayList<OperativeTemplate> menSplits = new ArrayList<>();
        String whereClause = null;
        String[] whereArgs = null;
        Cursor cursor = db.query(OperativeTemplate.DBTable.NAME, null, whereClause, whereArgs, null, null, OperativeTemplate.DBTable.operativeId + " DESC");
        if (cursor.moveToFirst()) {
            do {
                OperativeTemplate menSplit = new OperativeTemplate(cursor);
                menSplits.add(menSplit);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return menSplits;
    }
    public ArrayList<InspectionTemplate> getInspectionHseq() {
        ArrayList<InspectionTemplate> menSplits = new ArrayList<>();
        String whereClause = null;
        String[] whereArgs = null;
        Cursor cursor = db.query(InspectionTemplate.DBTable.NAME, null, whereClause, whereArgs, null, null, InspectionTemplate.DBTable.inspectionTemplateId + " DESC");
        if (cursor.moveToFirst()) {
            do {
                InspectionTemplate menSplit = new InspectionTemplate(cursor);
                menSplits.add(menSplit);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return menSplits;
    }
    public String getLatestInspectionTemplateId(String inspectionId) {
        String whereClause = InspectionTemplate.DBTable.inspectionTemplateId + " = ? ";
        String[] whereArgs = new String[]{inspectionId};

        Cursor cursor = db.query(InspectionTemplate.DBTable.NAME, null, whereClause,
                whereArgs, null, null, null);
        if (cursor.moveToFirst()) {
            return cursor.getString(1);
        }

        cursor.close();
        return null;
    }
    public ArrayList<InspectorTemplate> getInspectorHseq() {
        ArrayList<InspectorTemplate> menSplits = new ArrayList<>();
        String whereClause = null;
        String[] whereArgs = null;
        Cursor cursor = db.query(InspectorTemplate.DBTable.NAME, null, whereClause, whereArgs, null, null, InspectorTemplate.DBTable.inspectorId + " DESC");
        if (cursor.moveToFirst()) {
            do {
                InspectorTemplate menSplit = new InspectorTemplate(cursor);
                menSplits.add(menSplit);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return menSplits;
    }

    public boolean removePhotoTags(PhotoTags answer) {
        String whereClause = PhotoTags.DBTable.tagId + " = ?";
        String[] whereArgs = {String.valueOf(answer.getTagId())};
        int affectedRows = db.delete(PhotoTags.DBTable.NAME, whereClause, whereArgs);
        return affectedRows > 0;
    }

    public ArrayList<PhotoTags> getPhotoTags(int answerId) {
        String whereClause = PhotoTags.DBTable.answerId + " = ? ";
        String[] whereArgs = new String[]{String.valueOf(answerId)};
        ArrayList<PhotoTags> answers = new ArrayList<>();

        Cursor cursor = db.query(PhotoTags.DBTable.NAME, null, whereClause, whereArgs, null, null, PhotoTags.DBTable.tagId + " ASC");
        if (cursor.moveToFirst()) {
            do {
                answers.add(new PhotoTags(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return answers;
    }
    public int getPhotoTagsCount(int answerId) {
        String whereClause = PhotoTags.DBTable.answerId + " = ?";
        String[] whereArgs = {String.valueOf(answerId)};
        int count = 0;
        String query = "SELECT COUNT(*) FROM " + PhotoTags.DBTable.NAME+" WHERE "+whereClause;
        Cursor cursor = db.rawQuery(query, whereArgs);
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        if (cursor != null) {
            cursor.close();
        }
        return count;
    }

    public ArrayList<PhotoComments> getPhotoComments(String answerId) {
        String whereClause = PhotoComments.DBTable.answerId + " = ? ";
        String[] whereArgs = new String[]{answerId};
        ArrayList<PhotoComments> jobs = new ArrayList<>();
        //String query = "SELECT * FROM " + PhotoComments.DBTable.NAME;
        Cursor cursor = db.query(PhotoComments.DBTable.NAME, null, whereClause,
                whereArgs, null, null, PhotoComments.DBTable.date + " DESC");
        if (cursor.moveToFirst()) {
            do {
                PhotoComments job = new PhotoComments(cursor);
                jobs.add(job);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return jobs;
    }
    public int getPhotoCommentsCount(int answerId) {
        String whereClause = PhotoComments.DBTable.answerId + " = ?";
        String[] whereArgs = {String.valueOf(answerId)};
        int count = 0;
        String query = "SELECT COUNT(*) FROM " + PhotoComments.DBTable.NAME+" WHERE "+whereClause;
        Cursor cursor = db.rawQuery(query, whereArgs);
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        if (cursor != null) {
            cursor.close();
        }
        return count;
    }


    public List<Schedule> getScheduleLocal(int scheduledInspectionStatus) {
        ArrayList<Schedule> jobs = new ArrayList<>();
        String whereClause = Schedule.DBTable.scheduledInspectionStatus + " = ?";
        String[] whereArgs = {String.valueOf(scheduledInspectionStatus)};
        Cursor cursor = db.query(Schedule.DBTable.NAME, null, whereClause, whereArgs, null, null , Schedule.DBTable.dueDate);
        if (cursor.moveToFirst()) {
            do {
                Schedule job = new Schedule(cursor);
                jobs.add(job);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return jobs;
    }

    public ArrayList<Action> getActions(String actionType , String dueDate) {
        ArrayList<Action> actions = new ArrayList<>();
        String whereClause = Action.DBTable.actionType + " = ? AND "+Action.DBTable.dueDate + " = ?";
        String[] whereArgs = {String.valueOf(actionType), dueDate};
        Cursor cursor = db.query(Action.DBTable.NAME, null, whereClause, whereArgs, null, null, Action.DBTable.dueDate + " DESC");
        if (cursor.moveToFirst()) {
            do {
                Action action = new Action(cursor);
                actions.add(action);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return actions;
    }

    public ArrayList<String> getActionDueDates(String actionType) {
        ArrayList<String> actions = new ArrayList<>();
        String whereClause = Action.DBTable.actionType + " = ?";
        String[] whereArgs = {String.valueOf(actionType)};
        Cursor cursor = db.query(true , Action.DBTable.NAME, new String[]{Action.DBTable.dueDate}, whereClause, whereArgs, null, null, Action.DBTable.dueDate + " DESC", null);
        if (cursor.moveToFirst()) {
            do {
                actions.add(cursor.getString(cursor.getColumnIndex(Action.DBTable.dueDate)));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return actions;
    }

    public ArrayList<BriefingsRecipient> getBriefingsRecipient(String briefingId) {
        ArrayList<BriefingsRecipient> notify = new ArrayList<>();
        String whereClause = BriefingsData.DBTable.briefingId + " = ?";
        String[] whereArgs = {String.valueOf(briefingId)};
        Cursor cursor = db.query(BriefingsRecipient.DBTable.Name, null, whereClause, whereArgs, null, null, BriefingsData.DBTable.briefingId + " DESC");
        if (cursor.moveToFirst()) {
            do {
                BriefingsRecipient menSplit = new BriefingsRecipient(cursor);
                notify.add(menSplit);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return notify;
    }
    public ArrayList<BriefingsDocModal> getBriefings() {
        ArrayList<BriefingsDocModal> notify = new ArrayList<>();
        String whereClause = null;
        String[] whereArgs = null;
        Cursor cursor = db.query(BriefingsDocModal.DBTable.NAME, null, whereClause, whereArgs, null, null, BriefingsDocModal.DBTable.sentDate + " DESC");
        if (cursor.moveToFirst()) {
            do {
                BriefingsDocModal menSplit = new BriefingsDocModal(cursor);
                notify.add(menSplit);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return notify;
    }
    public void resetBriefingsShared() {
        try {
            String[] tables = {
                    IssuedModel.DBTable.NAME
            };

            db.beginTransaction();

            for (String table : tables) {
                db.delete(table, null, null);
            }

            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public ArrayList<IssuedModel> getBriefingsShared() {
        ArrayList<IssuedModel> notify = new ArrayList<>();
        String whereClause = null;
        String[] whereArgs = null;
        Cursor cursor = db.query(IssuedModel.DBTable.NAME, null, whereClause, whereArgs, null, null, "id" + " DESC");
        if (cursor.moveToFirst()) {
            do {
                IssuedModel menSplit = new IssuedModel(cursor);
                notify.add(menSplit);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return notify;
    }
    public void resetBriefingsRead() {
        try {
            String[] tables = {
                    ReceivedModel.DBTable.NAME
            };

            db.beginTransaction();

            for (String table : tables) {
                db.delete(table, null, null);
            }

            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public ArrayList<ReceivedModel> getBriefingsRead() {
        ArrayList<ReceivedModel> notify = new ArrayList<>();
        String whereClause = null;
        String[] whereArgs = null;
        Cursor cursor = db.query(ReceivedModel.DBTable.NAME, null, whereClause, whereArgs, null, null, "id" + " DESC");
        if (cursor.moveToFirst()) {
            do {
                ReceivedModel menSplit = new ReceivedModel(cursor);
                notify.add(menSplit);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return notify;
    }
    public void resetNotification() {
        try {
            String[] tables = {
                    NotifyModel.DBTable.NAME
            };

            db.beginTransaction();

            for (String table : tables) {
                db.delete(table, null, null);
            }

            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public ArrayList<NotifyModel> getNotification() {
        ArrayList<NotifyModel> notify = new ArrayList<>();
        String whereClause = null;
        String[] whereArgs = null;
        Cursor cursor = db.query(NotifyModel.DBTable.NAME, null, whereClause, whereArgs, null, null, NotifyModel.DBTable.notificationId + " DESC");
        if (cursor.moveToFirst()) {
            do {
                NotifyModel menSplit = new NotifyModel(cursor);
                notify.add(menSplit);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return notify;
    }
    public int getReadNotificationCount() {
        String whereClause = NotifyModel.DBTable.hasBeenRead + " = ?";
        String[] whereArgs = {String.valueOf(0)};
        int count = 0;
        String query = "SELECT COUNT(*) FROM " + NotifyModel.DBTable.NAME+" WHERE "+whereClause;
        Cursor cursor = db.rawQuery(query, whereArgs);
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        if (cursor != null) {
            cursor.close();
        }
        return count;
    }
    public void resetTags() {
        try {
            String[] tables = {
                    "Tags"
            };

            db.beginTransaction();

            for (String table : tables) {
                db.delete(table, null, null);
            }

            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public ArrayList<String> getTagsList() {
        ArrayList<String> jobs = new ArrayList<>();
        String query = "SELECT * FROM " + "Tags";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                jobs.add(cursor.getString(cursor.getColumnIndex("tagslist")));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return jobs;
    }

    public ArrayList<OperativeTemplate> getOperativeTemplateByGangId(String gangid) {
        ArrayList<OperativeTemplate> gangOperative = new ArrayList<>();
        String whereClause = "(',' || gangIds || ',') LIKE '%,"+gangid+",%'";
        Cursor cursor = db.query(OperativeTemplate.DBTable.NAME, null, null,
                null, null, null, whereClause + " DESC");
        if (cursor.moveToFirst()) {
            do {
                OperativeTemplate menSplit = new OperativeTemplate(cursor);
                gangOperative.add(menSplit);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return gangOperative;
    }
    public String GetGangOperative(String gangid)
    {
        String whereClause = OperativeTemplate.DBTable.gangIds + " LIKE ? ";
        String[] whereArgs = new String[]{gangid};
        Cursor cursor = db.query(OperativeTemplate.DBTable.NAME, null, whereClause,
                whereArgs, null, null, null);
        if (cursor.moveToFirst()) {
            return cursor.getString(1);
        }
        cursor.close();
        return null;
    }

    public ArrayList<String> GetGangOperativeList(String gangid)
    {
        ArrayList<String> arrayList = new ArrayList<>();
        String whereClause = "(',' || gangIds || ',') LIKE '%,"+gangid+",%'";
        String[] whereColArgs = new String[]{OperativeTemplate.DBTable.operativeFullName};
        //String query = "select operativeFullName from OperativesHseq where (',' || gangIds || ',') LIKE '%,"+gangid+",%'";
        //Cursor cursor = db.rawQuery(query, null);
        Cursor cursor = db.query(OperativeTemplate.DBTable.NAME, whereColArgs, whereClause,
                null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                arrayList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return arrayList;
    }

    public ArrayList<Answer> getRepeatedQuestionAnswers(long submissionId,
                                                        String uploadID, String repeatID) {

        String whereClause;
        String[] whereArgs;

        if (TextUtils.isEmpty(uploadID)) {
            whereClause = Answer.DBTable.submissionID + " = ? AND " +
                    Answer.DBTable.repeatID + " = ?";
            whereArgs = new String[]{String.valueOf(submissionId),
                    repeatID};
        } else {
            whereClause = Answer.DBTable.submissionID + " = ? AND " +
                    Answer.DBTable.uploadID + " = ? AND " +
                    Answer.DBTable.repeatID + " = ?";
            whereArgs = new String[]{String.valueOf(submissionId),
                    uploadID, repeatID};
        }


        Cursor cursor = db.query(Answer.DBTable.NAME, null, whereClause, whereArgs, null, null, Answer.DBTable.repeatCounter + " ASC");
        ArrayList<Answer> answers = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                answers.add(new Answer(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return answers;
    }

    @Nullable
    public ArrayList<Answer> getAnswerPic(long submissionID, String uploadID, String picTitle) {
        ArrayList<Answer> ans = new ArrayList<>();

        String whereClause = Answer.DBTable.submissionID + " = ? AND " + Answer.DBTable.uploadID + " = ? AND " + Answer.DBTable.displayAnswer + " LIKE ? ";
        String[] whereArgs = new String[]{String.valueOf(submissionID), uploadID, picTitle+"%"};

        Cursor cursor = db.query(Answer.DBTable.NAME, null, whereClause, whereArgs, null, null, Answer.DBTable.id + " ASC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                ans.add(new Answer(cursor));
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        return ans;
    }

    public ArrayList<TimeTypeActivity> getTimeTypeActivities() {
        ArrayList<TimeTypeActivity> timeTypeActivities = new ArrayList<>();
        String whereClause = null;
        String[] whereArgs = null;
        Cursor cursor = db.query(TimeTypeActivity.DBTable.NAME, null, whereClause, whereArgs, null, null, TimeTypeActivity.DBTable.timeTypeActivityName + " DESC");
        if (cursor.moveToFirst()) {
            do {
                TimeTypeActivity timeTypeActivity =new TimeTypeActivity(cursor);
                timeTypeActivities.add(timeTypeActivity);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return timeTypeActivities;
    }

    public TimeTypeActivity getTimeTypeActivity(String timeTypeActivityId) {
        TimeTypeActivity timeTypeActivity = null;
        String whereClause = TimeTypeActivity.DBTable.timeTypeActivityId + " = ? ";
        String[] whereArgs = new String[]{timeTypeActivityId};
        Cursor cursor = db.query(TimeTypeActivity.DBTable.NAME, null, whereClause, whereArgs,
                null, null, TimeTypeActivity.DBTable.timeTypeActivityName + " DESC");
        if (cursor.moveToFirst()) {
            timeTypeActivity = new TimeTypeActivity(cursor);
        }
        cursor.close();
        return timeTypeActivity;
    }


    public ArrayList<TimeSheetHour> getTimeHours(String weekCommencing) {
        ArrayList<TimeSheetHour> timeSheetHours = new ArrayList<>();

        String whereClause = TimeSheetHour.DBTable.weekCommencing + " = ? ";
        String[] whereArgs = new String[]{weekCommencing};

        Cursor cursor = db.query(TimeSheetHour.DBTable.NAME, null, whereClause, whereArgs, null, null, TimeSheetHour.DBTable.dateWorked + " ASC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                timeSheetHours.add(new TimeSheetHour(cursor));
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        return timeSheetHours;
    }

    public ArrayList<TimeSheet> getTimeSheets() {
        ArrayList<TimeSheet> timeSheets = new ArrayList<>();

        String whereClause = null;
        String[] whereArgs = null;

        Cursor cursor = db.query(TimeSheet.DBTable.NAME, null, whereClause, whereArgs, null, null, TimeSheet.DBTable.weekCommencing + " ASC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                timeSheets.add(new TimeSheet(cursor));
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        return timeSheets;
    }

    public String getTimeSheetsStatus(String weekCommencing) {

        if(TextUtils.isEmpty(weekCommencing)){
            return null;
        }
        if(!weekCommencing.contains("00:00:00")){
            weekCommencing +="T00:00:00";
        }
        TimeSheet timeSheet = null;

        String whereClause = TimeSheet.DBTable.weekCommencing+" = ?";
        String[] whereArgs = new String[]{weekCommencing};

        Cursor cursor = db.query(TimeSheet.DBTable.NAME, null, whereClause, whereArgs, null, null, TimeSheet.DBTable.weekCommencing + " ASC");

        if (cursor != null && cursor.moveToFirst()) {
            timeSheet = new TimeSheet(cursor);
        }

        if (cursor != null) {
            cursor.close();
        }
        if(timeSheet != null){
            return timeSheet.getTimesheetStatus();
        }

        return null;
    }



    public void removeTimeHours(String weekCommencing) {
        String whereClause = TimeSheetHour.DBTable.weekCommencing + " = ? ";
        String[] whereArgs = new String[]{weekCommencing};
        db.delete(TimeSheetHour.DBTable.NAME, whereClause, whereArgs);
    }

    public ArrayList<TimesheetOperative> getTimeSheetOperatives() {
        ArrayList<TimesheetOperative> timesheetOperatives = new ArrayList<>();
        String whereClause = null;
        String[] whereArgs = null;
        Cursor cursor = db.query(TimesheetOperative.DBTable.NAME, null, whereClause, whereArgs, null, null, TimesheetOperative.DBTable.operativeId + " DESC");
        if (cursor.moveToFirst()) {
            do {
                TimesheetOperative timesheetOperative = new TimesheetOperative(cursor);
                timesheetOperatives.add(timesheetOperative);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return timesheetOperatives;
    }

    public boolean removeTimesheetHour(String timesheetHoursId) {
        if(TextUtils.isEmpty(timesheetHoursId)){
            return false;
        }
        String whereClause = TimeSheetHour.DBTable.timesheetHoursId + " = ?";
        String[] whereArgs = {timesheetHoursId};
        int affectedRows = db.delete(TimeSheetHour.DBTable.NAME, whereClause, whereArgs);
        return affectedRows > 0;
    }

    public LinkedHashMap<String , ArrayList<LogHourItem>> getTimeSheetLogHoursByWeek(String weekCommencing) {

        String whereClause = TimeSheetHour.DBTable.weekCommencing + " = ?";
        String[] whereArgs = new String[]{weekCommencing};
        String[] columns = new String[]{TimeSheetHour.DBTable.dateWorked};

        Cursor cursor = db.query(true, TimeSheetHour.DBTable.NAME, columns, whereClause, whereArgs,null, null, TimeSheetHour.DBTable.dateWorked + " ASC", null);

        LinkedHashMap<String , ArrayList<LogHourItem>> dayMap = new LinkedHashMap<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String date = cursor.getString(cursor.getColumnIndex(TimeSheetHour.DBTable.dateWorked));
                if(!dayMap.containsKey(date)){
                    ArrayList<LogHourItem> items = new ArrayList<>();
                    items.addAll(getTimeSheetLogHours(date));
                    dayMap.put(date , items);
                }else{
                    ArrayList<LogHourItem> items = dayMap.get(date);
                    items.addAll(getTimeSheetLogHours(date));
                }
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        return dayMap;
    }

    public LinkedHashMap<String , String> getTimeSheetLogHoursStatusByWeek(String weekCommencing) {
        weekCommencing = weekCommencing.split("T")[0];
        String whereClause = TimeSheetHour.DBTable.weekCommencing + " = ?";
        String[] whereArgs = new String[]{weekCommencing};
        String[] columns = new String[]{TimeSheetHour.DBTable.dateWorked};

        Cursor cursor = db.query(true, TimeSheetHour.DBTable.NAME, columns, whereClause, whereArgs,null, null, TimeSheetHour.DBTable.dateWorked + " ASC", null);

        LinkedHashMap<String , String> dayMap = new LinkedHashMap<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String date = cursor.getString(cursor.getColumnIndex(TimeSheetHour.DBTable.dateWorked));
                if(!dayMap.containsKey(date)){
                    ArrayList<LogHourItem> items = getTimeSheetLogHours(date);
                    if(!items.isEmpty()){
                        dayMap.put(date , items.get(0).getStatus());
                    }
                }
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        return dayMap;
    }

    public ArrayList<LogHourItem> getTimeSheetLogHours(String dateWorked) {
        HashMap<String , HashMap<String , LogHourTime>> logHourTimeHashMap = new HashMap<>();

        HashMap<String , LogHourItem> logHourItemHashMap = new HashMap<>();

        String whereClause = TimeSheetHour.DBTable.dateWorked + " = ?";
        String[] whereArgs = new String[]{dateWorked};

        Cursor cursor = db.query(TimeSheetHour.DBTable.NAME, null, whereClause, whereArgs, null, null, TimeSheetHour.DBTable.dateWorked + " ASC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                TimeSheetHour timeSheetHour = new TimeSheetHour(cursor);
                if(!logHourTimeHashMap.containsKey(timeSheetHour.getUniqueKey())) {
                    logHourItemHashMap.put(timeSheetHour.getUniqueKey() , new LogHourItem(timeSheetHour ,
                            null));
                    HashMap<String , LogHourTime> timeHashMap = new HashMap<>();
                    timeHashMap.put(timeSheetHour.getHourKey() , new LogHourTime(timeSheetHour.getTimesheetHoursId(), timeSheetHour.getTimeTypeActivityId(), timeSheetHour.getTimeTypeActivityName() , timeSheetHour.getNormalTimeMinutes() , timeSheetHour.getOvertimeMinutes()));
                    logHourTimeHashMap.put(timeSheetHour.getUniqueKey(), timeHashMap);
                }else{
                    HashMap<String , LogHourTime> timeHashMap = logHourTimeHashMap.get(timeSheetHour.getUniqueKey());
                    timeHashMap.put(timeSheetHour.getHourKey() , new LogHourTime(timeSheetHour.getTimesheetHoursId(), timeSheetHour.getTimeTypeActivityId(), timeSheetHour.getTimeTypeActivityName() , timeSheetHour.getNormalTimeMinutes() , timeSheetHour.getOvertimeMinutes()));
                }
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        ArrayList<LogHourItem> logHourItems = new ArrayList<>();
        Set<String> keySet = logHourItemHashMap.keySet();
        if(!keySet.isEmpty()){
            for(String key : keySet){
                LogHourItem logHourItem = logHourItemHashMap.get(key);
                ArrayList<LogHourTime> times = new ArrayList<>();
                HashMap<String , LogHourTime> logHourTimeHashMap1 = logHourTimeHashMap.get(key);
                Set<String> timeKeySet = logHourTimeHashMap1.keySet();
                if(!timeKeySet.isEmpty()){
                    for(String timeKey : timeKeySet) {
                        times.add(logHourTimeHashMap1.get(timeKey));
                    }
                }
                logHourItem.setLogHourTimes(times);
                logHourItems.add(logHourItem);
            }
        }

        return logHourItems;
    }

    public ArrayList<IncidentSource> getIncidentSources() {
        ArrayList<IncidentSource> incidentSources = new ArrayList<>();
        String selection = null;
        String[] selectionArgs = null;

        Cursor cursor = db.query(IncidentSource.DBTable.NAME,
                null, selection, selectionArgs,
                null, null, IncidentSource.DBTable.incidentSourceId + " ASC");


        if (cursor.moveToFirst()) {
            do {
                incidentSources.add(new IncidentSource(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return incidentSources;
    }

    public ArrayList<IncidentSeverity> getIncidentSeverities() {
        ArrayList<IncidentSeverity> incidentSeverities = new ArrayList<>();
        String selection = null;
        String[] selectionArgs = null;

        Cursor cursor = db.query(IncidentSeverity.DBTable.NAME,
                null, selection, selectionArgs,
                null, null, IncidentSeverity.DBTable.incidentSeverityName + " ASC");


        if (cursor.moveToFirst()) {
            do {
                incidentSeverities.add(new IncidentSeverity(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return incidentSeverities;
    }

    public ArrayList<IncidentSubCategory> getIncidentSubCategories(String incidentCategoryId) {
        ArrayList<IncidentSubCategory> incidentSubCategories = new ArrayList<>();
        String selection = IncidentSubCategory.DBTable.incidentCategoryId + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(incidentCategoryId)};

        Cursor cursor = db.query(IncidentSubCategory.DBTable.NAME,
                null, selection, selectionArgs,
                null, null, IncidentSubCategory.DBTable.incidentSubcategoryName + " ASC");


        if (cursor.moveToFirst()) {
            do {
                incidentSubCategories.add(new IncidentSubCategory(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return incidentSubCategories;
    }

    public ArrayList<IncidentCategory> getIncidentCategories() {
        ArrayList<IncidentCategory> incidentCategories = new ArrayList<>();
        String selection = null;
        String[] selectionArgs = null;

        Cursor cursor = db.query(IncidentCategory.DBTable.NAME,
                null, selection, selectionArgs,
                null, null, IncidentCategory.DBTable.incidentCategoryName + " ASC");


        if (cursor.moveToFirst()) {
            do {
                incidentCategories.add(new IncidentCategory(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return incidentCategories;
    }

    public ArrayList<UniqueIncident> getUniqueIncident() {
        ArrayList<UniqueIncident> uniqueIncidents = new ArrayList<>();
        String selection = null;
        String[] selectionArgs = null;

        Cursor cursor = db.query(UniqueIncident.DBTable.NAME,
                null, selection, selectionArgs,
                null, null, UniqueIncident.DBTable.incidentTitle + " ASC");


        if (cursor.moveToFirst()) {
            do {
                uniqueIncidents.add(new UniqueIncident(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return uniqueIncidents;
    }
}
