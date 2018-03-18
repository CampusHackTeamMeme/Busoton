package meme.busoton;

import android.app.FragmentTransaction;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.algo.GridBasedAlgorithm;
import com.google.maps.android.clustering.algo.NonHierarchicalDistanceBasedAlgorithm;
import com.google.maps.android.clustering.algo.PreCachingAlgorithmDecorator;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;

import meme.busoton.comms.StopRequest;
import meme.busoton.comms.data.BusStop;

/**
 * Created by hb on 17/03/18.
 */

public class MapManager implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback, GoogleMap.OnCameraMoveListener{
    AppCompatActivity context;
    GoogleMap gmap;

    private HashMap<String, BusStop> stops;
    private HashMap<String, Marker> markers;

    private ClusterManager<BusStop> busStopManager;

    private HashSet<BusStop> added;



    public MapManager(){
        stops = new HashMap<>();
        markers = new HashMap<>();
        added = new HashSet<>();
    }

    public void showMap(AppCompatActivity context) {
        this.context = context;

        MapFragment map = MapFragment.newInstance();
        FragmentTransaction fragmentTransaction = context.getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.stops, map);
        fragmentTransaction.commit();
        map.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap gmap) {
        this.gmap = gmap;
        gmap.setMyLocationEnabled(true);
        gmap.setOnCameraMoveListener(this);

        busStopManager = new ClusterManager<>(context, gmap);

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria locCriteria = new Criteria();
        locCriteria.setAccuracy(Criteria.NO_REQUIREMENT);
        locationManager.getBestProvider(locCriteria, false);

        locationManager.requestSingleUpdate(locCriteria, new BusLocationListener(), null);
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
                changed = true;
            }
        }

        if(changed){
            busStopManager.cluster();
        }
    }

    private int counter = 0;

    @Override
    public void onCameraMove() {
        if(counter++ > 20){
            updateStops();
            counter = 0;
        }
    }

    protected void updateStops(){
        LatLngBounds bounds = gmap.getProjection().getVisibleRegion().latLngBounds;
        new Thread(() -> new StopRequest(bounds, MapManager.this).fetch().addStops()).start();
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
