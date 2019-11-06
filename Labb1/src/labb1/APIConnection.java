package labb1;


import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author oscar
 */
public class APIConnection {
    
    private String urlString;
    
    public APIConnection(String url)
    {
        this.urlString = url;
    }
    
    private Document getParsedDocument() throws Exception
    {
        URL url = new URL(urlString);
        URLConnection connection = url.openConnection();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        
        return builder.parse(connection.getInputStream());
    }
    
    public String getTemperature() throws Exception
    { 
        Document document = getParsedDocument();
        
        NodeList nodeList = document.getElementsByTagName("temperature");
        Node node = nodeList.item(0);
        NamedNodeMap namedNodeMap = node.getAttributes();
        String temperature = namedNodeMap.getNamedItem("value").getFirstChild().getTextContent();
        String unit = namedNodeMap.getNamedItem("unit").getFirstChild().getTextContent();

        //System.out.println(temperature);

        return temperature + "° " + unit;
    }
    
    /*public String getWindSpeed() throws Exception
    {
        
    }*/
    
}
