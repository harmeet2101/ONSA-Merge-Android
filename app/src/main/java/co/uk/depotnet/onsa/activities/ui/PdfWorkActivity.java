package co.uk.depotnet.onsa.activities.ui;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.listener.OnPageScrollListener;
import com.google.android.material.snackbar.Snackbar;
import com.shockwave.pdfium.PdfDocument;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import co.uk.depotnet.onsa.networking.CallUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import co.uk.depotnet.onsa.BuildConfig;
import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.modals.briefings.BriefingsDocModal;
import co.uk.depotnet.onsa.modals.briefings.BriefingsDocument;
import co.uk.depotnet.onsa.networking.APICalls;
import co.uk.depotnet.onsa.networking.CommonUtils;
import co.uk.depotnet.onsa.utils.GenericFileProvider;
import co.uk.depotnet.onsa.utils.Utils;

public class PdfWorkActivity extends AppCompatActivity implements View.OnClickListener,OnPageChangeListener, OnPageErrorListener, OnPageScrollListener, OnLoadCompleteListener {
    private static final String TAG = PdfWorkActivity.class.getSimpleName();
    public static final String Doc_Bag = "docModel";
    public static final String Doc_selectedBriefings = "selectedBriefings";
    public static final String Doc_Recipient = "Doc_Recipient";
    private BriefingsDocModal BagDocument;
    private LinearLayout progressBar;
    private TextView pdf_title,pdf_next,pdf_pagecount;
    private BriefingsDocument briefingsDocument;
    private PDFView pdfView;
    Integer pageNumber = 0; //class type to hold primitive object
    private byte[] decodestream;
    private String fileName,filePathString;
    private File filepath;
    private LinearLayout commandLay;
    private int doccounter = 0;
    private ArrayList<String> selectedBriefings;
    private ArrayList<String> recipients;

