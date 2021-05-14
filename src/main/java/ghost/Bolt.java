package ghost;

import processing.core.PApplet;
import processing.core.PImage;

public class Bolt extends PathBlock{

    /**
     * Constructor
     * @param x x position.
     * @param y y position.
     * @param sprite Sprite to be displayed.
     */
    public Bolt(int x, int y, PImage sprite){
        super(x, y, sprite);
        this.setHasItem(true);
    }

    /**
     * Sets the waka's speed to 4 for a duration.
     * @param manager GameManager instance.
     */
    public void consumeItem(GameManager manager){
        if (this.hasItem()){
            manager.deductFruit();
            this.setSprite(null);
            this.setHasItem(false);
            Waka.isBolting = true;
            Waka.boltTime = 0;
            manager.getWaka().speed = 4;

            // Re-adjust the waka so it is at the centre of the cell
            manager.getWaka().setX(manager.getWaka().coordinates[1] * 16 + 8);
            manager.getWaka().setY(manager.getWaka().coordinates[0] * 16 + 8);
        }
    }

    
}