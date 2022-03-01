package com.alaan.roamudriver.acitivities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.alaan.roamudriver.R;
import com.alaan.roamudriver.custom.GPSTracker;
import com.alaan.roamudriver.pojo.Pass;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.thebrownarrow.permissionhelper.ActivityManagePermission;
import com.thebrownarrow.permissionhelper.PermissionResult;
import com.thebrownarrow.permissionhelper.PermissionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GoogleMapsActivity extends ActivityManagePermission implements OnMapReadyCallback, DirectionCallback,
        Animation.AnimationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    public String NETWORK;
    public String TRYAGAIN;
    private PlacesClient placesClient;
    Pass pass;
    com.google.android.gms.maps.MapView mMapView;
    GoogleMap myMap;
    String permissionAsk[] = {PermissionUtils.Manifest_CAMERA, PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE, PermissionUtils.Manifest_READ_EXTERNAL_STORAGE, PermissionUtils.Manifest_ACCESS_FINE_LOCATION, PermissionUtils.Manifest_ACCESS_COARSE_LOCATION};
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Double currentLatitude;
    private Double currentLongitude;
    private Marker my_marker;
    EditText input_Place;
    TextView confirmTV;
    String placeName = "";
    String placeLatLong = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);
        NETWORK = getString(R.string.network_not_available);
        TRYAGAIN = getString(R.string.try_again);
        mMapView = (MapView) findViewById(R.id.fm_mapview);
        input_Place = (EditText) findViewById(R.id.input_Place);
        confirmTV = (TextView) findViewById(R.id.confirmTV);
        confirmTV.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (confirmTV.getText().toString().matches("")) {
                    Toast.makeText(GoogleMapsActivity.this, getString(R.string.Post_Empty), Toast.LENGTH_LONG).show();
                } else {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("placeName", placeName);
                    returnIntent.putExtra("placeLatLong", placeLatLong);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }
        });

        MapsInitializer.initialize(GoogleMapsActivity.this);
        Places.initialize(getApplicationContext(), getString(R.string.google_android_map_api_key));
        pass = new Pass();
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
        placesClient = Places.createClient(GoogleMapsActivity.this);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                askCompactPermissions(permissionAsk, new PermissionResult() {
                    @Override
                    public void permissionGranted() {
                        if (!GPSEnable()) {
                            tunonGps();
                        } else {
                            getCurrentlOcation();
                        }
                    }

                    @Override
                    public void permissionDenied() {
                    }

                    @Override
                    public void permissionForeverDenied() {

                    }
                });

            } else {
                if (!GPSEnable()) {
                    tunonGps();
                } else {
                    getCurrentlOcation();
                }
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if (this != null && mMapView != null) {
                Log.i("ibrahim", "onPause");
                mMapView.onPause();
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (this != null && mMapView != null) {
                Log.i("ibrahim", "onResume");
                mMapView.onResume();
            }
            if (mGoogleApiClient != null) {
                mGoogleApiClient.connect();
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (mMapView != null) {
                mMapView.onDestroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            if (mMapView != null) {
                mMapView.onSaveInstanceState(outState);
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        try {
            if (mMapView != null) {
                mMapView.onLowMemory();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            if (mMapView != null) {
                mMapView.onStop();
            }
            if (mGoogleApiClient != null) {
                mGoogleApiClient.disconnect();
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;
        myMap.setMaxZoomPreference(80);
        myMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(final Marker marker) {
                View v = null;
                if (GoogleMapsActivity.this != null) {
                    v = GoogleMapsActivity.this.getLayoutInflater().inflate(R.layout.view_custom_marker, null);
                    TextView title = (TextView) v.findViewById(R.id.t);
                    TextView t1 = (TextView) v.findViewById(R.id.t1);
                    TextView t2 = (TextView) v.findViewById(R.id.t2);
                    Typeface font = Typeface.createFromAsset(GoogleMapsActivity.this.getAssets(), "font/AvenirLTStd_Medium.otf");
                    t1.setTypeface(font);
                    t2.setTypeface(font);
                    String name = marker.getTitle();
                    title.setText(name);
                    String info = marker.getSnippet();
                    t1.setText(info);
                }
                return v;
            }
        });
        if (myMap != null) {
            tunonGps();

        }
    }


    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        if (GoogleMapsActivity.this != null) {
            if (direction.isOK()) {
                ArrayList<LatLng> directionPositionList = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
                myMap.addPolyline(DirectionConverter.createPolyline(GoogleMapsActivity.this, directionPositionList, 5, Color.RED));
            } else {
            }
        }
    }

    @Override
    public void onDirectionFailure(Throwable t) {
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }

    private String getAdd(double latitude, double longitude) {
        String finalAddress = latitude + ", " + longitude;
        try {
            Geocoder geocoder;
            List<Address> addresses = null;
            geocoder = new Geocoder(getApplicationContext(), Locale.ENGLISH);
            if (latitude != 0 && longitude != 0) {
                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    if (addresses != null && addresses.size() > 0) {
                        String merged = "";
                        String address = addresses.get(0).getAddressLine(0);
                        String city = addresses.get(0).getLocality();
                        finalAddress = address;// + ", " + city;
                    }
                } catch (IOException | IllegalArgumentException e) {
                    //  e.printStackTrace();
                    //log.e("data", e.toString());
                }
            } else {
                finalAddress = latitude + ", " + longitude;
            }
        } catch (Exception e) {
            finalAddress = latitude + ", " + longitude;
        }
        return finalAddress;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            android.location.Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (location == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            } else {
                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();
                if (myMap != null) {
                    myMap.clear();
                    my_marker = myMap.addMarker(new MarkerOptions().position(new LatLng(currentLatitude, currentLongitude)).title("You are here.").icon(BitmapDescriptorFactory.fromResource(R.drawable.user_default)));
                    my_marker.showInfoWindow();
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(currentLatitude, currentLongitude), 12);
                    myMap.animateCamera(cameraUpdate);

                    try {
                        placeLatLong = currentLatitude + "," + currentLongitude;
                        placeName = getAdd(currentLatitude, currentLongitude);
                        input_Place.setText(getAdd(currentLatitude, currentLongitude));
                    } catch (NullPointerException e) {
                        System.err.println("Null pointer exception");
                    }

                    myMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                        @Override
                        public void onMapLongClick(LatLng latLng) {
                            try {
                                placeLatLong = latLng.toString();
                                placeName = getAdd(latLng.latitude, latLng.longitude);
                                input_Place.setText(getAdd(latLng.latitude, latLng.longitude));
                            } catch (NullPointerException e) {
                                System.err.println("Null pointer exception");
                            }

                        }
                    });

                    myMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                        @Override
                        public void onCameraChange(CameraPosition cameraPosition) {
                            Log.i("ibrahim", "onCameraChange");
                            // Cleaning all the markers.
                            if (myMap != null) {
//                                mGoogleMap.clear();
                            }
                            ;
                            placeLatLong = cameraPosition.target.toString();
                            placeName = getAdd(cameraPosition.target.latitude, cameraPosition.target.longitude);
                            input_Place.setText(getAdd(cameraPosition.target.latitude, cameraPosition.target.longitude));
                        }
                    });
                }
                setCurrentLocation(currentLatitude, currentLongitude);
            }
        } catch (Exception e) {

        }

    }

    public void setCurrentLocation(final Double lat, final Double log) {
        try {
            my_marker.setPosition(new LatLng(lat, log));
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(currentLatitude, currentLongitude), 12);
            myMap.animateCamera(cameraUpdate);
        } catch (Exception e) {

        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(GoogleMapsActivity.this, CONNECTION_FAILURE_RESOLUTION_REQUEST);

            } catch (IntentSender.SendIntentException e) {

                e.printStackTrace();
            }
        }

    }

    @Override
    public void onLocationChanged(android.location.Location location) {
        if (location != null) {
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();
            if (!currentLatitude.equals(0.0) && !currentLongitude.equals(0.0)) {
                setCurrentLocation(currentLatitude, currentLongitude);
            } else {
                Toast.makeText(GoogleMapsActivity.this, getString(R.string.couldnt_get_location), Toast.LENGTH_LONG).show();
            }
        }

    }

    public void getCurrentlOcation() {
        mGoogleApiClient = new GoogleApiClient.Builder(GoogleMapsActivity.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(30 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);
    }

    public void tunonGps() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(GoogleMapsActivity.this)
                    .addApi(LocationServices.API).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            mGoogleApiClient.connect();
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(30 * 1000);
            mLocationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(mLocationRequest);

            // **************************
            builder.setAlwaysShow(true); // this is the key ingredient
            // **************************

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                    .checkLocationSettings(mGoogleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result
                            .getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            getCurrentlOcation();
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                status.startResolutionForResult(GoogleMapsActivity.this, 1000);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }
                }
            });
        }
    }

    public Boolean GPSEnable() {
        GPSTracker gpsTracker = new GPSTracker(GoogleMapsActivity.this);
        if (gpsTracker.canGetLocation()) {
            return true;
        } else {
            return false;
        }
    }

}