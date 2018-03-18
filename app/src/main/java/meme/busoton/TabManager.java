package meme.busoton;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.widget.TabHost;

/**
 * Created by hb on 17/03/18.
 */

public class TabManager {
    AppCompatActivity context;

    public TabManager(AppCompatActivity context){
        this.context = context;

        TabHost tabHost = (TabHost) context.findViewById(R.id.tabhost);
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
    }
}
