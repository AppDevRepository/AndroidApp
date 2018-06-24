package pass.threestech.com.personalactivityscoringsystem;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.util.Log;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import pass.threestech.com.services.GPSLocationCoordinates;

public class ExportUtil {

    public Uri getEmailAttachment(Context context, String packageName)
    {
        Uri dataUri= null;
        try
        {
            List<GPSLocationCoordinates> mGPSCoordinates = (List<GPSLocationCoordinates>) InternalStorage.readObject(context, "GPSCoordinates");
            DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
            Date date = new Date();
            File CSVfile = null;
            Date dateVal = new Date();
            String filename = dateVal.toString();
            CSVfile = generateCsvFile (context, mGPSCoordinates);
             dataUri = FileProvider.getUriForFile(context, packageName+".fileprovider", CSVfile);
        }
        catch (Throwable t)
        {
            Log.e("Send Email", t.getMessage() );

        }
        return  dataUri;
    }

    public  File generateCsvFile(Context context, List<GPSLocationCoordinates> mGPSCoordinates) {

        DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        Date date = new Date();
        String filePath = context.getFilesDir().getAbsolutePath();
        File CSVfile = null;
        StringBuilder fileContent = new StringBuilder();
        try {
            fileContent.append("DateTime Stamp");
            fileContent.append("|");
            fileContent.append("Latitude");
            fileContent.append("|");
            fileContent.append("Longitude");
            fileContent.append("\n\r");
            for (GPSLocationCoordinates mloc : mGPSCoordinates) {
                fileContent.append(mloc.mDateTimeStamp);
                fileContent.append("|");
                fileContent.append(mloc.mlatitude);
                fileContent.append("|");
                fileContent.append(mloc.mlongitude);
            }
            CSVfile = new File(filePath + "/Export_" + dateFormat.format(date) + ".csv");
            // if file doesnt exists, then create it
            if (!CSVfile.exists()) {
                CSVfile.createNewFile();
            }

            FileWriter fw = new FileWriter(CSVfile.getAbsoluteFile(), true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(fileContent.toString());
            bw.close();
            fw.close();


        } catch (IOException e) {
            Log.e("CSV file", e.getMessage());
        }
        return CSVfile;
    }
}