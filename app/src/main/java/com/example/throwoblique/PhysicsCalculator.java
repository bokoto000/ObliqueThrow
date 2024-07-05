package com.example.throwoblique;

public class PhysicsCalculator {
    private static final double GRAVITY = 9.8;

    public static double[] calculateTrajectory(double speed, double angle, double time) {
        double angleRad = Math.toRadians(angle);
        double x = speed * time * Math.cos(angleRad);
        double y = speed * time * Math.sin(angleRad) - 0.5 * GRAVITY * time * time;
        return new double[]{x, y};
    }

    public static double calculateFlightTime(double speed, double angle) {
        double angleRad = Math.toRadians(angle);
        return (2 * speed * Math.sin(angleRad)) / GRAVITY;
    }

    public static double calculateFinalXPosition(double speed, double angle, double flightTime) {
        double angleRad = Math.toRadians(angle);
        return speed * flightTime * Math.cos(angleRad);
    }
}
