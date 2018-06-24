package pass.threestech.com.services;


import java.io.Serializable;

public class GPSLocationCoordinates implements Serializable{
    public String mDateTimeStamp;
    public double mlatitude;
    public double mlongitude;

    GPSLocationCoordinates(double latitude, double longitude, String DateTimeStamp){
        mDateTimeStamp = DateTimeStamp;
        mlatitude = latitude;
        mlongitude = longitude;
    }
}
