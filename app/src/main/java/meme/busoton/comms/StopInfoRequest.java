package meme.busoton.comms;

import android.support.v7.util.SortedList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import meme.busoton.comms.data.BusStop;
import meme.busoton.comms.data.BusTime;
import meme.busoton.comms.data.NextBus;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by hb on 17/03/18.
 */

public class StopInfoRequest {
    private static final String timetableRequestURL = "https://bus.joshp.tech/api/timetable";

    private String stopID;
    private ArrayList<NextBus> nextBus;

    public StopInfoRequest(String stopID){
        this.stopID = stopID;
        nextBus = new ArrayList<>();
    }

    private FormBody makeForm() throws JSONException {
        FormBody.Builder builder = new FormBody.Builder()
                .add("stop", stopID);

        return builder.build();
    }


    public StopInfoRequest fetch(){
        try{
            OkHttpClient client = new OkHttpClient.Builder()
                    .followRedirects(true)
                    .build();

            FormBody body = makeForm();

            Request request = new Request.Builder()
                    .url(timetableRequestURL)
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            getTimes(response);
            return this;
        } catch (JSONException | IOException e){
            return this;
        }
    }

    public void addNextBus(BusStop bs){
        bs.addNextBus(nextBus);
    }

    public void getTimes(Response response){
        try {
            //System.out.println(response.body().string());
            JSONObject jsonResponse = new JSONObject(response.body().string());
            Iterator<?> keys = jsonResponse.keys();

            while(keys.hasNext()){
                String serviceName = (String) keys.next();
                if(jsonResponse.get(serviceName) instanceof JSONObject){
                    JSONObject current = jsonResponse.getJSONObject(serviceName);

                    String operator = current.getString("operator");
                    String destination = current.getString("destination");

                    NextBus nb = new NextBus(serviceName, operator, destination);

                    JSONArray time = current.getJSONArray("time");

                    for(int i=0; i<time.length(); i++){
                        nb.addTime(new BusTime(time.getString(i)));
                    }

                    nextBus.add(nb);
                }
            }
        } catch (IOException | JSONException | IllegalStateException e){
        }
    }
}
