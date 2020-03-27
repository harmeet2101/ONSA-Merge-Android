package co.uk.depotnet.onsa.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import co.uk.depotnet.onsa.modals.forms.Photo;


public class AnnotationView extends View implements TextWatcher {

    public final static int MODE_PEN = 1;
    public final static int MODE_TEXT = 2;
    public final static int MODE_ECLIPSE = 3;
    public final static int MODE_LINE = 4;
    public final static int MODE_SQUARE = 5;
    public final static int MODE_ARROW = 6;
    public final static int MODE_SELECTION = 7;


    public static final int DEFAULT_BG_COLOR = Color.WHITE;
    private static final float TOUCH_TOLERANCE = 4;
    private int brushSize;
    private int painColor;
    private int mode;


    private float mX, mY;
    private Path mPath;
    private Paint mPaint;
    private Paint mLinePaint;
    private ArrayList<FingerPath> paths = new ArrayList<>();
    private ArrayList<FingerPath> backPaths = new ArrayList<>();
    private int currentColor;
    private int backgroundColor = DEFAULT_BG_COLOR;
    private int strokeWidth;
    private boolean emboss;
    private boolean blur;
    private MaskFilter mEmboss;
    private MaskFilter mBlur;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mBitmapPaint = new Paint(Paint.DITHER_FLAG);
    private FingerPath fp;
    private Bitmap bitmap;
    private Context context;

    //    private InputMethodManager imm;
    private boolean isTextDrawing;
    private OnTextListener onTextListener;

