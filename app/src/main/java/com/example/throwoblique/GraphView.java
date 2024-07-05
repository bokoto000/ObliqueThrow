package com.example.throwoblique;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class GraphView extends View {
    private Paint paint;
    private Paint textPaint;
    private double[] trajectoryX;
    private double[] trajectoryY;
    private double ballX;
    private double ballY;
    private static final double PADDING_RATIO = 0.1; // 10% padding
    private static final double PADDING_RATIO_X = 0.2; // 20% padding

    private String xAxisLabel, yAxisLabel;

    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(40);
        textPaint.setAntiAlias(true);
    }

    public void setTrajectory(double[] x, double[] y) {
        trajectoryX = x;
        trajectoryY = y;
        invalidate();
    }

    public void setLabels(String xLabel, String yLabel) {
        xAxisLabel = xLabel;
        yAxisLabel = yLabel;
        invalidate();
    }

    public void setBallPosition(double x, double y) {
        ballX = x;
        ballY = y;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (trajectoryX == null || trajectoryY == null) {
            Log.e("GraphView", "Trajectory arrays are not initialized");
            return;
        }

        double maxX = getMax(trajectoryX);
        double maxY = getMax(trajectoryY);
        if (maxX == 0 || maxY == 0) {
            Log.e("GraphView", "Maximum values for scaling are zero");
            return;
        }

        int width = getWidth();
        int height = getHeight();

        double scaleX = (width * (1 - PADDING_RATIO_X)) / maxX;
        double scaleY = (height * (1 - PADDING_RATIO)) / maxY;
        double scale = Math.min(scaleX, scaleY);

        double offsetX = (width - (maxX * scale)) / 2 + 15;
        double offsetY = (height - (maxY * scale)) / 2;

        // Draw X and Y axis labels
        drawAxisLabels(canvas, width, height, offsetX, offsetY);
        // Draw Y-axis label ("Height")
        float yAxisLabelXPosition = (float) offsetX + 40; // Adjust as necessary for your layout
        float yAxisLabelYPosition = (float) height / 2;
        canvas.save();
        canvas.rotate(-90, yAxisLabelXPosition, yAxisLabelYPosition);
        canvas.drawText(yAxisLabel, yAxisLabelXPosition, yAxisLabelYPosition, textPaint);
        canvas.restore();

        // Draw X-axis label ("Time")
        float xAxisLabelXPosition = (float) (width / 2 );
        float xAxisLabelYPosition = (float) (height - offsetY - 10); // Adjust as necessary for your layout
        canvas.drawText(xAxisLabel, xAxisLabelXPosition, xAxisLabelYPosition, textPaint);


        // Draw the trajectory
        for (int i = 1; i < trajectoryX.length; i++) {
            float startX = (float) (trajectoryX[i - 1] * scale + offsetX);
            float startY = (float) (height - (trajectoryY[i - 1] * scale + offsetY)); // invert Y-axis
            float endX = (float) (trajectoryX[i] * scale + offsetX);
            float endY = (float) (height - (trajectoryY[i] * scale + offsetY)); // invert Y-axis

            canvas.drawLine(startX, startY, endX, endY, paint);
        }

        // Draw the ball
        paint.setColor(Color.RED);
        float ballPosX = (float) (ballX * scale + offsetX);
        float ballPosY = (float) (height - (ballY * scale + offsetY)); // invert Y-axis
        canvas.drawCircle(ballPosX, ballPosY, 10, paint);

        // Draw the bounding box
        paint.setColor(Color.BLACK);
        canvas.drawLine((float) offsetX, (float) (height - offsetY), (float) (offsetX + maxX * scale), (float) (height - offsetY), paint); // bottom
        canvas.drawLine((float) offsetX, (float) (height - offsetY), (float) offsetX, (float) (height - offsetY - maxY * scale), paint); // left
    }

    private void drawAxisLabels(Canvas canvas, int width, int height, double offsetX, double offsetY) {
        double maxX = getMax(trajectoryX);
        int numberOfXLabels = Math.max(3, (int) (width / 200)); // Adjust the divisor for label spacing
        double xLabelInterval = maxX / numberOfXLabels;

        // Draw Y-axis labels (height)
        double maxY = getMax(trajectoryY);
        int numberOfYLabels = Math.max(3, (int) (height / 120)); // Adjust the divisor for label spacing
        double yLabelInterval = maxY / numberOfYLabels;

        float labelYXPosition = (float) offsetX - 60; // Move Y-axis labels 60 pixels to the left of the Y-axis
        for (int i = 0; i <= numberOfYLabels; i++) {
            float labelY = (float) (height - offsetY - (i * (height - 2 * offsetY) / numberOfYLabels));
            double heightValue = yLabelInterval * i;
            canvas.drawText(String.format("%.1f", heightValue), labelYXPosition, labelY, textPaint);
        }

        // Draw X-axis labels (time)
        float labelYYPosition = height - 30; // Adjust Y position for X-axis labels
        for (int i = 0; i <= numberOfXLabels; i++) {
            float x = (float) (offsetX + i * (width - 2 * offsetX) / numberOfXLabels);
            double timeValue = xLabelInterval * i;
            canvas.drawText(String.format("%.1f", timeValue), x, labelYYPosition + 15, textPaint);
        }
    }
    private double getMax(double[] array) {
        if (array == null || array.length == 0) {
            Log.e("GraphView", "Array is null or empty");
            return 0;
        }
        double max = array[0];
        for (double v : array) {
            if (v > max) {
                max = v;
            }
        }
        return max;
    }
}
