package com.SpaceTrackGO;

import androidx.annotation.NonNull;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Queries APIs and constructs SpaceData objects with the results. SpaceData objects hold
 * informational updates on space activity.
 */
public class SpaceData {
  private final String id;
  private final DataType dataType;
  private final String dateAndTime;
  private final String description;
  private final URL hyperlink;

  // Replace with own API KEY if deploying large-scale
  static final String API_KEY = "DEMO_KEY";

  /**
   * Represents the different types of space data and the APIs they can be obtained from.
   */
  enum DataType {
    CME("https://api.nasa.gov/DONKI/CME"),
    GST("https://api.nasa.gov/DONKI/GST"),
    FLR("https://api.nasa.gov/DONKI/FLR");

    public final String hyperlink;

    DataType(String hyperlink) {
      this.hyperlink = hyperlink;
    }
  }

  /**
   * Private constructor that is needed as SpaceData objects are only constructed internally.
   * @param id unique ID of data from API
   * @param dataType type of space data
   * @param dateAndTime date and time that will appear on card
   * @param description description of event that will appear on card
   * @param hyperlink link to informational page on event
   */
  private SpaceData(String id,
                    DataType dataType,
                    String dateAndTime,
                    String description,
                    URL hyperlink) {

    this.id = id;
    this.dataType = dataType;
    this.dateAndTime = dateAndTime;
    this.description = description;
    this.hyperlink = hyperlink;
  }

  /**
   * Returns the unique identifier of the space data object provided by the API that sent it.
   * @return the id of the space data
   */
  public String getId() {
    return id;
  }

  /**
   * Returns the type of the space data object, decided by which API endpoint it was taken from.
   * @return the type of the space data
   */
  public DataType getDataType() {
    return dataType;
  }

  /**
   * Returns the date and time that the event the space data object describes occurred.
   * @return the date and time of the space data in UTC
   */
  public String getDateAndTime() {
    return dateAndTime;
  }

  /**
   * Returns a description of the space event using information obtained from the API endpoint that
   * it was taken from.
   * @return the description of the space event
   */
  public String getDescription() {
    return description;
  }

  /**
   * Returns a URL to the official report on the space event described by the space data object.
   * @return the report URL
   */
  public URL getHyperlink() {
    return hyperlink;
  }

  /**
   * Finds the maximum Kp-index of all measurements of a geomagnetic storm that are present in a
   * JSON array.
   * @param allKpIndex JSON array containing objects with kpIndex values
   * @return highest Kp-Index value in the JSONArray
   * @throws JSONException throws if allKpIndex is not structured as expected from API or is empty
   */
  private static int getKpIndex(JSONArray allKpIndex) throws JSONException {
    int maxKpIndex = 0;
    for (int i = 0; i < allKpIndex.length(); i++) {
      int currentKpIndex = allKpIndex.getJSONObject(i).getInt("kpIndex");
      maxKpIndex = Math.max(maxKpIndex, currentKpIndex);
    }
    return maxKpIndex;
  }

