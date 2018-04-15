package com.quangpham.drs;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by quangpham on 3/22/18.
 */

public class FetchAddressIntentService extends IntentService {

    public ResultReceiver mReceiver;

    public FetchAddressIntentService() {
        super("Intent Service");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        mReceiver = intent.getParcelableExtra(AppConstant.RECEIVER);

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        if (intent == null) {
            return;
        }
        String errorMessage = "";

        // Get the location passed to this service through an extra.
        String location = intent.getExtras().getString(AppConstant.LOCATION_DATA_EXTRA);
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocationName(location,
                    // In this sample, get just a single address.
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            errorMessage = "service_not_available";
            Log.e("TAG", errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            errorMessage = "invalid_lat_long_used";
            Log.e("TAG", errorMessage , illegalArgumentException);
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size()  == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = "no_address_found";
                Log.e("TAG", errorMessage);
            }
            deliverResultToReceiver(AppConstant.FAILURE_RESULT, errorMessage, null);
        } else {
            Address address = addresses.get(0);
            double lat = address.getLatitude();
            double lng = address.getLongitude();

            Log.i("TAG", "address_found");
            deliverResultToReceiver(AppConstant.SUCCESS_RESULT,
                    String.valueOf(lat), String.valueOf(lng));
        }
    }


    private void deliverResultToReceiver(int resultCode, String msg1, String msg2) {
        Bundle bundle = new Bundle();
        bundle.putString(AppConstant.RESULT_DATA_KEY1, msg1);
        bundle.putString(AppConstant.RESULT_DATA_KEY2, msg2);
        mReceiver.send(resultCode, bundle);
    }

}
