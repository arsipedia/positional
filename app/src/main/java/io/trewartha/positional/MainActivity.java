package io.trewartha.positional;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.crash.FirebaseCrash;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleApiClient.ConnectionCallbacks {

    private static final String ALTITUDE_UNIT_FEET = "feet";
    private static final String ALTITUDE_UNIT_METERS = "meters";
    private static final int REQUEST_CODE_ACCESS_FINE_LOCATION = 1;
    private static final int LOCATION_UPDATE_PRIORITY = LocationRequest.PRIORITY_HIGH_ACCURACY;
    private static final long LOCATION_UPDATE_INTERVAL = 1000; // ms

    @BindView(R.id.waiting_text_view) TextView waitingTextView;
    @BindView(R.id.altitude_text_view) TextView altitudeTextView;
    @BindView(R.id.altitude_unit_text_view) TextView altitudeUnitTextView;
    @BindView(R.id.coordinates_latitude_text_view) TextView latitudeTextView;
    @BindView(R.id.coordinates_longitude_text_view) TextView longitudeTextView;
    @BindView(R.id.ad_view) AdView adView;

    private String altitudeUnit;
    private GoogleApiClient googleApiClient;
    private Location location;
    private LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();

        locationRequest = LocationRequest.create()
                .setPriority(LOCATION_UPDATE_PRIORITY)
                .setInterval(LOCATION_UPDATE_INTERVAL);

        SharedPreferences sharedPreferences = getSharedPreferences(Prefs.NAME, Context.MODE_PRIVATE);
        altitudeUnit = sharedPreferences.getString(Prefs.KEY_ALTITUDE_UNIT, ALTITUDE_UNIT_FEET);
        populateLocationViews(altitudeUnit, location);

        loadBannerAd();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_ACCESS_FINE_LOCATION) {
            if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                FirebaseCrash.log("ACCESS_FINE_LOCATION permission granted");
                requestLocationUpdates();
            } else {
                FirebaseCrash.log("ACCESS_FINE_LOCATION permission request cancelled");
                new AlertDialog.Builder(this)
                        .setTitle(R.string.access_fine_location_permission_explanation_title)
                        .setMessage(R.string.access_fine_location_permission_explanation_message)
                        .setCancelable(false)
                        .setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                requestAccessFineLocationPermission();
                            }
                        })
                        .show();
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        FirebaseCrash.log("Google Play Services connection established");
        requestLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        FirebaseCrash.log("Google Play Services connection suspended");
        suspendLocationUpdates();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        FirebaseCrash.log("Google Play Services connection failed");
        new AlertDialog.Builder(this)
                .setTitle(R.string.google_play_services_connection_failed_title)
                .setMessage(R.string.google_play_services_connection_failed_message)
                .setPositiveButton(R.string.exit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // TODO: Log this to Firebase
                        MainActivity.this.finish();
                    }
                })
                .show();
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        if (location != null && location.hasAltitude()) {
            populateLocationViews(altitudeUnit, location);
        }
    }

    @OnClick(R.id.altitude_unit_text_view)
    public void onAltitudeUnitClicked() {
        altitudeUnit = altitudeUnit.equals(ALTITUDE_UNIT_FEET) ? ALTITUDE_UNIT_METERS : ALTITUDE_UNIT_FEET;
        FirebaseCrash.log("Switching altitude unit to " + altitudeUnit);
        populateLocationViews(altitudeUnit, location);
        saveAltitudeUnit(altitudeUnit);
    }

    private void populateLocationViews(@Nullable String altitudeUnit, @Nullable Location location) {
        final String latitudeText = String.format(Locale.getDefault(), "%.5f", location == null ? 0.0f : location.getLatitude());
        final String longitudeText = String.format(Locale.getDefault(), "%.5f", location == null ? 0.0f : location.getLongitude());
        final String altitudeText;
        final String altitudeUnitText;
        if (altitudeUnit == null || altitudeUnit.equals(ALTITUDE_UNIT_FEET)) {
            altitudeText = String.format(Locale.getDefault(), "%,d", (int) UnitConverter.metersToFeet(location == null ? 0.0 : location.getAltitude()));
            altitudeUnitText = getString(R.string.unit_feet);
        } else {
            altitudeText = String.format(Locale.getDefault(), "%,d", (int) (location == null ? 0.0 : location.getAltitude()));
            altitudeUnitText = getString(R.string.unit_meters);
        }
        waitingTextView.setVisibility(location == null ? View.VISIBLE : View.INVISIBLE);
        latitudeTextView.setText(latitudeText);
        longitudeTextView.setText(longitudeText);
        altitudeTextView.setText(altitudeText);
        altitudeUnitTextView.setText(altitudeUnitText);
    }

    private void requestAccessFineLocationPermission() {
        FirebaseCrash.log("Requesting permission for ACCESS_FINE_LOCATION");
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ACCESS_FINE_LOCATION);
    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            FirebaseCrash.log("ACCESS_FINE_LOCATION permission is needed");
            requestAccessFineLocationPermission();
        } else {
            FirebaseCrash.log("Requesting location updates");
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
    }

    private void suspendLocationUpdates() {
        FirebaseCrash.log("Suspending location updates");
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    private void saveAltitudeUnit(@NonNull String altitudeUnit) {
        FirebaseCrash.log("Saving altitude unit as " + altitudeUnit);
        final SharedPreferences.Editor editor = getSharedPreferences(Prefs.NAME, Context.MODE_PRIVATE).edit();
        editor.putString(Prefs.KEY_ALTITUDE_UNIT, altitudeUnit);
        editor.apply();
    }

    private void loadBannerAd() {
        MobileAds.initialize(getApplicationContext(), getString(R.string.ad_mob_app_id));
        final AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
        if (BuildConfig.DEBUG) {
            adRequestBuilder
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice(getString(R.string.ad_mob_device_id_mike_trewartha));
        }
        adView.loadAd(adRequestBuilder.build());
    }
}
