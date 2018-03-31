package pass.threestech.com.services;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NetworkConnection extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int connectionStatus = NetworkUtil.getConnectionStatus(context);
            if (connectionStatus == NetworkUtil.TYPE_MOBILE ||
                    connectionStatus == NetworkUtil.TYPE_WIFI) {
                //data or wifi available
            } else {

             //no service available
            }
        }
    }

