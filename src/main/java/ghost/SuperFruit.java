package ghost;

import processing.core.PApplet;
import processing.core.PImage;

public class SuperFruit extends PathBlock{

    /**
     * Constructor
     * @param x x position.
     * @param y y position.
     * @param sprite Sprite to be displayed.
     */
    public SuperFruit(int x, int y, PImage sprite){
        super(x, y, sprite);
        this.setHasItem(true);
    }

    /**
     * The item is eaten, and all the ghosts go into the frightened state for a duration.
     * If ghosts are already in a frightened state, the time is still reset back to 0.
     * @param manager GameManager instance
     */
    public void consumeItem(GameManager manager){
        if (this.hasItem()){
            manager.deductFruit();
            this.setSprite(null);
            this.setHasItem(false);
            Ghost.frightened = true;
            Ghost.frightenedTime = 0;
            for (Ghost g : manager.getGhosts()){
                g.setSprite(Ghost.frightenedSprite);
            }
        }
    }
    
}