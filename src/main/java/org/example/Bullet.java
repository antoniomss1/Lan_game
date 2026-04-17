package org.example;

import java.io.Serializable;

public class Bullet implements Serializable {
    public double x, y;
    public double angle;
    public int colorId;

    public double speed = 300; // pixels por segundo

    public Bullet(double x, double y, double angle, int colorId) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.colorId = colorId;
    }
}