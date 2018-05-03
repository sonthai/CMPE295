package com.quangpham.drs.ui;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.quangpham.drs.MainActivity;
import com.quangpham.drs.R;
import com.quangpham.drs.dto.ProductInfo;
import com.quangpham.drs.utils.AppConstant;


import java.util.Calendar;

/**
 * Created by quangpham on 4/4/18.
 */

public class FragmentLocation extends Fragment implements OnMapReadyCallback, LocationListener {

    private static GoogleMap mMap;
    private View rootView;
    private Location currentBestLocation = null;
    private final int WAIT_TIME = 1000 * 60 * 2;
    private TextView mallName;
    private TextView mallHour;

    public static Fragment newInstance() {
        return new FragmentLocation();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_location, container, false);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ImageView avatar = rootView.findViewById(R.id.profile_image_location);
        if (MainActivity.user.getAvatar() != null && !MainActivity.user.getAvatar().equals("null")) {
            avatar.setImageBitmap(ProductInfo.covertBase64ToBitmap(MainActivity.user.getAvatar()));
        }

        TextView memberSince = rootView.findViewById(R.id.member_since);
        memberSince.setText("Member since " + MainActivity.user.getYearJoined());

        mallHour = rootView.findViewById(R.id.hour);
        mallName = rootView.findViewById(R.id.mall_name);

        TextView userName = rootView.findViewById(R.id.location_username);
        String fullName = MainActivity.user.getFullName();
        if (fullName != null && !fullName.equals("null")) {
            userName.setText(fullName);
        } else {
            userName.setText(MainActivity.user.getEmail().split("@")[0]);
        }

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(rootView.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            Location location = getLastBestLocation();
            if (location != null) {
                LatLng geo = new LatLng(location.getLatitude(), location.getLongitude());
                float d = 0;
                int pos = 0;
                for (int i = 0; i < AppConstant.STORES.length; i++) {
                    float[] dis = new float[1];
                    Location.distanceBetween(geo.latitude, geo.longitude,
                            AppConstant.STORES[i].getGeoLocation().latitude,
                            AppConstant.STORES[i].getGeoLocation().longitude, dis);
                    if (i == 0) { d = dis[0]; }
                    else if (d > dis[0]) {
                        d = dis[0];
                        pos = i;
                    }
                }
                mallName.setText(AppConstant.STORES[pos].getStoreName());
                mallHour.setText("Hour today: " + AppConstant.STORE_HOUR_1[Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1]);
                //user location
                mMap.addMarker(new MarkerOptions().position(geo).icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).title(MainActivity.user.getEmail()));
                //mall location
                mMap.addMarker(new MarkerOptions().position(AppConstant.STORES[pos].getGeoLocation()).icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_RED)).title(AppConstant.STORES[pos].getStoreName()));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(geo, 10));
            }

            Log.d("PERMISSION_GRANTED", "HAVE PERMISSION");
        } else {
            Log.d("PERMISSION_GRANTED", "NO PERMISSION");
        }
    }

    private Location getLastBestLocation() {
        Location locationGPS = null, locationNet = null;
        if (ContextCompat.checkSelfPermission(rootView.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationManager locationManager = (LocationManager) MainActivity.mainActivity.getSystemService(Context.LOCATION_SERVICE);
            locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        long GPSLocationTime = 0;
        if (locationGPS != null) { GPSLocationTime = locationGPS.getTime(); }

        long NetLocationTime = 0;

        if (locationNet != null) {
            NetLocationTime = locationNet.getTime();
        }

        if ( GPSLocationTime - NetLocationTime > 0) {
            return locationGPS;
        }
        else {
            return locationNet;
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        makeUseOfNewLocation(location);

        if(currentBestLocation == null){
            currentBestLocation = location;
        }

    }

    void makeUseOfNewLocation(Location location) {
        if ( isBetterLocation(location, currentBestLocation) ) {
            currentBestLocation = location;
        }
    }


    /** Determines whether one location reading is better than the current location fix
     * @param location  The new location that you want to evaluate
     * @param currentBestLocation  The current location fix, to which you want to compare the new one.
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > WAIT_TIME;
        boolean isSignificantlyOlder = timeDelta < -WAIT_TIME;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location,
        // because the user has likely moved.
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse.
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }


    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

}
