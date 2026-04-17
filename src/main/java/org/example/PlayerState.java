package org.example;
import java.io.Serializable;

public class PlayerState implements Serializable {
    public String id;
    public double x, y;
    public double angle;
    public int colorId;

    public PlayerState(String id, double x, double y, double angle, int colorId) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.colorId = colorId;
    }
}