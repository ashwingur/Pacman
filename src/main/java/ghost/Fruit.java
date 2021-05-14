package ghost;

import processing.core.PApplet;
import processing.core.PImage;

public class Fruit extends PathBlock{

    /**
     * Constructor
     * @param x x position.
     * @param y y position.
     * @param sprite Sprite to be displayed.
     */
    public Fruit(int x, int y, PImage sprite){
        super(x, y, sprite);
        this.setHasItem(true);
    }


    /**
     * The item is consumed.
     * @param manager GameManager instance.
     */
    public void consumeItem(GameManager manager){
        if (this.hasItem()){
            manager.deductFruit();
            this.setSprite(null);
            this.setHasItem(false);
        }
    }

}