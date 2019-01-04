package com.jianjunhuang.lib.stateprogressbar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import com.jianjunhuang.lib.lib.StateProgressBar;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    AtomicInteger pos = new AtomicInteger();
    StateProgressBar stateProgressBar = findViewById(R.id.state_progress_bar);
    stateProgressBar.addState("Review order", "Review order", "Review order","Review order", "Review order", "Review order");
    stateProgressBar.setStatePos(0);

    Button btnLeft = findViewById(R.id.left);
    btnLeft.setOnClickListener(v -> {
      stateProgressBar.setStatePos(pos.decrementAndGet());
    });

    Button btnRight = findViewById(R.id.right);
    btnRight.setOnClickListener(v -> {
      stateProgressBar.setStatePos(pos.incrementAndGet());
    });

  }
}
