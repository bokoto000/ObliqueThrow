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

        double scaleX = (width * (1 - PADDING_RATIO)) / maxX;
        double scaleY = (height * (1 - PADDING_RATIO)) / maxY;
        double scale = Math.min(scaleX, scaleY);

        double offsetX = (width - (maxX * scale)) / 2;
        double offsetY = (height - (maxY * scale)) / 2;

        // Draw X and Y axis labels
        drawAxisLabels(canvas, width, height, offsetX, offsetY);

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
        canvas.drawLine((float) offsetX, (float) (height - offsetY - maxY * scale), (float) (offsetX + maxX * scale), (float) (height - offsetY - maxY * scale), paint); // top
        canvas.drawLine((float) (offsetX + maxX * scale), (float) (height - offsetY), (float) (offsetX + maxX * scale), (float) (height - offsetY - maxY * scale), paint); // right
    }

    private void drawAxisLabels(Canvas canvas, int width, int height, double offsetX, double offsetY) {
        // Draw Y-axis labels (height)
        for (int i = 0; i <= 10; i++) {
            float y = (float) (height - offsetY - i * (height * (1 - PADDING_RATIO)) / 10);
            canvas.drawText(String.format("%.1f", getMax(trajectoryY) * i / 10), 10, y, textPaint);
        }

        // Draw X-axis labels (time)
        for (int i = 0; i <= 10; i++) {
            float x = (float) (offsetX + i * (width * (1 - PADDING_RATIO)) / 10);
            canvas.drawText(String.format("%.1f", getMax(trajectoryX) * i / 10), x, height - 10, textPaint);
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
