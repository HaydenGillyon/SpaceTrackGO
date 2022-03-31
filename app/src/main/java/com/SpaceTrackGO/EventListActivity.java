package com.SpaceTrackGO;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates the Event List activity that displays a list of data fetched from a selected API service.
 */
public class EventListActivity extends AppCompatActivity {
  SharedPreferences prefs;
  private SpaceDataViewModel viewModel;
  List<SpaceData> spaceDataList;
  RecyclerView recyclerView;
  SpaceData.DataType dataTypeSelection;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_event_list);

    prefs = this.getSharedPreferences(getString(R.string.prefs_name), MODE_PRIVATE);
    setDataType();

    viewModel = new ViewModelProvider(this).get(SpaceDataViewModel.class);

    recyclerView = (RecyclerView) findViewById(R.id.dataRecyclerView);
    spaceDataList = new ArrayList<>();  // Initial empty list to avoid NPE
    SpaceDataAdapter adapter = new SpaceDataAdapter(spaceDataList);
    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));

    viewModel.getSpaceData().observe(this, new Observer<List<SpaceData>>() {
      /**
       * Updates the list of space data objects and alerts the RecyclerView's adapter to the change.
       * @param spaceData list of space data objects to be displayed
       */
      @Override
      public void onChanged(List<SpaceData> spaceData) {
        spaceDataList.clear();
        spaceDataList.addAll(spaceData);
        // Extent of changes to data are unknown so update everything
        recyclerView.getAdapter().notifyDataSetChanged();
      }
    });

    // Allow listed items to have a context menu
    registerForContextMenu(recyclerView);

    // Load data for first time
    refresh();
  }

  private void setDataType() {
    String selection = prefs.getString("selection", "");
    switch (selection) {
      case "CME":
        dataTypeSelection = SpaceData.DataType.CME;
        break;
      case "GST":
        dataTypeSelection = SpaceData.DataType.GST;
        break;
      case "FLR":
        dataTypeSelection = SpaceData.DataType.FLR;
        break;
      default:
        Toast.makeText(this, "An error occurred, please try again.", Toast.LENGTH_SHORT)
            .show();
    }
  }

  private void refresh() {
    SpaceDataRepository.getInstance().loadSpaceData(dataTypeSelection);
  }

  /**
   * Inflates the event list options menu into the passed menu object.
   * @param menu object to load options menu into
   * @return true if menu was inflated successfully
   */
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.event_list_menu, menu);
    return true;
  }

  /**
   * Checks if the user selected the refresh option and refreshes the space data if so.
   * @param item menu item selected by the user
   * @return true if handling the option selection was successful
   */
  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    switch (item.getItemId()) {
      case R.id.refresh:
        if (dataTypeSelection != null) refresh(); // Avoid refreshing before selection stored
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  /**
   * Inflates the event list options menu into the passed menu object.
   * @param menu object to load context menu into
   * @param v the view for which the context menu is being built
   * @param menuInfo extra info about the item for which the context menu will be shown
   */
  @Override
  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.share_item_menu, menu);
  }

  /**
   * Checks if the user selected the share item option and sends the space data the user selected in
   * a share intent if so.
   * @param item menu item selected by the user
   * @return false to allow normal context menu processing to continue
   */
  @Override
  public boolean onContextItemSelected(@NonNull MenuItem item) {
    // No null pointer exception as RecyclerView should have adapter to return by this point
    if (item.getItemId() == R.id.share_item)
      ((SpaceDataAdapter) recyclerView.getAdapter()).shareItem(this);
    return super.onContextItemSelected(item);
  }
}