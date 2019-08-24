package com.example.section6_hikerswatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;
    TextView dataTextView;
    String latitude, longitude;

    private void convert(double lat, double lon) {
        StringBuilder builder = new StringBuilder();

        String latitudeDegrees = Location.convert(Math.abs(lat), Location.FORMAT_SECONDS);
        String[] latitudeSplit = latitudeDegrees.split(":");
        builder.append(latitudeSplit[0]);
        builder.append("°");
        builder.append(latitudeSplit[1]);
        builder.append("'");
        builder.append(latitudeSplit[2]);
        builder.append("\"");

        if (lat < 0) {
            builder.append(" S");
        } else {
            builder.append(" N");
        }

        latitude = String.valueOf(builder);
        builder.setLength(0);

        String longitudeDegrees = Location.convert(Math.abs(lon), Location.FORMAT_SECONDS);
        String[] longitudeSplit = longitudeDegrees.split(":");
        builder.append(longitudeSplit[0]);
        builder.append("°");
        builder.append(longitudeSplit[1]);
        builder.append("'");
        builder.append(longitudeSplit[2]);
        builder.append("\"");

        if (lon < 0) {
            builder.append(" W");
        } else {
            builder.append(" E");
        }

        longitude = String.valueOf(builder);
    }

    @Override //Za pierwszym uruchomieniem sprawdza czy user dał permission w oncreate i updatuje.
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener); //przesyła do location listenera na bieżąco info
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataTextView = findViewById(R.id.dataTextView);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
//                    String latitude = Location.convert(location.getLatitude(), Location.FORMAT_DEGREES);
//                    String longitude = Location.convert(location.getLongitude(), Location.FORMAT_DEGREES);

                    List<Address> listAddresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if (listAddresses != null && listAddresses.size() > 0) {
                        convert(location.getLatitude(),location.getLongitude());
                        dataTextView.setText("Address:\n");
                        if (listAddresses.get(0).getThoroughfare() != null) {
                            dataTextView.append(listAddresses.get(0).getThoroughfare() + " ");
                        }
                        if (listAddresses.get(0).getFeatureName() != null) {
                            dataTextView.append(listAddresses.get(0).getFeatureName() + "\n");
                        }
                        if (listAddresses.get(0).getPostalCode() != null) {
                            dataTextView.append(listAddresses.get(0).getPostalCode() + " ");
                        }
                        if (listAddresses.get(0).getLocality() != null) {
                            dataTextView.append(listAddresses.get(0).getLocality() + "\n");
                        }
                        if (listAddresses.get(0).getAdminArea() != null) {
                            dataTextView.append(listAddresses.get(0).getAdminArea() + "\n");
                        }
                        if (listAddresses.get(0).getCountryName() != null) {
                            dataTextView.append(listAddresses.get(0).getCountryName() + " ");
                        }
                        if (listAddresses.get(0).getCountryCode() != null) {
                            dataTextView.append("(" + listAddresses.get(0).getCountryCode() + ")\n\n");
                        }
                        dataTextView.append("Geo data:\n");
                        dataTextView.append("Latitude: "+latitude+"\n");
                        dataTextView.append("Longitude: "+longitude+"\n");
                        dataTextView.append("Altitude: "+location.getAltitude()+ " m.a.s.l.\n");
                        dataTextView.append("Accuracy: "+location.getAccuracy()+" m.");

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) { //gdy provider jest enabled przez usera
            }

            @Override
            public void onProviderDisabled(String s) { //gdy provider jest disabled przez usera
            }
        };
        //jeśli user NIE DAŁ permission do lokalizacji...
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1); //... zapytaj go o nie
        } else { //Jeśli już dał permissions (na przykład uruchamiasz program po raz kolejny) to updatuj lokalizację (przesyła do locationListenera dane)
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }
}
