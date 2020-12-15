package co.uk.depotnet.onsa.modals;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.ArrayList;

import co.uk.depotnet.onsa.database.DBHandler;

public class KitBagDocument implements Parcelable {

    private int id;
    private int parentId;
    private boolean isDocument;
    private String text;
    private String documentId;
    private String documentUpdatedDate;
    private ArrayList<KitBagDocument> childNodes;


    public KitBagDocument(Cursor cursor) {
        id = cursor.getInt(cursor.getColumnIndex(DBTable.id));
        text = cursor.getString(cursor.getColumnIndex(DBTable.text));
        documentId = cursor.getString(cursor.getColumnIndex(DBTable.documentId));
        documentUpdatedDate = cursor.getString(cursor.getColumnIndex(DBTable.documentUpdatedDate));
        isDocument = cursor.getInt(cursor.getColumnIndex(DBTable.isDocument)) == 1;
        parentId = cursor.getInt(cursor.getColumnIndex(DBTable.parentId));
        childNodes = new ArrayList<>();
        childNodes.addAll(DBHandler.getInstance().getKitBagDoc(id));
    }


    protected KitBagDocument(Parcel in) {
        id = in.readInt();
        parentId = in.readInt();
        isDocument = in.readByte() != 0;
        text = in.readString();
        documentId = in.readString();
        documentUpdatedDate = in.readString();
        childNodes = in.createTypedArrayList(KitBagDocument.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(parentId);
        dest.writeByte((byte) (isDocument ? 1 : 0));
        dest.writeString(text);
        dest.writeString(documentId);
        dest.writeString(documentUpdatedDate);
        dest.writeTypedList(childNodes);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<KitBagDocument> CREATOR = new Creator<KitBagDocument>() {
        @Override
        public KitBagDocument createFromParcel(Parcel in) {
            return new KitBagDocument(in);
        }

        @Override
        public KitBagDocument[] newArray(int size) {
            return new KitBagDocument[size];
        }
    };

    public ContentValues toContentValues() {
        getFolder(this);
        return null;
    }

    private void getFolder(KitBagDocument document){

        ContentValues cv = new ContentValues();
        cv.put(DBTable.text, document.text);
        cv.put(DBTable.documentId, document.documentId);
        cv.put(DBTable.documentUpdatedDate, document.documentUpdatedDate);
        cv.put(DBTable.parentId, document.parentId);
        cv.put(DBTable.isDocument, document.isDocument?1 : 0);

        long id = DBHandler.getInstance().replaceData(KitBagDocument.DBTable.NAME, cv);

        if(document.isDocument()){
            return;
        }

        ArrayList<KitBagDocument> childDocs = document.getChildNodes();

        if(childDocs == null || childDocs.isEmpty()){
            return;
        }



        for (KitBagDocument folder: childDocs) {
            folder.setParentId((int)id);
            getFolder(folder);
        }

    }

    public static class DBTable {
        public static final String NAME = "KitBagDocuments";
        public static final String id = "id";
        public static final String text = "text";
        public static final String documentId = "documentId";
        public static final String documentUpdatedDate = "documentUpdatedDate";
        public static final String isDocument = "isDocument";
        public static final String parentId = "parentId";
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDocumentId() {
        return documentId;
    }

    public int getId() {
        return id;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getUpdatedDate() {
        return documentUpdatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.documentUpdatedDate = updatedDate;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public boolean isDocument() {
        return isDocument;
    }

    public ArrayList<KitBagDocument> getChildNodes() {
        return childNodes;
    }
}