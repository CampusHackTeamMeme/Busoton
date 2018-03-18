package meme.busoton.comms;

import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayDeque;

import meme.busoton.Main;
import meme.busoton.MapManager;
import meme.busoton.comms.data.BusStop;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by hb on 17/03/18.
 */

public class StopRequest {
    private static final String stopRequestURL = "http://192.168.0.33:8080/api/busstops";

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private LatLngBounds bounds;
    private MapManager mapManager;

    public ArrayDeque<BusStop> stops;



    public StopRequest(LatLngBounds bounds, MapManager mapManager){
        this.bounds = bounds;
        this.mapManager = mapManager;

        stops = new ArrayDeque<>();
    }

    private FormBody makeForm() throws JSONException {
        FormBody.Builder builder = new FormBody.Builder()
        .add("startLon", Double.toString(bounds.southwest.longitude))
        .add("startLat", Double.toString(bounds.southwest.latitude))
        .add("endLon", Double.toString(bounds.northeast.longitude))
        .add("endLat", Double.toString(bounds.northeast.latitude));

        return builder.build();
    }

    public StopRequest fetch() {
        try{
            OkHttpClient client = new OkHttpClient();

            RequestBody body = makeForm();
            Request request = new Request.Builder()
                    .url(stopRequestURL)
                    .method("get", body)
                    .build();

            Response response = client.newCall(request).execute();
            buildBusStops(response);
            return this;
        } catch (JSONException | IOException e){
            return this;
        }
    }

    public void buildBusStops(Response response) {
        try {
            JSONObject jsonResponse = new JSONObject(response.body().string());
            JSONArray data = jsonResponse.getJSONArray("data");

            for(int i=0; i<data.length(); i++){
                JSONObject current = data.getJSONObject(i);

                String stopID = current.getString("stop_id");
                String name = current.getString("name");
                Double lat = current.getDouble("lat");
                Double lon = current.getDouble("lon");

                stops.add(new BusStop(stopID, name, new LatLng(lat,lon)));
            }
        } catch (IOException | JSONException | IllegalStateException e){
        }
    }

    public void addStops(){
        new Thread(() -> mapManager.populateMap(stops)).start();
    }
}
