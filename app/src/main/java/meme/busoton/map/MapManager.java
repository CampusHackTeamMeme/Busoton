package meme.busoton.map;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;

import meme.busoton.Main;
import meme.busoton.R;
import meme.busoton.comms.StopRequest;
import meme.busoton.comms.data.BusStop;

/**
 * Created by hb on 17/03/18.
 */

public class MapManager implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback, GoogleMap.OnCameraMoveListener, GoogleMap.InfoWindowAdapter, GoogleMap.OnInfoWindowClickListener {
    Main context;
    GoogleMap gmap;

    private HashMap<String, BusStop> stops;

    private ClusterManager<BusStop> busStopManager;

    private HashSet<BusStop> added;


    public MapManager() {
        stops = new HashMap<>();
        added = new HashSet<>();
    }

    public BusStop getStop(String stopID) {
        return stops.get(stopID);
    }

    public MapManager showMap(Main context) {
        this.context = context;

        MapFragment map = MapFragment.newInstance();
        FragmentTransaction fragmentTransaction = context.getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.stops, map);
        fragmentTransaction.commit();
        map.getMapAsync(this);

        return this;
    }

    @Override
    public void onMapReady(GoogleMap gmap) {
        this.gmap = gmap;

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

            }

            if (ActivityCompat.shouldShowRequestPermissionRationale(context,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {


                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(context,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    123);
            }
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        } else {
            gmap.setMyLocationEnabled(true);
        }
        gmap.setOnCameraMoveListener(this);

        //setup clustering
        busStopManager = new ClusterManager<>(context, gmap);
        busStopManager.setRenderer(new NoClusteringRenderer(context, gmap, busStopManager));

        //setup info windows
        gmap.setInfoWindowAdapter(this);
        gmap.setOnInfoWindowClickListener(this);

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria locCriteria = new Criteria();
        locCriteria.setAccuracy(Criteria.NO_REQUIREMENT);
        locationManager.getBestProvider(locCriteria, false);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        } else {
            locationManager.requestSingleUpdate(locCriteria, new BusLocationListener(), null);
        }
    }

    @Override
    public void onMapLoaded() {

    }

    public void populateMap(ArrayDeque<BusStop> stops){
        context.runOnUiThread(() -> actuallyPopulateMap(stops));
    }

    private void actuallyPopulateMap(ArrayDeque<BusStop> stops){
        boolean changed = false;

        for(BusStop bs: stops){
            if(added.add(bs)){
                busStopManager.addItem(bs);
                this.stops.put(bs.stopID, bs);
                changed = true;
            }
        }

        if(changed){
            busStopManager.cluster();
        }
    }

    private int counter = 0;
    private boolean showToast = true;

    @Override
    public void onCameraMove() {
        if(counter++ > 20){
            updateStops();
            counter = 0;
        }

        if(gmap.getCameraPosition().zoom <= 14){
            added = new HashSet<>();
            busStopManager.clearItems();
            busStopManager.cluster();
            if(showToast) {
                Toast.makeText(context, "Zoom in to see bus stops", Toast.LENGTH_SHORT).show();
                showToast = false;
            }
        }
    }

    protected void updateStops(){
        if (gmap.getCameraPosition().zoom > 14) {
            showToast = true;
            LatLngBounds bounds = gmap.getProjection().getVisibleRegion().latLngBounds;
            new Thread(() -> new StopRequest(bounds, MapManager.this).fetch().addStops()).start();
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View v = context.getLayoutInflater().inflate(R.layout.marker_info, null);

        TextView name = v.findViewById(R.id.bus_stop_name);

        // Setting the longitude
        name.setText(marker.getTitle());

        // Returning the view containing InfoWindow contents
        return v;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        //the snippet is the stopID
        context.showBusStop(marker.getSnippet());
    }

    private class BusLocationListener implements LocationListener{

        @Override
        public void onLocationChanged(Location location) {
            if(location != null){
                gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16));
            }

            updateStops();
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }
}
