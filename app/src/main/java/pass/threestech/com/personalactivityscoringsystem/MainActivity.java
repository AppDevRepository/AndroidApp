package pass.threestech.com.personalactivityscoringsystem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpslocation);
        Intent passIntent = new Intent(this, GPSLocationActivity.class);
        startActivity(passIntent);
        finish();
    }
}
