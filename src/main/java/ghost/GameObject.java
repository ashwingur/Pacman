package ghost;

import processing.core.PApplet;
import processing.core.PImage;
import java.util.*;

public abstract class GameObject{
    private int x;
    private int y;
    private PImage sprite;
    /**
     * Cell coordinates of the object [y,x].
     */
    protected int[] coordinates;
    private int xyOffset;

    /**
     * Constructor
     * @param x pixel coordinate of the object in the x direction.
     * @param y pixel coordinate of the object in the y direction.
     * @param sprite The sprite to be displayed on the screen.
     */
    public GameObject(int x, int y, PImage sprite){
        this.x = x;
        this.y = y;
        this.sprite = sprite;
        this.coordinates = new int[] {y/16, x/16};
    }

    /**
     * Gets the current x value of the object.
     * @return Position of the object in the x direction.
     */
    public int getX(){
        return this.x;
    }

    /**
     * Gets the current y value of the object.
     * @return Position of the object in the y direction.
     */
    public int getY(){
        return this.y;
    }

    /**
     * Sets the current x position of the object.
     * @param x Pixel coordinate representing the x position of the object.
     */
    public void setX(int x){
        this.x = x;
    }
    
    /**
     * Sets the current y position of the object.
     * @param y Pixel coordinate representing the x position of the object.
     */
    public void setY(int y){
        this.y = y;
    }

    /**
     * Gets the current sprite of the object.
     * @return Current sprite of the object.
     */
    public PImage getSprite(){
        return this.sprite;
    }

    /**
     * Sets the current sprite of the object.
     * @param sprite The sprite that the current sprite will be set to.
     */
    public void setSprite(PImage sprite){
        this.sprite = sprite;
    }

    /**
     * Gets the offset value that will be used in the final sprite draw.
     * @return An integer representing the pixel offset.
     */
    public int getOffSet(){
        return this.xyOffset;
    }

    /**
     * Sets the offset value that will be used in the final sprite draw.
     * @param offSet An integer representing the pixel offset.
     */
    public void setOffSet(int offSet){
        this.xyOffset = offSet;
    }

    /**
     * Used to detect collision between entities and obstacles/pathblocks
     * @param map Current map
     * @param direction Integer value representing the direction to check the collision.
     * @return True if a collision occurs, false if it does not.
     */
    public abstract boolean collide(List<List<GameObject>> map, int direction);
}