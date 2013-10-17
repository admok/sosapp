package com.emergency.EasySOS;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

/**
 * @author: adrianm
 * Created Date:
 * Description:
 * Changes:
 */

public class Map extends Activity {

    private GoogleMap googleMap;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        setUpMapIfNeeded();
        GetCurrentLocation();
    }

    private void setUpMapIfNeeded() {
        if (googleMap == null) {

            Log.e("", "Into null map");
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

//            googleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(
//                    Map.this));

            if (googleMap != null) {
                Log.e("", "Into full map");
                googleMap.setMapType(googleMap.MAP_TYPE_NORMAL);
                googleMap.getUiSettings().setZoomControlsEnabled(true);
            }
        }
    }

    private void GetCurrentLocation() {

        double[] d = getlocation();
        double lat = d[0];
        double lng = d[1];

        googleMap
                .addMarker(new MarkerOptions()
                        .position(new LatLng(lat, lng))
                        .title("Current Location")
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.dot_blue)));

        googleMap
                .animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(lat, lng), 5));
    }

    public double[] getlocation() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = lm.getProviders(true);

        Location l = null;
        for (int i = 0; i < providers.size(); i++) {
            l = lm.getLastKnownLocation(providers.get(i));
            if (l != null)
                break;
        }
        double[] gps = new double[2];

        if (l != null) {
            gps[0] = l.getLatitude();
            gps[1] = l.getLongitude();
        }
        return gps;
    }
}
