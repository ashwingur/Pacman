package ghost;

import processing.core.PApplet;
import processing.core.PImage;
import java.util.*;

public abstract class Entity extends GameObject{


    private int xVel;
    private int yVel;
    /**
     * Entity speed in pixels per frame.
     */
    protected int speed;
    private int nextMove;
    // 1 = right, 2 = left, 3 = down, 4 = up
    private int direction;
    private final int xSpawn;
    private final int ySpawn;
    /**
     * The offset value to apply to the sprite in the final drawing.
     */
    protected int offSet;
    
    private boolean changeSprite = true;
    /**
     * Increments each frame, and resets when the sprite is updated
     */
    protected int changeSpriteCounter;
    /**
     * Amount of frames sprite1 should last.
     */
    protected int changeSpriteFrame1;
    /**
     * Amount of frames sprite2 should last.
     */
    protected int changeSpriteFrame2;
    /**
     * One of two sprites that is displayed for a certain amount of time.
     */
    protected PImage sprite1;
    /**
     * One of two sprites that is displayed for a certain amount of time.
     */
    protected PImage sprite2;
    /**
     * GameManager reference
     */
    protected GameManager manager;

    /**
     * 
     * @param x Pixel coordinate in the x direction.
     * @param y Pixel coordinate in the y direction.
     * @param sprite Sprite to be displayed.
     * @param manager To access the speed specified in config.json.
     */
    public Entity(int x, int y, PImage sprite, GameManager manager){
        super(x, y, sprite);
        this.xSpawn = x;
        this.ySpawn = y;
        this.speed = Math.toIntExact(manager.getSpeed());
        this.xVel = -speed;
        this.yVel = 0;
        this.direction = 2;
        this.changeSpriteCounter = 0;
        this.manager = manager;
    }

    /**
     * Updates the x and y coordinates of the entity based on the current x and y velocity.
     */
    public void move(){
        this.setX(this.getX() + xVel);
        this.setY(this.getY() + yVel);
    }

    /**
     * Check if the entity is going to collide with an obstacle.
     * @param map Current map state
     * @param d Direction in which to check for a collision
     * @return true if the entity is at the centre of its current cell and the direction it is headed is an obstacle and false otherwise.
     */
    public boolean collide(List<List<GameObject>> map, int d){
        int[] c = coordinates;
        
        if (d == 1){
            GameObject next = map.get(c[0]).get(c[1] + 1);
            // Is the next wall a collision or not?
            if (next.collide(null, 0)){
                // Only allow a turn if the waka is at the centre of the cell
                if (getX() % 16 == 8){
                    this.xVel = 0;
                    return true;
                }
            }
        } else if (d == 2){
            GameObject next = map.get(c[0]).get(c[1] - 1);
            if (next.collide(null, 0)){
                if (getX() % 16 == 8){
                    this.xVel = 0;
                    return true;
                }
            }
        } else if (d == 3){
            GameObject next = map.get(c[0] + 1).get(c[1]);
            if (next.collide(null, 0)){
                if (getY() % 16 == 8){
                    this.yVel = 0;
                    return true;
                }
            }
        } else if (d == 4){
            GameObject next = map.get(c[0] - 1).get(c[1]);
            if (next.collide(null, 0)){
                if (getY() % 16 == 8){
                    this.yVel = 0;
                    return true;
                }
                
            }
        }

        return false;
    }

    /**
     * Setting the x velocity to positive or negative.
     * @param direction If true, x velocity is positive. If false, x velocity is negative.
     */
    public void setXVel(boolean direction){
        if (direction){
            this.xVel = speed;
        } else {
            this.xVel = -speed;
        }
        this.yVel = 0;
    }

    /**
     * Setting the y velocity to positive or negative.
     * @param direction If true, y velocity is positive. If false, y velocity is negative.
     */
    public void setYVel(boolean direction){
        if (direction){
            this.yVel = -speed;
        } else {
            this.yVel = speed;
        }
        this.xVel = 0;
    }

    /**
     * Update the cell coordinates of the entity based on its current pixel coordinates.
     */
    public void updateCoords(){
        this.coordinates[0] = this.getY() / 16;

        this.coordinates[1] = this.getX() / 16;
        
    }

    /**
     * Changes the sprite if the currently active sprite has been used for its specified time.
     */
    public void changeSprite(){
        if (this.changeSprite){
            this.setSprite(sprite1);
            this.changeSpriteCounter++;
            if (this.changeSpriteCounter == changeSpriteFrame1){
                this.changeSprite = false;
                this.changeSpriteCounter = 0;
            }
            
        } else {
            this.setSprite(sprite2);
            this.changeSpriteCounter++;
            if (this.changeSpriteCounter == changeSpriteFrame2) {
                this.changeSprite = true;
                this.changeSpriteCounter = 0;
            }
        }
    }

    /**
     * Draw the current entity on the screen while applying the x and y offset.
     * @param app App instance.
     */
    public void drawEntity(App app){
        app.image(this.getSprite(), this.getX() - offSet, this.getY() - offSet);
    }

    /**
     * 
     * @param d Direction (1: right, 2: left, 3: down, 4: up).
     */
    public void setDirection(int d){
        this.direction = d;
    }

    /**
     * Gets the current direction
     * @return int value representing direction (1: right, 2: left, 3: down, 4: up).
     */
    public int getDirection(){
        return this.direction;
    }

    /**
     * Sets the direction of the next move the entity is attemping to make.
     * @param m int value representing direciton (1: right, 2: left, 3: down, 4: up).
     */
    public void setNextMove(int m){
        this.nextMove = m;
    }

    /**
     * Gets the direction of the next move the entity is attempting to make.
     * @return int value representing direciton (1: right, 2: left, 3: down, 4: up).
     */
    public int getNextMove(){
        return this.nextMove;
    }

    /**
     * Gets the x value of the original spawn point.
     * @return integer representing the x position of the spawn point (pixel value).
     */
    public int getXSpawn(){
        return this.xSpawn;
    }

    /**
     * Gets the y value of the original spawn point.
     * @return integer representing the y position of the spawn point (pixel value).
     */
    public int getYSpawn(){
        return this.ySpawn;
    }

    /**
     * The entity logic that is performed every frame.
     * This includes movement and collisions.
     * @param manager GameManger instance.
     */
    public abstract void tick(GameManager manager);


}