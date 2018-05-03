package com.quangpham.drs.dto;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by quangpham on 4/14/18.
 */

public class StoreInfo {
    private String storeName;
    private LatLng geoLocation;
    private String hoursScheduled[];

    public StoreInfo(String storeName, LatLng geoLocation, String[] hoursScheduled) {
        this.storeName = storeName;
        this.geoLocation = geoLocation;
        this.hoursScheduled = hoursScheduled;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public LatLng getGeoLocation() {
        return geoLocation;
    }

    public void setGeoLocation(LatLng geoLocation) {
        this.geoLocation = geoLocation;
    }

    public String[] getHoursScheduled() {
        return hoursScheduled;
    }

    public void setHoursScheduled(String[] hoursScheduled) {
        this.hoursScheduled = hoursScheduled;
    }
}