    public AnnotationView(Context context, AttributeSet attrs) {
        super(context, attrs);

        brushSize = 8;
        painColor = Color.BLACK;
        // mode = MODE_TEXT;
        mode = MODE_PEN;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(painColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setXfermode(null);
        mPaint.setAlpha(0xff);

        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setDither(true);
        mLinePaint.setColor(painColor);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(4);
        mLinePaint.setStrokeJoin(Paint.Join.ROUND);
        mLinePaint.setStrokeCap(Paint.Cap.ROUND);
        mLinePaint.setXfermode(null);
        mLinePaint.setAlpha(0xff);

        mEmboss = new EmbossMaskFilter(new float[]{1, 1, 1}, 0.4f, 6, 3.5f);
        mBlur = new BlurMaskFilter(5, BlurMaskFilter.Blur.NORMAL);

//        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);


    }

    public void setImageUrl(String url) {
        File imgFile = new File(url);
        if (imgFile.exists()) {
            bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        }

        if (bitmap != null) {
            bitmap = getScaledBitMapBaseOnScreenSize(bitmap);
        }


    }

    public void init(DisplayMetrics metrics) {
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

        currentColor = painColor;
        strokeWidth = brushSize;
    }

    public void normal() {
        emboss = false;
        blur = false;
    }

    public void emboss() {
        emboss = true;
        blur = false;
    }

    public void blur() {
        emboss = false;
        blur = true;
    }

    public void clear() {
        backgroundColor = DEFAULT_BG_COLOR;
        paths.clear();
        normal();
        invalidate();
    }

    private Bitmap getScaledBitMapBaseOnScreenSize(Bitmap bitmapOriginal) {

        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int width = bitmapOriginal.getWidth();
        int height = bitmapOriginal.getHeight();

        int scaleWidth = metrics.widthPixels;
        int scaleHeight = metrics.heightPixels;

        if (width >= scaleWidth || height >= scaleHeight) {
            return bitmapOriginal;
        }


        try {
            // create a matrix for the manipulation
//            Matrix matrix = new Matrix();
//            // resize the bit map
//            matrix.postScale(scaleWidth, scaleHeight);

            return Bitmap.createScaledBitmap(bitmapOriginal, scaleWidth, scaleHeight, false);

            // recreate the new Bitmap
//            return Bitmap.createBitmap(bitmapOriginal, 0, 0, width, height, matrix, true);
        } catch (Exception e) {
            e.printStackTrace();
            return bitmapOriginal;
        }
    }

    public void saveSignature(Photo photo) {

        Bitmap bitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        this.draw(canvas);

        File file = new File(photo.getUrl());

        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        mCanvas.drawColor(backgroundColor);

        mCanvas.drawBitmap(bitmap, getMatrix(), mPaint);
        for (FingerPath fp : paths) {
            mPaint.setColor(fp.color);
            mPaint.setStrokeWidth(fp.strokeWidth);
            mPaint.setMaskFilter(null);
            mPaint.setStyle(Paint.Style.STROKE);
            if (fp.mode == MODE_PEN) {
                if (fp.emboss)
                    mPaint.setMaskFilter(mEmboss);
                else if (fp.blur)
                    mPaint.setMaskFilter(mBlur);

                mCanvas.drawPath(fp.path, mPaint);
            } else if (fp.mode == MODE_LINE) {
                mCanvas.drawLine(fp.startLineX, fp.startLineY, fp.endLineX, fp.endLineY, mPaint);
            } else if (fp.mode == MODE_SQUARE) {
                mCanvas.drawRect(fp.getRect(), mPaint);

            } else if (fp.mode == MODE_ECLIPSE) {
                mCanvas.drawOval(fp.getRect(), mPaint);

            } else if (fp.mode == MODE_ARROW) {

//                mCanvas.drawLine(fp.startLineX, fp.startLineY, fp.endLineX,
//                        fp.endLineY, mPaint);
                mPaint.setStyle(Paint.Style.FILL);
                mCanvas.drawPath(fp.path, mPaint);
            } else if (fp.mode == MODE_TEXT) {
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setTextSize(80);
                mCanvas.drawText(fp.getText(), fp.getRect().left + 10,
                        fp.getRect().bottom - 10, mPaint);
            }


        }


        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.restore();
    }

    private void touchStart(float x, float y) {
        switch (mode) {
            case MODE_PEN:
                touchStartPen(x, y);
                break;
            case MODE_TEXT:
                isTextDrawing = !isTextDrawing;
                onTextListener.onTextModeEnabled(isTextDrawing);
                if (isTextDrawing) {
                    String text = "tes";
                    Rect bounds = new Rect();
                    mPaint.setStyle(Paint.Style.FILL);
                    mPaint.setTextSize(80);
                    mPaint.getTextBounds(text, 0, text.length(), bounds);
                    int height = bounds.bottom - bounds.top;
                    int width = bounds.right - bounds.left;

                    RectF rectText = new RectF(x, y, x + width, y + height);

                    fp = new FingerPath(mode, "", currentColor, rectText, strokeWidth);
                    paths.add(fp);

                } else {
                    fp = null;
                }

                break;
            case MODE_ECLIPSE:
                RectF oval = new RectF(x, y, x, y);
                fp = new FingerPath(mode, currentColor, oval, strokeWidth);
                paths.add(fp);
                break;
            case MODE_LINE:
                mX = x;
                mY = y;
                fp = new FingerPath(mode, currentColor, x, y, x, y, strokeWidth);
                paths.add(fp);
                break;
            case MODE_SQUARE:
                RectF rect = new RectF(x, y, x, y);
                fp = new FingerPath(mode, currentColor, rect, strokeWidth);
                paths.add(fp);
                break;
            case MODE_ARROW:
                mPath = new Path();
                mX = x;
                mY = y;
                fp = new FingerPath(mode, currentColor, x, y, x, y, strokeWidth, mPath);
                paths.add(fp);
                break;
            case MODE_SELECTION:
                mPath = new Path();
                mX = x;
                mY = y;

                for (int i = paths.size() - 1; i >= 0; i--) {
                    FingerPath path = paths.get(i);
                    if (path.isSelected(x, y)) {
                        fp = path;
                        break;
                    }
                }
                break;
        }


        mX = x;
        mY = y;
    }

    private void touchMove(float x, float y) {


        switch (mode) {
            case MODE_PEN:
                touchMovePen(x, y);
                break;
            case MODE_TEXT:
                break;
            case MODE_ECLIPSE:
                mX = x;
                mY = y;
                if (fp != null) {
                    fp.getRect().right = mX;
                    fp.getRect().bottom = mY;

                }
                break;
            case MODE_LINE:
                mX = x;
                mY = y;
                if (fp != null) {
                    fp.setEndLineX(mX);
                    fp.setEndLineY(mY);
                }
                break;
            case MODE_SQUARE:
                mX = x;
                mY = y;
                if (fp != null) {
                    fp.getRect().right = mX;
                    fp.getRect().bottom = mY;
                }
                break;
            case MODE_ARROW:
                mX = x;
                mY = y;

                if (fp != null) {
                    mPath.reset();

                    fp.setEndLineX(mX);
                    fp.setEndLineY(mY);
                    drawArrow(mPath, fp.startLineX, fp.startLineY, fp.endLineX, fp.endLineY);


                }
                break;

            case MODE_SELECTION:
                if (fp != null) {
                    float dx = Math.abs(x - mX);
                    float dy = Math.abs(y - mY);

                    if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                        fp.move(x - mX, y - mY);
                        mX = x;
                        mY = y;
                        invalidate();
                    }

                }
                break;
        }

    }

