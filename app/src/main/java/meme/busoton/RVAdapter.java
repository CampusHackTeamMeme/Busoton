package meme.busoton;

import android.support.v7.util.SortedList;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridLayout.LayoutParams;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import meme.busoton.comms.data.BusTime;
import meme.busoton.comms.data.NextBus;

/**
 * Created by hb on 19/03/18.
 */

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.TimesViewAdapter>{
    ArrayList<NextBus> buses;

    public RVAdapter (ArrayList<NextBus> buses){
        this.buses = buses;
        Collections.sort(this.buses);
    }

    @Override
    public TimesViewAdapter onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.bus_card, parent, false);
        return new TimesViewAdapter(v, parent);
    }

    @Override
    public void onBindViewHolder(TimesViewAdapter holder, int position) {
        NextBus bus = buses.get(position);

        holder.serviceName.setText(bus.serviceName);
        holder.operatorName.setText(bus.getPrettyOperator());

        LayoutParams params = new LayoutParams();

        for(int i=0; i<bus.times.size(); i++){
            Button button = new Button(holder.vg.getContext());
            button.setText(bus.times.get(i).time);
            //tv.setLayoutParams(params);
            holder.times.addView(button);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return buses.size();
    }

    public class TimesViewAdapter extends RecyclerView.ViewHolder {
        CardView cv;
        TextView serviceName;
        TextView operatorName;
        GridLayout times;
        ViewGroup vg;

        public TimesViewAdapter(View itemView, ViewGroup vg) {
            super(itemView);

            this.vg = vg;

            cv = itemView.findViewById(R.id.cardview);
            serviceName = itemView.findViewById(R.id.serviceName);
            operatorName = itemView.findViewById(R.id.operatorName);
            times = itemView.findViewById(R.id.timeGrid);
        }
    }
}
