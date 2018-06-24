package pass.threestech.com.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 *
 */

public class PollAlarmReceiver extends BroadcastReceiver {
    public static final String ACTION_PUSH_LOCATION_ALARM =
            "pass.threestech.com.services.ACTION_PUSH_LOCATION_ALARM";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startIntent = new Intent(context, PollLocationService.class);
        context.startService(startIntent);
    }
}
