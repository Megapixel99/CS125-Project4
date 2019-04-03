//////////////////////////////////////////////////////////////////////
// This program uses a public API to do something interesting...
//////////////////////////////////////////////////////////////////////

import java.io.*;
import java.net.*;
import org.json.*;

public class WebServiceAPITemplate
{
	// Example working request
	private final static String lat = "-33.8670522";
	private final static String lon = "151.1957362";
	private final static String type = "restaurant";
	private final static String keyword = "mexican";
	private final static String apiKey = "APIKey";
	private final static double dist = 20 * 1609.34;
	private final static String Url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + lat + "," + lon + "&radius=" + dist + "&type=" + type + "&keyword=" + keyword + "&key=" + apiKey;
	
	// Can get your own GPC coords from https://www.gps-coordinates.net/
	
	// Get your own key by clicking "Get A Key" on the following website: https://developers.google.com/places/web-service/
	


	public static void main(String[] args)
	{
		parseResponseAndPrintResults(generateRequestUrl());
	}

	////////////////////////////////////////////////////////////////////////
	// Uses JSON external library to parse JSON objects/arrays/values. 
	// Specifically parses out the main/secondary text/description of
	// each establishment and prints.
	//
	// In order to use this library, you will need to:
	//	1) Download the newest JSON library/JAR (org.json) from: http://mvnrepository.com/artifact/org.json/json
	//	2) Create a new folder called "libs" right by the "src" folder in your project
	//	3) Copy the JSON JAR file to the "libs" folder you just created
	//	4) Add the JAR file to the project; in Eclipse:
	//		a) Click "Project->Properties" from the menu at the top of Eclipse
	//		b) Click "Java Build Path" from the list on the right
	//		c) Click the "Libraries" tab and then click the "Add External JARs..." button
	//		d) Find/select your JAR file on the file system
	////////////////////////////////////////////////////////////////////////
	private static void parseResponseAndPrintResults(String response)
	{
		JSONObject obj = new JSONObject(response);
		JSONArray a = new JSONArray(obj.get("results").toString());
		for (int i = 0; i < a.length(); i++) {
			JSONObject ar = new JSONObject(a.get(i).toString());
			System.out.println(ar.get("name").toString() + " (" + ar.get("rating").toString() + ") - " + ar.get("vicinity").toString());
			
		}
	}

	////////////////////////////////////////////////////////////////////////
	// Generates a URL request for the Google Places web-services API
	// according to the following web-page: https://developers.google.com/places/web-service/
	////////////////////////////////////////////////////////////////////////
	public static String generateRequestUrl()
	{
		return getStandardHtmlResponse(Url);
	}

	////////////////////////////////////////////////////////////////////////
	// Uses the google places web-services API by sending a standard
	// HTML request and returns the result as a String in JSON format.
	////////////////////////////////////////////////////////////////////////
	public static String getStandardHtmlResponse(String urlRequest)
	{
		// Create URL Request
		HttpURLConnection connection = null;

		try
		{
			//Create connection
			URL url = new URL(urlRequest);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			//connection.setRequestProperty("Content-Length", Integer.toString(urlParameters.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");  

			connection.setUseCaches(false);
			connection.setDoOutput(true);

			//Send request
			DataOutputStream wr = new DataOutputStream (connection.getOutputStream());
			//wr.writeBytes(urlParameters);
			wr.close();

			//Get Response  
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
			String line;
			while ((line = rd.readLine()) != null)
			{
				response.append(line);
				response.append('\r');
			}
			rd.close();

			return response.toString();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("Connection failed.");
		}
		finally
		{
			if (connection != null)
			{
				connection.disconnect();
			}
		}
		return null;
	}
}
