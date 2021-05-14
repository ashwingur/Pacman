package ghost;

import processing.core.PApplet;
import processing.core.PImage;
import java.util.*;

public class PathBlock extends GameObject{

    private boolean hasItem;

    /**
     * The sprite of the pathblock if it has an item on it.
     */
    protected PImage originalSprite;

    /**
     * Constructor
     * @param x x position.
     * @param y y position.
     * @param sprite Sprite of the pathblock if it has one.
     */
    public PathBlock(int x, int y, PImage sprite){
        super(x, y, sprite);
        this.originalSprite = sprite;
    }

    /**
     * returns false because waka can walk through it.
     */
    public boolean collide(List<List<GameObject>> map, int direction){
        return false;
    }

    /**
     * Is overriden in inherited powerup classes. Air pathblocks do nothing.
     * @param manager GameManager instance.
     */
    public void consumeItem(GameManager manager){
    }

    /**
     * @return true if the block still has an item on it, otherwise false.
     */
    public boolean hasItem(){
        return this.hasItem;
    }

    /**
     * @param bool true to restore the item on the pathblock again.
     */
    public void setHasItem(boolean bool){
        this.hasItem = bool;
    }

    /**
     * Resets the block back to its original state.
     * @param manager GameManager instance.
     */
    public void resetBlock(GameManager manager){
        this.setSprite(this.originalSprite);
        this.setHasItem(true);
        manager.addFruit();
    }

}