package co.uk.depotnet.onsa.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.modals.forms.Photo;

public class VideoActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        Photo photo=getIntent().getParcelableExtra("photos");
        VideoView videoView=findViewById(R.id.videoView);
        findViewById(R.id.img_cancel).setOnClickListener(this);
        TextView title=findViewById(R.id.txt_photo_name);
        title.setText(photo.getTitle());
        //"/storage/emulated/0/Balram Onsa/VID_20190822_103833.mp4"
      /*  Uri uri = Uri.parse(photo.getUrl());
        videoView.setVideoURI(uri);
        videoView.setMediaController(new MediaController(this));
        videoView.start();*/

            videoView.setVideoPath(photo.getUrl());
            MediaController mediaController = new MediaController(VideoActivity.this);
            videoView.setMediaController(mediaController);
            mediaController.setMediaPlayer(videoView);
            videoView.setVisibility(View.VISIBLE);
            videoView.start();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_cancel:
                finish();
                break;
        }
    }
}
