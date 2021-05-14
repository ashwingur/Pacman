package ghost;

import processing.core.PApplet;
import processing.core.PImage;

public class Ignorant extends Ghost{

    /**
     * Constructor
     * @param x x position.
     * @param y y position.
     * @param app App reference.
     * @param manager GameManager instance.
     * @param sprite Main sprite to be displayed.
     */
    public Ignorant(int x, int y, App app, GameManager manager, PImage sprite){
        super(x, y, app, manager, sprite, manager.app.loadImage("src/main/resources/ignorantBlink.png"));
        
    }

    /**
     * Sets the scatter target of ignorant to be the bottom left corner of the map.
     */
    public void setScatterTarget(){
        this.scatterTarget = new int[] {manager.getMap().size() - 1, 0};
    }

    /**
     * If more than 8 units away from Waka (straight line distance), target location is Waka. 
     * Otherwise, target location is bottom left corner 
     */
    public void chase(){
        if (Ghost.distance(this.coordinates, manager.getWaka().coordinates) > 8){
            this.setTarget(manager.getWaka().coordinates);
        } else {
            // Same as scatterTarget (Bottom left corner)
            this.setTarget(scatterTarget);
        }
        this.goTo(this.getTarget(), this.manager.getMap());
    }

    /**
     * Sets the scatter target of the ignorant and then goes to it.
     */
    public void scatter(){
        this.setTarget(scatterTarget);
        this.goTo(this.getTarget(), this.manager.getMap());
    }
}