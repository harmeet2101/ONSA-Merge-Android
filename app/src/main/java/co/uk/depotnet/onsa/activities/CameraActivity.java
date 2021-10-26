package co.uk.depotnet.onsa.activities;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.location.Location;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.activities.ui.EditShareActivity;
import co.uk.depotnet.onsa.adapters.AdapterCameraPhoto;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.listeners.LocationListener;
import co.uk.depotnet.onsa.listeners.LocationPermissionListener;
import co.uk.depotnet.onsa.modals.forms.Answer;
import co.uk.depotnet.onsa.modals.forms.FormItem;
import co.uk.depotnet.onsa.modals.forms.Photo;
import co.uk.depotnet.onsa.networking.CommonUtils;
import co.uk.depotnet.onsa.utils.PathUtil;
import co.uk.depotnet.onsa.utils.Utils;
import co.uk.depotnet.onsa.views.CameraView;

public class CameraActivity extends AppCompatActivity implements
        ActivityCompat.OnRequestPermissionsResultCallback,
        View.OnClickListener, AdapterCameraPhoto.OnImageChange,
        LocationPermissionListener {

    public static final String BACK_STACK_TAG = CameraActivity.class.getName();
    public static final int PICK_IMAGE_REQUEST = 1001;
    public static final int PICK_MODIFY_IMAGE = 1002;
    public static final int PERMISSION_ACTIVITY_REQUEST = 1003;
    public static final String ARG_SUBMISSION_ID = "_arg_submission_id";
    public static final String ARG_FORM_ITEM = "_arg_form_item";
    public static final String ARG_REPEAT = "_arg_repeat";
    public static final String ARG_COLOR = "color";
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final int MY_PERMISSIONS_REQUEST_VIDEO = 9;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 10;
    private static final int MY_PERMISSIONS_LOCATION = 11;
    private static final int MY_PERMISSIONS_REQUEST_GALLERY = 12;
    private static String fileVideoPath = "";
    private long count = 0;
    private Timer timer;
    private double latitude = -1000;
    private double longitude = -1000;
    private Camera camera;
    private CameraView cameraView;
    private MediaRecorder mediaRecorder;
    private ImageView btnCancel;
    private ImageView btnTakePic;
    private ImageView btnGallery;
    private ImageView btnVideo;
    private ProgressBar progressBar;
    private FrameLayout frameLayoutCamera = null;
    private Camera.PictureCallback pictureCallback;
    private ArrayList<Photo> photos;
    private long submissionID;
    private int repeatCount = 0;
    private FormItem formItem;
    private String title;
    private AdapterCameraPhoto adapter;
    private ArrayList<Answer> answers;
    private String lastPhotoPath;
    private boolean isRecording = false;
    private LinearLayout llTimer;
    private TextView tvTimer;
    private boolean isVideoModeOn;
    private String themeColor;
    private FusedLocationProviderClient mFusedLocationClient;
    private boolean isPermissionSettingRequested;
    private DBHandler dbHandler;

    private SimpleTarget target = new SimpleTarget<BitmapDrawable>() {

        @Override
        public void onResourceReady(@NonNull BitmapDrawable resource,
                                    @Nullable Transition<? super BitmapDrawable> transition) {
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(lastPhotoPath);
                lastPhotoPath = null;
                resource.getBitmap().compress(Bitmap.CompressFormat.JPEG, 70, out);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                new Handler(Looper.getMainLooper()).post(() -> {
                    restartCamera(false);
                    adapter.notifyDataSetChanged();
                });
            }
        }
    };



    private static File getOutputMediaFile(int type) {
        File filepath = Environment.getExternalStorageDirectory();
        File mediaStorageDir = new File(filepath.getAbsolutePath() + "/" + "Onsa" + "/");
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.UK).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
            if(!mediaFile.exists()){
                try {
                    mediaFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            return null;
        }
        fileVideoPath = mediaFile.getAbsolutePath();
        return mediaFile;
    }

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
            if (c == null) {
                return Camera.open(0);
            }
        } catch (Exception e) {
        }
        return c;
    }

    private void restartCamera(boolean shouldCheckPermission) {
        if (camera != null) {
            releaseCameraAndPreview();
            releaseMediaRecorder();       // if you are using MediaRecorder, release it first
            releaseCamera();
        }
        if (frameLayoutCamera != null) {
            frameLayoutCamera.removeView(cameraView);
            cameraView = null;
        }
        startCameraPreview(shouldCheckPermission);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        this.dbHandler = DBHandler.getInstance(this);
        Intent intent = getIntent();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        formItem = intent.getParcelableExtra(ARG_FORM_ITEM);
        submissionID = intent.getLongExtra(ARG_SUBMISSION_ID, 0);
        repeatCount = intent.getIntExtra(ARG_REPEAT, 0);
        themeColor = intent.getStringExtra(ARG_COLOR);
        title = formItem.getTitle();
        initPhotos();

        btnCancel = findViewById(R.id.img_cancel);
        btnTakePic = findViewById(R.id.img_btn_camera);
        btnGallery = findViewById(R.id.img_btn_gallery);
        btnVideo = findViewById(R.id.img_btn_video);
        TextView btnDone = findViewById(R.id.btn_done);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        TextView txtTitle = findViewById(R.id.txt_photo_name);
        if (photos != null && photos.size() > 0)
            txtTitle.setText(title);
        progressBar = findViewById(R.id.camera_progress_bar);

        llTimer = findViewById(R.id.llTimer);
        tvTimer = findViewById(R.id.tvTimer);

        btnCancel.setOnClickListener(this);
        btnTakePic.setOnClickListener(this);
        btnGallery.setOnClickListener(this);
        btnVideo.setOnClickListener(this);
        btnDone.setOnClickListener(this);
        btnCancel.setVisibility(View.GONE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        if (photos != null && photos.size() > 0)
            adapter = new AdapterCameraPhoto(this, answers, this);
        recyclerView.setAdapter(adapter);
        pictureCallback = (data, camera) -> {
            File pictureFile = null;

            try {
                pictureFile = Utils.createImageFile(CameraActivity.this);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            if (pictureFile == null) {
                restartCamera(false);
                return;
            }

            try {
                changeOrientation(data, pictureFile.getAbsolutePath());
                setPicture(pictureFile.getPath());
            } catch (Exception e) {
                restartCamera(false);
                e.printStackTrace();
            }
        };

        checkCameraPermission(true);
    }

    private void initPhotos() {
        this.photos = formItem.getPhotos();
        answers = new ArrayList<>();
        
        answers.addAll(dbHandler.getPhotos(submissionID, String.valueOf(formItem.getPhotoId()),
                formItem.getTitle(), repeatCount));
        for (int i = 0; i < answers.size(); i++) {
            photos.get(i).setUrl(answers.get(i).getAnswer());
        }
    }

    private void bindLocation() {
        getLocation(new LocationListener() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
            }

            @Override
            public void onFailure() {
            }
        });
    }

    @Override
    public void onClick(View view) {
        long id = view.getId();
        if(id == R.id.btn_done){
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }else if(id == R.id.btn_img_cancel){
            btnCancel.setVisibility(View.GONE);
            if (camera != null) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        }else if(id == R.id.img_btn_gallery){
            checkGalleryPermission();
        }else if(id == R.id.img_btn_camera){
            takePicture();
        }else if(id == R.id.img_btn_video){
            checkVideoPermissions();
        }


    }



    @Override
    public void notifyImage(Answer photo, int position) {

        if (photos == null || photos.isEmpty()) {
            return;
        }
        Photo p = photos.get(position);
        String photoUrl = p.getUrl();
        if (TextUtils.isEmpty(photoUrl)) {
            return;
        }

        Intent intent = new Intent(this, EditShareActivity.class);
        intent.putExtra("photos", photo);
        intent.putExtra("photomodel", p);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra("POSITION", position);
        intent.putExtra(ARG_COLOR, themeColor);
        startActivityForResult(intent, PICK_MODIFY_IMAGE);
    }

    private void startVideoCapturing() {
        if (isRecording) {
            // stop recording and release camera
            mediaRecorder.stop();// stop the recording
            llTimer.setVisibility(View.GONE);
            count = 0;
            timer.cancel();
            timer.purge();
            releaseMediaRecorder(); // release the MediaRecorder object
            camera.lock();         // take camera access back from MediaRecorder
            // inform the user that recording has stopped
            Toast.makeText(CameraActivity.this, "Capture", Toast.LENGTH_SHORT).show();
            btnTakePic.setImageResource(R.drawable.btn_drawable_camer);
            isRecording = false;
            setVideo(fileVideoPath);
        } else {
            if (prepareVideoRecorder()) {
                llTimer.setVisibility(View.VISIBLE);
                count = 0;
                mediaRecorder.start();
                timerVideo();
                Toast.makeText(CameraActivity.this, "Stop", Toast.LENGTH_SHORT).show();
                isRecording = true;
            } else {
                releaseMediaRecorder();
            }
        }
    }

    private void timerVideo() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {

                    NumberFormat f = new DecimalFormat("00");
                    long hour = 0;
                    long min = 0;
                    long sec = count;
                    if (sec == 60) {
                        sec = 0;
                    }
                    if (count == 60) {
                        min++;
                        count = 0;
                    }
                    if (min == 60) {
                        hour++;
                        min = 0;
                    }
                    tvTimer.setText(String.format("%s:%s:%s", f.format(hour), f.format(min), f.format(sec)));
                    // tvTimer.setText("count="+count);
                    count++;
                });
            }
        }, 1000, 1000);

    }

    public void setPicture(String picturePath) {

        for (int i = 0; i < answers.size(); i++) {
            String url = answers.get(i).getAnswer();
            photos.get(i).setUrl(url);
        }

        int currentIndex = answers.size();
        if (currentIndex >= photos.size()) {
            currentIndex = photos.size() - 1;
        }

        Photo photoTaken = photos.get(currentIndex);

        Answer answer = dbHandler.getPhoto(submissionID, String.valueOf(formItem.getPhotoId()),
                repeatCount, photos.get(currentIndex).getTitle());

        if (answer == null) {
            answer = new Answer(submissionID, String.valueOf(formItem.getPhotoId()), 1);
        }

        answer.setAnswer(picturePath);
        Date date = new Date();
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String timeTaken = input.format(date);
        answer.setTakenDateTime(timeTaken);
        answer.setRepeatCount(repeatCount);
        answer.setDisplayAnswer(photoTaken.getTitle());
        photoTaken.setUrl(picturePath);
        if (answers.size() < photos.size()) {
            this.answers.add(answer);
        } else {
            this.answers.set(photos.size() - 1, answer);
        }
        answer.setLatitude(latitude);
        answer.setLongitude(longitude);
        dbHandler.replaceData(Answer.DBTable.NAME, answer.toContentValues());
        lastPhotoPath = picturePath;

        RequestOptions myOptions = new RequestOptions()
                .fitCenter()
                .override(600, 600);

        Glide.with(this)
                .load(new File(picturePath))
                .apply(myOptions)
                .into(target);
    }

    public void setVideo(String picturePath) {


        Photo photoTaken = photos.get(0);

        for (int i = 0; i < photos.size(); i++) {
            String url = photos.get(i).getUrl();
            if (url == null || url.isEmpty()) {
                photoTaken = photos.get(i);
                break;
            }
        }

        Answer answer = dbHandler.getAnswer(submissionID, String.valueOf(photoTaken.getPhoto_id()),
                repeatCount, photoTaken.getTitle());

        if (answer == null) {
            answer = new Answer(submissionID, String.valueOf(photoTaken.getPhoto_id()), 2);
        }

        answer.setAnswer(picturePath);
        answer.setRepeatCount(repeatCount);
        answer.setDisplayAnswer(photoTaken.getTitle());
        photoTaken.setUrl(picturePath);
        answer.setLatitude(latitude);
        answer.setLongitude(longitude);
        photoTaken.setIsVideo(true);

        long id = dbHandler.replaceData(Answer.DBTable.NAME, answer.toContentValues());
        lastPhotoPath = picturePath;
        adapter.notifyDataSetChanged();
    }



    @Override
    public void onResume() {
        super.onResume();
        checkCameraPermission(false);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (camera != null) {
            releaseCameraAndPreview();
            releaseMediaRecorder();
            releaseCamera();
        }
        if (frameLayoutCamera != null) {
            frameLayoutCamera.removeView(cameraView);
            cameraView = null;
        }
    }

    private void startCameraPreview(boolean shouldCheckPermission) {
        checkLocation(shouldCheckPermission);
        btnCancel.setVisibility(View.GONE);
        CameraOpenTask camOpen = new CameraOpenTask();
        camOpen.execute();
    }

    private Camera safeCameraOpen() {
        Camera camera;
        try {
            if (this.camera != null)
                camera = this.camera;
            else
                camera = Camera.open();
            if (camera == null) {
                return Camera.open(0);
            } else {
                return camera;
            }

        } catch (Exception e) {
            Log.e(BACK_STACK_TAG, "Failed to open camera: " + e.getMessage());
        }

        return null;
    }

    private void releaseCameraAndPreview() {
        try {
            camera.stopPreview();
            camera.setPreviewCallback(null);
            cameraView.getHolder().removeCallback(cameraView);
            camera.release();
            camera = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void takePicture() {
        if (!isVideoModeOn) {
            if (camera != null) {
                try {
                    btnTakePic.setVisibility(View.GONE);
                    btnGallery.setVisibility(View.GONE);
                    btnVideo.setVisibility(View.GONE);
                    camera.takePicture(null, null, null, pictureCallback);
                    btnTakePic.setImageResource(R.drawable.btn_drawable_camer);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        } else {
            btnTakePic.setImageResource(R.drawable.btn_drawable_video_press);
            try {
                startVideoCapturing();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    private void changeOrientation(byte[] data, String fileUrl) {

        int orientation = cameraView.getOrientation();
        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
//        Bitmap bmp = BitmapFactory.decodeFile(fileUrl);
//        Bitmap bmp = BitmapFactory.decodeFile(photopath);

        Matrix matrix = new Matrix();
        if (orientation == 90) {
            matrix.postRotate(90);
        } else if (orientation == 180) {
            matrix.postRotate(180);
        } else if (orientation == 270) {
            matrix.postRotate(270);
        } else {
            matrix.postRotate(0);
        }

        bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);

        FileOutputStream fOut;
        try {
            fOut = new FileOutputStream(new File(fileUrl));
            bmp.compress(Bitmap.CompressFormat.JPEG, 65, fOut);
            fOut.flush();
            fOut.close();

        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkGalleryPermission(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        }else{
            String permissionRationale = getString(R.string.location_default_permission_rationale);
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    permissionRationale , MY_PERMISSIONS_REQUEST_GALLERY);
//            requestPermissions(
//                    new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                    MY_PERMISSIONS_REQUEST_GALLERY);
        }
    }

    private void checkLocation(boolean shouldCheckPermission){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(this , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            String permissionRationale = getString(R.string.location_default_permission_rationale);
            if(shouldCheckPermission) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        permissionRationale, MY_PERMISSIONS_LOCATION);
            }
            return;
        }

        if(latitude == -1000 || longitude == -1000) {
            bindLocation();
        }
    }

    private void checkCameraPermission(boolean shouldRequestPermission){
        isPermissionSettingRequested = false;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) && ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startCameraPreview(shouldRequestPermission);
        }else{
            if(shouldRequestPermission) {
                String permissionRationale = getString(R.string.location_default_permission_rationale);
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        permissionRationale, MY_PERMISSIONS_REQUEST_CAMERA);
            }
        }
    }

    public void checkVideoPermissions(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) && ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
            startVideo();
        }else{
            String permissionRationale = getString(R.string.location_default_permission_rationale);
            requestPermissions(new String[]{Manifest.permission.CAMERA , Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
                    permissionRationale , MY_PERMISSIONS_REQUEST_CAMERA);
        }
    }

    private void startVideo(){
        isVideoModeOn = !isVideoModeOn;
        btnVideo.setImageResource(isVideoModeOn ? R.drawable.ic_baseline_videocam :
                R.drawable.ic_baseline_camera_alt);
    }

    private void openGallery() {
        btnTakePic.setVisibility(View.GONE);
        btnGallery.setVisibility(View.GONE);
        btnVideo.setVisibility(View.GONE);
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_GALLERY:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery();
                }else{
                    showPermissionDialog();
                }
                break;
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                boolean isPermissionGranted = true;
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    isPermissionGranted = false;
                }

                if (grantResults.length > 1
                        && grantResults[1] == PackageManager.PERMISSION_DENIED) {
                    isPermissionGranted = false;
                }

                if (isPermissionGranted) {
                    startCameraPreview(true);
                }else{
                    showPermissionDialog();
                }
                break;
            }
            case MY_PERMISSIONS_REQUEST_VIDEO: {
                boolean isPermissionGranted = true;
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    isPermissionGranted = false;
                }

                if (grantResults.length > 1
                        && grantResults[1] == PackageManager.PERMISSION_DENIED) {
                    isPermissionGranted = false;
                }

                if (grantResults.length > 2
                        && grantResults[2] == PackageManager.PERMISSION_DENIED) {
                    isPermissionGranted = false;
                }

                if (isPermissionGranted) {
                    startVideo();
                }else{
                    showPermissionDialog();
                }
                break;
            }
            case MY_PERMISSIONS_LOCATION:
                if(grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        bindLocation();
                    } else {
                        showPermissionDialog();
                    }
                }
                break;

        }
    }


    private void showPermissionDialog(){
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(CameraActivity.this, R.style.DialogTheme)
                .setTitle(getString(R.string.permission_denied))
                .setMessage(getString(R.string.permissions_location_failure))
                .setPositiveButton(getString(R.string.permissions_settings), (dialogInterface, i) -> {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", (CameraActivity.this).getApplication().getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent , PERMISSION_ACTIVITY_REQUEST);
                    dialogInterface.dismiss();
                    finish();
                })
                .setNegativeButton(getString(R.string.generic_cancel), (dialogInterface, i) -> {
//                    isLocationPermissionFirstTime = false;
                    isPermissionSettingRequested = true;
                    dialogInterface.dismiss();
                    finish();
                });
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        btnGallery.setVisibility(View.VISIBLE);
        btnTakePic.setVisibility(View.VISIBLE);
        btnVideo.setVisibility(View.VISIBLE);
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE_REQUEST) {
            if (data.getClipData() != null) {
                ClipData mClipData = data.getClipData();
                int cout = mClipData.getItemCount();
                for (int i = 0; i < cout; i++) {
                    // adding imageuri in array
                    Uri uri = data.getClipData().getItemAt(i).getUri();
                    if (uri != null) {
                        String path = PathUtil.getPath(this, uri);
                        setPicture(path);
                    }
                }
            } else {
                Uri uri = data.getData();
                if (uri != null) {
                    String path = PathUtil.getPath(this, uri);
                    setPicture(path);
                }
            }

        } else if (resultCode == Activity.RESULT_OK && requestCode == PICK_MODIFY_IMAGE) {
            Answer photo = data.getParcelableExtra("photo");
            int position = data.getIntExtra("position", 0);
            answers.set(position, photo);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    public void requestPermissions(@NonNull final String[] permissions, String permissionRequestRationale , int requestCode) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        ArrayList<String> failedPermissions = new ArrayList<>();

//        boolean shouldShowRationale = false;
        for (String permission : permissions) {
            int checkResult = ContextCompat.checkSelfPermission(CameraActivity.this, permission);
            if (checkResult != PackageManager.PERMISSION_GRANTED) {
                failedPermissions.add(permission);
                break;
            }
        }

        if (!failedPermissions.isEmpty()) {
            String fP[] = new String[failedPermissions.size()];
            fP = failedPermissions.toArray(fP);
            requestPermissions(fP, requestCode);

        } else {
            int[] permissionGrant = new int[permissions.length];
            for (int i = 0; i < permissions.length; i++) {
                permissionGrant[i] = PackageManager.PERMISSION_GRANTED;
            }
            onRequestPermissionsResult(requestCode, permissions, permissionGrant);
        }

    }


    @Override
    public void getLocation(final LocationListener listener) {

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M || ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            mFusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            listener.onSuccess(location);
                        } else {
                            createLocationRequest(listener);
                            listener.onFailure();
                        }
                    })
                    .addOnFailureListener(e -> {
                        createLocationRequest(listener);
                        e.printStackTrace();
                        listener.onFailure();
                    });
        }
    }


    private void createLocationRequest(LocationListener listener) {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);


        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener((Activity) this, locationSettingsResponse -> {
            startLocationUpdates(listener);
        });

        task.addOnFailureListener((Activity) this, e -> {
            if (e instanceof ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult((Activity) this,
                            11);
                } catch (IntentSender.SendIntentException sendEx) {
                    // Ignore the error.
                }
            }
        });
    }

    private void startLocationUpdates(LocationListener listener) {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.requestLocationUpdates(locationRequest,
                new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        if (locationResult == null) {
                            return;
                        }
                        for (Location location : locationResult.getLocations()) {
                            if (location != null) {
                                listener.onSuccess(location);
                                mFusedLocationClient.removeLocationUpdates(this);
                                break;
                            }
                        }
                    }
                },
                Looper.getMainLooper());
    }

    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.reset();   // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = null;
            camera.lock();// lock camera for later use

        }
    }

    private void releaseCamera() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    private boolean prepareVideoRecorder() {

        camera = getCameraInstance();
        setCameraDisplayOrientation();
        mediaRecorder = new MediaRecorder();
        camera.unlock();
        mediaRecorder.setCamera(camera);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
        mediaRecorder.setOrientationHint(90);

        File filePath = null;
        try {
            filePath = Utils.createVideoFile(this);
        }catch (Exception e){

        }


        if (filePath == null) {
            return false;
        }
        fileVideoPath = filePath.getAbsolutePath();
        mediaRecorder.setOutputFile(filePath.getAbsolutePath());
        mediaRecorder.setPreviewDisplay(cameraView.getHolder().getSurface());
        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d("*****", "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d("*****", "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    private void setCameraDisplayOrientation() {
        int cameraId = -1;
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                break;
            }
        }
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }

        camera.setDisplayOrientation(result);
    }

    private class CameraOpenTask extends AsyncTask<Void, Void, Camera> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Camera doInBackground(Void... params) {
            return safeCameraOpen();
        }

        @Override
        protected void onPostExecute(Camera camera) {
            progressBar.setVisibility(View.GONE);
            CameraActivity.this.camera = camera;
            if (CameraActivity.this.camera != null) {
                cameraView = new CameraView(CameraActivity.this, camera);
                frameLayoutCamera = findViewById(R.id.camera_preview);
                frameLayoutCamera.addView(cameraView);
            }
            btnTakePic.setVisibility(View.VISIBLE);
            btnGallery.setVisibility(View.VISIBLE);
            if (!photos.get(0).getTitle().equalsIgnoreCase("Take Photo or Video"))
                btnVideo.setVisibility(View.GONE);
            else
                btnVideo.setVisibility(View.VISIBLE);
        }
    }

}
