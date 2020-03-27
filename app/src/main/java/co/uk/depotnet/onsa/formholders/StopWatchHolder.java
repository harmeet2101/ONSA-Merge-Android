package co.uk.depotnet.onsa.formholders;

import android.os.Handler;
import android.os.SystemClock;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import co.uk.depotnet.onsa.R;

public class StopWatchHolder extends RecyclerView.ViewHolder {
    public View view;
    public TextView txtTime;
    public TextView btnReset;
    public TextView btnStart;
    private long millisecondTime, startTime, timeBuff, updateTime = 0L ;

    private int seconds, minutes, hours ;
    private Handler handler;

    public StopWatchHolder(@NonNull View itemView) {
        super(itemView);
        this.view = itemView;
        this.txtTime = itemView.findViewById(R.id.txt_time);
        this.btnReset = itemView.findViewById(R.id.btn_reset);
        this.btnStart = itemView.findViewById(R.id.btn_start);
        btnReset.setVisibility(View.GONE);
        this.handler = new Handler();
    }


    public void resetTime() {
        stopTimer();
        millisecondTime = 0L ;
        startTime = 0L ;
        timeBuff = 0L ;
        updateTime = 0L ;
        seconds = 0 ;
        minutes = 0 ;
        hours = 0;
        btnReset.setVisibility(View.GONE);
        setTimeToText(seconds);
    }

    public void stopTimer() {
        timeBuff += millisecondTime;
        handler.removeCallbacks(runnable);
        btnReset.setVisibility(View.VISIBLE);
        btnStart.setText(R.string.start);
    }

    public void startTimer() {
        startTime = SystemClock.uptimeMillis();
        timeBuff = updateTime*1000;
        handler.postDelayed(runnable, 0);
        btnReset.setVisibility(View.GONE);
        btnStart.setText(R.string.stop);
    }

    private Runnable runnable = new Runnable() {

        public void run() {

            millisecondTime = SystemClock.uptimeMillis() - startTime;

            updateTime = (timeBuff + millisecondTime)/1000;

            setTimeToText((int) (updateTime));
            handler.postDelayed(this, 0);
        }

    };

    public void setTimeToText(int timeInSeconds){
        minutes = timeInSeconds / 60;

        hours = minutes/60;

        minutes = minutes % 60;

        seconds = timeInSeconds % 60;

        txtTime.setText(String.format("%02d", hours) + ":"
                + String.format("%02d", minutes) + ":"
                + String.format("%02d", seconds));
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }
}