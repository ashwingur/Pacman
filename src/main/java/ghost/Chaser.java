package ghost;

import processing.core.PApplet;
import processing.core.PImage;

public class Chaser extends Ghost{

    /**
     * Constructor
     * @param x x position.
     * @param y y position.
     * @param app App reference.
     * @param manager GameManager instance.
     * @param sprite Main sprite to be displayed.
     */
    public Chaser(int x, int y, App app, GameManager manager, PImage sprite){
        super(x, y, app, manager, sprite, manager.app.loadImage("src/main/resources/chaserBlink.png"));
        if (Whim.chaser == null){
            Whim.chaser = this;
        }
    }

     /**
     * Sets the chaser's target to the top left corner of the map.
     */
    public void setScatterTarget(){
        this.scatterTarget = new int[] {0,0};
    }

    /**
     * Sets the chaser's target to be the location of Waka.
     */
    public void chase(){
        this.setTarget(this.manager.getWaka().coordinates);
        this.goTo(this.getTarget(), this.manager.getMap());
    }

    /**
     * Sets the scatter target of the chaser and then goes to it.
     */
    public void scatter(){
        this.setTarget(scatterTarget);
        this.goTo(this.getTarget(), this.manager.getMap());
    }
}