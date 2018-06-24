package pass.threestech.com.personalactivityscoringsystem;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pass.threestech.com.services.GPSLocationCoordinates;
import pass.threestech.com.services.PollLocationService;

public class ViewLocationUpdatesActivity extends AppCompatActivity implements View.OnClickListener {


    DecimalFormat dcoOrdinates = new DecimalFormat(".####");
    private String TAG;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewlocationupdates);
        addHeaders();
        addData();
    }

    private TextView getTextView(int id, String title, int color, int typeface, int bgColor) {
        TextView tv = new TextView(this);
        tv.setId(id);
        tv.setText(title);
        tv.setTextColor(color);
        tv.setPadding(40, 40, 40, 40);
        tv.setTypeface(Typeface.DEFAULT, typeface);
        tv.setBackgroundColor(bgColor);
        tv.setLayoutParams(getLayoutParams());
        tv.setOnClickListener(this);
        return tv;
    }

    @NonNull
    private LayoutParams getLayoutParams() {
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(2, 0, 0, 2);
        return params;
    }

    @NonNull
    private TableLayout.LayoutParams getTblLayoutParams() {
        return new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    /**
     * This function adds the headers to the table
     **/
    public void addHeaders() {
        TableLayout tl = findViewById(R.id.table);
        TableRow tr = new TableRow(this);
        tr.setLayoutParams(getLayoutParams());
        tr.addView(getTextView(0, "DATE TIME STAMP", Color.WHITE, Typeface.BOLD, Color.BLUE));
        tr.addView(getTextView(0, "LATITUDE", Color.WHITE, Typeface.BOLD, Color.BLUE));
        tr.addView(getTextView(0, "LONGITUDE", Color.WHITE, Typeface.BOLD, Color.BLUE));
        tl.addView(tr, getTblLayoutParams());
    }

    /**
     * This function adds the data to the table
     **/
    public void addData() {
        TableLayout tl = findViewById(R.id.table);
        try {
            List<GPSLocationCoordinates> mGPSCoordinates = (List<GPSLocationCoordinates>) InternalStorage.readObject(this.getApplicationContext(), "GPSCoordinates");
                for (GPSLocationCoordinates mloc : mGPSCoordinates) {
                int j = 0;
                TableRow tr = new TableRow(this);
                tr.setLayoutParams(getLayoutParams());
                tr.addView(getTextView(j + 1, String.valueOf(mloc.mDateTimeStamp), Color.BLACK, Typeface.NORMAL, Color.WHITE));
                tr.addView(getTextView(j + 1, String.valueOf(dcoOrdinates.format(mloc.mlatitude)), Color.BLACK, Typeface.NORMAL, Color.WHITE));
                tr.addView(getTextView(j + 1, String.valueOf(dcoOrdinates.format(mloc.mlongitude)), Color.BLACK, Typeface.NORMAL, Color.WHITE));
                tl.addView(tr, getTblLayoutParams());
                j++;
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }catch (ClassNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        TextView tv = findViewById(id);
        if (null != tv) {
            Log.i("onClick", "Clicked on row :: " + id);
            Toast.makeText(this, "Clicked on row :: " + id + ", Text :: " + tv.getText(), Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_export_data) {
            Uri dataUri =null;
            DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
            Date date = new Date();
            final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            emailIntent.setType("plain/text");
            ExportUtil exportData = new ExportUtil();
            dataUri = exportData.getEmailAttachment(this.getApplicationContext(), getPackageName());
            emailIntent.putExtra(Intent.EXTRA_STREAM, dataUri);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT,  "Personal Activity Tracker: " + dateFormat.format(date));
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Please find the attached file for GPS tracking details.");
            this.startActivity(Intent.createChooser(emailIntent,"Sending email..."));
            finish();
        }
        return  true;
    }
}
