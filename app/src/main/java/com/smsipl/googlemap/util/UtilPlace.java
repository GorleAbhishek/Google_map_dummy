/*
package com.smsipl.googlemap.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Locale;
*/
/**
 * @author Ivan Vodyasov on 10.08.2017.
 *//*



public class UtilPlace {

    public static final String TAG = UtilPlace.class.getSimpleName();

    */
/**
     * Open a google maps picker fragment
     * @param activity context
     *//*

*/
/*    public static void pickOrFindPlace(Activity activity) {
        PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
        try {
            Intent intent = intentBuilder.build(activity);
            activity.startActivityForResult(intent, 1);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            Log.e(TAG, "GooglePlayService error", e);
        }
    }*//*


    */
/**
     * Open a search place fragment
     * @param activity context
     * @param requestCode of searching point (origin/destination)
     *//*

    public static void searchPlace(Activity activity, int requestCode) {
        try {
            Intent intent =
                    new PlaceAutocomplete
                            .IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(activity);
            activity.startActivityForResult(intent, requestCode);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            Log.e(TAG, "GooglePlayService error", e);
        }
    }

    public static void pickOrFindPlace(Activity activity) {
        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            activity.startActivityForResult(builder.build(activity), 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    */
/**
     * Get full address name
     * @param context activity context
     * @param latLng coordinates of place
     * @return string full address name
     *//*

    public static String getAddressByLatLng(Context context, LatLng latLng) {
        String result = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address returnedAddress = addresses.get(0);
                StringBuilder address = new StringBuilder();
                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    address.append(returnedAddress.getAddressLine(i)).append(", ");
                }
                result = address.toString().substring(0, address.toString().length()-2);
                Log.w(TAG, "Current location : " + result);
            } else {
                Log.w(TAG, "No Address returned!");
            }
        } catch (Exception e) {
            Log.e(TAG, "Get address error", e);
        }
        return result;
    }

}
*/
