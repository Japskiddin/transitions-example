package ru.androidtools.transitions_example;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import ru.androidtools.transitions_example.one_activity.SceneActivity;
import ru.androidtools.transitions_example.two_activitiws.FirstActivity;

public class MainActivity extends Activity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Button btn_one_activty = findViewById(R.id.btn_one_activity);
    Button btn_two_activities = findViewById(R.id.btn_two_activities);
    btn_two_activities.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        startActivity(new Intent(MainActivity.this, FirstActivity.class));
      }
    });
    btn_one_activty.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        startActivity(new Intent(MainActivity.this, SceneActivity.class));
      }
    });
  }
}
