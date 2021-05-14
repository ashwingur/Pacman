package ghost;

import processing.core.PApplet;
import processing.core.PImage;
import java.util.*;

public class Obstacle extends GameObject{

    /**
     * Constructor.
     * @param x x pixel coordinate.
     * @param y y pixel coordinate.
     * @param sprite Obstacle sprite.
     */
    public Obstacle(int x, int y, PImage sprite){
        super(x, y, sprite);
    }

    /**
     * Always true because an entity cannot move through a wall
     * @return true.
     */
    public boolean collide(List<List<GameObject>> map, int direction){
        return true;
    }
}