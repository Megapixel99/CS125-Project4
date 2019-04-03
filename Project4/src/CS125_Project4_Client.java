import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.BoxView;
import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.border.BevelBorder;

import org.json.*;

public class CS125_Project4_Client extends JFrame {

	private static final long serialVersionUID = 1L;
	static ///////////////////////////////////////////////////////////////////
	// Instance variables/components

	JTextPane txtDetails;
	JLabel lblStreetAddress;
	JLabel lblSearchRadius;
	JLabel lblLookingFor;
	JLabel lblMiles;
	JButton searchbttn;
	static JList<Place> resultList;
	JButton directionsBttn;
	JButton detailsBttn;
	JTextField LookingForText;
	JTextField StreetAddrText;
	JProgressBar progressBar;
	static JComboBox<String> comboBox;

	///////////////////////////////////////////////////////////////////
	// DefaultListModel is bound to lstResults
	private DefaultListModel<Place> dlmResult = new DefaultListModel<Place>();

	///////////////////////////////////////////////////////////////////
	// APIs SECTION
	///////////////////////////////////////////////////////////////////
	private final static String mapsGeocodeBaseUrl = "https://maps.googleapis.com/maps/api/geocode/json?";
	private final static String placesNearbySearchBaseUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
	private final static String placesDetailBaseUrl = "https://maps.googleapis.com/maps/api/place/details/json?";
	private final static String mapsDirectionBaseUrl = "https://maps.googleapis.com/maps/api/directions/json?";

	// Once you obtain a key, include it here instead of "YOUR_KEY_HERE":
	private final static String mapsGeocodeApiKey = "AIzaSyBpdjZ-yg7VSKh7sGskbxO5EjkAiLebXpU"; // Get your own key by
	// clicking "Get A Key"
	// on the following
	// website:
	// https://developers.google.com/maps/documentation/geocoding/start
	private final static String mapsDirectionsApiKey = "AIzaSyBpdjZ-yg7VSKh7sGskbxO5EjkAiLebXpU"; // Get your own key by
	// clicking "Get A
	// Key" on the
	// following
	// website:
	// https://developers.google.com/maps/documentation/directions/
	private final static String placesApiKey = "AIzaSyBpdjZ-yg7VSKh7sGskbxO5EjkAiLebXpU"; // Get your own key by
	private JTextField textField;
	private JSpinner SearchRadText;
	// clicking "Get A Key" on
	// the following website:
	// https://developers.google.com/places/web-service/

	///////////////////////////////////////////////////////////////////
	// MAIN
	///////////////////////////////////////////////////////////////////

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				// Your program should always output your name and the project number.
				// DO NOT DELETE OR COMMENT OUT. Replace with relevant info.
				System.out.println("Seth Wheeler");
				System.out.println("Project 4" + "\n");

