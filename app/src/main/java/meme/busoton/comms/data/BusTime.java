package meme.busoton.comms.data;

import android.support.annotation.NonNull;

/**
 * Created by hb on 19/03/18.
 */

public class BusTime implements Comparable<BusTime> {
    public String time;

    public BusTime(String time){
        this.time = time;
    }


    @Override
    public int compareTo(@NonNull BusTime busTime) {
        if(this.time == "Due"){
            return 1;
        }

        if(busTime.time == "Due"){
            return -1;
        }

        return this.time.compareTo(busTime.time);
    }
}
