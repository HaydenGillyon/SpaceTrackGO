package com.SpaceTrackGO;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

/**
 * Holds access to the list of SpaceData objects in a lifecycle conscious way, ensuring that
 * configuration changes do not impact the loading and storage of this data.
 */
public class SpaceDataViewModel extends AndroidViewModel {
  private SpaceDataRepository repo = SpaceDataRepository.getInstance();

  private LiveData<List<SpaceData>> liveSpaceData;

  /**
   * Constructs the SpaceDataViewModel and obtains a reference to the LiveData held in the
   * SpaceDataRepository.
   * @param application global application state
   */
  public SpaceDataViewModel(@NonNull Application application) {
    super(application);
    // Obtain LiveData reference
    liveSpaceData = repo.getSpaceData();
  }

  /**
   * Returns the immutable LiveData list of SpaceData objects. This will be populated with items
   * once data from an API has been collected with SpaceData.getApiData().
   * @return the LiveData list of SpaceData objects
   */
  public LiveData<List<SpaceData>> getSpaceData() {
    return liveSpaceData;
  }
}
