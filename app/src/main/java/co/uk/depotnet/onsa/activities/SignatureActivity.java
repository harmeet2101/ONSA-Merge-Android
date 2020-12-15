package co.uk.depotnet.onsa.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.modals.forms.Answer;
import co.uk.depotnet.onsa.modals.forms.FormItem;
import co.uk.depotnet.onsa.utils.Utils;

public class SignatureActivity extends ThemeBaseActivity {
    public static final String ARG_SUBMISSION_ID = "SubmissionID";
    public static final String ARG_FORM_ITEM = "FormItem";
    public static final String ARG_REPEAT_COUNT = "RepeatCount";
    public static final String ARG_COLOR = "color";
    private static final int MY_PERMISSIONS_REQUEST_WRITE = 1;

    private SignaturePad signaturePad;
    private long submissionID;
    private FormItem formItem;
    private int repeatCount;
    private String themeColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        submissionID = intent.getLongExtra(ARG_SUBMISSION_ID , -1);
        formItem = intent.getParcelableExtra(ARG_FORM_ITEM);
        repeatCount = intent.getIntExtra(ARG_REPEAT_COUNT, 0);
        themeColor = intent.getStringExtra(ARG_COLOR);


        setContentView(R.layout.activity_signature);
        if (themeColor != null && !themeColor.isEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.parseColor(themeColor));
            }
        }

        signaturePad = findViewById(R.id.signature_pad);

        Answer answer = DBHandler.getInstance().getAnswer(submissionID, formItem.getUploadId(), formItem.getRepeatId(), repeatCount);
        if (answer != null) {
            String path = answer.getAnswer();
            if (!TextUtils.isEmpty(path)) {
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                if (bitmap != null) {
                    signaturePad.setSignatureBitmap(bitmap);
                }
            }
        }


        findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveFile();
            }
        });

        findViewById(R.id.btn_img_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signaturePad.clear();
            }
        });
    }


    private void saveFile() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE);
            return;
        }

        if (!signaturePad.isEmpty()) {
            Bitmap bitmap = signaturePad.getSignatureBitmap();
            File file = Utils.saveSignature(this , bitmap);
            if (file != null) {
                Answer answer = DBHandler.getInstance().getAnswer(submissionID, formItem.getUploadId(), formItem.getRepeatId(), repeatCount);
                if (answer == null) {
                    answer = new Answer(submissionID, formItem.getUploadId());
                }

                answer.setAnswer(file.getAbsolutePath());
                answer.setRepeatCount(repeatCount);
                answer.setRepeatID(formItem.getRepeatId());
                answer.setIsPhoto(1);
                answer.setSignatureUrl(formItem.getSignatureUrl());
                Date date = new Date();
                SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                String timeTaken = input.format(date);
                answer.setTakenDateTime(timeTaken);
                DBHandler.getInstance().replaceData(Answer.DBTable.NAME, answer.toContentValues());
            }



            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE:
                boolean isPermissionGranted = true;

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, "Save photo permission not granted!",
                            Toast.LENGTH_SHORT).show();
                    isPermissionGranted = false;
                }

                if (isPermissionGranted) {
                    saveFile();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        super.onBackPressed();
    }
}
