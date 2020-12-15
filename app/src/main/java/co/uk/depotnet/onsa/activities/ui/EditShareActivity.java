package co.uk.depotnet.onsa.activities.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import co.uk.depotnet.onsa.BuildConfig;
import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.activities.ImageAnnotationActivity;
import co.uk.depotnet.onsa.activities.ThemeBaseActivity;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.modals.forms.Answer;
import co.uk.depotnet.onsa.modals.forms.Photo;
import co.uk.depotnet.onsa.modals.hseq.PhotoComments;
import co.uk.depotnet.onsa.modals.hseq.PhotoTags;
import co.uk.depotnet.onsa.utils.GenericFileProvider;

public class EditShareActivity extends ThemeBaseActivity implements View.OnClickListener {
    private BottomNavigationView mBottomNavigationView;
    public static final int PICK_EditShare = 1009;
    private Answer answer;
    private Photo photomodel;
    private int position;
    private String themeColor;
    private TextView editshare_title;
    private ImageView edit_share_photoview;
    private RequestOptions myOptions;
    private BadgeDrawable badgeDrawable,badgeTags;
    private MaterialButton edSubmit;
    private AppCompatEditText inputcomment;
    private AutoCompleteTextView actv;
    private RecyclerView tag_recycler;
    private AlertDialog dialogcomment,dialogtag;
    //String[] phototags ={"Pre-Site","SLG","HSEQ","On-Site","Dummy","Test","tags","dev"};
    ArrayList<String> phototags=new ArrayList<>();
    ArrayList<PhotoTags> tagsfinal=new ArrayList<>();
    int commentsTotal=0;
    int tagstotal=0;
    private boolean doubleBackToExitPressedOnce=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_share);
        Toolbar toolbar = findViewById(R.id.edit_share_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        answer=getIntent().getParcelableExtra("photos");
        photomodel=getIntent().getParcelableExtra("photomodel");
        position=getIntent().getIntExtra("POSITION",0);
        themeColor=getIntent().getStringExtra("color");
        editshare_title=findViewById(R.id.editshare_title);
        setupBottomNavigation();
        if (savedInstanceState == null) {
            //loadHomeFragment();
            badgeDrawable=mBottomNavigationView.getOrCreateBadge(R.id.action_comment);
            badgeDrawable.clearNumber();
            badgeDrawable.setVisible(false);
            badgeTags=mBottomNavigationView.getOrCreateBadge(R.id.action_tag);
            badgeTags.clearNumber();
            badgeTags.setVisible(false);
            try {
                commentsTotal= DBHandler.getInstance().getPhotoCommentsCount(answer.getID());
                if (commentsTotal>0) {
                    badgeDrawable.setVisible(true);
                    badgeDrawable.isVisible();// for only dots..
                    badgeDrawable.setNumber(commentsTotal); // for count..
                    //badgeDrawable.setBadgeTextColor(ContextCompat.getColor(this,R.color.white));
                }
                tagstotal=DBHandler.getInstance().getPhotoTagsCount(answer.getID());
                if (tagstotal>0)
                {
                    badgeTags.setVisible(true);
                    badgeTags.isVisible();
                    badgeTags.setNumber(tagstotal);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        edit_share_photoview=findViewById(R.id.edit_share_photoview);
        myOptions = new RequestOptions()
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true);

        Glide.with(this)
                .load(answer.getAnswer())
                .apply(myOptions)
                .into(edit_share_photoview);
        findViewById(R.id.editshare_shareview).setOnClickListener(this);
        try {
            phototags.clear();
            phototags.addAll(DBHandler.getInstance().getTagsList());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {

            commentsTotal=DBHandler.getInstance().getPhotoCommentsCount(answer.getID());
            if (commentsTotal>0) {
                badgeDrawable.setVisible(true);
                badgeDrawable.isVisible();// for only dots..
                badgeDrawable.setNumber(commentsTotal); // for count..
                //badgeDrawable.setBadgeTextColor(ContextCompat.getColor(this,R.color.white));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void setupBottomNavigation() {
        mBottomNavigationView = findViewById(R.id.bottom_navigation_editshare);
        mBottomNavigationView.getMenu().getItem(0).setCheckable(false);// for non selection any item....
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_annotate:
                        editshare_title.setText("Annotate Mode");
                        mBottomNavigationView.getMenu().getItem(0).setCheckable(true);
                        Intent intent = new Intent(EditShareActivity.this , ImageAnnotationActivity.class);
                        intent.putExtra("photos" , answer);
                        intent.putExtra("POSITION" , position);
                        startActivityForResult(intent , PICK_EditShare);
                        return true;
                    case R.id.action_comment:
                        editshare_title.setText("Comment on Photo");
                        Dialogcomment();
                        return true;
                    case R.id.action_tag:
                        editshare_title.setText("Add Tags");
                        DialogTags();
                        return true;
                }
                return false;
            }
        });
    }
    private void Dialogcomment()
    {
        ViewGroup viewGroup1 = findViewById(android.R.id.content);
        final View commentview= LayoutInflater.from(this).inflate(R.layout.editshare_comment,viewGroup1,false);
        commentview.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));

        ViewGroup.MarginLayoutParams mlParams = (ViewGroup.MarginLayoutParams)commentview.getLayoutParams();
        //dialogView.setBackgroundColor(Color.TRANSPARENT);
        commentview.setLayoutParams(mlParams);
        AlertDialog.Builder buildercomment=new AlertDialog.Builder(EditShareActivity.this);
        buildercomment.setView(commentview);
        inputcomment=commentview.findViewById(R.id.edcomment_inputtxt);
        edSubmit=commentview.findViewById(R.id.edcomment_submit);
        edSubmit.setOnClickListener(this);
        commentview.findViewById(R.id.edcomment_close).setOnClickListener(this);
        commentview.findViewById(R.id.edcomment_view).setOnClickListener(this);
        dialogcomment = buildercomment.create();
        dialogcomment.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogcomment.setCanceledOnTouchOutside(false);
        dialogcomment.show();
    }
    private void DialogTags()
    {
        ViewGroup viewGroup = findViewById(android.R.id.content);
        final View tagview= LayoutInflater.from(this).inflate(R.layout.editshare_tag,viewGroup,false);
        tagview.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));

        ViewGroup.MarginLayoutParams mlParams = (ViewGroup.MarginLayoutParams)tagview.getLayoutParams();
        //dialogView.setBackgroundColor(Color.TRANSPARENT);
        tagview.setLayoutParams(mlParams);
        AlertDialog.Builder buildertag=new AlertDialog.Builder(EditShareActivity.this);
        buildertag.setView(tagview);
        tagview.findViewById(R.id.edtag_close).setOnClickListener(this);
        actv=tagview.findViewById(R.id.edtag_suggesstion);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this,android.R.layout.simple_list_item_1,phototags);
        actv.setThreshold(2);
        actv.setAdapter(adapter);
        tag_recycler=tagview.findViewById(R.id.edtag_recycler);
        StaggeredGridLayoutManager layoutManager=new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL);//if horizontal use for scrolling
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        tag_recycler.setLayoutManager(layoutManager);
        EdTag_Selection edTag_selection=new EdTag_Selection();
        tag_recycler.setAdapter(edTag_selection);
        tagsfinal=DBHandler.getInstance().getPhotoTags(answer.getID());
        if (tagsfinal!=null && tagsfinal.size()>0)
        {
            edTag_selection.update(tagsfinal);
            edTag_selection.notifyDataSetChanged();
        }

        actv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String actvtags=actv.getText().toString();
            if (actvtags.isEmpty())
            {
                return;
            }
              PhotoTags photoTags=new PhotoTags();
                photoTags.setAnswerId(answer.getID());
                photoTags.setTagName(actvtags);
                DBHandler.getInstance().replaceData(PhotoTags.DBTable.NAME, photoTags.toContentValues());
                tagsfinal=DBHandler.getInstance().getPhotoTags(answer.getID());
                //tagsfinal.add(photoTags);
                 edTag_selection.update(tagsfinal);
                 edTag_selection.notifyDataSetChanged();
                    //Log.d("abhikr","photo model added: "+ DBHandler.getInstance().getPhotoTags(answer.getID()));
                actv.clearComposingText();
                actv.setText("");
                actv.requestFocus();
            }
        });
        actv.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String actvtags=actv.getText().toString();
                    if (!actvtags.isEmpty())
                    {
                        PhotoTags photoTags=new PhotoTags();
                        photoTags.setAnswerId(answer.getID());
                        photoTags.setTagName(actvtags);
                        DBHandler.getInstance().replaceData(PhotoTags.DBTable.NAME, photoTags.toContentValues());
                        tagsfinal = DBHandler.getInstance().getPhotoTags(answer.getID());
                        //tagsfinal.add(photoTags);
                        edTag_selection.update(tagsfinal);
                        edTag_selection.notifyDataSetChanged();
                        //Log.d("abhikr","photo model added: "+ DBHandler.getInstance().getPhotoTags(answer.getID()));
                        actv.clearComposingText();
                        actv.setText("");
                        actv.requestFocus();
                    }

                    return true;
                }
                return false;
            }
        });

        dialogtag = buildertag.create();
        dialogtag.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogtag.setCanceledOnTouchOutside(false);
        dialogtag.show();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_EditShare) {
            answer = data.getParcelableExtra("photo");
            position = data.getIntExtra("position" , 0);

            Glide.with(this)
                    .load(answer.getAnswer())
                    .apply(myOptions)
                    .into(edit_share_photoview);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.edcomment_close:
                if (answer!=null)
                {
                    answer.setComments(DBHandler.getInstance().getPhotoComments(String.valueOf(answer.getID())));
                    try {
                        commentsTotal=DBHandler.getInstance().getPhotoCommentsCount(answer.getID());
                        if (commentsTotal>0) {
                            badgeDrawable.setVisible(true);
                            badgeDrawable.isVisible();// for only dots..
                            badgeDrawable.setNumber(commentsTotal); // for count..
                            //badgeDrawable.setBadgeTextColor(ContextCompat.getColor(this,R.color.white));
                        }
                        Log.d("abhikr","comments: "+answer.getComments());
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                dialogcomment.dismiss();
                break;
            case R.id.edcomment_submit:
                if (TextUtils.isEmpty(Objects.requireNonNull(inputcomment.getText()).toString())) {
                    inputcomment.setError("please enter any additional comment.");
                    inputcomment.requestFocus();
                } else {
                    PhotoComments photoCommen=new PhotoComments();
                    photoCommen.setAnswerId(answer.getID());
                    photoCommen.setComments(inputcomment.getText().toString());
                    Date currentTime = Calendar.getInstance().getTime();
                    photoCommen.setDate(currentTime.toString());
                    //photoCommen.toContentValues();//adding to db.
                    DBHandler.getInstance().replaceData(PhotoComments.DBTable.NAME, photoCommen.toContentValues());
                    inputcomment.setText("");
                }
                break;
            case R.id.edcomment_view:
                Intent comIntent=new Intent(this, PhotoCommentsActivity.class);
                comIntent.putExtra("photos" , answer);
                comIntent.putExtra("color" , themeColor);
                startActivity(comIntent);
                dialogcomment.dismiss();
                break;
            case R.id.edtag_close:
                if (tagsfinal!=null && tagsfinal.size()>0)
                {
                    answer.setTags(DBHandler.getInstance().getPhotoTags(answer.getID()));
                    tagstotal=DBHandler.getInstance().getPhotoTagsCount(answer.getID());
                    if (tagstotal>0)
                    {
                        badgeTags.setVisible(true);
                        badgeTags.isVisible();
                        badgeTags.setNumber(tagstotal);
                    }
                    Log.d("abhikr","comments: "+answer.getTags());
                }
                dialogtag.dismiss();
                break;
            case R.id.editshare_shareview:
                try {
                    Intent intentShareFile = new Intent(Intent.ACTION_SEND);
                    File filepath = new File(answer.getAnswer());
                    if(filepath.exists()) {
                        intentShareFile.setType("application/image");
                        Uri uri = GenericFileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".fileprovider", filepath);
                        intentShareFile.putExtra(Intent.EXTRA_STREAM, uri);

                        intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                                "Sharing Image...");
                        intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing Image...");

                        startActivity(Intent.createChooser(intentShareFile, "Share Image"));
                    }
                    else
                    {
                        Toast.makeText(this, "File not found! please try after some time..", Toast.LENGTH_SHORT).show();
                    }
                } catch (ActivityNotFoundException e) {
                    Snackbar.make(findViewById(android.R.id.content), "No Image file found to share! please try after some time.", Snackbar.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }
    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new MaterialAlertDialogBuilder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Intent intent=new Intent();
        intent.putExtra("position" , position);
        intent.putExtra("photo" , answer);
        setResult(RESULT_OK,intent);
        //finish();
        Toast.makeText(this, "Press again to exit.", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce=false, 2000);
    }
}

