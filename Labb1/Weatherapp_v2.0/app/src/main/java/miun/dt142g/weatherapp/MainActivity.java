package miun.dt142g.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback
{
    private GoogleMap map;

    private double latitude = 62.39;
    private double longitude = 17.31;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        loadWeatherData();

        findViewById(R.id.refreshButton).setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                loadWeatherData();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        System.out.println("Map Ready!");

        map = googleMap;

        LatLng sundsvall = new LatLng(latitude, longitude);
        map.addMarker(new MarkerOptions().position(sundsvall).title("Marker in Sundsvall"));
        map.moveCamera(CameraUpdateFactory.newLatLng(sundsvall));

        CameraPosition cameraPosition = new CameraPosition.Builder().
                target(new LatLng(latitude, longitude)).
                //tilt(60).
                zoom(10).
                bearing(0).
                build();

        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public String getCityFromLocation(double latitude, double longitude)
    {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;

        try
        {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        String locationString = addresses.get(0).getAddressLine(0);

        String[] splitStr = locationString.split(",");

        //String adress = splitStr[0];
        String city = splitStr[1];
        String country = splitStr[2];

        return city + ", " + country;
    }

    public void loadWeatherData()
    {
        findViewById(R.id.progressLoader).setVisibility(View.VISIBLE);

        String city = getCityFromLocation(latitude, longitude);

        ((TextView)findViewById(R.id.titleLabel)).setText(city);

        final WeatherFetcher weatherFetcher = new WeatherFetcher(longitude, latitude);

        weatherFetcher.refresh(new WeatherFetcher.Response()
        {
            @Override
            public void processFinish()
            {
                findViewById(R.id.progressLoader).setVisibility(View.GONE);

                TextView tempLabel = findViewById(R.id.temperatureValueLabel);
                tempLabel.setText(weatherFetcher.getTemperature() + "°C");

                TextView windSpeedLabel = findViewById(R.id.windSpeedValueLabel);
                windSpeedLabel.setText(weatherFetcher.getWindSpeed() + "mps towards " + weatherFetcher.getWindDirection());

                TextView cloudinessLabel = findViewById(R.id.cloudinessValueLabel);
                cloudinessLabel.setText(weatherFetcher.getCloudinessPercentage() + "%");

                TextView precipitationLabel = findViewById(R.id.precipitationValueLabel);
                precipitationLabel.setText(weatherFetcher.getPrecipitationMinValue() + " mm and " + weatherFetcher.getPrecipitationMaxValue() + " mm");
            }
        });
    }
}
