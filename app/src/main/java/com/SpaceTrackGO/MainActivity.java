package com.SpaceTrackGO;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
  SharedPreferences prefs;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    prefs = this.getSharedPreferences(getString(R.string.prefs_name), MODE_PRIVATE);

    findViewById(R.id.CME_button).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        setSelection("CME");
        Intent listEvents = new Intent(MainActivity.this, EventListActivity.class);
        startActivity(listEvents);
      }
    });
    findViewById(R.id.GST_button).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        setSelection("GST");
        Intent listEvents = new Intent(MainActivity.this, EventListActivity.class);
        startActivity(listEvents);
      }
    });
    findViewById(R.id.FLR_button).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        setSelection("FLR");
        Intent listEvents = new Intent(MainActivity.this, EventListActivity.class);
        startActivity(listEvents);
      }
    });
    findViewById(R.id.Saved_button).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        setSelection("saved");
        Intent listEvents = new Intent(MainActivity.this, EventListActivity.class);
        startActivity(listEvents);
      }
    });
  }

  /**
   * Stores the selection of the user's button press in SharedPreferences.
   * @param selection name of user's selected data type
   */
  private void setSelection(String selection) {
    SharedPreferences.Editor editor = prefs.edit();
    editor.putString("selection",selection);
    editor.apply();
  }
}