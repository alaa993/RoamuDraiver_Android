package com.alaan.roamudriver.fragement;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.alaan.roamudriver.adapter.SearchUserAdapter;
import com.alaan.roamudriver.pojo.firebaseClients;
import com.fxn.stash.Stash;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.alaan.roamudriver.R;
import com.alaan.roamudriver.Server.Server;
import com.alaan.roamudriver.acitivities.HomeActivity;
import com.alaan.roamudriver.custom.GPSTracker;
import com.alaan.roamudriver.custom.Utils;
import com.alaan.roamudriver.pojo.Pass;
import com.alaan.roamudriver.pojo.PendingRequestPojo;
import com.alaan.roamudriver.session.SessionManager;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.thebrownarrow.permissionhelper.FragmentManagePermission;
import com.thebrownarrow.permissionhelper.PermissionResult;
import com.thebrownarrow.permissionhelper.PermissionUtils;
import com.google.android.libraries.places.api.Places;

import net.skoumal.fragmentback.BackFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

public class HomeFragment extends FragmentManagePermission implements OnMapReadyCallback, DirectionCallback,
        Animation.AnimationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, BackFragment, LocationListener, GoogleMap.OnMarkerClickListener {

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    public String NETWORK;
    public String ERROR = "error occured";
    public String TRYAGAIN;
    //    Boolean flag = false;
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1234;
    private PlacesClient placesClient;
    Pass pass;
    MapView mMapView;
    GoogleMap myMap;
    private int PLACE_PICKER_REQUEST = 7896;

    ImageView current_location, clear;

    int i = 0;
    String result = "";
    Animation animFadeIn, animFadeOut;
    String TAG = "home";
    LinearLayout linear_request;
    String permissionAsk[] = {PermissionUtils.Manifest_CAMERA, PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE, PermissionUtils.Manifest_READ_EXTERNAL_STORAGE, PermissionUtils.Manifest_ACCESS_FINE_LOCATION, PermissionUtils.Manifest_ACCESS_COARSE_LOCATION};
    CardView rides, earnings;
    private String driver_id = "";
    private String cost = "";
    private String unit = "";
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Double currentLatitude;
    private Double currentLongitude;
    private View rootView;
    private String check = "";
    private String drivername = "";
    private Marker my_marker;
    private boolean isShown = true;
    private RelativeLayout header, footer, footer2;
    TextView pickup_location, drop_location;
    RelativeLayout relative_drop;
    Button show_eringn;
    RelativeLayout linear_pickup;
    com.google.android.libraries.places.api.model.Place pickup;
    Place drop;
    TextView textView_today, textView_overall, textView_totalride;

    int ride_number;

    List<PendingRequestPojo> list;
    private List<Marker> markers;

    private LatLng origin;
    private LatLng destination;

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        NETWORK = getString(R.string.network_not_available);
        TRYAGAIN = getString(R.string.tryagian);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            rootView = inflater.inflate(R.layout.home_fragment, container, false);
            ((HomeActivity) getActivity()).fontToTitleBar(getString(R.string.home));
            bindView(savedInstanceState);
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
                        openSettingsApp(getActivity());
                    }
                });

            } else {
                if (!GPSEnable()) {
                    tunonGps();
                } else {
                    getCurrentlOcation();
                }
            }
        } catch (NullPointerException e) {
                            System.err.println("Null pointer exception");
                        } catch (Exception e) {

            //log.e("tag", "Inflate exception   " + e.toString());
        }

        linear_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                pass.setFromPlace(pickup);
                pass.setToPlace(drop);
                pass.setDriverId(driver_id);
                pass.setFare(cost);
                pass.setDriverName(drivername);
                bundle.putSerializable("data", pass);
                RequestFragment fragobj = new RequestFragment();
                fragobj.setArguments(bundle);
                ((HomeActivity) getActivity()).changeFragment(fragobj, "Request Ride");
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (header.getVisibility() == View.VISIBLE && footer.getVisibility() == View.VISIBLE) {
                    header.startAnimation(animFadeOut);
                    footer.startAnimation(animFadeOut);
                    header.setVisibility(View.GONE);
//                    footer.setVisibility(View.VISIBLE);
                }
            }
        });

        pickup_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    /* Intent intent =
                             new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                     .build(getActivity());
                     startActivityForResult(intent, PLACE_PICKER_REQUEST);*/


                List<com.google.android.libraries.places.api.model.Place.Field> fields = Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.ID, com.google.android.libraries.places.api.model.Place.Field.NAME, com.google.android.libraries.places.api.model.Place.Field.ADDRESS, com.google.android.libraries.places.api.model.Place.Field.LAT_LNG);

// Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(getActivity());
                startActivityForResult(intent, PLACE_PICKER_REQUEST);
            }
        });

        drop_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    /*  Intent intent =
                              new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                      .build(getActivity());
                      startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);

                  */

                List<com.google.android.libraries.places.api.model.Place.Field> fields = Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.ID, com.google.android.libraries.places.api.model.Place.Field.NAME, com.google.android.libraries.places.api.model.Place.Field.ADDRESS, com.google.android.libraries.places.api.model.Place.Field.LAT_LNG);

// Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(getActivity());
                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
            }
        });

        GetRides();
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("result");
                getCurrentlOcation();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }

        }
        if (requestCode == 1000) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("result");
                getCurrentlOcation();
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        } else if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                pickup = Autocomplete.getPlaceFromIntent(data);

                pickup_location.setText(pickup.getAddress());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                //log.e(TAG, status.toString());
                Toast.makeText(getActivity(), status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                drop = Autocomplete.getPlaceFromIntent(data);
                drop_location.setText(drop.getAddress());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(getActivity(), status.getStatusMessage(), Toast.LENGTH_LONG).show();

            }
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if (getActivity() != null && mMapView != null) {
                mMapView.onPause();
            }
            //by ibrahim
//            if (mGoogleApiClient != null) {
//                if (mGoogleApiClient.isConnected()) {
//                    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
//                    mGoogleApiClient.disconnect();
//                }
//            }
        } catch (NullPointerException e) {
                            System.err.println("Null pointer exception");
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
        } catch (NullPointerException e) {
                            System.err.println("Null pointer exception");
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
        } catch (NullPointerException e) {
                            System.err.println("Null pointer exception");
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
        } catch (NullPointerException e) {
                            System.err.println("Null pointer exception");
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
        } catch (NullPointerException e) {
                            System.err.println("Null pointer exception");
                        } catch (Exception e) {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (mMapView != null) {
                mMapView.onResume();
            }
            if (mGoogleApiClient != null) {
                mGoogleApiClient.connect();
            }
        } catch (NullPointerException e) {
                            System.err.println("Null pointer exception");
                        } catch (Exception e) {

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;
        myMap.setOnMarkerClickListener((GoogleMap.OnMarkerClickListener) this);
        myMap.setMaxZoomPreference(80);
//        GetRides();
//        requestDirection();
        //by ibrahim
        myMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(final Marker marker) {
                View v = null;
                if (getActivity() != null) {
                    v = getActivity().getLayoutInflater().inflate(R.layout.view_custom_marker, null);
                    TextView title = (TextView) v.findViewById(R.id.t);
                    TextView t1 = (TextView) v.findViewById(R.id.t1);
                    TextView t2 = (TextView) v.findViewById(R.id.t2);
                    Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "font/AvenirLTStd_Medium.otf");
                    t1.setTypeface(font);
                    t2.setTypeface(font);
                    String name = marker.getTitle();
                    title.setText(name);
                    String info = marker.getSnippet();
                    t1.setText(info);
//                    driver_id = (String) marker.getTag();
//                    drivername = marker.getTitle();
                }
                return v;
            }
        });
        if (myMap != null) {
            tunonGps();
        }
    }


    public void requestDirection() {
//        if (list.size()>0)
//        {
//            for (int i=0; i<list.size(); i++) {
//                String[] pickuplatlong = list.get(i).getpickup_location().split(",");
//                double pickuplatitude = Double.parseDouble(pickuplatlong[0]);
//                double pickuplongitude = Double.parseDouble(pickuplatlong[1]);
//                origin = new LatLng(pickuplatitude, pickuplongitude);
//
//                String[] droplatlong = list.get(i).getdrop_location().split(",");
//                double droplatitude = Double.parseDouble(droplatlong[0]);
//                double droplongitude = Double.parseDouble(droplatlong[1]);
//                destination = new LatLng(droplatitude, droplongitude);
//
//                GoogleDirection.withServerKey(getString(R.string.google_android_map_api_key))
//                        .from(origin)
//                        .to(destination)
//                        .transportMode(TransportMode.DRIVING)
//                        .execute(this);
//
//            }
//        }
        try {
//            Snackbar.make(rootView, getString(R.string.direct_requesting), Snackbar.LENGTH_SHORT).show();
        } catch (NullPointerException e) {
                            System.err.println("Null pointer exception");
                        } catch (Exception e) {

        }
        GoogleDirection.withServerKey(getString(R.string.google_android_map_api_key))
                .from(origin)
                .to(destination)
                .transportMode(TransportMode.DRIVING)
                .execute(this);
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        if (getActivity() != null) {
            if (direction.isOK()) {
                ArrayList<LatLng> directionPositionList = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
                myMap.addPolyline(DirectionConverter.createPolyline(getActivity(), directionPositionList, 5, Color.RED));
                myMap.addMarker(new MarkerOptions().position(new LatLng(origin.latitude, origin.longitude)).title("Pickup Location").snippet(list.get(i).getPickup_address()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                myMap.addMarker(new MarkerOptions().position(new LatLng(destination.latitude, destination.longitude)).title("Drop Location").snippet(list.get(i).getDrop_address()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
//                myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 10));
//                calculateDistance(Double.valueOf(direction.getRouteList().get(0).getLegList().get(0).getDistance().getValue()) / 1000);
            } else {
//                distanceAlert(direction.getErrorMessage());
                //calculateFare.setVisibility(View.GONE);
//                dismiss();
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

    public void bindView(Bundle savedInstanceState) {

        MapsInitializer.initialize(this.getActivity());
        markers = new ArrayList<Marker>();
        mMapView = (MapView) rootView.findViewById(R.id.hf_mapview);
        header = (RelativeLayout) rootView.findViewById(R.id.header);
        footer = (RelativeLayout) rootView.findViewById(R.id.footer2);
        footer2 = (RelativeLayout) rootView.findViewById(R.id.footer);
        textView_today = (TextView) rootView.findViewById(R.id.txt_todayearning);
        textView_overall = (TextView) rootView.findViewById(R.id.txt_overallearning);
        textView_totalride = (TextView) rootView.findViewById(R.id.txt_total_ridecount);

        footer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //log.i("ibrahim", "insideFooter2");
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", list.get(ride_number));
                AcceptRideFragment detailFragment = new AcceptRideFragment();
                detailFragment.setArguments(bundle);

                ((HomeActivity) getContext()).changeFragment(detailFragment, "Passenger Information");
            }
        });

        pickup_location = (TextView) rootView.findViewById(R.id.pickup_location);
        drop_location = (TextView) rootView.findViewById(R.id.drop_location);
        current_location = (ImageView) rootView.findViewById(R.id.current_location);
        linear_pickup = (RelativeLayout) rootView.findViewById(R.id.linear_pickup);
        relative_drop = (RelativeLayout) rootView.findViewById(R.id.relative_drop);
        clear = (ImageView) rootView.findViewById(R.id.clear);
        Places.initialize(getApplicationContext(), getString(R.string.google_android_map_api_key));
        pass = new Pass();
        show_eringn = (Button) rootView.findViewById(R.id.show_erning);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
        animFadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.dialogue_scale_anim_open);
        animFadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.dialogue_scale_anim_exit);
        animFadeIn.setAnimationListener(this);
        animFadeOut.setAnimationListener(this);
        linear_request = (LinearLayout) rootView.findViewById(R.id.linear_request);
        rides = (CardView) rootView.findViewById(R.id.cardview_totalride);
        earnings = (CardView) rootView.findViewById(R.id.earnings);
        placesClient = Places.createClient(getActivity());

        Utils.overrideFonts(getActivity(), rootView);
//        getEarningInfo();
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drop_location.setText("");
                if (header.getVisibility() == View.VISIBLE && footer.getVisibility() == View.VISIBLE) {
                    header.startAnimation(animFadeOut);
                    footer.startAnimation(animFadeOut);
                    header.setVisibility(View.GONE);
                    footer.setVisibility(View.GONE);
                }
            }
        });

        current_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    askCompactPermissions(permissionAsk, new PermissionResult() {
                        @Override
                        public void permissionGranted() {
                            if (pickup_location.getText().toString().trim().equals("")) {
                                setCurrentLocation();
                            } else {
                                pickup_location.setText("");
                                current_location.setColorFilter(ContextCompat.getColor(getActivity(), R.color.black));
                            }
                        }

                        @Override
                        public void permissionDenied() {

                        }

                        @Override
                        public void permissionForeverDenied() {
//                            Snackbar.make(rootView, getString(R.string.allow_permission), Snackbar.LENGTH_LONG).show();
                            openSettingsApp(getActivity());

                        }
                    });
                } else {
                    if (!GPSEnable()) {
                        tunonGps();
                    } else {
                        if (pickup_location.getText().toString().trim().equals("")) {
                            setCurrentLocation();
                        } else {
                            pickup_location.setText("");
                            current_location.setColorFilter(ContextCompat.getColor(getActivity(), R.color.black));
                        }
                    }
                }
            }
        });