				// Launch GUI
				try {
					CS125_Project4_Client frame = new CS125_Project4_Client();
					frame.setVisible(true);
					frame.setResizable(false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public CS125_Project4_Client() {
		setTitle("Seths' Food Finder");

		initComponents();
		createEvents();
	}

	///////////////////////////////////////////////////////////////////
	// All component initializations done here in this method
	///////////////////////////////////////////////////////////////////
	private void initComponents() {
		JPanel contentPane;

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 959, 360);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		txtDetails = new JTextPane();
		txtDetails.setBounds(2, 2, 516, 321);
		txtDetails.setEditable(false);
		// txtDetails.setAutoscrolls(false);
		txtDetails.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		contentPane.add(txtDetails);
		JScrollPane scrollPaneDetails = new JScrollPane(txtDetails);
		scrollPaneDetails.setBounds(437, 11, 520, 325);
		contentPane.add(scrollPaneDetails);

		lblStreetAddress = new JLabel("Street Address:");
		lblStreetAddress.setBounds(11, 11, 102, 16);
		contentPane.add(lblStreetAddress);

		lblSearchRadius = new JLabel("Search Radius:");
		lblSearchRadius.setBounds(11, 39, 102, 16);
		contentPane.add(lblSearchRadius);

		lblLookingFor = new JLabel("Looking For:");
		lblLookingFor.setBounds(11, 67, 102, 16);
		contentPane.add(lblLookingFor);

		LookingForText = new JTextField();
		LookingForText.setBounds(125, 62, 130, 26);
		LookingForText.setToolTipText("What type of food do you want?");
		lblLookingFor.setLabelFor(LookingForText);
		contentPane.add(LookingForText);
		LookingForText.setColumns(10);

		StreetAddrText = new JTextField();
		StreetAddrText.setBounds(125, 5, 130, 26);
		StreetAddrText.setToolTipText("Approximately, where are you?");
		lblStreetAddress.setLabelFor(StreetAddrText);
		contentPane.add(StreetAddrText);
		StreetAddrText.setColumns(10);

		lblMiles = new JLabel("miles");
		lblMiles.setBounds(206, 39, 41, 16);
		contentPane.add(lblMiles);

		searchbttn = new JButton("Search");
		searchbttn.setBounds(250, 90, 127, 29);
		contentPane.add(searchbttn);

		directionsBttn = new JButton("Directions");
		directionsBttn.setBounds(323, 133, 102, 29);
		contentPane.add(directionsBttn);
		directionsBttn.setEnabled(false);

		resultList = new JList<Place>(dlmResult); //Hey Mario, do you actually read this?
		resultList.setAutoscrolls(true); //this does... scrolling stuff
		resultList.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		resultList.setBounds(5, 118, 300, 213);
		JScrollPane scrollPane = new JScrollPane(resultList);
		scrollPane.setBounds(10, 123, 300, 213);
		contentPane.add(scrollPane);

		detailsBttn = new JButton("Details");
		detailsBttn.setBounds(323, 184, 102, 29);
		contentPane.add(detailsBttn);
		detailsBttn.setEnabled(false);

		progressBar = new JProgressBar();
		progressBar.setBounds(323, 225, 102, 20);
		contentPane.add(progressBar);
		progressBar.setVisible(false);

		JLabel lblType = new JLabel("Type:");
		lblType.setBounds(11, 95, 34, 16);
		contentPane.add(lblType);

		comboBox = new JComboBox<String>();
		comboBox.setBounds(125, 91, 130, 27);
		comboBox.addItem("Fast food");
		comboBox.addItem("Delivery");
		comboBox.addItem("Restruant");
		contentPane.add(comboBox);

		SearchRadText = new JSpinner();
		SearchRadText.setBounds(125, 34, 76, 26);
		contentPane.add(SearchRadText);

		// TODO 1: Use WindowBuilder to layout controls (most
		// auto-generated WindowBuilder code will go here)
	}

	///////////////////////////////////////////////////////////////////
	// All event handlers placed here in this method
	///////////////////////////////////////////////////////////////////
	private void createEvents() {
		///////////////////////////////////////////////////////////////
		// Calls Google Maps (Geocode) and Google Places APIs to return
		// a list of places with the search radius from the starting
		// address
		// TODO 2: Uncomment, read/understand when you have the btnSearch
		// button in the JFrame
		searchbttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				if (StreetAddrText.getText().length() > 0 && SearchRadText.toString().length() > 0
						&& Integer.parseInt(SearchRadText.getValue().toString()) > 0
						&& LookingForText.getText().length() > 0) {
					dlmResult.clear();
					progressBar.setVisible(true);
					// Convert address to latitude/longitude
					String address = StreetAddrText.getText();
					String geocodeUrl = generateGeocodeRequestUrl(address);
					progressBar.setValue(25);
					String geocodeResponse = getStandardHtmlResponse(geocodeUrl);
					String latLong = parseGeocodeResponse(geocodeResponse);
					System.out.println("DEBUG PRINT: " + geocodeUrl);

					progressBar.setValue(50);
					// Making web call to get nearby places search results from user input
					double meters = Double.parseDouble(SearchRadText.getValue().toString()) * 1609.34;
					String nearbyPlacesUrl = generateNearbyPlacesRequestUrl(latLong, LookingForText.getText(), //Here I get the url
							(int) meters);
					System.out.println("DEBUG PRINT: " + nearbyPlacesUrl);
					String nearbyPlacesResponse = getStandardHtmlResponse(nearbyPlacesUrl);
					progressBar.setValue(75);
					ArrayList<Place> places = parseNearbyPlacesResponse(nearbyPlacesResponse);

					progressBar.setValue(100);
					// Populate JList with our results
					if (!places.isEmpty()) {
						detailsBttn.setEnabled(true);
						directionsBttn.setEnabled(true);
						for (Place p : places)
							dlmResult.addElement(p);
					} else {
						detailsBttn.setEnabled(false);
						directionsBttn.setEnabled(false);
						dlmResult.addElement(new Place("No Results Found", 0, "", ""));
					}
				} else if (!dlmResult.isEmpty()) {
					detailsBttn.setEnabled(true);
					directionsBttn.setEnabled(true);
				} else {
					detailsBttn.setEnabled(false);
					directionsBttn.setEnabled(false);
				}
				if (progressBar.getValue() == 100) {
					progressBar.setVisible(false);
				}
			}
		});

		///////////////////////////////////////////////////////////////
		// Calls Google Places API to return some place details for
		// the selected places (in the list)
		// TODO 7: Uncomment and read/understand when you have the btnDetails

		detailsBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (resultList.getSelectedValue() != null) {
					progressBar.setVisible(true);
					txtDetails.setContentType("text/html");
					// Get URL and response for place details
					String placeDetailUrl = generatePlaceDetailsRequestUrl((Place) resultList.getSelectedValue());
					String placeDetailResponse = getStandardHtmlResponse(placeDetailUrl);
					progressBar.setValue(50);
					// Format the response and place in text area
					String formattedPlaceDetails = parsePlaceDetailResponse(placeDetailResponse); //more URL generation
					txtDetails.setText(formattedPlaceDetails);
					progressBar.setValue(100);
					if (progressBar.getValue() == 100) {
						progressBar.setVisible(false);
					}
				}
			}
		});

		///////////////////////////////////////////////////////////////
		// Calls Google Maps API to return some directions to
		// the selected places (in the list, from the starting address)
		// TODO 10: Uncomment and read/understand when you have the btnDirections

		directionsBttn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (resultList.getSelectedValue() != null) {
					txtDetails.setContentType("text/html");
					// txtDetails.setContentType("text");
					progressBar.setVisible(true);
					// Get URL and response for place details
					String mapDirectionUrl = generateDirectionsRequestUrl(StreetAddrText.getText(),
							(Place) resultList.getSelectedValue());
					String mapDirectionResponse = getStandardHtmlResponse(mapDirectionUrl);
					progressBar.setValue(50);
					System.out.println("DEBUG PRINT: " + mapDirectionUrl);

					// Format the response and place in text area
					String formattedDirectionDetails = parseDirectionsResponse(mapDirectionResponse);
					txtDetails.setText(formattedDirectionDetails);
					progressBar.setValue(100);
					if (progressBar.getValue() == 100) {
						progressBar.setVisible(false);
					}
				}
			}
		});
	}

	////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////
	// The following four methods generate the URL request strings for //
	// the four API calls we need to make. //
	////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////
	// Generates a URL request for the Google Maps Geocode web-services API
	// according to the following web-page:
	//////////////////////////////////////////////////////////////////////// https://developers.google.com/maps/documentation/geocoding/start
	// The purpose of this call is to get a JSON response with latitude and
	// longitude for the supplied address.
	////////////////////////////////////////////////////////////////////////
	public static String generateGeocodeRequestUrl(String address) {
		String url = mapsGeocodeBaseUrl + "address=" + address.replaceAll(" ", "+") + "&key=" + mapsGeocodeApiKey;
		// https://maps.googleapis.com/maps/api/geocode/json?address=1600+Amphitheatre+Parkway,+Mountain+View,+CA&key=AIzaSyDZgOApo61EyCWuLYTSIB1cbL7obcdY058
		// TODO 3: Finish forming URL

		return url;
	}

	////////////////////////////////////////////////////////////////////////
	// Generates a URL request for the Google Places web-services API
	// according to the following web-page:
	//////////////////////////////////////////////////////////////////////// https://developers.google.com/places/web-service/
	////////////////////////////////////////////////////////////////////////
	public static String generateNearbyPlacesRequestUrl(String latLong, String query, int meters) {
		String type;
		if (comboBox.getSelectedItem().toString().toLowerCase().equals("restruant")) {
			type = "restruant";
		} else if (comboBox.getSelectedItem().toString().toLowerCase().equals("delivery")) {
			type = "meal_delivery";
		} else {
			type = "meal_takeaway";
		}
		String url = placesNearbySearchBaseUrl + "location=" + latLong + "&radius=" + meters + "&type=" + type
				+ "&keyword=" + query + "&key=" + placesApiKey;
		// TODO 5: Finish forming URL

		return url;
	}

	////////////////////////////////////////////////////////////////////////
	// Generates a URL request for the Google Places web-services API
	// according to the following web-page:
	//////////////////////////////////////////////////////////////////////// https://developers.google.com/places/web-service/
	////////////////////////////////////////////////////////////////////////
	public static String generatePlaceDetailsRequestUrl(Place place) {
		String url = placesDetailBaseUrl + "placeid=" + place.getPlacesId()
				+ "&fields=formatted_address,name,formatted_phone_number,opening_hours,url" + "&key=" + placesApiKey;
		// TODO 8: Finish forming URL
		// https://maps.googleapis.com/maps/api/place/details/json?placeid=ChIJN1t_tDeuEmsRUsoyG83frY4&fields=name,rating,formatted_phone_number&key=AIzaSyBpdjZ-yg7VSKh7sGskbxO5EjkAiLebXpU
		return url;
	}

	////////////////////////////////////////////////////////////////////////
	// Generates a URL request for the Google Maps directions web-services API
	// according to the following web-page:
	//////////////////////////////////////////////////////////////////////// https://developers.google.com/maps/documentation/directions/
	////////////////////////////////////////////////////////////////////////
	public static String generateDirectionsRequestUrl(String origin, Place destPlace) {
		String url = mapsDirectionBaseUrl + "origin=" + origin.replaceAll(" ", "+") + "&destination="
				+ destPlace.getAddress().replaceAll(" ", "+") + "&key=" + mapsDirectionsApiKey;
		// TODO 11: Finish forming URL

		return url.replaceAll("#", "");
	}

	////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////
	// The following four methods use the JSONObject and JSONArray //
	// classes defined in the org.json JAR file you imported to parse //
	// through each of the four responses from the four URL requests. //
	// Each method's header describes what kind of data it is returning. //
	////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////
	// Return the lat,long pair from the given geocode response
	// Ex: 34.5029,39.40201
	////////////////////////////////////////////////////////////////////////
	private static String parseGeocodeResponse(String response) {
		// TODO 4: Convert response string to JSON & dive down into JSON response to get
		// lat/long

		String lat = "";
		String lng = "";
		JSONObject obj = new JSONObject(response);
		JSONArray a = new JSONArray(obj.get("results").toString());
		for (int i = 0; i < a.length(); i++) {
			JSONObject ar = new JSONObject(a.get(i).toString());
			JSONObject geom = new JSONObject(ar.get("geometry").toString());
			JSONObject loc = new JSONObject(geom.get("location").toString());
			lat = loc.get("lat").toString();
			lng = loc.get("lng").toString();
		}

		return lat + "," + lng;
	}

	////////////////////////////////////////////////////////////////////////
	// Returns an arrayList of places given the nearby places results/response
	// This method needs to parse out the relevant info for a place that
	// we need (see the instance variables in Place.java) and create an object
	// for each place in the response that is passed in as a parameter (each
	// response is added to a new ArrayList, which is returned).
	////////////////////////////////////////////////////////////////////////
	private static ArrayList<Place> parseNearbyPlacesResponse(String response) {
		// Init places arrayList
		ArrayList<Place> places = new ArrayList<Place>();

		// TODO 6: Dive down into response string; when you find the actual
		// nearby places results, iteration through each one, creating a
		// Place object and adding to the places array.

		JSONObject obj = new JSONObject(response);
		if (obj.has("results")) {
			JSONArray a = new JSONArray(obj.get("results").toString());
			for (int i = 0; i < a.length(); i++) {
				double rating = 0.0;
				JSONObject ar = new JSONObject(a.get(i).toString());
				if (ar.has("rating"))
					rating = Double.parseDouble(ar.get("rating").toString());
				places.add(new Place(ar.get("name").toString(), rating, ar.get("vicinity").toString(),
						ar.get("place_id").toString()));

			}
		}

		return places;
	}

	////////////////////////////////////////////////////////////////////////
	// Return a formatted directions string given the directions response.
	// Ex of formatted return String:
	// START ADDRESS:
	// 901 E Alosta Ave, Azusa, CA 91702, USA
	//
	// END ADDRESS:
	// 1377 E Gladstone St, Glendora, CA 91740, USA
	//
	// DISTANCE (TIME):
	// 4.5 mi (10 mins)
	//
	// STEP-BY-STEP NAVIGATION:
	// 1: Head south toward E Alosta Ave/Historic Rte 66 W
	// 2: Turn right onto E Alosta Ave/Historic Rte 66 W
	// 3: Turn left at the 1st cross street onto N Citrus Ave
	// 4: Take the Interstate 210 ramp to San Bernardino
	// 5: Merge onto I-210 E
	// 6: Take exit 43 for Sunflower Ave
	// 7: Turn right onto S Sunflower Ave
	// 8: Turn left onto E Gladstone St
	// 9: Turn left at N Shellman Ave
	// 10: Turn right
	// 11: Turn right
	// 12: Turn left
	////////////////////////////////////////////////////////////////////////
	private static String parseDirectionsResponse(String response) {

		// TODO 9: Convert response string to JSON & dive down into JSON response
		// to get all the relevant data

		String StartAddr = "";
		String EndAddr = "";
		String Distance = "";
		String Duration = "";
		String Nav = "";
		JSONArray Steps = null;

		JSONObject obj = new JSONObject(response);
		if (new JSONArray(obj.get("routes").toString()).length() > 0) {
			JSONArray route = new JSONArray(obj.get("routes").toString());
			JSONArray legs = ((JSONObject) (route.get(0))).getJSONArray("legs");
			if (((JSONObject) legs.get(0)).has("start_address"))
				StartAddr = ((JSONObject) legs.get(0)).getString("start_address");
			else
				StartAddr = "Location unknown";
		
			if (((JSONObject) legs.get(0)).has("end_address"))
				EndAddr = ((JSONObject) legs.get(0)).getString("end_address");
			else
				EndAddr = "Location unknown";
		
			if (((JSONObject) ((JSONObject) legs.get(0)).get("distance")).has("text"))
				Distance = ((JSONObject) ((JSONObject) legs.get(0)).get("distance")).getString("text");
			else
				Distance = "Distance unknown";
			
			if (((JSONObject) ((JSONObject) legs.get(0)).get("duration")).has("text"))
				Duration = ((JSONObject) ((JSONObject) legs.getJSONObject(0)).get("duration")).getString("text");
			else
				Duration = "Time unknown";
		
			
			if (((JSONObject) legs.get(0)).has("steps")) {
				Steps = ((JSONObject) legs.get(0)).getJSONArray("steps");
				for (int i = 0; i < Steps.length(); i++) {
					Nav += "&#9;" + (i+1) + ": " + ((JSONObject) Steps.get(i)).getString("html_instructions") + "<br>";
				}
				Nav = Nav.replaceAll("<div style=\"font-size:0.9em\">", "<br> &#9;");
			} else
				Nav = "&#9; Directions Unknown";
		} else {
			StartAddr = "Address Unknown";
			EndAddr = "Address Unknown";
			Distance = "Distance Unknown";
			Duration = "Duration Unknown";
			Nav = "&#9; Directions Unknown";
		}

		return "<b> START ADDRESS: </b>" + "<br> &#9;" + StartAddr + "<br><br>" + "<b> END ADDRESS:</b>" + "<br> &#9;"
				+ EndAddr + "<br><br>" + "<b> DISTANCE (TIME): </b>" + "<br> &#9;" + Distance + " (" + Duration + ")"
				+ "<br><br>" + "<b> STEP-BY-STEP NAVIGATION:</b>" + "<br>" + Nav;
	}

	////////////////////////////////////////////////////////////////////////
	// Returns a formatted place details string given the details response.
	// Ex. of formatted details return string:
	// ADDRESS:
	// 1377 East Gladstone Street #104, Glendora
	//
	// PHONE:
	// (909) 305-0505
	//
	// WEB:
	// https://www.mooyah.com/locations/glendora-ca-268/
	//
	// HOURS:
	// N/A Monday: 11:00 AM - 9:00 PM
	// Tuesday: 11:00 AM - 9:00 PM
	// Wednesday: 11:00 AM - 9:00 PM
	// Thursday: 11:00 AM - 9:00 PM
	// Friday: 11:00 AM - 10:00 PM
	// Saturday: 11:00 AM - 10:00 PM
	// Sunday: 11:00 AM - 9:00 PM
	////////////////////////////////////////////////////////////////////////
	private static String parsePlaceDetailResponse(String response) {
		// Create variables
		String phone = "N/A";
		String hours = "N/A";
		String web = "N/A";
		String address = "N/A";
		String dayhours = "";

		// TODO 12: Convert response string to JSON & dive down into JSON response
		// to get all the relevant data

		JSONObject obj = new JSONObject(response);
		JSONObject res = new JSONObject(obj.get("result").toString());
		if (res.has("url"))
			web = res.getString("url");
		else
			web = "No website found";
		if (res.has("formatted_address"))
			address = res.getString("formatted_address");
		else
			address = "No address found";
		if (res.has("formatted_phone_number"))
			phone = res.getString("formatted_phone_number");
		else
			phone = "No phone found";
		if (res.has("opening_hours")) {
			JSONArray arr = new JSONArray(((JSONObject) (res.get("opening_hours"))).get("weekday_text").toString());
			for (int i = 0; i < arr.length(); i++) {
				dayhours += arr.get(i).toString() + "<br> &#9;";
			}
		} else
			dayhours = "No hours found";

		return "<b> ADDRESS:</b> " + "<br> &#9;" + address + "<br><br>" + "<b>PHONE:</b>" + "<br> &#9;" + phone
				+ "<br><br>" + "<b>WEB:</b>" + "<br> &#9;" + web + "<br><br>" + "<b>HOURS:</b> <br> &#9;" + dayhours;
	}

	////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////
	// The following method is included for your convenience and should //
	// NOT be edited for any reason.
	////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////
	// Uses the google places web-services API by sending a standard
	// HTML request and returns the result as a String in JSON format.
	////////////////////////////////////////////////////////////////////////
	public static String getStandardHtmlResponse(String urlRequest) {
		// Create URL Request
		HttpURLConnection connection = null;

		try {
			// Create connection
			URL url = new URL(urlRequest);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			// connection.setRequestProperty("Content-Length",
			// Integer.toString(urlParameters.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");

			connection.setUseCaches(false);
			connection.setDoOutput(true);

			// Send request
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			// wr.writeBytes(urlParameters);
			wr.close();

			// Get Response
			InputStream is = connection.getInputStream();

			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
			String line;
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			return response.toString();
		} catch (Exception e) {
			System.out.println("Connection failed.");
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
		return null;
	}
}
