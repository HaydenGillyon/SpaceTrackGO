package com.SpaceTrackGO;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }

  /**
   * Creates and shows a long toast with the provided message.
   * @param context application context for showing the toast in
   * @param message information to show in a toast message on screen
   */
  public static void showToast(Context context, String message) {
    Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
    toast.show();
  }
}