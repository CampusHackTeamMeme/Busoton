package meme.busoton.notify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by hb on 22/03/18.
 */

public class MyReceiver extends BroadcastReceiver {
    public MyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("Alarm went off");

        Intent intent1 = new Intent(context, MyNewIntentService.class);

        //get data
        intent1.putExtra("stopName", intent.getStringExtra("stopName"));
        intent1.putExtra("serviceName", intent.getStringExtra("serviceName"));
        intent1.putExtra("operator", intent.getStringExtra("operator"));
        intent1.putExtra("time", intent.getStringExtra("time"));

        context.startService(intent1);
    }
}