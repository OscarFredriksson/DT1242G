package miun.dt142g.weatherapp;

import android.view.View;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class WeatherFetcher
{
    private String baseURL = "https://api.met.no/weatherapi/locationforecast/1.9/?";

    private double longitude;
    private double latitude;

    private String temperature;
    private String windSpeed;
    private String windDirection;
    private String cloudinessPercentage;
    private String precipitationMinValue;
    private String precipitationMaxValue;

    WeatherFetcher(double longitude, double latitude)
    {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public interface Response
    {
        void processFinish();
    }


    public void refresh(final Response delegate)
    {
        String urlString = baseURL + "lat=" + latitude + ";lon=" + longitude;

        System.out.println("Sending Request");

        new HttpGetRequest(new HttpGetRequest.Response()
        {
            @Override
            public void processFinish(String output) {

                try
                {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();

                    InputStream is = new ByteArrayInputStream(output.getBytes("UTF-8"));
                    Document doc = builder.parse(is);
                    is.close();

                    temperature = doc.getElementsByTagName("temperature").item(0).getAttributes().getNamedItem("value").getTextContent();

                    System.out.println("TEMP:" + WeatherFetcher.this.temperature);

                    windSpeed = doc.getElementsByTagName("windSpeed").item(0).getAttributes().getNamedItem("mps").getTextContent();
                    windDirection = doc.getElementsByTagName("windDirection").item(0).getAttributes().getNamedItem("name").getTextContent();
                    cloudinessPercentage = doc.getElementsByTagName("cloudiness").item(0).getAttributes().getNamedItem("percent").getTextContent();

                    NamedNodeMap nodeMap = doc.getElementsByTagName("precipitation").item(0).getAttributes();
                    precipitationMinValue = nodeMap.getNamedItem("minvalue").getTextContent();
                    precipitationMaxValue = nodeMap.getNamedItem("maxvalue").getTextContent();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    delegate.processFinish();
                }

            }
        }).execute(urlString);
    }

    public String getTemperature()
    {
        return temperature;
    }

    public String getWindSpeed()
    {
        return windSpeed;
    }

    public String getWindDirection()
    {
        return windDirection;
    }

    public String getCloudinessPercentage()
    {
        return cloudinessPercentage;
    }

    public String getPrecipitationMinValue()
    {
        return precipitationMinValue;
    }

    public String getPrecipitationMaxValue()
    {
        return precipitationMaxValue;
    }
}
