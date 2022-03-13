package com.SpaceTrackGO;

import android.content.Context;

import androidx.annotation.NonNull;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Queries APIs and constructs SpaceData objects with the results. SpaceData objects hold
 * informational updates on space activity.
 */
public class SpaceData {
  String id;
  DataType dataType;
  String dateAndTime;
  String description;
  URL hyperlink;

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
   * Forms an array of space data objects from JSON data using a selected data type.
   * @param jsonData JSON array containing response data from API
   * @param selection type of API that data was requested from
   * @return array of SpaceData objects
   */
  private SpaceData[] formSpaceData(@NonNull SpaceData.DataType selection,
                                    @NonNull JSONArray jsonData) {
    // TODO: process JSON object data into SpaceData objects and return as array
    SpaceData[] spaceData = new SpaceData[jsonData.length()];
    for (int i = 0; i < jsonData.length(); i++) {
      switch (selection) {
        case CME:
          break;
        case GST:
          break;
        case FLR:
          break;
      }
    }
    return spaceData;
  }

  /**
   * Queries a certain API for recent data based on the selection, and produces SpaceData objects
   * from the returned results.
   * @param context application context
   * @param selection name of selected API to query where:
   *                  CME is Coronal Mass Ejection,
   *                  GST is Geomagnetic Storm,
   *                  FLR is Solar Flare
   * @return array of SpaceData objects with data from the selected API or null if no data was
   *         received
   */
  public SpaceData[] getApiData(Context context, @NonNull SpaceData.DataType selection) {
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
        MainActivity.showToast(context,
            "A problem occurred. No data was received from the API.");
        return null;  // No data could be obtained from API
      }
    } catch (MalformedURLException e) {
      throw new AssertionError(e); // URL in enum shouldn't be malformed
    } catch (IOException e) {
      e.printStackTrace();
      MainActivity.showToast(context,
          "A problem occurred. Error when trying to connect to API."
              + " Try turning on WI-FI and refreshing");
      return null;
    }

    return formSpaceData(selection, data);
  }
}
