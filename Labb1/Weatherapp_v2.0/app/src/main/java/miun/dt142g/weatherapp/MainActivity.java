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

    private String city;


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
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String locationString = addresses.get(0).getAddressLine(0);

        String[] splitStr = locationString.split(",");

        String adress = splitStr[0];
        String city = splitStr[1];
        String country = splitStr[2];

        return city + ", " + country;
    }

    public void loadWeatherData()
    {
        String urlString = "https://api.met.no/weatherapi/locationforecast/1.9/?lat=" + latitude + ";lon=" + longitude;


        System.out.println("Sending Request");


        findViewById(R.id.progressLoader).setVisibility(View.VISIBLE);

        //Instantiate new instance of our class
        new HttpGetRequest(new HttpGetRequest.Response()
        {
            @Override
            public void processFinish(String output) {

                extractWeatherData(output);

            }
        }).execute(urlString);
    }


    public void extractWeatherData(String data)
    {
        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            InputStream is = new ByteArrayInputStream(data.getBytes("UTF-8"));
            Document doc = builder.parse(is);
            is.close();


            setCity();
            //extractDate(doc);
            extractTemperatureData(doc);
            extractWindData(doc);
            extractCloudinessData(doc);
            extractPrecipitationData(doc);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            findViewById(R.id.progressLoader).setVisibility(View.GONE);
        }
    }

    public void setCity()
    {
        city = getCityFromLocation(latitude, longitude);

        TextView label = findViewById(R.id.titleLabel);
        label.setText(city);
    }

    public void extractDate(Document doc)
    {
        String date = doc.getElementsByTagName("time").item(0).getAttributes().getNamedItem("from").getTextContent();

        TextView label = findViewById(R.id.titleLabel);
        label.setText(date);
    }

    public void extractTemperatureData(Document doc)
    {
        String temp = doc.getElementsByTagName("temperature").item(0).getAttributes().getNamedItem("value").getTextContent();

        TextView label = findViewById(R.id.temperatureValueLabel);
        label.setText(temp + "°C");
    }

    public void extractWindData(Document doc)
    {
        String speed = doc.getElementsByTagName("windSpeed").item(0).getAttributes().getNamedItem("mps").getTextContent();
        String direction = doc.getElementsByTagName("windDirection").item(0).getAttributes().getNamedItem("name").getTextContent();

        TextView label = findViewById(R.id.windSpeedValueLabel);
        label.setText(speed + "mps towards " + direction);
    }

    public void extractCloudinessData(Document doc)
    {
        String percent = doc.getElementsByTagName("cloudiness").item(0).getAttributes().getNamedItem("percent").getTextContent();

        TextView label = findViewById(R.id.cloudinessValueLabel);
        label.setText(percent + "%");
    }

    public void extractPrecipitationData(Document doc)
    {
        NamedNodeMap nodeMap = doc.getElementsByTagName("precipitation").item(0).getAttributes();
        String minValue = nodeMap.getNamedItem("minvalue").getTextContent();
        String maxValue = nodeMap.getNamedItem("maxvalue").getTextContent();

        TextView label = findViewById(R.id.precipitationValueLabel);
        label.setText(minValue + " mm and " + maxValue + " mm");
    }



}
