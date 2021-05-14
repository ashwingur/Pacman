package ghost;

import processing.core.PApplet;
import processing.core.PImage;

public class SodaCan extends PathBlock{

    /**
     * Constructor
     * @param x x position.
     * @param y y position.
     * @param sprite Sprite to be displayed.
     */
    public SodaCan(int x, int y, PImage sprite){
        super(x, y, sprite);
        this.setHasItem(true);
    }

    /**
     * Ghosts go into soda mode for a duration.
     * This item overrides the super fruit mode, so when soda finishes, ghosts go back to 
     * normal mode.
     * @param manager GameManager instance.
     */
    public void consumeItem(GameManager manager){
        if (this.hasItem()){
            manager.deductFruit();
            this.setSprite(null);
            this.setHasItem(false);
            Ghost.isSoda = true;
            Ghost.sodaTime = 0;
            Ghost.frightened = false;
            Ghost.frightenedTime = 0;
            for (Ghost g : manager.getGhosts()){
                g.setSprite(Ghost.sodaSprite);
            }
        }
    }
    
}