    private ArrayList<BriefingsDocument> briefingsDocuments=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_work);
        Intent intent = getIntent();
        BagDocument=intent.getParcelableExtra(Doc_Bag);
        selectedBriefings= intent.getStringArrayListExtra(Doc_selectedBriefings);
        recipients= intent.getStringArrayListExtra(Doc_Recipient);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.ColorBriefing));
        }


        pdf_title=findViewById(R.id.pdf_title);
        progressBar=findViewById(R.id.pdf_ui_blocker);
        pdf_next=findViewById(R.id.pdf_next);
        pdfView=findViewById(R.id.pdfView);

        if(BagDocument == null || selectedBriefings == null || selectedBriefings.isEmpty()) {
            Toast.makeText(this, "Briefings document found empty! try after some time..", Toast.LENGTH_SHORT).show();
            this.finish();
        }


            briefingsDocuments.clear();
            try {
                GetFileBYID(selectedBriefings.get(doccounter));
                doccounter++;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }


        //pdf user commands
        pdf_pagecount=findViewById(R.id.pdf_pagecount);
        commandLay=findViewById(R.id.commandLay);
        findViewById(R.id.pdf_share).setOnClickListener(this);
        findViewById(R.id.pdf_download).setOnClickListener(this);
        findViewById(R.id.pdf_print).setOnClickListener(this);
        pdf_next.setOnClickListener(this);
    }
    private void GetFileBYID(String documentID)
    {
        ShowProgress(true);
        CallUtils.enqueueWithRetry(APICalls.GetBriefingsDocData(documentID, DBHandler.getInstance().getUser().gettoken()),new Callback<BriefingsDocument>() {
            @Override
            public void onResponse(@NonNull Call<BriefingsDocument> call, @NonNull Response<BriefingsDocument> response) {
                if(CommonUtils.onTokenExpired(PdfWorkActivity.this , response.code())){
                    return;
                }


                if(!response.isSuccessful()){
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(getApplicationContext(), jObjError.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    ShowProgress(false);
                    return;
                }

                briefingsDocument = response.body();
                    if (briefingsDocument!=null) {
                        pdf_title.setText(briefingsDocument.getBriefingName());
                        if(briefingsDocument.getBriefingDocumentFileBytes() != null && ! briefingsDocument.getBriefingDocumentFileBytes().isEmpty()) {
                            decodestream = Base64.decode(briefingsDocument.getBriefingDocumentFileBytes(), Base64.DEFAULT);
                            if (decodestream != null && decodestream.length > 0) {
                                pdfView.fromBytes(decodestream)
                                        .defaultPage(pageNumber)
                                        .onPageChange(PdfWorkActivity.this)
                                        .onPageError(PdfWorkActivity.this)
                                        .enableSwipe(true)
                                        .onLoad(PdfWorkActivity.this)
                                        .load();
                                fileName = "BriefingDoc_" + briefingsDocument.getBriefingName() + ".pdf";
                                filePathString = Utils.getSaveBriefingsDir(getApplicationContext()) + "/" + fileName;
                                filepath = new File(filePathString);
                                writeDocToDisk();
                                briefingsDocuments.add(briefingsDocument);// adding here to display in

                            }
                        }
                    }
                    else {
                        Toast.makeText(PdfWorkActivity.this, "Briefing doc File not found!", Toast.LENGTH_SHORT).show();
                    }


                ShowProgress(false);
            }

            @Override
            public void onFailure(@NonNull Call<BriefingsDocument> call, @NonNull Throwable t) {
                Log.d(TAG,"briefings pdf response error: "+t.getLocalizedMessage());
                ShowProgress(false);
            }
        });
    }
    private void writeDocToDisk() {
        try {
            FileOutputStream fos=null;
            try {
           //File fileDir = new File(Utils.getSaveDir(getApplicationContext()) + "/" + fileName);
                if (!filepath.exists()) {
                    filepath.createNewFile();

                fos = new FileOutputStream(filepath);// standard way
                fos.write(decodestream);
                fos.close();
                Log.d(TAG, "file download path: " + filepath + " of " + fos);
                }
                commandLay.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                Log.e(TAG, "file download path error: "+e.getMessage());
            } finally {
                if (fos != null) {
                    fos.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
   private void PDF_Commands() {
       try {
           FileOutputStream fos=null;
           try {
               File filepdf = new File(Environment
                       .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                       + "/" + fileName);
               if (!filepdf.exists()) {
                   filepdf.createNewFile();

                   fos = new FileOutputStream(filepdf);
                   fos.write(decodestream);
                   fos.close();
                   //Log.d(TAG, "file download path: " + filepath + " of " + fos);
                   Toast.makeText(this, fileName+" Successfully Saved to downloads..", Toast.LENGTH_LONG).show();
                   //startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
               }
               else
               {
                   try {
                       Intent intent = new Intent(Intent.ACTION_VIEW);
                       if (filepdf.exists())
                       {
                           Uri uri = GenericFileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".fileprovider", filepdf);
                           String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(uri.toString());
                           String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                           if (extension.equalsIgnoreCase("") || mimetype == null) {
                               // if there is no extension or there is no definite mimetype, still try to open the file
                               intent.setDataAndType(uri, "text/*");
                           } else {
                               intent.setDataAndType(uri, mimetype);
                           }
                           intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                           startActivity(Intent.createChooser(intent, fileName+" - please Choose an Application:"));
                       }
                       else
                       {
                           Toast.makeText(this, "file not found! please try after some time", Toast.LENGTH_LONG).show();
                       }

                   } catch (ActivityNotFoundException e) {
                       Snackbar.make(findViewById(android.R.id.content), "No PDF reader found to open this file.", Snackbar.LENGTH_LONG).show();
                   }
               }
           } catch (Exception e) {
               Log.e(TAG, "file download path error: "+e.getMessage());
           } finally {
               if (fos != null) {
                   fos.close();
               }
           }
       } catch (IOException e) {
           e.printStackTrace();
       }
    }
    private void PDF_Share()
    {
        try {
            Intent intentShareFile = new Intent(Intent.ACTION_SEND);
            if(filepath.exists()) {
                intentShareFile.setType("application/pdf");
                Uri uri = GenericFileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".fileprovider", filepath);
                intentShareFile.putExtra(Intent.EXTRA_STREAM, uri);

                intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                        "Sharing File...");
                intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...");

                startActivity(Intent.createChooser(intentShareFile, "Share File"));
            }
            else
            {
                Toast.makeText(this, "File not found! please try after some time..", Toast.LENGTH_SHORT).show();
            }
        } catch (ActivityNotFoundException e) {
            Snackbar.make(findViewById(android.R.id.content), "No PDF file found to share! please try after some time.", Snackbar.LENGTH_LONG).show();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void PDF_Print()
    {
        PrintManager printManager= (PrintManager) getSystemService(Context.PRINT_SERVICE);
        try
        {
            PrintDocumentAdapter printAdapter = new PdfDocumentAdapter(getApplicationContext(), filePathString );
            printManager.print("Document", printAdapter,new PrintAttributes.Builder().build());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    @Override
    public void loadComplete(int nbPages) {
        //Toast.makeText(this, "total pages: "+nbPages, Toast.LENGTH_SHORT).show();
       /* PdfDocument.Meta meta = pdfView.getDocumentMeta();
        Log.e(TAG, "title = " + meta.getTitle());
        Log.e(TAG, "author = " + meta.getAuthor());
        Log.e(TAG, "subject = " + meta.getSubject());
        Log.e(TAG, "keywords = " + meta.getKeywords());
        Log.e(TAG, "creator = " + meta.getCreator());
        Log.e(TAG, "producer = " + meta.getProducer());
        Log.e(TAG, "creationDate = " + meta.getCreationDate());
        Log.e(TAG, "modDate = " + meta.getModDate());
        printBookmarksTree(pdfView.getTableOfContents(), "-");*/
    }
    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

            Log.e(TAG, String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }
    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
        pdf_pagecount.setText((String.format("%s %s / %s", "", page + 1, pageCount)));
    }

    @Override
    public void onPageError(int page, Throwable t) {
        Log.e(TAG, "Cannot load page " + page);
    }

    @Override
    public void onPageScrolled(int page, float positionOffset) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.pdf_share:
                PDF_Share();
                break;
            case R.id.pdf_download:
                PDF_Commands();
                break;
            case R.id.pdf_print:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    PDF_Print();
                }
                break;
            case R.id.pdf_next:
                try {
                      Log.d(TAG,"docs counter: "+ doccounter);
                       if (selectedBriefings.size() != doccounter)
                        {
                            GetFileBYID(selectedBriefings.get(doccounter));
                            doccounter++;
                        }
                       else
                       {
                           Intent intent=new Intent(getApplicationContext(),BriefingReadActivity.class);
//                           intent.putExtra(BriefingReadActivity.ARG_DOCS, briefingsDocument);
                           ArrayList<BriefingsDocument> documents = new ArrayList<>();
                           if(briefingsDocuments != null && !briefingsDocuments.isEmpty()) {
                               for (BriefingsDocument document : briefingsDocuments) {
                                    BriefingsDocument document1 = document.getDocumentWithoutFileBytes();
                                    documents.add(document1);
                               }
                           }
                           intent.putParcelableArrayListExtra("readDocs", documents);
                           intent.putStringArrayListExtra("Doc_Recipient", recipients);
                           startActivity(intent);
                       }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }
    private void ShowProgress(boolean flag)
    {
        if (flag)
        {
            progressBar.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
        else
        {
            progressBar.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

    }
}