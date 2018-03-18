package meme.busoton.comms.data;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by hb on 17/03/18.
 */

public class BusStop implements ClusterItem{
    public LatLng position;
    public String name;
    public String stopID;

    public BusStop(String stopID, String name, LatLng position){
        this.stopID = stopID;
        this.name = name;
        this.position = position;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public String getSnippet() {
        return stopID;
    }

    @Override
    public int hashCode(){
        return stopID.hashCode();
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof BusStop){
            return ((BusStop) o).hashCode() == this.hashCode();
        } else {
            return false;
        }
    }
}
