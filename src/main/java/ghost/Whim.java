package ghost;

import java.util.Arrays;

import processing.core.PApplet;
import processing.core.PImage;

public class Whim extends Ghost{

    /**
     * The chaser ghost that whim will be basing its chase location off.
     */
    public static Chaser chaser;

     /**
     * Constructor
     * @param x x position.
     * @param y y position.
     * @param app App reference.
     * @param manager GameManager instance.
     * @param sprite Main sprite to be displayed.
     */
    public Whim(int x, int y, App app, GameManager manager, PImage sprite){
        super(x, y, app, manager, sprite, manager.app.loadImage("src/main/resources/whimBlink.png"));
    }

    /**
     * Sets the whim's target to the bottom right corner of the map.
     */
    public void setScatterTarget(){
        this.scatterTarget = new int[] {manager.getMap().size() - 1, manager.getMap().get(0).size() - 1};
    }

    /**
     * Goes to double the vector from Chaser to 2 grid spaces ahead of Waka.
     */
    public void chase(){
        int[] tempGoTo = this.manager.getWaka().coordinates.clone();
        if (this.manager.getWaka().getDirection() == 1){
            tempGoTo[1] += 2;
        } else if (this.manager.getWaka().getDirection() == 2){
            tempGoTo[1] -= 2;
        } else if (this.manager.getWaka().getDirection() == 3){
            tempGoTo[0] -= 2;
        } else if (this.manager.getWaka().getDirection() == 4){
            tempGoTo[0] += 2;
        }

        tempGoTo[0] = Whim.chaser.coordinates[0] + 2 * (tempGoTo[0] - Whim.chaser.coordinates[0]);
        tempGoTo[1] = Whim.chaser.coordinates[1] + 2 * (tempGoTo[1] - Whim.chaser.coordinates[1]);

        if (tempGoTo[0] < 0){
            tempGoTo[0] = 0;
        } else if (tempGoTo[0] >= this.manager.getMap().size()){
            tempGoTo[0] = this.manager.getMap().size() - 1;
        }

        if (tempGoTo[1] < 0){
            tempGoTo[1] = 0;
        } else if (tempGoTo[1] >= this.manager.getMap().get(0).size()){
            tempGoTo[1] = this.manager.getMap().get(0).size() - 1;
        }

        this.setTarget(tempGoTo);
        this.goTo(this.getTarget(), this.manager.getMap());
    }

    /**
     * Sets the scatter target of the whim and then goes to it.
     */
    public void scatter(){
        this.setTarget(scatterTarget);
        this.goTo(this.getTarget(), this.manager.getMap());
    }
}