package co.uk.depotnet.onsa.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;


public class CameraView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = CameraView.class.getName();

    private SurfaceHolder holder;
    private Camera camera;
    private Context context;

    public CameraView(Context context, Camera camera) {
        super(context);

        this.context = context;
        this.camera = camera;
        //get the holder and set this class as the callback, so we can get camera data here
        holder = getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
    }

    private void setCameraDisplayOrientation() {
        camera.setDisplayOrientation(getOrientation());
    }

    public int getOrientation(){
        if(camera == null){
            return 0;
        }
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(0, info);

        int rotation = ((Activity) context).getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch(rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        return (info.orientation - degrees + 360) % 360;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try{
            Camera.Parameters parameters = camera.getParameters();
            parameters.set("orientation","portrait");
            if(parameters.getSupportedFocusModes()
                    .contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }
//            parameters.set("jpeg-quality", 70);
//            parameters.setPictureFormat(PixelFormat.RGB_565);
//            parameters.setPictureSize(640, 480);
            camera.setParameters(parameters);
            setCameraDisplayOrientation();

            //when the surface is created, we can set the camera to draw images in this surfaceholder
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (IOException e) {
            Log.e(TAG, "Camera error on surfaceCreated " + e.getMessage());
        } catch (Exception e){
            Log.d(TAG, "Failed to get camera: " + e.getMessage());
            return;
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        //before changing the application orientation,
        // you need to stop the preview, rotate and then start it again
        if(holder.getSurface() == null || camera == null) {
            Log.i(TAG, "Camera or Surtface is null");
            return;
        }

        try{
            camera.stopPreview();
        } catch (Exception e){
            //this will happen when you are trying the camera if it's not running
            Log.e(TAG, "Camera is not running " + e.getMessage());
        }

        //now, recreate the camera preview
        try{
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (Exception e) {
            Log.e(TAG, "Camera error on surfaceChanged " + e.getMessage());
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        //TODO: Maybe need to release camera in here instead?
    }
}