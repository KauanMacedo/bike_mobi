package br.senai.sp.informatica.mobile.bikemobi.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
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
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import br.senai.sp.informatica.mobile.bikemobi.R;
import br.senai.sp.informatica.mobile.bikemobi.dao.AvaliacaoDao;
import br.senai.sp.informatica.mobile.bikemobi.dao.RotaPesquisadaDao;
import br.senai.sp.informatica.mobile.bikemobi.dao.RotaRealizadaDao;
import br.senai.sp.informatica.mobile.bikemobi.model.Avaliacao;
import br.senai.sp.informatica.mobile.bikemobi.model.RotaPesquisada;
import br.senai.sp.informatica.mobile.bikemobi.model.RotaRealizada;
import br.senai.sp.informatica.mobile.bikemobi.util.PermissionUtils;

public class NavActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClientLocation;

    private Location mLastLocation;
    private LocationRequest mLocationRequest;

    private String origem;
    private String destino;

    private int avalTraj;
    private int avalSeg;

    private int idLogin;
    private Location localInicial = null;
    private Location localAnterior = null;
    private int idRotaPesq;
    private int idRotaReal = 0;
    private float kilometragem = 0;

    private LatLng destinoLatLng;
    private ImageView fechar;
    private TextView rotaInfo;

    private boolean mPermissionDenied = false;

    private static final int overview = 0;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters

    private static final String TAG = MainActivity.class.getSimpleName();

    private RotaPesquisadaDao rotaPesqDao = RotaPesquisadaDao.instance;
    private RotaPesquisada rotaPesquisada;

    private RotaRealizadaDao rotaRealDao = RotaRealizadaDao.instance;
    private RotaRealizada rotaReal;

    private AvaliacaoDao avaliacaoDao = AvaliacaoDao.instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fechar = findViewById(R.id.ivFechar);
        rotaInfo = findViewById(R.id.tvNavRotaInfo);


        Intent intent = getIntent();
        if (intent != null) {
            Bundle dados = intent.getExtras();
            if (dados != null) {
                origem = dados.getString("origem");
                destino = dados.getString("destino");
                idLogin = dados.getInt ("login");
            }
        }

        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();
            createLocationRequest();

        }
        displayLocation();

        if (mGoogleApiClientLocation.isConnected()){
            //togglePeriodicLocationUpdates();
            startLocationUpdates();
        }


        //Toast.makeText(this, origem, Toast.LENGTH_SHORT).show();

        fechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //displayLocation();
                finish();
            }
        });





        //Toast.makeText(this, "" + origem, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClientLocation != null) {
            mGoogleApiClientLocation.connect();
            //Toast.makeText(this, mGoogleApiClientLocation.isConnected() + "", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();

        // Resuming the periodic location updates
        //if (mGoogleApiClientLocation.isConnected() && mRequestingLocationUpdates) {
        if (mGoogleApiClientLocation.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
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

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        /*
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        */

        if (!origem.isEmpty() && !destino.isEmpty()) {
            setupGoogleMapScreenSettings(googleMap);

            try {
                //DirectionsResult results = getDirectionsDetails("Diadema, São Paulo","Jabaquara, São Paulo",TravelMode.DRIVING);
                //DirectionsResult results = getDirectionsDetails("Diadema, São Paulo",destino,TravelMode.DRIVING);
                //DirectionsResult results = getDirectionsDetails(origem,"Jabaquara, São Paulo",TravelMode.DRIVING);
                DirectionsResult results = getDirectionsDetails(origem, destino, TravelMode.BICYCLING);
                if (results != null) {
                    addPolyline(results, googleMap);
                    positionCamera(results.routes[overview], googleMap);
                    addMarkersToMap(results, googleMap);
                    //Toast.makeText(this, "origem: " + origem + "\ndestino: " + destino, Toast.LENGTH_LONG).show();
                    displayLocation();

                    rotaInfo.setText(String.valueOf(results.routes[overview].legs[overview].duration)
                            + " (" + String.valueOf(results.routes[overview].legs[overview].distance) + ")");

                    rotaPesquisada = new RotaPesquisada();
                    rotaPesquisada.setDistancia(results.routes[overview].legs[overview].distance.inMeters);
                    rotaPesquisada.setDuracao(String.valueOf(results.routes[overview].legs[overview].duration));
                    rotaPesquisada.setDestinoEnd(String.valueOf(results.routes[overview].legs[overview].endAddress));
                    rotaPesquisada.setDestinoLat(results.routes[overview].legs[overview].endLocation.lat);
                    rotaPesquisada.setDestinoLng(results.routes[overview].legs[overview].endLocation.lng);
                    rotaPesquisada.setOrigemEnd(String.valueOf(results.routes[overview].legs[overview].startAddress));
                    rotaPesquisada.setOrigemLat(results.routes[overview].legs[overview].startLocation.lat);
                    rotaPesquisada.setOrigemLng(results.routes[overview].legs[overview].startLocation.lng);
                    rotaPesquisada.setPolylinePoints(String.valueOf(results.routes[overview].overviewPolyline));

                    rotaPesquisada.setIdLogin(idLogin);
                    rotaPesquisada.setCriadoEm(Calendar.getInstance().getTime());

                    idRotaPesq = rotaPesqDao.postRotaPesquisada(rotaPesquisada);
                    //Log.d("BikeLog", results.routes[overview].legs[overview].distance + "");

                }
            } catch (Exception e) {
                Log.d("BikeLog", "Não foi possível traçar a rota");
            }

        }

        enableMyLocation();
        //startLocationUpdates();


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

    private GeoApiContext getGeoContext() {
        GeoApiContext geoApiContext = new GeoApiContext();
        return geoApiContext
                .setQueryRateLimit(3)
                .setApiKey(getString(R.string.directionsApiKey))
                .setConnectTimeout(1, TimeUnit.SECONDS)
                .setReadTimeout(1, TimeUnit.SECONDS)
                .setWriteTimeout(1, TimeUnit.SECONDS);
    }

    private void addPolyline(DirectionsResult results, GoogleMap mMap) {
        List<LatLng> decodedPath = PolyUtil.decode(results.routes[overview].overviewPolyline.getEncodedPath());
        mMap.addPolyline(new PolylineOptions().addAll(decodedPath));
    }

    private void positionCamera(DirectionsRoute route, GoogleMap mMap) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(route.legs[overview].startLocation.lat, route.legs[overview].startLocation.lng), 12));
    }

    private void addMarkersToMap(DirectionsResult results, GoogleMap mMap) {
        mMap.addMarker(new MarkerOptions().position(new LatLng(results.routes[overview].legs[overview].startLocation.lat, results.routes[overview].legs[overview].startLocation.lng)).title(results.routes[overview].legs[overview].startAddress));
        mMap.addMarker(new MarkerOptions().position(new LatLng(results.routes[overview].legs[overview].endLocation.lat, results.routes[overview].legs[overview].endLocation.lng)).title(results.routes[overview].legs[overview].startAddress).snippet(getEndLocationTitle(results)));
    }
    private String getEndLocationTitle(DirectionsResult results) {
        return "Time :" + results.routes[overview].legs[overview].duration.humanReadable + " Distance :" + results.routes[overview].legs[overview].distance.humanReadable;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();

        //if (mRequestingLocationUpdates) {
        if (true) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClientLocation.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //Toast.makeText(this, "Falha", Toast.LENGTH_SHORT).show();
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
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClientLocation = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
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

            if (localInicial == null){
                localInicial = mLastLocation;
            }
            //Toast.makeText(this, latitude + ", " + longitude + mGoogleApiClientLocation.isConnected(), Toast.LENGTH_SHORT).show();

            Geocoder geocoder = new Geocoder(this);
            List<Address> listAddress;
            try {
                listAddress = geocoder.getFromLocationName(destino, 5);
                Address address = listAddress.get(0);
                float[] distancia = new float[1];
                Location.distanceBetween(mLastLocation.getLatitude(), mLastLocation.getLongitude()
                        , address.getLatitude(), address.getLongitude()
                , distancia);
                //Toast.makeText(this, ""+distancia[0], Toast.LENGTH_SHORT).show();
                if (localAnterior != null) {
                    float[] kmFloat = new float[1];
                    Location.distanceBetween(mLastLocation.getLatitude(), mLastLocation.getLongitude()
                            , localAnterior.getLatitude(), localAnterior.getLongitude()
                            , kmFloat);
                    kilometragem = kilometragem + kmFloat[0];
                }
                if (distancia[0] < 20l){
                    //Toast.makeText(this, "Você chegou!", Toast.LENGTH_SHORT).show();

                    CharSequence[] charTraj = {"Ótimo", "Bom", "Regular","Ruim", "Péssimo"};

                    final AlertDialog.Builder bTraj = new AlertDialog.Builder(this);
                    bTraj.setTitle("Você chegou! Avalie o trajeto.");
                    //alerta.setMessage("Avalie o trajeto");
                    bTraj.setSingleChoiceItems(charTraj, -1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            avalTraj = which + 1;

                            //Toast.makeText(NavActivity.this, avalTraj + "", Toast.LENGTH_SHORT).show();
                            CharSequence[] charSeg = {"Muito Seguro", "Seguro","Médio", "Perigoso", "Muito Perigoso"};
                            final AlertDialog.Builder bSeg = new AlertDialog.Builder(NavActivity.this);
                            bSeg.setTitle("Avalie a segurança.");
                            //alerta.setMessage("Avalie o trajeto");

                            bSeg.setSingleChoiceItems(charSeg, -1, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    avalSeg = which + 1;
                                    //Toast.makeText(NavActivity.this, avalSeg + "", Toast.LENGTH_SHORT).show();
                                }
                            });
                            bSeg.setNegativeButton("Sair", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    avaliar();
                                    finish();
                                }
                            });
                            bSeg.setPositiveButton("Compartilhar Passeio", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            bSeg.setIcon(R.mipmap.ic_launcher_bike_mobi);
                            bSeg.create();
                            bSeg.show();

                        }
                    });
                    bTraj.setNegativeButton("Sair", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    bTraj.setPositiveButton("Compartilhar Passeio", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    bTraj.setIcon(R.mipmap.ic_launcher_bike_mobi);
                    bTraj.create();
                    bTraj.show();

                    if (idRotaReal == 0) {
                        rotaReal = new RotaRealizada();
                        rotaReal.setLatInicio(localInicial.getLatitude());
                        rotaReal.setLatFim(localInicial.getLongitude());
                        rotaReal.setLatFim(mLastLocation.getLatitude());
                        rotaReal.setLngFim(mLastLocation.getLongitude());
                        //Todo: Ajustar duracao e km
                        Calendar dtAtual = Calendar.getInstance();
                        Calendar dtInicio = Calendar.getInstance();
                        dtInicio.setTime(rotaPesquisada.getCriadoEm());
                        long duracao = (dtAtual.getTimeInMillis() - dtInicio.getTimeInMillis()) / 1000 / 60;

                        rotaReal.setDuracaoString(duracao + " mins");
                        rotaReal.setDuracaoInt((int)duracao);
                        rotaReal.setKilometros((int)kilometragem);
                        rotaReal.setIdLogin(idLogin);
                        rotaReal.setIdRotaPesquisada(idRotaPesq);

                        idRotaReal = rotaRealDao.postRotaReal(rotaReal);
                    }
                }

            } catch (IOException e) {
                Log.d("BikeLog", "Não foi possível finalizar a rota");
            }
        } else {
            //Toast.makeText(this, "(Couldn't get the location. Make sure location is enabled on the device)", Toast.LENGTH_SHORT).show();

        }
        localAnterior = mLastLocation;
    }


    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            if (Build.VERSION.SDK_INT >= 23) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}
                        , LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
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
        Log.d("BikeLog", "startLocation executada.");
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClientLocation, this);
        Log.d("BikeLog", "stopLocation executada.");
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;

        //Toast.makeText(getApplicationContext(), "Location changed!", Toast.LENGTH_SHORT).show();

        // Displaying the new location on UI
        displayLocation();
    }

    public void avaliar(){
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setAvTrajeto(avalTraj);
        avaliacao.setAvSeguranca(avalSeg);
        avaliacao.setIdRotaRealizada(idRotaReal);
        avaliacao.setIdLogin(idLogin);

        avaliacaoDao.postAvaliacao(avaliacao);
    }
}
