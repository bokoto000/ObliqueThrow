package com.example.throwoblique;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private EditText speedInput, angleInput;
    private Button startButton;
    private TextView timeTextView, positionXTextView, positionYTextView;
    private GraphView graphView, graphView2;
    private Handler handler;
    private double speed, angle, flightTime;
    private double[] trajectoryX, trajectoryY, timeValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        speedInput = findViewById(R.id.speedInput);
        angleInput = findViewById(R.id.angleInput);
        startButton = findViewById(R.id.startButton);
        timeTextView = findViewById(R.id.timeTextView);
        positionXTextView = findViewById(R.id.positionXTextView);
        positionYTextView = findViewById(R.id.positionYTextView);
        graphView2 = findViewById(R.id.graphView2);
        graphView = findViewById(R.id.graphView);
        handler = new Handler();

        startButton.setOnClickListener(v -> {
            if (TextUtils.isEmpty(speedInput.getText()) || TextUtils.isEmpty(angleInput.getText())) {
                return;
            }

            speed = Double.parseDouble(speedInput.getText().toString());
            angle = Double.parseDouble(angleInput.getText().toString());

            flightTime = PhysicsCalculator.calculateFlightTime(speed, angle);
            int steps = (int) (flightTime * 100);
            trajectoryX = new double[steps];
            trajectoryY = new double[steps];

            for (int i = 0; i < steps; i++) {
                double time = i / 100.0;
                double[] position = PhysicsCalculator.calculateTrajectory(speed, angle, time);
                trajectoryX[i] = position[0];
                trajectoryY[i] = position[1];
            }

            double finalX = PhysicsCalculator.calculateFinalXPosition(speed, angle, flightTime);
            trajectoryX = Arrays.copyOf(trajectoryX, trajectoryX.length + 1);
            trajectoryY = Arrays.copyOf(trajectoryY, trajectoryY.length + 1);
            trajectoryX[trajectoryX.length - 1] = finalX;
            trajectoryY[trajectoryY.length - 1] = 0;
            graphView.setTrajectory(trajectoryX, trajectoryY);
            timeValues = new double[trajectoryY.length];
            for (int i = 0; i < timeValues.length; i++) {
                timeValues[i] = i / 100.0; // Assuming 100 steps per second as in the original setup
            }
            graphView2.setTrajectory(timeValues, trajectoryY);
            graphView.setLabels("Distance (m)", "Height (m)");
            graphView2.setLabels("Time (s)", "Height (m)");
            startAnimation();
        });
    }

    private void startAnimation() {
        handler.postDelayed(new Runnable() {
            private int i = 0;

            @Override
            public void run() {
                if (i < trajectoryX.length) {
                    graphView.setBallPosition(trajectoryX[i], trajectoryY[i]);
                    graphView2.setBallPosition(timeValues[i], trajectoryY[i]);
                    timeTextView.setText(String.format("Time: %.2fs", i / 100.0));
                    positionXTextView.setText(String.format("Position X: %.2fm", trajectoryX[i]));
                    positionYTextView.setText(String.format("Position Y: %.2fm", trajectoryY[i]));
                    i++;
                    handler.postDelayed(this, 10);
                }
            }
        }, 10);
    }
}
