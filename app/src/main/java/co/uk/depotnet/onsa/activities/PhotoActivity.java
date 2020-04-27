package co.uk.depotnet.onsa.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.adapters.GalleryAdapter;
import co.uk.depotnet.onsa.modals.Job;
import co.uk.depotnet.onsa.utils.VerticalSpaceItemDecoration;

public class PhotoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Job job = intent.getParcelableExtra("Job");

        setContentView(R.layout.activity_photo);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(20 ));
        GalleryAdapter adapter = new GalleryAdapter(this , job.getjobId());
        recyclerView.setAdapter(adapter);

        findViewById(R.id.btn_img_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
