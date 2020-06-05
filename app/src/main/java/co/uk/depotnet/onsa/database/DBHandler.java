package co.uk.depotnet.onsa.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.os.Parcel;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import co.uk.depotnet.onsa.listeners.DropDownItem;
import co.uk.depotnet.onsa.modals.A75Groups;
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
import co.uk.depotnet.onsa.modals.RiskElementType;
import co.uk.depotnet.onsa.modals.User;
import co.uk.depotnet.onsa.modals.WorkItem;
import co.uk.depotnet.onsa.modals.forms.Answer;
import co.uk.depotnet.onsa.modals.forms.Submission;
import co.uk.depotnet.onsa.modals.store.MyRequest;
import co.uk.depotnet.onsa.modals.store.MyStore;
import co.uk.depotnet.onsa.modals.store.MyStoreFav;
import co.uk.depotnet.onsa.modals.store.ReceiptItems;
import co.uk.depotnet.onsa.modals.store.Receipts;
import co.uk.depotnet.onsa.modals.store.RequestItem;
import co.uk.depotnet.onsa.modals.store.StockItems;

public class DBHandler {

    private static DBHandler dbHandler;
    private SQLiteDatabase db;
    private DBHelper dbHelper;

    private DBHandler() {
    }


    public static DBHandler getInstance() {
        if (dbHandler == null) {
            synchronized (DBHandler.class) {
                if (dbHandler == null) {
                    dbHandler = new DBHandler();
                }
            }
        }

        if (dbHandler.db == null) {
            dbHandler.openDatabase();
        }
        return dbHandler;
    }

    public void init(Context context) {
        dbHelper = DBHelper.init(context);
        openDatabase();
    }

    private void openDatabase() {
        if (dbHelper == null) {
            return;
        }
        try {
            dbHelper.openDatabase();
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


    public List<KitBagDocument> getKitBagDoc() {
        ArrayList<KitBagDocument> kitBagDocuments = new ArrayList<>();

        Cursor cursor = db.query(KitBagDocument.DBTable.NAME, null, null,
                null, null, null, null);

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
                    User.DBTable.NAME,
                    RiskElementType.DBTable.NAME,
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

    public void clearUser() {
        try {
            String[] tables = {
                    User.DBTable.NAME,
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

    public void resetDatasetTable() {
        String[] tables = {
                KitBagDocument.DBTable.NAME,
                WorkItem.DBTable.NAME,
                ItemType.DBTable.NAME,
                RiskElementType.DBTable.NAME,
                StockItems.DBTable.NAME,
        };
        try {
            db.beginTransaction();
            for (String table : tables) {
                db.delete(table, null, null);
            }
            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception e) {

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
                null, null, null);
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

    public JobWorkItem getJobWorkItem(String jobId, String code) {

        String selection = JobWorkItem.DBTable.jobId + " = ? AND " + JobWorkItem.DBTable.itemCode + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(jobId), code};
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

    public ArrayList<WorkItem> getWorkItem(String type, String contractNumber, String orderBy) {

        String selection = WorkItem.DBTable.type + " = ? AND " + WorkItem.DBTable.contractNumber + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(type), contractNumber};

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
                null, null, Notice.DBTable.noticeId + " ASC");
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
}
