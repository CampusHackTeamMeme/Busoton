package meme.busoton.comms.data;

import android.support.annotation.NonNull;
import android.support.v7.util.SortedList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

/**
 * Created by hb on 19/03/18.
 */

public class NextBus implements Comparable<NextBus>{
    public String serviceName;
    public String operator;
    public String destination;

    public ArrayList<BusTime> times;

    public NextBus(String serviceName, String operator, String destination){
        times = new ArrayList<>();

        this.serviceName = serviceName;
        this.operator = operator;
        this.destination = destination;
    }

    @Override
    public int compareTo(@NonNull NextBus nextBus) {
        return serviceName.compareTo(nextBus.serviceName);
    }

    public void addTime(BusTime bt){
        times.add(bt);
    }

    public String getPrettyOperator(){
        switch (operator){
            case "UNIL":
                return "Unilink";
            case "BLUS":
                return "Bluestar";
            case "FHAM":
                return "Fareham";
            default:
                return operator;
        }
    }
}
