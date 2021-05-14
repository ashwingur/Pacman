package ghost;

import processing.core.PApplet;
import processing.core.PImage;

import java.util.*;

public class Waka extends Entity{

    private long lives;
    private PImage[] directionImage;

    /**
     * Length of time the speed buff should last.
     */
    protected static double boltLength;
    /**
     * Current time speed buff has been active.
     */
    protected static double boltTime = 0;
    /**
     * Indicates whether bolt mode is active.
     */
    protected static boolean isBolting = false;

    /**
     * Constructor.
     * Sets the waka's lives, speed, loads the direction images of the waka.
     * Sets the amount of frames each sprite will last before switching.
     * Sets the offset.
     * @param x x pixel coordinate.
     * @param y y pixel coordinate.
     * @param app App instance.
     * @param manager GameManager instance.
     */
    public Waka(int x, int y, App app, GameManager manager){
        super(x, y, app.loadImage("src/main/resources/playerClosed.png"), manager);
    
        this.lives = manager.getTotalLives();
        
        PImage pUp = app.loadImage("src/main/resources/playerUp.png");
        PImage pRight = app.loadImage("src/main/resources/playerRight.png");
        PImage pDown = app.loadImage("src/main/resources/playerDown.png");
        PImage pLeft = app.loadImage("src/main/resources/playerLeft.png");

        this.directionImage = new PImage[] {pRight, pLeft, pDown, pUp};
        this.offSet = 12;
        this.changeSpriteFrame1 = 7;
        this.changeSpriteFrame2 = 7;

        this.sprite1 = app.loadImage("src/main/resources/playerClosed.png");

    }

    /**
     * Gets the direction sprite based on the current direction of the waka.
     * @param i Integer representing direction (1: right, 2: left, 3: down, 4: up). Note that 1 must be subtracted from the direction to get the correct index reference.
     * @return The direction sprite.
     */
    public PImage getDirectionImage(int i){
        return this.directionImage[i];
    }

    /**
     * If the waka is at the centre of a cell, eat the fruit in the cell if it exists.
     * @param pathBlocks 2D list of pathBlocks.
     * @param manager GameManager instance.
     */
    public void eatFruit(List<List<PathBlock>> pathBlocks, GameManager manager){
        if (this.getX() % 16 == 8 || this.getY() % 16 == 8){
            PathBlock pb = pathBlocks.get(this.coordinates[0]).get(this.coordinates[1]);
            
            pb.consumeItem(manager);
        }
    }

    /**
     * Waka logic.
     * Updates coordinates, attempts to move waka in direction of key press.
     * Checks for collision, moves Waka.
     * Changes sprite.
     */
    public void tick(GameManager manager){
        this.updateCoords();

        if (manager.initialMove){
            // First ever move in the game, go left.
            manager.action(37);
        } else if  (!this.collide(manager.getMap(), this.getNextMove())){
            if (this.getNextMove() == 1){
                manager.action(39);
            } else if (this.getNextMove() == 2){
                manager.action(37);
            } else if (this.getNextMove() == 3){
                manager.action(40);
            } else if (this.getNextMove() == 4){
                manager.action(38);
            }
        }
        this.collide(manager.getMap(), this.getDirection());
        this.move();
        this.eatFruit(manager.pathBlocks, manager);
        this.setDirectionSprite();
        this.changeSprite();
    }

    /**
     * Sets the next move of the Waka based on keyboard input/keyCode.
     * If the move is valid, the direction and velocity is changed to the relevant direction.
     * @param keyCode integer representing the key pressed.
     * @param manager Game manager instance.
     */
    public void wakaAction(int keyCode, GameManager manager){
        /*
            37 -- Left
            38 -- Up
            39 -- Right
            40 -- Down
        */
        if (keyCode == 38){
            this.setNextMove(4);
            if (!this.collide(manager.getMap(), 4)){
                if (this.getX() % 16 == 8){
                    this.setYVel(true);
                    this.setDirection(4);
                }
            }

        } else if (keyCode == 39){
            this.setNextMove(1);
            if (!this.collide(manager.getMap(), 1)){
                if (this.getY() % 16 == 8){
                    this.setXVel(true);
                    this.setDirection(1);
                }
            }

        } else if (keyCode == 40){
            this.setNextMove(3);
            if (!this.collide(manager.getMap(), 3)){
                if (this.getX() % 16 == 8){
                    this.setYVel(false);
                    this.setDirection(3);
                }
            }

        } else if (keyCode == 37){
            this.setNextMove(2);
            if (!this.collide(manager.getMap(), 2)){
                if (this.getY() % 16 == 8){
                    this.setXVel(false);
                    this.setDirection(2);
                }
            }
        }
    }

    /**
     * Set the direction image of the sprite based on its current direction.
     */
    public void setDirectionSprite(){
        this.sprite2 = this.getDirectionImage(this.getDirection() - 1);
    }

    /**
     * Decrement a life from the waka.
     */
    public void deductLife(){
        this.lives--;
    }

    /**
     * Gets the remaining lives of the waka.
     * @return Remaining lives.
     */
    public long getLives(){
        return this.lives;
    }

    /**
     * Sets the remaining lives of the Waka.
     * @param l lives.
     */
    public void setLives(long l){
        this.lives = l;
    }

}