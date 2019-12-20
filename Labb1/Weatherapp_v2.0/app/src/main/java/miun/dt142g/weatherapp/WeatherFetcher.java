package miun.dt142g.weatherapp;

import android.graphics.drawable.Drawable;
import android.view.View;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class WeatherFetcher
{
    private String baseURL = "https://api.met.no/weatherapi/locationforecast/1.9/?";
    private String sunriseURL = "https://api.met.no/weatherapi/sunrise/2.0/?";

    private double longitude;
    private double latitude;

    private String temperature;
    private String windSpeed;
    private String windDirection;
    private String cloudinessPercentage;
    private String precipitationMinValue;
    private String precipitationMaxValue;

    private String sunriseTime;
    private String sunsetTime;

    private String iconNumber;


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

                    windSpeed = doc.getElementsByTagName("windSpeed").item(0).getAttributes().getNamedItem("mps").getTextContent();
                    windDirection = doc.getElementsByTagName("windDirection").item(0).getAttributes().getNamedItem("name").getTextContent();
                    cloudinessPercentage = doc.getElementsByTagName("cloudiness").item(0).getAttributes().getNamedItem("percent").getTextContent();

                    NamedNodeMap nodeMap = doc.getElementsByTagName("precipitation").item(0).getAttributes();
                    precipitationMinValue = nodeMap.getNamedItem("minvalue").getTextContent();
                    precipitationMaxValue = nodeMap.getNamedItem("maxvalue").getTextContent();

                    iconNumber = doc.getElementsByTagName("symbol").item(0).getAttributes().getNamedItem("number").getTextContent();

                    //System.out.println("ICON: " + iconURL);
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


        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        String sunriseString = sunriseURL + "lat=" + latitude + "&lon=" + longitude + "&date=" + df.format(c) + "&offset=+01:00";

        new HttpGetRequest(new HttpGetRequest.Response()
        {
            @Override
            public void processFinish(String output)
            {

                try
                {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();

                    InputStream is = new ByteArrayInputStream(output.getBytes("UTF-8"));
                    Document doc = builder.parse(is);
                    is.close();

                    String sunriseString = doc.getElementsByTagName("sunrise").item(0).getAttributes().getNamedItem("time").getTextContent();
                    String[] sunriseSplitString = sunriseString.split("T")[1].split(":");

                    sunriseTime = sunriseSplitString[0] + ":" + sunriseSplitString[1];

                    String sunsetString = doc.getElementsByTagName("sunset").item(0).getAttributes().getNamedItem("time").getTextContent();
                    String[] sunsetSplitString = sunsetString.split("T")[1].split(":");

                    sunsetTime = sunsetSplitString[0] + ":" + sunsetSplitString[1];

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
        }).execute(sunriseString);
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

    public String getSunriseTime() {
        return sunriseTime;
    }

    public String getSunsetTime() {
        return sunsetTime;
    }

    public String getIconURL()
    {
        String iconURL = "https://api.met.no/weatherapi/weathericon/1.1/?symbol=" + iconNumber + "&content_type=image/png";

        return iconURL;
    }
}