//        show_eringn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (isShown) {
//                    isShown = false;
//                    header.setVisibility(View.GONE);
//                    rides.startAnimation(animFadeOut);
//                    rides.setVisibility(View.GONE);
//                    earnings.startAnimation(animFadeOut);
//                    footer2.setVisibility(View.VISIBLE);
//                } else {
//                    isShown = true;
//                    rides.setVisibility(View.VISIBLE);
//                    rides.startAnimation(animFadeIn);
//                    header.setVisibility(View.VISIBLE);
//                    footer2.setVisibility(View.GONE);
//                }
//
//
//            }
//        });
    }

    private void setCurrentLocation() {
        if (!GPSEnable()) {
            tunonGps();
        } else {
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                try {
                    /*@SuppressLint("MissingPermission") Task<PlaceLikelihoodBufferResponse> placeResult = mPlaceDetectionClient.getCurrentPlace(null);
                    placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                            try {
                                if (task.isSuccessful()) {
                                    PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
                                    for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                                        pickup = placeLikelihood.getPlace().freeze();
                                        pickup_location.setText(placeLikelihood.getPlace().getAddress());
                                        current_location.setColorFilter(ContextCompat.getColor(getActivity(), R.color.current_lolcation));

                                    }
                                    likelyPlaces.release();
                                }
                            } catch (NullPointerException e) {
                            System.err.println("Null pointer exception");
                        } catch (Exception e) {

                            }

                        }
                    });*/

                    // Use fields to define the data types to return.
                    List<com.google.android.libraries.places.api.model.Place.Field> placeFields = Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.NAME, com.google.android.libraries.places.api.model.Place.Field.ADDRESS, com.google.android.libraries.places.api.model.Place.Field.LAT_LNG);

// Use the builder to create a FindCurrentPlaceRequest.
                    FindCurrentPlaceRequest request =
                            FindCurrentPlaceRequest.builder(placeFields).build();

// Call findCurrentPlace and handle the response (first check that the user has granted permission).
                    if (ContextCompat.checkSelfPermission(getActivity(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
                        placeResponse.addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FindCurrentPlaceResponse response = task.getResult();
                                /*for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                                    //log.i(TAG, String.format("Place '%s' has likelihood: %f",
                                            placeLikelihood.getPlace().getName(),
                                            placeLikelihood.getLikelihood()));
                                    pickup = placeLikelihood.getPlace();
                                    pickup_location.setText(placeLikelihood.getPlace().getAddress());
                                    current_location.setColorFilter(ContextCompat.getColor(getActivity(), R.color.current_lolcation));

                                }
*/
                                if (response != null && response.getPlaceLikelihoods() != null) {
                                    PlaceLikelihood placeLikelihood = response.getPlaceLikelihoods().get(0);
                                    pickup = placeLikelihood.getPlace();
                                    //log.i("ibrahim", "gps");
                                    //log.i("ibrahim", placeLikelihood.getPlace().getLatLng().toString());

                                    pickup_location.setText(placeLikelihood.getPlace().getAddress());
                                    current_location.setColorFilter(ContextCompat.getColor(getActivity(), R.color.current_lolcation));

                                }
                            } else {
                                Exception exception = task.getException();
                                if (exception instanceof ApiException) {
                                    ApiException apiException = (ApiException) exception;
                                    //log.e(TAG, "Place not found: " + apiException.getStatusCode());
                                }
                            }
                        });
                    }
                } catch (NullPointerException e) {
                            System.err.println("Null pointer exception");
                        } catch (Exception e) {

                }


            }
        }
    }

    public void getEarningInfo() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
        progressDialog.show();
        RequestParams params = new RequestParams();
        if (Utils.haveNetworkConnection(getActivity())) {
            params.put("driver_id", SessionManager.getUserId());
        }
        Server.setHeader(SessionManager.getKEY());
        Server.get(Server.EARN, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                //log.e("earn", response.toString());
                try {
                    if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {
                        if (response.getJSONObject("data").getJSONObject("request").length() != 0) {
                            Gson gson = new Gson();

                            PendingRequestPojo pojo = gson.fromJson(response.getJSONObject("data").getJSONObject("request").toString(), PendingRequestPojo.class);
                            ((HomeActivity) getActivity()).setPojo(pojo);
                            ((HomeActivity) getActivity()).setStatus(pojo, "", false);

                        }


                        String today_earning = response.getJSONObject("data").getString("today_earning");
                        String week_earning = response.getJSONObject("data").getString("week_earning");
                        String total_earning = response.getJSONObject("data").getString("total_earning");
                        String total_rides = response.getJSONObject("data").getString("total_rides");

                        try {
                            String unit = response.getJSONObject("data").getString("unit");

                            //SessionManager.getInstance().setUnit(unit);
                        } catch (JSONException e) {

                        }

                        TextView textView_today = (TextView) rootView.findViewById(R.id.txt_todayearning);
//                        TextView textView_week = (TextView) rootView.findViewById(R.id.txt_weekearning);
                        TextView textView_overall = (TextView) rootView.findViewById(R.id.txt_overallearning);
                        TextView textView_totalride = (TextView) rootView.findViewById(R.id.txt_total_ridecount);

                        textView_today.setText(today_earning);
//                        textView_week.setText(week_earning);
                        textView_overall.setText(total_earning);
                        textView_totalride.setText(total_rides);


                    } else {
                        Toast.makeText(getActivity(), response.getString("data"), Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        });
    }

    @SuppressWarnings({"MissingPermission"})
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            android.location.Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (location == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            } else {
                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();

                if (myMap != null) {
                    myMap.clear();
                    my_marker = myMap.addMarker(new MarkerOptions().position(new LatLng(currentLatitude, currentLongitude)).title("You are here.").icon(BitmapDescriptorFactory.fromResource(R.drawable.taxi)));
                    my_marker.showInfoWindow();
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(currentLatitude, currentLongitude), 10);
                    myMap.animateCamera(cameraUpdate);

                    myMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng latLng) {
                            if (footer2.getVisibility() == View.VISIBLE) {
                                footer2.setVisibility(View.GONE);
                            }
                        }
                    });
                }
                setCurrentLocation(currentLatitude, currentLongitude);
            }
        } catch (NullPointerException e) {
                            System.err.println("Null pointer exception");
                        } catch (Exception e) {

        }

    }

    public void setCurrentLocation(final Double lat, final Double log) {
        try {
            my_marker.setPosition(new LatLng(lat, log));
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(currentLatitude, currentLongitude), 10);
            myMap.animateCamera(cameraUpdate);
            RequestParams par = new RequestParams();
            Server.setHeader(SessionManager.getKEY());
            par.put("user_id", SessionManager.getUserId());
            par.add("latitude", String.valueOf(currentLatitude));
            par.add("longitude", String.valueOf(currentLongitude));
            Server.post(Server.UPDATE, par, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                }

            });
        } catch (NullPointerException e) {
                            System.err.println("Null pointer exception");
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
                connectionResult.startResolutionForResult(getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);

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
                Toast.makeText(getActivity(), getString(R.string.couldnt_get_location), Toast.LENGTH_LONG).show();
            }
        }

    }

    public void getCurrentlOcation() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
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
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
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
                            // All location settings are satisfied. The client can
                            // initialize location
                            // requests here.
                            getCurrentlOcation();
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be
                            // fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling
                                // startResolutionForResult(),
                                // and checkky the result in onActivityResult().
                                status.startResolutionForResult(getActivity(), 1000);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have
                            // no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
        }
    }

    public Boolean GPSEnable() {
        GPSTracker gpsTracker = new GPSTracker(getActivity());
        if (gpsTracker.canGetLocation()) {
            return true;

        } else {
            return false;
        }
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public int getBackPriority() {
        return 0;
    }

    private void GetRides() {
        RequestParams params = new RequestParams();
        params.put("travel_id", "-1");
        params.put("ride_id", "-1");
        Server.setHeader(SessionManager.getKEY());
        Server.get(Server.GET_SEARCHUSER1, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                //log.e("success", response.toString());
                try {
                    Gson gson = new GsonBuilder().create();
                    list = gson.fromJson(response.getJSONArray("data").toString(), new TypeToken<List<PendingRequestPojo>>() {
                    }.getType());
                    try {
                        Snackbar.make(rootView, getString(R.string.direct_requesting), Snackbar.LENGTH_SHORT).show();
                    } catch (NullPointerException e) {
                            System.err.println("Null pointer exception");
                        } catch (Exception e) {
                    }

                    for (int i = 0; i < list.size(); i++) {
                        String[] pickuplatlong = list.get(i).getpickup_location().split(",");
                        double pickuplatitude = Double.parseDouble(pickuplatlong[0]);
                        double pickuplongitude = Double.parseDouble(pickuplatlong[1]);
                        origin = new LatLng(pickuplatitude, pickuplongitude);

                        String[] droplatlong = list.get(i).getdrop_location().split(",");
                        double droplatitude = Double.parseDouble(droplatlong[0]);
                        double droplongitude = Double.parseDouble(droplatlong[1]);
                        destination = new LatLng(droplatitude, droplongitude);
//                        my_marker = myMap.addMarker(new MarkerOptions().position(new LatLng(origin.latitude, origin.longitude)).title("User").icon(BitmapDescriptorFactory.fromResource(R.drawable.user_default)));
//                        myMap.addMarker(new MarkerOptions().position(new LatLng(destination.latitude, destination.longitude)).title("Drop Location").snippet(list.get(i).getDrop_address()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                        Marker myMarker = myMap.addMarker(new MarkerOptions().position(new LatLng(origin.latitude, origin.longitude)).title("Travel").snippet(list.get(i).getPickup_address()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                        markers.add(myMarker);
                    }

                } catch (JSONException e) {
                    //log.e("Get Data", e.getMessage());
                }
            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        //log.i("ibrahim", "insideMarker");
//        if (footer2.getVisibility() == View.GONE) {
//            footer2.setVisibility(View.VISIBLE);
//        } else {
//            footer2.setVisibility(View.GONE);
//        }
        textView_totalride.setText("");
        textView_today.setText("");
        textView_overall.setText("");
        //log.i("ibrahim", String.valueOf(markers.size()));
        for (int i = 0; i < markers.size(); i++) {
            //log.i("ibrahim", "beforeloop");
            //log.i("ibrahim", markers.get(i).getTitle().toString());

            if (marker.equals(markers.get(i))) {
                //log.i("ibrahim", "insideMarker");
                //log.i("ibrahim", "" + i);

                textView_totalride.setText(list.get(i).getUser_name());
                textView_today.setText(list.get(i).getPickup_address());
                textView_overall.setText(list.get(i).getDrop_address());
                footer2.setVisibility(View.VISIBLE);
                ride_number = i;
                break;
            }
        }

//        myMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker marker) {
//                String locAddress = marker.getTitle();
//                //log.i("ibrahim", "insideMarker");
//                //log.i("ibrahim", locAddress);
//                if (footer2.getVisibility() == View.GONE) {
//                    footer2.setVisibility(View.VISIBLE);
//                } else {
//                    footer2.setVisibility(View.GONE);
//                }
//                return true;
//            }
//        });
        return false;
    }
}