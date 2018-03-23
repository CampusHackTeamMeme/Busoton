package meme.busoton;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import meme.busoton.comms.StopInfoRequest;
import meme.busoton.comms.data.BusStop;
import meme.busoton.comms.data.NextBus;

/**
 * Created by hb on 17/03/18.
 */

public class TabManager {
    AppCompatActivity context;

    private TabHost tabHost;

    public TabManager(AppCompatActivity context){
        this.context = context;

        tabHost = context.findViewById(R.id.tabhost);
        tabHost.setup();

        //add map tab
        TabHost.TabSpec spec = tabHost.newTabSpec("Map");
        spec.setContent(R.id.stops);
        spec.setIndicator("Map");
        tabHost.addTab(spec);

        //add live times tab
        spec = tabHost.newTabSpec("Live Times");
        spec.setContent(R.id.stopinfo);
        spec.setIndicator("Live Times");
        tabHost.addTab(spec);

        //add route tab
        spec = tabHost.newTabSpec("Route");
        spec.setContent(R.id.routes);
        spec.setIndicator("Route");
        tabHost.addTab(spec);

        //hide tab select
        //TabWidget tabs = context.findViewById(R.id.tabs);
    }

    public int getCurrentTab(){
        return tabHost.getCurrentTab();
    }

    public void showBusStop(BusStop bs){
        tabHost.setCurrentTabByTag("Live Times");
        new Thread(new Runnable() {
            @Override
            public void run() {
                new StopInfoRequest(bs.name, bs.stopID).fetch().addNextBus(bs);
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(bs.nextBus.size() == 0){
                            View v = context.getLayoutInflater().inflate(R.layout.reload_times, null);
                            ConstraintLayout parent = context.findViewById(R.id.parent);
                            parent.removeAllViews();

                            parent.addView(v);
                            Button reload = v.findViewById(R.id.reload);
                            reload.setOnClickListener(ev -> showBusStop(bs));
                        } else {
                            View v = context.getLayoutInflater().inflate(R.layout.live_times, null);
                            ConstraintLayout parent = context.findViewById(R.id.parent);

                            parent.removeAllViews();

                            parent.addView(v);
                            RecyclerView recycle = context.findViewById(R.id.recycle);
                            recycle.setHasFixedSize(true);

                            GridLayoutManager glm = new GridLayoutManager(context, 1);
                            recycle.setLayoutManager(glm);

                            RVAdapter adapter = new RVAdapter(bs.nextBus, context);
                            recycle.setAdapter(adapter);
                        }
                    }
                });
            }
        }).start();
    }
}
