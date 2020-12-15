package co.uk.depotnet.onsa.activities.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.activities.ThemeBaseActivity;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.modals.forms.Answer;
import co.uk.depotnet.onsa.modals.hseq.PhotoComments;

public class PhotoCommentsActivity extends ThemeBaseActivity {
    private Answer answer;
    private String themeColor;
    private RecyclerView recyclerView;
    private TextView error,photo_coment_back;
    private EdComment_Selection edComment_selection;
    private ArrayList<PhotoComments> comments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_comments);

        Toolbar toolbar = findViewById(R.id.edit_share_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null)
        {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        answer=getIntent().getParcelableExtra("photos");
        themeColor=getIntent().getStringExtra("color");
        recyclerView=findViewById(R.id.photo_coment_recycler);
        error=findViewById(R.id.photo_coment_error);
        photo_coment_back=findViewById(R.id.photo_coment_back);
        edComment_selection=new EdComment_Selection();
        recyclerView.setAdapter(edComment_selection);
        try {
            comments = DBHandler.getInstance().getPhotoComments(String.valueOf(answer.getID()));
            if (comments!=null && comments.size()>0)
            {
                error.setVisibility(View.GONE);
                edComment_selection.update(comments);
            }
            else
            {
                error.setVisibility(View.VISIBLE);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        photo_coment_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}

class EdComment_Selection extends RecyclerView.Adapter<EdComment_Selection.SelectionHolder>
{
    List<PhotoComments> tagList=new ArrayList<>();
    @NonNull
    @Override
    public EdComment_Selection.SelectionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SelectionHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_comment_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull EdComment_Selection.SelectionHolder holder, int position) {
        PhotoComments ak=tagList.get(position);
        holder.photo_comment_text.setText(ak.getComments());
        holder.photo_comment_date.setText(ak.getDate());

    }
    public void update(List<PhotoComments> tags) {
        tagList.clear();
        tagList.addAll(tags);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return tagList.size();
    }

    public class SelectionHolder extends RecyclerView.ViewHolder {
        TextView photo_comment_date,photo_comment_text;
        public SelectionHolder(@NonNull View itemView) {
            super(itemView);
            photo_comment_date=itemView.findViewById(R.id.photo_comment_date);
            photo_comment_text=itemView.findViewById(R.id.photo_comment_text);
        }
    }
}