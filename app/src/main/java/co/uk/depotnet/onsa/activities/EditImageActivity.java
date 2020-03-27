package co.uk.depotnet.onsa.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.listeners.ColorListener;
import co.uk.depotnet.onsa.modals.Color;
import co.uk.depotnet.onsa.modals.forms.Photo;
import co.uk.depotnet.onsa.views.AnnotationView;
import co.uk.depotnet.onsa.views.FilterBottomSheet;

public class EditImageActivity extends AppCompatActivity implements View.OnClickListener,
        FilterBottomSheet.FilterListener, ColorListener {

    private LinearLayout bottomBar, llAddColorSize, llPickColor, llPointSize, llBtnPen, llBtnText, llBtnSelect, llBtnEclipse,
            llBtnLine, llBtnSquare, llBtnArrow;
    private ImageView imgClose, imgBack, imgRemoveChange, imgAddChange, colorImage;
    private TextView textPointSize, txtSave;
    private int pointSizeCount = 2;
    private AnnotationView annotationView;
    private ArrayList<Color> colors;
    private List<Integer> filterColor;
    private EditText editText;
//    private Bitmap bitmap;
    private Photo photo;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);
        editText = findViewById(R.id.editText);
         photo=getIntent().getParcelableExtra("photos");
         position=getIntent().getIntExtra("POSITION",0);

        init();
        addColor();
        filterColor = new ArrayList<>();
    }


    private void init() {
        bottomBar = findViewById(R.id.bottom_bar);
        llAddColorSize = findViewById(R.id.ll_add_color_size);
        llPickColor = findViewById(R.id.ll_pick_color);
        llPointSize = findViewById(R.id.ll_point_size);


        llBtnPen = findViewById(R.id.ll_btn_pen);
        llBtnText = findViewById(R.id.ll_btn_text);
        llBtnSelect = findViewById(R.id.ll_btn_select);
        llBtnEclipse = findViewById(R.id.ll_btn_eclipse);
        llBtnLine = findViewById(R.id.ll_btn_line);
        llBtnSquare = findViewById(R.id.ll_btn_square);
        llBtnArrow = findViewById(R.id.ll_btn_arrow);

        imgClose = findViewById(R.id.img_close);
        imgBack = findViewById(R.id.btn_img_back);
        imgRemoveChange = findViewById(R.id.btn_img_remove_change);
        imgAddChange = findViewById(R.id.btn_img_add_change);
        colorImage = findViewById(R.id.colorImage);

        textPointSize = findViewById(R.id.text_Size);
        textPointSize.setText(String.valueOf(pointSizeCount));
        txtSave = findViewById(R.id.btn_save);

        llBtnPen.setOnClickListener(this);
        llBtnText.setOnClickListener(this);
        llBtnSelect.setOnClickListener(this);
        llBtnEclipse.setOnClickListener(this);
        llBtnLine.setOnClickListener(this);
        llBtnSquare.setOnClickListener(this);
        llBtnArrow.setOnClickListener(this);
        llPointSize.setOnClickListener(this);
        llPickColor.setOnClickListener(this);

        imgClose.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        imgRemoveChange.setOnClickListener(this);
        imgAddChange.setOnClickListener(this);
        txtSave.setOnClickListener(this);


        annotationView = findViewById(R.id.paintView);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        annotationView.init(metrics);

        annotationView.setImageUrl(photo.getUrl());
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);

        editText.addTextChangedListener(annotationView);

        annotationView.setOnTextListener(new AnnotationView.OnTextListener() {
            @Override
            public void onTextModeEnabled(boolean enabled) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                editText.setText("");
                if (enabled) {
                    editText.setVisibility(View.VISIBLE);
                    editText.requestFocus();
                    inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                } else {
                    editText.setVisibility(View.GONE);
                    editText.clearFocus();
                    inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                }
            }
        });


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_btn_pen:
                llAddColorSize.setVisibility(View.VISIBLE);
                annotationView.setMode(AnnotationView.MODE_PEN);
                break;
            case R.id.ll_btn_text:
                llAddColorSize.setVisibility(View.VISIBLE);
                annotationView.setMode(AnnotationView.MODE_TEXT);
                break;
            case R.id.ll_btn_select:
                llAddColorSize.setVisibility(View.GONE);
                annotationView.setMode(AnnotationView.MODE_SELECTION);
                break;
            case R.id.ll_btn_eclipse:
                llAddColorSize.setVisibility(View.VISIBLE);
                annotationView.setMode(AnnotationView.MODE_ECLIPSE);
                break;
            case R.id.ll_btn_line:
                llAddColorSize.setVisibility(View.VISIBLE);
                annotationView.setMode(AnnotationView.MODE_LINE);
                break;
            case R.id.ll_btn_square:
                llAddColorSize.setVisibility(View.VISIBLE);
                annotationView.setMode(AnnotationView.MODE_SQUARE);
                break;
            case R.id.ll_btn_arrow:
                llAddColorSize.setVisibility(View.VISIBLE);
                annotationView.setMode(AnnotationView.MODE_ARROW);
                break;
            case R.id.img_close:
                llAddColorSize.setVisibility(View.GONE);
                break;
            case R.id.ll_point_size:
                pointSizeCount = pointSizeCount + 2;
                textPointSize.setText(String.valueOf(pointSizeCount));
                annotationView.setBrushSize(pointSizeCount);
                break;
            case R.id.ll_pick_color:
                getColor();
                break;
            case R.id.btn_img_back:
                finish();
                break;
            case R.id.btn_img_remove_change:
                annotationView.goBack();
                break;
            case R.id.btn_img_add_change:
                annotationView.goForward();
                break;
            case R.id.btn_save:
                annotationView.saveSignature(photo);
                Intent intent=new Intent();
                setResult(RESULT_OK,intent);
                finish();
                break;


        }

    }

    private void getColor() {
        if (colors != null && !colors.isEmpty()) {
            for (Color c : colors) {
                c.setSelected(false);
                for (int fc : filterColor) {
                    if (fc == c.getId()) {
                        c.setSelected(true);
                        break;
                    }
                }
            }

            FilterBottomSheet filterBottomSheet = FilterBottomSheet.newInstance(colors, this);
            filterBottomSheet.setListener(this);
            filterBottomSheet.show(getSupportFragmentManager(), FilterBottomSheet.class.getName());
        }
    }

    private void addColor() {
        Color color;
        colors = new ArrayList<>();
        if (colors != null && !colors.isEmpty()) {
            colors.clear();
        }
        color = new Color();
        color.setRgb_code("#000000");
        color.setName("Black");
        color.setId(1);
        color.setSelected(false);
        colors.add(color);
        color = new Color();
        color.setRgb_code("#F54700");
        color.setName("Orange");
        color.setId(2);
        color.setSelected(false);
        colors.add(color);
        color = new Color();
        color.setRgb_code("#F5B63C");
        color.setName("Yellow");
        color.setId(3);
        color.setSelected(false);
        colors.add(color);
        color = new Color();
        color.setRgb_code("#48AD83");
        color.setName("Light green");
        color.setId(4);
        color.setSelected(false);
        colors.add(color);
        color = new Color();
        color.setRgb_code("#FF0000");
        color.setName("Red");
        color.setId(5);
        color.setSelected(false);
        colors.add(color);


    }

    @Override
    public void onFilterApplied(List<Integer> selectedColors) {
        this.filterColor.clear();
        this.filterColor.addAll(selectedColors);

    }

    @Override
    public void colorSelected(String colorCode) {
        int color = android.graphics.Color.parseColor(colorCode);
        colorImage.setBackgroundColor(color);
        annotationView.setPainColor(color);
    }
}
