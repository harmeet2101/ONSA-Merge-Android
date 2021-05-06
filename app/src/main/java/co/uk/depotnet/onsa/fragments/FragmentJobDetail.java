package co.uk.depotnet.onsa.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.adapters.JobDetailsPagerAdapter;
import co.uk.depotnet.onsa.listeners.FragmentActionListener;
import co.uk.depotnet.onsa.modals.Job;


public class FragmentJobDetail extends Fragment
        implements OnMapReadyCallback, View.OnClickListener {

    private static final String ARG_JOB = "Job";
    private Context context;
    private MapView mapView;
    private GoogleMap googleMap;
    private Job job;
    private Handler handler;

    private FragmentActionListener listener;
//52.293060 -1.779800

    public static FragmentJobDetail newInstance(Job job) {
        FragmentJobDetail fragment = new FragmentJobDetail();
        Bundle args = new Bundle();
        args.putParcelable(ARG_JOB, job);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        job = args.getParcelable(ARG_JOB);
        handler = new Handler(context.getMainLooper());
        listener.setTitle("Job ID: " + job.getjobNumber());

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_job_detail, container,
                false);
        mapView = view.findViewById(R.id.map_view);
        view.findViewById(R.id.btn_directions).setOnClickListener(this);
        view.findViewById(R.id.btn_img_cancel).setOnClickListener(this);
        TextView txtToolbarTitle = view.findViewById(R.id.txt_toolbar_title);

        if(job.isSubJob()){
            txtToolbarTitle.setText(String.format("%s-S%s", job.getestimateNumber(), job.getSubJobNumber()));
        }else{
            txtToolbarTitle.setText(String.format("%s", job.getestimateNumber()));
        }

        ViewPager vpPager = view.findViewById(R.id.view_pager);
        TabLayout tabStrip = view.findViewById(R.id.pager_header);
        JobDetailsPagerAdapter adapter = new JobDetailsPagerAdapter(context, job, getChildFragmentManager());
        vpPager.setAdapter(adapter);
        tabStrip.setupWithViewPager(vpPager);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof FragmentActionListener) {
            listener = (FragmentActionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.getUiSettings().setAllGesturesEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setScrollGesturesEnabled(true);
        googleMap.getUiSettings().setTiltGesturesEnabled(false);

        handleMap();
    }

    private void handleMap() {
        Thread geoThread = new Thread(() -> {
            Geocoder geocoder = new Geocoder(getActivity(), Locale.UK);
            String postCode = job.getpostCode();

            try {
                if(job.getlatitude() != 0 && job.getlongitude() != 0){
                    LatLng latLng = new LatLng(job.getlatitude(), job.getlongitude());
                    setPosition(latLng);
                    return;
                }else

                if(!TextUtils.isEmpty(postCode)){
                    List<Address> addresses = geocoder.getFromLocationName(postCode, 1);
                    if(addresses != null && !addresses.isEmpty()){
                        LatLng latLng = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                        setPosition(latLng);
                        return;
                    }
                }
                LatLng latLng = new LatLng(52.293060, -1.779800);
                setPosition(latLng);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        geoThread.start();
    }

    private void setPosition(final LatLng latLng){

        handler.post(() -> {
            MarkerOptions marker = new MarkerOptions().position(
                    latLng).title("Location");

            marker.icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_RED));

            // adding marker
            googleMap.addMarker(marker);
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng).zoom(12).build();
            googleMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_directions) {

                String geoUri = "http://maps.google.com/maps?q=loc: 52.293060 , -1.779800 ( "+job.getlocationAddress()+" )";

                if(job.getlatitude() != 0 && job.getlongitude() != 0){
                    geoUri = "http://maps.google.com/maps?q=loc: "+job.getlatitude()+" , "+job.getlongitude()+" ( "+job.getlocationAddress()+" )";
                }else
                if(!TextUtils.isEmpty(job.getpostCode())){
                    geoUri = "http://maps.google.com/maps?daddr=" + job.getpostCode();
                }
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse(geoUri));
                startActivity(intent);

        }else if (view.getId() == R.id.btn_img_cancel) {
            ((Activity)context).onBackPressed();
        }
    }

}
