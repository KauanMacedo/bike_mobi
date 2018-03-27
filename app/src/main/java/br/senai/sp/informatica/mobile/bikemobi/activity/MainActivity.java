package br.senai.sp.informatica.mobile.bikemobi.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
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
import com.google.android.gms.location.LocationServices;
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

import org.joda.time.DateTime;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import br.senai.sp.informatica.mobile.bikemobi.R;
import br.senai.sp.informatica.mobile.bikemobi.dao.LoginDao;
import br.senai.sp.informatica.mobile.bikemobi.dao.PerfilDao;
import br.senai.sp.informatica.mobile.bikemobi.model.Login;
import br.senai.sp.informatica.mobile.bikemobi.model.Perfil;
import br.senai.sp.informatica.mobile.bikemobi.util.PermissionUtils;


/**
 * Created by rodol on 10/03/2018.
 */

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks{

    private PerfilDao dao = PerfilDao.instance;
    private Perfil perfil;

    private LoginDao loginDao = LoginDao.instance;
    private Login login;

    private TextView tvNavHeaderNome;
    private TextView tvNavHeaderEmail;

    private final static int ATUALIZA_PERFIL = 0;

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
    private String clique;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        tvNavHeaderNome = header.findViewById(R.id.tvNavHeaderNome);
        tvNavHeaderEmail = header.findViewById(R.id.textViewEditarPerfilNavDrawer);

        getDados();

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
                Intent intent = new Intent(v.getContext(), NavActivity.class);
                if (!etOrigem.getText().toString().equals("Seu Local")) {
                    intent.putExtra("origem", etOrigem.getText().toString());
                } else {
                    intent.putExtra("origem", localAtual);
                }
                intent.putExtra("destino", etDestino.getText().toString());
                intent.putExtra("login", login.getId());
                if (!etDestino.getText().toString().equals("Onde você gostaria de ir?")) {
                    startActivity(intent);
                } else {
                    Toast.makeText(v.getContext(), "Escolha um destino.", Toast.LENGTH_LONG).show();
                }
            }
        });

        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClientLocation();
        }

        etDestino.setFocusable(false);
        etDestino.setKeyListener(null);
        etDestino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clique = "destino";
                getPlace(v);
            }
        });

        etOrigem.setFocusable(false);
        etOrigem.setKeyListener(null);
        etOrigem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clique = "origem";
                getPlace(v);
            }
        });

        btLocAtual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayLocation();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        tvNavHeaderNome.setText(preferences.getString(this.getResources().getString(R.string.nome_perfil_key), this.getResources().getString(R.string.nome_perfil_default)));
        tvNavHeaderEmail.setText(preferences.getString(this.getResources().getString(R.string.email_perfil_key), this.getResources().getString(R.string.email_perfil_default)));

        if (mGoogleApiClientLocation != null) {
            mGoogleApiClientLocation.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        switch (id) {
            case R.id.nav_drawer_perfil:
                intent = new Intent(this, PerfilActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_drawer_cadastro:
                intent = new Intent(this, CadastroActivity.class);
                startActivityForResult(intent,ATUALIZA_PERFIL);
                break;

            case R.id.nav_drawer_historico:
                intent = new Intent(this, HistoricoActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_drawer_avaliacao:
                intent = new Intent(this, AvaliacaoActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_drawer_rota:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_drawer_bt:
                intent = new Intent(this, BluetoothActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_drawer_bt2:
                intent = new Intent(this, BluetoothV2Activity.class);
                startActivity(intent);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ATUALIZA_PERFIL:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, "Perfil atualizado", Toast.LENGTH_SHORT).show();
                    getDados();
                }
        }
        if (requestCode == PLACE_PICKER_REQUEST_DESTINO && resultCode == RESULT_OK) {
            displayPlace(PlacePicker.getPlace(data, this));
        }
    }

    public void getDados() {
        perfil = dao.getPerfil(1l);
        login = loginDao.getLogin(1l);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();

        if (perfil != null) {
            editor.putString(this.getResources().getString(R.string.nome_perfil_key), perfil.getNome());

        }

        if (login != null) {
            editor.putString(this.getResources().getString(R.string.email_perfil_key), login.getEmail());
        }
        editor.apply();
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

        googleMap.clear();
        if (!localAtual.isEmpty() && !etDestino.equals("Onde você gostaria de ir?")) {
            setupGoogleMapScreenSettings(googleMap);
            String origem;
            if (etOrigem.getText().toString().equals("Seu Local")) {
                origem = localAtual;
            } else {
                origem = etOrigem.getText().toString();
            }
            String destino = etDestino.getText().toString();
            try {
                DirectionsResult results = getDirectionsDetails(origem, destino, TravelMode.BICYCLING);

                if (results != null) {
                    addPolyline(results, googleMap);
                    positionCamera(results.routes[overview], googleMap);
                    addMarkersToMap(results, googleMap);

                    tvRotaInfo.setText(String.valueOf(results.routes[overview].legs[overview].duration)
                            + " (" + String.valueOf(results.routes[overview].legs[overview].distance) + ")");

                    displayLocation();
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
        //Toast.makeText(this, "Entrou",Toast.LENGTH_SHORT).show();

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
        //Toast.makeText(this, "Sua localização atual é: \n\n" + endereco, Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        //Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
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

        if (clique.equals("destino")){
            //etOrigem.setText(endereco);
            etDestino.setText(place.getAddress());
        } else {
            etOrigem.setText(place.getAddress());
        }

        onMapReady(mMap);
        //Toast.makeText(this, builder.toString(), Toast.LENGTH_LONG).show();
        //tvPlace.setText(builder.toString());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();


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

            //Toast.makeText(this, latitude + ", " + longitude, Toast.LENGTH_SHORT).show();

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


}