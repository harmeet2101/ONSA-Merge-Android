package co.uk.depotnet.onsa.views;

import android.graphics.Path;
import android.graphics.RectF;

public class FingerPath {

    public float startLineX;
    public float startLineY;
    public float endLineX;
    public float endLineY;
    public int mode;
    public int color;
    public boolean emboss;
    public boolean blur;
    public int strokeWidth;
    public Path path;
    public RectF rect;
    private String text;

    public FingerPath(int mode, int color, boolean emboss, boolean blur, int strokeWidth, Path path) {
        this.mode = mode;
        this.color = color;
        this.emboss = emboss;
        this.blur = blur;
        this.strokeWidth = strokeWidth;
        this.path = path;
    }

    public FingerPath(int mode, int color, float startLineX, float startLineY,
                      float endLineX, float endLineY, int strokeWidth) {
        this.mode = mode;
        this.color = color;
        this.startLineX = startLineX;
        this.startLineY = startLineY;
        this.endLineX = endLineX;
        this.endLineY = endLineY;
        this.strokeWidth = strokeWidth;
    }

    public FingerPath(int mode, int color, float startLineX, float startLineY,
                      float endLineX, float endLineY, int strokeWidth, Path path) {
        this.mode = mode;
        this.color = color;
        this.startLineX = startLineX;
        this.startLineY = startLineY;
        this.endLineX = endLineX;
        this.endLineY = endLineY;
        this.strokeWidth = strokeWidth;
        this.path = path;
    }

    public FingerPath(int mode, int color, RectF rect, int strokeWidth) {
        this.mode = mode;
        this.color = color;
        this.rect = rect;
        this.strokeWidth = strokeWidth;
    }

    public FingerPath(int mode, String text, int color, RectF rect, int strokeWidth) {
        this.mode = mode;
        this.color = color;
        this.rect = rect;
        this.strokeWidth = strokeWidth;
        this.text = text;
    }


    public void setEndLineX(float endLineX) {
        this.endLineX = endLineX;
    }

    public void setEndLineY(float endLineY) {
        this.endLineY = endLineY;
    }

    public RectF getRect() {
        return rect;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isSelected(float x, float y) {
        if (mode == AnnotationView.MODE_PEN) {
            return false;
        }

        if (mode == AnnotationView.MODE_LINE || mode == AnnotationView.MODE_ARROW) {
            int distanceAB = distance(startLineX , startLineY , endLineX , endLineY);
            int distanceBC = distance(x , y , endLineX , endLineY);
            int distanceAC = distance(startLineX , startLineY , x , y);


            return distanceAB == distanceAC+distanceBC;
        }

        boolean isSelected = false;
        if(rect.isEmpty()){
            if(rect.left >= rect.right) {
               isSelected &= x > rect.right && x < rect.left;
            }else{
                isSelected &= x > rect.left && x < rect.right;
            }

            if(rect.top >= rect.bottom) {
                isSelected &= y > rect.bottom && y < rect.top;
            }else{
                isSelected &= y > rect.top && y < rect.bottom;
            }
            return isSelected;

        }

//        return (x > rect.left && x < rect.right &&
//                y > rect.top && y < rect.bottom);

        return rect.contains(x , y);
    }

    public void move(float dx, float dy) {
        if (mode == AnnotationView.MODE_PEN) {
            return;
        }

        if (mode == AnnotationView.MODE_LINE || mode == AnnotationView.MODE_ARROW) {
            startLineX+= dx;
            endLineX+= dx;
            startLineY += dy;
            endLineY += dy;

            return;
        }



        rect.left += dx;
        rect.right += dx;
        rect.top += dy;
        rect.bottom += dy;

    }

    private int distance(float x1 , float y1 , float x2 , float y2){
        return  (int)Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
    }
}