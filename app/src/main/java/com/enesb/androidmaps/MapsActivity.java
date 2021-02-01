package com.enesb.androidmaps;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this); // Bunu yazmazsan sorun cikar. Yukarida ki ile bagladik

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Konum dinleyicisi
        locationListener = new LocationListener() {
            @Override
            // Konum degistiginde
            public void onLocationChanged(@NonNull Location location) {
                /*
                mMap.clear(); // Haritanin üzerinde ki markerlari vs kaldirir.
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude()); // Enlem ve Boylam istiyor
                mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
                 */

                // Locale.getDefault() = Kullanici hangi dili kullaniyorsa ona göre calisir
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                try {
                    List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if (addressList != null && addressList.size() > 0){
                        System.out.println("Address: " + addressList.get(0).toString());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Eger konum erisimine izin verilmediyse
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else {
            // Izin verildiyse
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0,locationListener); // Bu kadar süre zarfinda location u yeniler

            Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER); // Son bilinen konumu alma

            if (lastLocation != null) {
                LatLng userLastLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                mMap.addMarker(new MarkerOptions().position(userLastLocation).title("Your Last Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLastLocation, 15));
            }



        }


        /*
        // Latitude = Enlem, Longitude = Boylam
        LatLng eiffel = new LatLng(48.85391, 2.2913515);
        mMap.addMarker(new MarkerOptions().position(eiffel).title("Eiffel Tower")); // Marker ekleme
        mMap.moveCamera(CameraUpdateFactory.newLatLng(eiffel)); // Kamerayi oynatma
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eiffel, 15)); // Zoomlama
        */
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (grantResults.length > 0) {
            if (requestCode == 1) {
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0,locationListener);
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onMapLongClick(LatLng latLng) { // Bir yere tiklandigi zaman oranin adresini gösterme
        mMap.clear(); // Her tiklamadan sonra temizleyecek mapi

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        String address = "";

        try {
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                if (addressList.get(0).getThoroughfare() != null) {
                    address += addressList.get(0).getThoroughfare();

                    if (addressList.get(0).getSubThoroughfare() != null) {
                        address += " " + addressList.get(0).getSubThoroughfare();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        mMap.addMarker(new MarkerOptions().position(latLng).title(address)); // Yukarida almis oldugumuz adresi marker olarak yazdirdik.
    }
}