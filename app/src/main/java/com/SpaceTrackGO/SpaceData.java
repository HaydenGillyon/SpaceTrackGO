package com.SpaceTrackGO;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * For querying APIs and constructing SpaceData objects with the results. SpaceData objects hold
 * informational updates on space activity.
 */
public class SpaceData {
    String ID;
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
     * Private constructor as SpaceData objects are only constructed internally.
     * @param ID unique ID of data from API
     * @param dataType type of space data
     * @param dateAndTime date and time that will appear on card
     * @param description description of event that will appear on card
     * @param hyperlink link to informational page on event
     */
    private SpaceData(String ID,
                      DataType dataType,
                      String dateAndTime,
                      String description,
                      URL hyperlink) {

        this.ID = ID;
        this.dataType = dataType;
        this.dateAndTime = dateAndTime;
        this.description = description;
        this.hyperlink = hyperlink;
    }

    /**
     * Queries a certain API for recent data based on the selection.
     * @param selection name of selected API to query where:
     *                  CME is Coronal Mass Ejection,
     *                  GST is Geomagnetic Storm,
     *                  FLR is Solar Flare
     * @return array of SpaceData objects with data from the selected API
     */
    public SpaceData[] getAPIData(SpaceData.DataType selection) {
        // TODO: Set up HttpURLConnection to fetch data from up to 7 days before
        try {
            URL url = new URL(selection.hyperlink);
        } catch (MalformedURLException e) {
            throw new Error(e);
        }
        // TODO: Build URL with parameters

        // TODO: process data into SpaceData objects and return as array
        switch (selection) {
            case CME:
                break;
            case GST:
                break;
            case FLR:
                break;
        }

        return new SpaceData[]{};   // TODO: REMOVE
    }

}
