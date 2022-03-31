package com.SpaceTrackGO;

import android.content.Context;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Keeps mutable access to the live space data list and handles the asynchronous calls to load new
 * data and change what is being stored.
 */
public class SpaceDataRepository {
  private static SpaceDataRepository instance;

  private MutableLiveData<List<SpaceData>> liveSpaceData;

  /**
   * Gets the single instance of the SpaceDataRepository class. This won't be instantiated until
   * called for the first time.
   * @return the singleton instance of SpaceDataRepository
   */
  public static SpaceDataRepository getInstance() {
    if (instance == null) {
      synchronized (SpaceDataRepository.class) {
        if (instance == null) {
          instance = new SpaceDataRepository();
        }
      }
    }
    return instance;
  }

  /**
   * Returns the liveSpaceData list in an immutable state for reading the latest space data that
   * has been obtained.
   * @return an immutable list of SpaceData objects
   */
  public LiveData<List<SpaceData>> getSpaceData() {
    if (liveSpaceData == null) {
      liveSpaceData = new MutableLiveData<List<SpaceData>>();
    }
    return liveSpaceData;
  }

  /**
   * Asynchronously loads new space data from the API corresponding to the selected dataType.
   * @param dataType the type of SpaceData to load from an API
   */
  public void loadSpaceData(SpaceData.DataType dataType) {
    new Thread(() -> {
      // Update liveSpaceData from API
      try {
        liveSpaceData.postValue(SpaceData.getApiData(dataType));
      } catch (JSONException | IOException e) {
        e.printStackTrace();
      }
    }).start();
  }
}