    private void touchUp() {
        switch (mode) {
            case MODE_PEN:
                touchUpPen();
                break;
            case MODE_TEXT:
                break;
            case MODE_ECLIPSE:
                if (fp != null) {
                    fp.getRect().right = mX;
                    fp.getRect().bottom = mY;

                }
                break;
            case MODE_LINE:
                if (fp != null) {
                    fp.setEndLineX(mX);
                    fp.setEndLineY(mY);
                }
                break;
            case MODE_SQUARE:
                if (fp != null) {
                    fp.getRect().right = mX;
                    fp.getRect().bottom = mY;

                }
                break;
            case MODE_ARROW:
                mPath.reset();
                fp.endLineX = mX;
                fp.endLineY = mY;
                drawArrow(mPath, fp.startLineX, fp.startLineY, fp.endLineX, fp.endLineY);

                break;
            case MODE_SELECTION:
                if (fp != null) {
                    fp = null;

                }
                break;
        }
    }

    private void touchStartPen(float x, float y) {

        mPath = new Path();
        FingerPath fp = new FingerPath(mode, currentColor, emboss, blur, strokeWidth, mPath);
        paths.add(fp);

        mPath.reset();
        mPath.moveTo(x, y);

    }

    private void touchMovePen(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);

        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void touchUpPen() {
        mPath.lineTo(mX, mY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touchUp();
                invalidate();
                break;
        }

        return true;
    }

    public void setBrushSize(int brushSize) {
        this.brushSize = brushSize;
        this.strokeWidth = brushSize;
    }

    public void setPainColor(int painColor) {
        this.painColor = painColor;
        currentColor = painColor;
        mPaint.setColor(painColor);
        mLinePaint.setColor(painColor);
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void goBack() {
        if (paths.isEmpty()) {
            return;
        }

        backPaths.add(paths.remove(paths.size() - 1));
        invalidate();
    }

    public void goForward() {
        if (backPaths.isEmpty()) {
            return;
        }
        paths.add(backPaths.remove(backPaths.size() - 1));
        invalidate();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (fp != null && isTextDrawing && mode == MODE_TEXT) {
            fp.setText(s.toString());
            Rect bounds = new Rect();
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setTextSize(80);
            mPaint.getTextBounds(fp.getText(), 0, fp.getText().length(), bounds);
            int height = bounds.bottom - bounds.top;
            int width = bounds.right - bounds.left;

            fp.rect.right = fp.rect.left + width;
            fp.rect.bottom = fp.rect.top + height;


            invalidate();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public void setOnTextListener(OnTextListener onTextListener) {
        this.onTextListener = onTextListener;
    }

    public void drawArrow(Path path, float fromx, float fromy, float tox, float toy) {
        int headlen = 10; // length of head in pixels
        float dx = tox - fromx;
        float dy = toy - fromy;
        float angle = (float) Math.atan2(dy, dx);
        path.moveTo(fromx, fromy);
        path.lineTo(tox, toy);
        path.lineTo((float) (tox - headlen * Math.cos(angle - Math.PI / 7)), (float) (toy - headlen * Math.sin(angle - Math.PI / 7)));

        path.lineTo((float) (tox - headlen * Math.cos(angle + Math.PI / 7)), (float) (toy - headlen * Math.sin(angle + Math.PI / 7)));
        path.lineTo(tox, toy);
        path.lineTo((float) (tox - headlen * Math.cos(angle - Math.PI / 7)), (float) (toy - headlen * Math.sin(angle - Math.PI / 7)));
    }


    public interface OnTextListener {
        void onTextModeEnabled(boolean enabled);
    }


}
