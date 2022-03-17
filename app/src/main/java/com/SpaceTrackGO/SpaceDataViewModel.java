package com.SpaceTrackGO;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class SpaceDataViewModel extends AndroidViewModel {
  private SpaceDataRepository repo = SpaceDataRepository.getInstance();

  private LiveData<List<SpaceData>> liveSpaceData;

  public SpaceDataViewModel(@NonNull Application application) {
    super(application);
    // Obtain LiveData reference
    liveSpaceData = repo.getSpaceData();
  }

  public LiveData<List<SpaceData>> getSpaceData() {
    return liveSpaceData;
  }
}
