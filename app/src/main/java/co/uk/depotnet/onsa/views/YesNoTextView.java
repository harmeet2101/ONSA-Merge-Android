package co.uk.depotnet.onsa.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import androidx.annotation.ColorInt;
import androidx.annotation.Dimension;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;

import co.uk.depotnet.onsa.R;

public class YesNoTextView extends AppCompatTextView {

    @ColorInt
    private int selectedBackgroundColor;
    @ColorInt
    private int unSelectedBackgroundColor;
    @ColorInt
    private int unSelectedTextColor;
    @ColorInt
    private int selectedTextColor;
    @Dimension
    private int strokeWidth;


    public YesNoTextView(Context context) {
        super(context);
    }

    public YesNoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        style(context, attrs);
    }

    public YesNoTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        style(context, attrs);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        changeBackground(selected);
    }

    private void changeBackground(boolean selected) {
        GradientDrawable drawable = (GradientDrawable) getBackground();

        if (selected) {
            setTextColor(selectedTextColor);
            drawable.setStroke(strokeWidth, selectedBackgroundColor);
            drawable.setColor(selectedBackgroundColor);

        } else {
            drawable.setStroke(strokeWidth, selectedBackgroundColor);
            drawable.setColor(unSelectedBackgroundColor);
            setTextColor(unSelectedTextColor);
        }

    }


    private void style(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.YesNoTextView);
        selectedBackgroundColor = a.getColor(R.styleable.YesNoTextView_selected_background_color ,
                ContextCompat.getColor(context ,R.color.depotnet_color));
        unSelectedBackgroundColor = a.getColor(R.styleable.YesNoTextView_unselected_background_color ,
                ContextCompat.getColor(context ,R.color.depotnet_color));
        selectedTextColor = a.getColor(R.styleable.YesNoTextView_selected_txt_color,
                ContextCompat.getColor(context ,R.color.txt_color_light_grey));
        unSelectedTextColor = a.getColor(R.styleable.YesNoTextView_unselected_txt_color ,
                ContextCompat.getColor(context ,R.color.txt_color_light_grey));
        strokeWidth = a.getDimensionPixelSize(R.styleable.YesNoTextView_stroke_size , 2);
        a.recycle();

        setSelected(isSelected());
    }


}
