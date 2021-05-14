package ghost;

import java.util.Arrays;

import processing.core.PApplet;
import processing.core.PImage;

public class Ambusher extends Ghost{

    /**
     * Constructor
     * @param x x position.
     * @param y y position.
     * @param app App reference.
     * @param manager GameManager instance.
     * @param sprite Main sprite to be displayed.
     */
    public Ambusher(int x, int y, App app, GameManager manager, PImage sprite){
        super(x, y, app, manager, sprite, manager.app.loadImage("src/main/resources/ambusherBlink.png"));
    }

    /**
     * Sets the ambusher's target to the top right corner of the map.
     */
    public void setScatterTarget(){
        this.scatterTarget = new int[] {0, this.manager.getMap().get(0).size() - 1};
    }

    /**
     * Goes to 8 tiles ahead of the Waka's current direction.
     * If 8 tiles ahead goes out of bounds of the screen, then go as far as possible instead.
     */
    public void chase(){
        int[] tempGoTo = manager.getWaka().coordinates.clone();
        if (this.manager.getWaka().getDirection() == 1){
            tempGoTo[1] += 8;
            if (tempGoTo[1] >= manager.getMap().get(0).size()){
                tempGoTo[1] = manager.getMap().get(0).size();
            }
        } else if (this.manager.getWaka().getDirection() == 2){
            tempGoTo[1] -= 8;
            if (tempGoTo[1] < 0){
                tempGoTo[1] = 0;
            }
        } else if (this.manager.getWaka().getDirection() == 4){
            tempGoTo[0] -= 8;
            if (tempGoTo[0] < 0){
                tempGoTo[0] = 0;
            }
        } else if (this.manager.getWaka().getDirection() == 3){
            tempGoTo[0] += 8; 
            if (tempGoTo[0] >= manager.getMap().size()){
                tempGoTo[0] = manager.getMap().size();
            }
        }
        this.setTarget(tempGoTo);
        this.goTo(this.getTarget(), this.manager.getMap());
    }


    /**
     * Sets the scatter target of the ambusher and then goes to it.
     */
    public void scatter(){
        this.setTarget(scatterTarget);
        this.goTo(this.getTarget(), this.manager.getMap());
    }
}