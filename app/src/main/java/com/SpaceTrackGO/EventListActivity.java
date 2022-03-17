package com.SpaceTrackGO;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

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
      @Override
      public void onChanged(List<SpaceData> spaceData) {
        spaceDataList.clear();
        spaceDataList.addAll(spaceData);
        // Extent of changes to data are unknown so update everything
        recyclerView.getAdapter().notifyDataSetChanged();
      }
    });

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
      case "saved":
        dataTypeSelection = null;
        break;
      default:
        Toast.makeText(this, "An error occurred, please try again.", Toast.LENGTH_SHORT)
            .show();
    }
  }

  private void refresh() {
    SpaceDataRepository.getInstance().loadSpaceData(this, dataTypeSelection);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.event_list_menu, menu);
    return true;
  }

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
}