class EdTag_Selection extends RecyclerView.Adapter<EdTag_Selection.SelectionHolder>
{
    private List<PhotoTags> photoTagsList=new ArrayList<>();
    @NonNull
    @Override
    public EdTag_Selection.SelectionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SelectionHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_share_tag_select,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull EdTag_Selection.SelectionHolder holder, int position) {
    PhotoTags ak=photoTagsList.get(position);
    holder.tagview.setText(ak.getTagName());
    holder.tagview.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(holder.getAdapterPosition()!= RecyclerView.NO_POSITION) {
                photoTagsList.remove(holder.getAdapterPosition());
                DBHandler.getInstance().removePhotoTags(ak);// delete on behalf of id
                notifyItemRemoved(holder.getAdapterPosition()); // recyclerview dev submit..
                //Log.d("abhikr","photo tags: "+ DBHandler.getInstance().getPhotoTags(0));
            }
        }
    });
    }
    public void update(List<PhotoTags> tags) {
        photoTagsList.clear();
        photoTagsList.addAll(tags);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return photoTagsList.size();
    }

    public class SelectionHolder extends RecyclerView.ViewHolder {
        MaterialButton tagview;
        public SelectionHolder(@NonNull View itemView) {
            super(itemView);
            tagview=itemView.findViewById(R.id.ed_tag_selection);
        }
    }
}