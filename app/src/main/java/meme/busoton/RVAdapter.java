package meme.busoton;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

import meme.busoton.comms.data.BusTime;
import meme.busoton.comms.data.NextBus;
import meme.busoton.notify.MyReceiver;

/**
 * Created by hb on 19/03/18.
 */

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.TimesViewAdapter>{
    ArrayList<NextBus> buses;
    Context context;

    public RVAdapter (ArrayList<NextBus> buses, Context context){
        this.buses = buses;
        Collections.sort(this.buses);

        this.context = context;
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

        for(int i=0; i<bus.times.size(); i++){
            Button button = new Button(holder.vg.getContext());
            String time = bus.times.get(i).time;
            button.setText(time);

            button.setOnClickListener(ev -> setupNotification(bus, time));
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

    public void setupNotification(NextBus bus, String time){
        if(time.equals("Due")){
            Toast.makeText(context, "You can't make a reminder for a bus that's due.", Toast.LENGTH_SHORT).show();
            return;
        }

        int busHour = Integer.parseInt(time.split(":")[0]);
        int busMinute = Integer.parseInt(time.split(":")[1]);

        new TimePickerDialog(
                context,
                (tp, hour, minute) -> setNotification(bus, hour, minute, time),
                busHour,
                busMinute,
                true
        ).show();
    }

    public void setNotification(NextBus bus, int hour, int minute, String time) {
        Calendar alarmTime = Calendar.getInstance();
        alarmTime.set(Calendar.HOUR_OF_DAY, hour);
        alarmTime.set(Calendar.MINUTE, minute);
        alarmTime.set(Calendar.SECOND, 0);

        long possibleTime = alarmTime.getTimeInMillis();

        if(possibleTime < System.currentTimeMillis()){
            //alarmTime.roll(Calendar.DATE, 1);
            possibleTime = alarmTime.getTimeInMillis();
        }

        Intent notifyIntent = new Intent(context,MyReceiver.class);
        notifyIntent.putExtra("stopName", bus.stopName);
        notifyIntent.putExtra("serviceName", bus.serviceName);
        notifyIntent.putExtra("operator", bus.operator);
        notifyIntent.putExtra("time", time);

        PendingIntent pendingIntent = PendingIntent.getBroadcast
                (context, 12343, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, possibleTime, pendingIntent);
        System.out.println("Alarm set for " + alarmTime.getTime().toString());
    }
}
