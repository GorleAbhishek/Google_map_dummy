package com.smsipl.googlemap;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.android.PolyUtil;
import com.smsipl.googlemap.apiServices.DirectionsApiService;
import com.smsipl.googlemap.util.DirectionsResponse;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private EditText sourceEditText;
    private EditText destinationEditText;
    TextView detailsText;

    String flag = "0";

//    private Button findRouteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sourceEditText = findViewById(R.id.sourceEditText);
        destinationEditText = findViewById(R.id.destinationEditText);
        FloatingActionButton findRouteButton = findViewById(R.id.findRouteButton);
        detailsText = findViewById(R.id.detailsText);
        detailsText.setVisibility(View.GONE);

        showAlertDialogForInstuructions();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        findRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag.equals("0")){
                    String source = sourceEditText.getText().toString();
                    String destination = destinationEditText.getText().toString();
                    if (!source.isEmpty() && !destination.isEmpty()) {
                        findRoute(source, destination);
                    } else {
                        Toast.makeText(MainActivity.this, "Please enter both source and destination", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    restartActivity();
                }

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    private void findRoute(String source, String destination) {
        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> sourceAddresses = geocoder.getFromLocationName(source, 1);
            List<Address> destinationAddresses = geocoder.getFromLocationName(destination, 1);

            if (sourceAddresses.isEmpty() || destinationAddresses.isEmpty()) {
                Toast.makeText(this, "Invalid source or destination", Toast.LENGTH_SHORT).show();
                return;
            }

            LatLng sourceLatLng = new LatLng(sourceAddresses.get(0).getLatitude(), sourceAddresses.get(0).getLongitude());
            LatLng destinationLatLng = new LatLng(destinationAddresses.get(0).getLatitude(), destinationAddresses.get(0).getLongitude());

            mMap.addMarker(new MarkerOptions().position(sourceLatLng).title("Source"));
            mMap.addMarker(new MarkerOptions().position(destinationLatLng).title("Destination"));

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sourceLatLng, 10));

            // Use Retrofit to call the OSRM API
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://router.project-osrm.org/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            DirectionsApiService service = retrofit.create(DirectionsApiService.class);

            String coordinates = sourceLatLng.longitude + "," + sourceLatLng.latitude + ";" + destinationLatLng.longitude + "," + destinationLatLng.latitude;

            Call<DirectionsResponse> call = service.getDirections(
                    coordinates,
                    "full",
                    "polyline"
            );

            call.enqueue(new Callback<DirectionsResponse>() {
                @Override
                public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                    if (response.isSuccessful()) {
                        DirectionsResponse DirectionsResponse = response.body();
                        if (DirectionsResponse != null && DirectionsResponse.routes != null && !DirectionsResponse.routes.isEmpty()) {
                            DirectionsResponse.Route route = DirectionsResponse.routes.get(0);
                            flag = "1";
                            float distanceKm = (float)convertDistanceToKm(route.distance);
                            float durationHours = (float )convertDurationToHours(route.duration);
                            int durationHoursSec = (int) route.duration;
                            String formattedDuration = convertDurationToFormattedString(durationHoursSec);
                            detailsText.setVisibility(View.VISIBLE);
                            /*detailsText.setText("Distance: " +  String.format("%.2f", distanceKm) +" Km."+"\n" +
                                                "Duration: " + String.format("%.2f", formattedDuration) + " Hr.");*/
                            detailsText.setText("Distance: " +  String.format("%.2f", distanceKm) +" Km."+"\n" +
                                                "Duration: " + formattedDuration);

                            // Display distance and duration
//                            Toast.makeText(MainActivity.this, "Distance: " + route.distance + " meters, Duration: " + route.duration + " seconds", Toast.LENGTH_LONG).show();

                            // Decode the polyline and draw the route on the map
                            List<LatLng> points = PolyUtil.decode(route.geometry);
                            mMap.addPolyline(new PolylineOptions().addAll(points));
                        } else {
                            Toast.makeText(MainActivity.this, "No route found", Toast.LENGTH_SHORT).show();
                            Log.d("MainActivity", "No route found in the response.");
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Failed to get route", Toast.LENGTH_SHORT).show();
                        Log.d("MainActivity", "API response was unsuccessful. Code: " + response.code() + ", Message: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("MainActivity", "API call failed. Error: " + t.getMessage());
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error finding route", Toast.LENGTH_SHORT).show();
            Log.d("MainActivity", "Geocoder exception: " + e.getMessage());
        }
    }
    public static double convertDistanceToKm(double meters) {
        return meters / 1000.0;
    }

    public static double convertDurationToHours(double seconds) {
        return seconds / 3600.0;
    }

    public static String convertDurationToFormattedString(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int remainingSeconds = totalSeconds % 3600;
        int minutes = remainingSeconds / 60;
        int seconds = remainingSeconds % 60;

        StringBuilder formattedTime = new StringBuilder();
        if (hours > 0) {
            formattedTime.append(hours).append("hr ");
        }
        if (minutes > 0) {
            formattedTime.append(minutes).append("min.");
        }
        /*if (seconds > 0) {
            formattedTime.append(seconds).append("sec");
        }*/

        // Trim any trailing space and return the result
        return formattedTime.toString().trim();
    }
    private void restartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    private void showAlertDialogForInstuructions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Instructions");
        builder.setMessage("This application use to get distance & duration is OnMapReady API ");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
