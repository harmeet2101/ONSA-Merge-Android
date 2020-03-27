package co.uk.depotnet.onsa.fragments;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.adapters.JobDetailsPagerAdapter;
import co.uk.depotnet.onsa.listeners.FragmentActionListener;
import co.uk.depotnet.onsa.modals.Job;
import co.uk.depotnet.onsa.modals.User;

import java.util.List;
import java.util.Locale;


public class FragmentJobDetail extends Fragment
        implements OnMapReadyCallback, View.OnClickListener {

    private static final String ARG_USER = "User";
    private static final String ARG_JOB = "Job";
    private Context context;
    private MapView mapView;
    private GoogleMap googleMap;
    private Job job;
    private Handler handler;

    private FragmentActionListener listener;
    private User user;

    public FragmentJobDetail() {
        // Required empty public constructor
    }


    public static FragmentJobDetail newInstance(User user, Job job) {
        FragmentJobDetail fragment = new FragmentJobDetail();
        Bundle args = new Bundle();
        args.putParcelable(ARG_USER, user);
        args.putParcelable(ARG_JOB, job);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        user = args.getParcelable(ARG_USER);
        job = args.getParcelable(ARG_JOB);
        handler = new Handler();
        listener.setTitle("Job ID: " + job.getjobNumber());

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_job_detail, container,
                false);
        mapView = view.findViewById(R.id.map_view);
        view.findViewById(R.id.btn_directions).setOnClickListener(this);


        ViewPager vpPager = view.findViewById(R.id.view_pager);
        TabLayout tabStrip = view.findViewById(R.id.pager_header);
        JobDetailsPagerAdapter adapter = new JobDetailsPagerAdapter(context, user, job, getChildFragmentManager());
        vpPager.setAdapter(adapter);
        tabStrip.setupWithViewPager(vpPager);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        listener.onFragmentHomeVisible(false);
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
    public void onAttach(Context context) {
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
        googleMap.getUiSettings().setAllGesturesEnabled(false);
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.getUiSettings().setTiltGesturesEnabled(false);

        handleMap();
    }

    private void handleMap() {
        boolean attemptToGeocode = false;
        if (job != null) {
            if (job.getpostCode() != null) {
                attemptToGeocode = true;
            }
        }

        if (!attemptToGeocode) return;

        Thread geoThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(getActivity(), Locale.UK);
                try {
                    List<Address> addresses = geocoder.getFromLocationName(job.getpostCode(), 1);

                    if (addresses.size() > 0) {
                        final LatLng latLng = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());

                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                // create marker
                                MarkerOptions marker = new MarkerOptions().position(
                                        latLng).title("Location");

                                // Changing marker icon
                                marker.icon(BitmapDescriptorFactory
                                        .defaultMarker(BitmapDescriptorFactory.HUE_RED));

                                // adding marker
                                googleMap.addMarker(marker);
                                CameraPosition cameraPosition = new CameraPosition.Builder()
                                        .target(latLng).zoom(12).build();
                                googleMap.animateCamera(CameraUpdateFactory
                                        .newCameraPosition(cameraPosition));


                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        geoThread.start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_directions:
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?daddr=" + job.getpostCode()));
                startActivity(intent);
                break;
        }
    }

}
