package meme.busoton.map;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import meme.busoton.comms.data.BusStop;

/**
 * Created by hb on 19/03/18.
 */

public class NoClusteringRenderer extends DefaultClusterRenderer<BusStop> {
    public NoClusteringRenderer(Context context, GoogleMap map, ClusterManager<BusStop> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster<BusStop> cluster){
        return false;
    }
}
