package br.senai.sp.informatica.mobile.bikemobi.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;

import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import br.senai.sp.informatica.mobile.bikemobi.R;
import br.senai.sp.informatica.mobile.bikemobi.util.PermissionUtils;

import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;


public class RotaActivity extends AppCompatActivity
        implements
        OnMapReadyCallback,
        OnMyLocationButtonClickListener,
        OnMyLocationClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        LocationListener {

    private static final int overview = 0;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private GoogleMap mMap;
    private Location location;

    private boolean mPermissionDenied = false;

    private GoogleApiClient mGoogleApiClient;
    private String TAG = "GPLACES";
    private int PLACE_PICKER_REQUEST_DESTINO = 2;

    private EditText etOrigem;
    private EditText etDestino;
    private TextView tvRotaInfo;

    private String localAtual = "";

    private Location mLastLocation;

    private GoogleApiClient mGoogleApiClientLocation;

    private Button btLocAtual;
    private Button btLocAtualizar;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private boolean mRequestingLocationUpdates = false;

    private LocationRequest mLocationRequest;

    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rota);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        etOrigem = findViewById(R.id.etRotaOrigem);
        etDestino = findViewById(R.id.etRotaDestino);
        tvRotaInfo = findViewById(R.id.tvRotaInfo);
        btLocAtual = findViewById(R.id.btLocalAtual);
        btLocAtualizar = findViewById(R.id.btLocAtualizar);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        FloatingActionButton fab = findViewById(R.id.floatingActionButton3);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //displayLocation();
                //togglePeriodicLocationUpdates();
            }
        });

        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClientLocation();
            createLocationRequest();
        }

        etDestino.setFocusable(false);
        etDestino.setKeyListener(null);
        etDestino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPlace(v);
            }
        });

        etOrigem.setFocusable(false);

        btLocAtual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayLocation();
            }
        });
        btLocAtualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePeriodicLocationUpdates();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClientLocation != null) {
            mGoogleApiClientLocation.connect();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();

        // Resuming the periodic location updates
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdates();
    }

    private DirectionsResult getDirectionsDetails(String origin, String destination, TravelMode mode) {
        DateTime now = new DateTime();
        try {
            return DirectionsApi.newRequest(getGeoContext())
                    .mode(mode)
                    .origin(origin)
                    .destination(destination)
                    .departureTime(now)
                    .await();
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        if (!localAtual.isEmpty() && !etDestino.equals("Onde você gostaria de ir?")) {
            //if (!etDestino.getText().toString().isEmpty()) {
            setupGoogleMapScreenSettings(googleMap);
            //DirectionsResult results = getDirectionsDetails("Rua São Francisco de Assis, Diadema, Sao Paulo", "Jabaquara, Sao Paulo", TravelMode.BICYCLING);
            //DirectionsResult results = getDirectionsDetails(etOrigem.getText().toString(), etDestino.getText().toString(), TravelMode.BICYCLING);
            String origem = localAtual;
            String destino = etDestino.getText().toString();
            try {
                DirectionsResult results = getDirectionsDetails(origem, destino, TravelMode.BICYCLING);

                if (results != null) {
                    addPolyline(results, googleMap);
                    positionCamera(results.routes[overview], googleMap);
                    addMarkersToMap(results, googleMap);

                    tvRotaInfo.setText(String.valueOf(results.routes[overview].legs[overview].duration)
                            + " (" + String.valueOf(results.routes[overview].legs[overview].distance) + ")");
                    /*
                    Toast.makeText(this
                            , String.valueOf(results.routes[overview].legs[overview].duration)
                                    + "\n" + String.valueOf(results.routes[overview].legs[overview].distance)
                            , Toast.LENGTH_LONG).show();
                            */
                }
            } catch (Exception e) {
                Log.d("BikeLog", "Não foi possível traçar a rota");
            }

        }
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        enableMyLocation();


    }


    private void setupGoogleMapScreenSettings(GoogleMap mMap) {
        mMap.setBuildingsEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.setTrafficEnabled(false);
        UiSettings mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);
        mUiSettings.setRotateGesturesEnabled(true);
    }

    private void addMarkersToMap(DirectionsResult results, GoogleMap mMap) {
        mMap.addMarker(new MarkerOptions().position(new LatLng(results.routes[overview].legs[overview].startLocation.lat, results.routes[overview].legs[overview].startLocation.lng)).title(results.routes[overview].legs[overview].startAddress));
        mMap.addMarker(new MarkerOptions().position(new LatLng(results.routes[overview].legs[overview].endLocation.lat, results.routes[overview].legs[overview].endLocation.lng)).title(results.routes[overview].legs[overview].startAddress).snippet(getEndLocationTitle(results)));
    }

    private void positionCamera(DirectionsRoute route, GoogleMap mMap) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(route.legs[overview].startLocation.lat, route.legs[overview].startLocation.lng), 12));
    }

    private void addPolyline(DirectionsResult results, GoogleMap mMap) {
        List<LatLng> decodedPath = PolyUtil.decode(results.routes[overview].overviewPolyline.getEncodedPath());
        mMap.addPolyline(new PolylineOptions().addAll(decodedPath));
    }

    private String getEndLocationTitle(DirectionsResult results) {
        return "Time :" + results.routes[overview].legs[overview].duration.humanReadable + " Distance :" + results.routes[overview].legs[overview].distance.humanReadable;
    }

    private GeoApiContext getGeoContext() {
        GeoApiContext geoApiContext = new GeoApiContext();
        return geoApiContext
                .setQueryRateLimit(3)
                .setApiKey(getString(R.string.directionsApiKey))
                .setConnectTimeout(1, TimeUnit.SECONDS)
                .setReadTimeout(1, TimeUnit.SECONDS)
                .setWriteTimeout(1, TimeUnit.SECONDS);
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {

        location = mMap.getMyLocation();

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        String endereco;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            endereco = addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            endereco = "Endereço não encontrado";
        }
        Toast.makeText(this, "Sua localização atual é: \n\n" + endereco, Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void getPlace(View v) {
        Log.d("BikeLog", "id v:" + v.getId());
        Log.d("BikeLog", "id origem:" + R.id.etRotaOrigem);
        if (mGoogleApiClient == null || !mGoogleApiClient.isConnected())
            return;

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST_DESTINO);
        } catch (GooglePlayServicesRepairableException e) {
            Log.d(TAG, "GooglePlayServicesRepairableException thrown");
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.d(TAG, "GooglePlayServicesNotAvailableException thrown");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST_DESTINO && resultCode == RESULT_OK) {
            displayPlace(PlacePicker.getPlace(data, this));
        }
    }

    private void displayPlace(Place place) {
        if (place == null)
            return;

        StringBuilder builder = new StringBuilder();

        if (!TextUtils.isEmpty(place.getName())) {
            builder.append("Name: " + place.getName() + "\n");
        }
        if (!TextUtils.isEmpty(place.getAddress())) {
            builder.append("Address: " + place.getAddress() + "\n");
        }
        if (!TextUtils.isEmpty(place.getPhoneNumber())) {
            builder.append("Phone: " + place.getPhoneNumber());
        }

        location = mMap.getMyLocation();

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        String endereco;
        String enderecoLatLng;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            endereco = addresses.get(0).getAddressLine(0);
            //endereco = endereco.substring(endereco.indexOf("-") + 2, endereco.length());
            localAtual = String.valueOf(location.getLatitude()) + ", " + String.valueOf(location.getLongitude());
        } catch (IOException e) {
            endereco = "Endereço não encontrado";
        }
        etOrigem.setText(endereco);
        etDestino.setText(place.getAddress());
        onMapReady(mMap);
        Toast.makeText(this, builder.toString(), Toast.LENGTH_LONG).show();
        //tvPlace.setText(builder.toString());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    private void displayLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClientLocation);

        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();
            CameraUpdate update = CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
            mMap.moveCamera(update);
            mMap.animateCamera(zoom);

            Toast.makeText(this, latitude + ", " + longitude, Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "(Couldn't get the location. Make sure location is enabled on the device)", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClientLocation.connect();

    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    protected synchronized void buildGoogleApiClientLocation() {
        mGoogleApiClientLocation = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    private void togglePeriodicLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            // Changing the button text
            btLocAtualizar.setText("Parar Atualização de Local");

            mRequestingLocationUpdates = true;

            // Starting the location updates
            startLocationUpdates();

            Log.d(TAG, "Periodic location updates started!");

        } else {
            // Changing the button text
            btLocAtualizar.setText("Iniciar Atualização de Local");

            mRequestingLocationUpdates = false;

            // Stopping the location updates
            stopLocationUpdates();

            Log.d(TAG, "Periodic location updates stopped!");
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT); // 10 meters
    }

    protected void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClientLocation, mLocationRequest, this);

    }
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClientLocation, this);
    }


    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;

        Toast.makeText(getApplicationContext(), "Location changed!",
                Toast.LENGTH_SHORT).show();

        // Displaying the new location on UI
        displayLocation();
    }
}
