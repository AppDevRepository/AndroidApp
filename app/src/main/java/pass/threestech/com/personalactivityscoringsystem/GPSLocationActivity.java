package pass.threestech.com.personalactivityscoringsystem;

import android.Manifest;
import android.app.DialogFragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.view.View.OnClickListener;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import pass.threestech.com.services.Constants;
import pass.threestech.com.services.NetworkUtil;
import pass.threestech.com.services.PollLocationService;


public class GPSLocationActivity extends AppCompatActivity implements ChangePushTimeFragment.DialogListener {
    // Preferences
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    // App info
    private int mTimeToPoll = 1;
    private boolean mAlreadyRunning;

    // Check connection
    private ConnectionStatusReceiver mReceiver = new ConnectionStatusReceiver();
    private IntentFilter iFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);

    // Maps variables
    private double latitude;
    private double longitude;
    DecimalFormat dcoOrdinates = new DecimalFormat(".####");
    private boolean isGPSenabled;
    private boolean isNetworkEnabled;

    private LocationListener _locationListener;
    private LocationManager _locationManager;

    private TextView mCurrentLocationTextview;
    private Button mStartStopLocationUpdatesbutton;
    private Button mPollIntervalButton;
    private Button mViewLocationUpdates;

    Map<String, Object> mHashMapLocationData = new HashMap<String, Object>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpslocation);

        mCurrentLocationTextview = (TextView) findViewById(R.id.CurrentLocation_textview);

        // Instantiate shared preferences
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mSharedPreferences.edit();

        mAlreadyRunning = mSharedPreferences.getBoolean(Constants.SERVICE_RUNNING, false);

        mTimeToPoll = mSharedPreferences.getInt(Constants.POLL_TIME, 1);

        SetupButtonClickEvents();

        // Get the location given by the system
        _locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

       isGPSenabled = _locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = _locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        // Check if Google Maps is installed
        if (isGoogleMapsInstalled()) {
            // Create a location that updates when the location has changed
            _locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    //if(location.getAccuracy()<=20){
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        mCurrentLocationTextview.setText("");
                        mCurrentLocationTextview.append("Latitude: " + dcoOrdinates.format(location.getLatitude()) + "\n Longitude: " + dcoOrdinates.format(location.getLongitude()));
                    //}
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                }

            };
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}, 1);
                    return;
                } else {
                    locationUpdates();
                }
            } else {
                locationUpdates();
            }
        }

    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    locationUpdates();
                return;
        }

    }

    private void locationUpdates() {
        // Set the listener to the location manager
        if (isGPSenabled){
            _locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, _locationListener);
        }else
        {
            _locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, _locationListener);
        }

    }


    private void SetupButtonClickEvents() {
        mStartStopLocationUpdatesbutton = (Button) findViewById(R.id.Stop_LocationUpdates_button);
        mPollIntervalButton = (Button) findViewById(R.id.poll_interval_button);
        mViewLocationUpdates = (Button) findViewById(R.id.View_LocationUpdates_button);

        // Open the time dialog
            mPollIntervalButton.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ChangePushTimeFragment fragment = new ChangePushTimeFragment();
                    fragment.show(getFragmentManager(), "DIALOG_PUSH");
                }
            });

        mStartStopLocationUpdatesbutton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ButtonText = mStartStopLocationUpdatesbutton.getText().toString();
                if (ButtonText.contains("Start") || ButtonText.contains("Push")) {
                    updatePushButton();
                    mEditor.putInt(Constants.POLL_TIME, mTimeToPoll);
                    startRepeatingService();
                    mEditor.putBoolean(Constants.SERVICE_RUNNING, true);
                    createNotification();
                    mStartStopLocationUpdatesbutton.setText("Stop Location Updates");
                } else {
                    mStartStopLocationUpdatesbutton.setText("Start Location Updates");
                    mTimeToPoll = 0;
                    mEditor.putInt(Constants.POLL_TIME, mTimeToPoll);
                    mEditor.putBoolean(Constants.SERVICE_RUNNING, false);
                    stopService(new Intent(GPSLocationActivity.this, PollLocationService.class));
                    deleteNotification();
                }
                mEditor.apply();
            }
        });

        mViewLocationUpdates.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GPSLocationActivity.this, ViewLocationUpdatesActivity.class);
                startActivity(intent);
            }

        });
    }

    // Check if Google Maps is installed
    public boolean isGoogleMapsInstalled() {
        try {
            ApplicationInfo info = getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
    // Start the service
    public void startRepeatingService() {
        startService(new Intent(this, PollLocationService.class));
    }

    // Update the button to show the correct message
    public void updatePushButton() {
        String buttonMsg;
        if (mTimeToPoll < 60) {
            buttonMsg = getResources()
                    .getQuantityString(R.plurals.pushes_seconds, mTimeToPoll, mTimeToPoll);
        } else {
            buttonMsg = getResources()
                    .getQuantityString(R.plurals.pushes_minutes, mTimeToPoll / 60, mTimeToPoll / 60);
        }
        mStartStopLocationUpdatesbutton.setText(buttonMsg);
    }

    // Method from the dialog to handle the click
    //@Override
    public void onDialogPositiveClick(DialogFragment dialog, int which) {
        if (which == 0) {
            mTimeToPoll = 1;
        } else if (which == 1) {
            mTimeToPoll = 10;
        } else if (which == 2) {
            mTimeToPoll = 30;
        } else if (which == 3) {
            mTimeToPoll = 60;
        }

        // Add the time to push in the preferences
        mEditor.putInt(Constants.POLL_TIME, mTimeToPoll);
        mEditor.apply();

        // Update the text from the button
        updatePushButton();


    }

    @Override
    protected void onPause() {
        this.unregisterReceiver(mReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        this.registerReceiver(mReceiver, iFilter);
        super.onResume();
    }

    // Create the notification to notify the user that the service is running
    public void createNotification() {
        String ns = Context.NOTIFICATION_SERVICE;
        String notificationTitle = getString(R.string.notification_title);

        Intent intent = new Intent(this, GPSLocationActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id)).setSmallIcon(android.R.color.transparent).setContentTitle(notificationTitle).setContentIntent(pendingIntent);

        // Build the notification
        Notification notificationCompat = notification.build();

        // Create the manager
        NotificationManager notificationManager = (NotificationManager) getSystemService(ns);
        notificationCompat.flags |= Notification.FLAG_ONGOING_EVENT;

        // Push the notification
        notificationManager.notify(Constants.NOTIFICATION_ID, notificationCompat);
    }

    // Delete the notification
    public void deleteNotification() {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager notificationManager = (NotificationManager) getSystemService(ns);
        notificationManager.cancel(Constants.NOTIFICATION_ID);
    }

    public class ConnectionStatusReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int connectionStatus = NetworkUtil.getConnectionStatus(context);
            if (connectionStatus == NetworkUtil.TYPE_MOBILE || connectionStatus == NetworkUtil.TYPE_WIFI) {
                mPollIntervalButton.setEnabled(true);

            } else {
                if (mSharedPreferences.getBoolean(Constants.SERVICE_RUNNING, false)) {
                    deleteNotification();
                }
                mPollIntervalButton.setEnabled(false);
                mEditor.putBoolean(Constants.SERVICE_RUNNING, false);
                mEditor.apply();
            }
        }
    }

}
