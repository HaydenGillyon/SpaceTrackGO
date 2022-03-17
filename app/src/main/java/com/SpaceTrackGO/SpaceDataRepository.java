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

public class SpaceDataRepository {
  private static SpaceDataRepository instance;

  private MutableLiveData<List<SpaceData>> liveSpaceData;

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

  public LiveData<List<SpaceData>> getSpaceData() {
    if (liveSpaceData == null) {
      liveSpaceData = new MutableLiveData<List<SpaceData>>();
    }
    return liveSpaceData;
  }

  public void loadSpaceData(Context context, SpaceData.DataType dataType) {
    new Thread(() -> {
      if (dataType == null) {
        // Update liveSpaceData from saved data (database)
        //TODO
      } else {
        // Update liveSpaceData from API
        try {
          liveSpaceData.postValue(
              new ArrayList<>(Arrays.asList(SpaceData.getApiData(dataType)))
          );
        } catch (JSONException | IOException e) {
          e.printStackTrace();
        }
      }
    }).start();
  }
}