  /**
   * Forms an array of space data objects from JSON data using a selected data type.
   * @param jsonData JSON array containing response data from API
   * @param selection type of API that data was requested from
   * @return array of SpaceData objects
   * @throws JSONException throws if allKpIndex is not structured as expected from API or is empty
   */
  private static SpaceData[] formSpaceData(@NonNull SpaceData.DataType selection,
                                    @NonNull JSONArray jsonData) throws JSONException {
    SpaceData[] spaceData = new SpaceData[jsonData.length()];
    JSONObject dataObject;
    String id;
    String dateAndTime;
    String description;
    URL hyperlink;
    switch (selection) {
      case CME:
        for (int i = 0; i < jsonData.length(); i++) {
          dataObject = jsonData.getJSONObject(i);
          id = dataObject.getString("activityID");
          dateAndTime = dataObject.getString("startTime");
          description = dataObject.getString("note");
          try {
            hyperlink = new URL(dataObject.getString("link"));
          } catch (MalformedURLException e) {
            e.printStackTrace();
            hyperlink = null; // No clickable link if URL is empty or invalid
          }
          spaceData[i] = new SpaceData(id, selection, dateAndTime, description, hyperlink);
        }
        break;
      case GST:
        for (int i = 0; i < jsonData.length(); i++) {
          dataObject = jsonData.getJSONObject(i);
          id = dataObject.getString("gstID");
          dateAndTime = dataObject.getString("startTime");
          JSONArray allKpIndex = dataObject.getJSONArray("allKpIndex");
          if (allKpIndex.length() > 0) {
            description = "The current highest measured Kp-index is "
                + getKpIndex(allKpIndex)
                + ". The Kp-index measures how"
                + " much the geomagnetic storm is disturbing"
                + " the horizontal part of the Earth's magnetic field."
                + " It ranges from 1, for low solar wind activity,"
                + " to 5, for a geomagnetic storm, to 9, for an intense one.";
          } else {
            description = "There are no current measurements of the Kp index available."
                + " The Kp-index measures how"
                + " much the geomagnetic storm is disturbing"
                + " the horizontal part of the Earth's magnetic field."
                + " It ranges from 1, for low solar wind activity,"
                + " to 5, for a geomagnetic storm, to 9, for an intense one.";
          }
          try {
            hyperlink = new URL(dataObject.getString("link"));
          } catch (MalformedURLException e) {
            e.printStackTrace();
            hyperlink = null; // No clickable link if URL is empty or invalid
          }
          spaceData[i] = new SpaceData(id, selection, dateAndTime, description, hyperlink);
        }
        break;
      case FLR:
        for (int i = 0; i < jsonData.length(); i++) {
          dataObject = jsonData.getJSONObject(i);
          id = dataObject.getString("flrID");
          dateAndTime = dataObject.getString("beginTime");
          String classType = dataObject.getString("classType");
          description = "Solar flare class: " + classType
              + ". The letter, one of A, B, C, M, or X, represents the"
              + " solar flare's size class, where A is the smallest and"
              + " X is the largest. This is appended with a number"
              + " from 1 to 10 which more precisely represents its size.";
          try {
            hyperlink = new URL(dataObject.getString("link"));
          } catch (MalformedURLException e) {
            e.printStackTrace();
            hyperlink = null; // No clickable link if URL is empty or invalid
          }
          spaceData[i] = new SpaceData(id, selection, dateAndTime, description, hyperlink);
        }
        break;
    }

    return spaceData;
  }

  /**
   * Queries a certain API for recent data based on the selection, and produces SpaceData objects
   * from the returned results.
   * @param selection name of selected API to query where:
   *                  CME is Coronal Mass Ejection,
   *                  GST is Geomagnetic Storm,
   *                  FLR is Solar Flare
   * @return ArrayList of SpaceData objects with data from the selected API in reverse chronological
   *         order or null if no data was received
   * @throws JSONException if the data returned by the selected API is not structured as expected or
   *                       is empty
   * @throws IOException if a problem occurred when connecting to the selected API over the internet
   */
  public static ArrayList<SpaceData> getApiData(@NonNull SpaceData.DataType selection)
      throws JSONException, IOException{
    JSONArray data;
    try {
      // Build URL with parameters
      URL urlBase = new URL(selection.hyperlink);
      URL builtURL = new URL(urlBase, "?api_key=" + API_KEY);

      // Prepare connection object
      HttpURLConnection connection = (HttpURLConnection) builtURL.openConnection();

      // Open connection and read data
      try (InputStream in = new BufferedInputStream(connection.getInputStream());
           Scanner s = new Scanner(in).useDelimiter("\\A")) {
        String result = s.hasNext() ? s.next() : "";
        data = new JSONArray(result);

        // No finally connection.disconnect() as the streams are closed but Socket is cached
      } catch (JSONException e) {
        e.printStackTrace();
        String msg =
            "A problem occurred. Invalid or empty data was received from the API.";
        throw new JSONException(msg); // No data could be obtained from API
      }
    } catch (MalformedURLException e) {
      throw new AssertionError(e); // URL in enum shouldn't be malformed
    } catch (IOException e) {
      e.printStackTrace();
      String msg = "A problem occurred. Error when trying to connect to API."
          + " Try turning on WI-FI and refreshing";
      throw new IOException(msg);
    }

    // Extract data from JSONArray
    SpaceData[] spaceData;
    try {
      spaceData = formSpaceData(selection, data);
    } catch (JSONException e) {
      e.printStackTrace();
      String msg =
          "A problem occurred. Invalid or empty data was received from the API.";
      throw new JSONException(msg);
    }

    // Reverse data order to have most recent first and convert to list
    ArrayList<SpaceData> spaceDataList = new ArrayList<>(Arrays.asList(spaceData));
    Collections.reverse(spaceDataList);
    return spaceDataList;
  }
}
