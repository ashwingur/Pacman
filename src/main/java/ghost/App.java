package ghost;

import processing.core.PApplet;
import processing.core.PImage;
import java.util.*;

public class App extends PApplet {

    /**
     * Width of the application in pixels.
     */
    public static final int WIDTH = 448;
    /**
     * Height of the application in pixels.
     */
    public static final int HEIGHT = 576;
    /**
     * Framerate of the application (frames per second).
     */
    public static final int FRAMERATE = 60;

    /**
     * Amount of time that each frame lasts (seconds).
     */
    public static final double timePerFrame = 1.0 / FRAMERATE;

    /**
     * Reference to a GameManager instance.
     */
    public GameManager manager;

    public String configFile = "config.json";

    /**
     * Setting the framerate, line colour and initialises manager.
     */
    public void setup() {
        frameRate(FRAMERATE);
        // Lines are white
        stroke(255);
        this.manager = new GameManager(this, configFile);      
    }

    /**
     * Called during testing, for a custom config file that is not the default one.
     * @param configFile A config.json file
     */
    public void setup(String configFile){
        this.configFile = configFile;
        System.out.println("This is run");
        frameRate(FRAMERATE);
        // Lines are white
        stroke(255);
        this.manager = new GameManager(this, configFile); 
    }

    /**
     * Setting the dimensions of the application.
     */
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * Called once per frame. Processes the game logic and draws to screen.
     */
    public void draw() { 
        // With 60 framerate, increase time by 1/60 second each frame
        if (this.manager.inRestartMode){
            this.manager.restart(this.manager.gameFinishedText);
        } else {
            this.manager.gameMove();
            this.manager.drawMap(this);
            this.manager.drawEntities(this);   
        }
    }

    /**
     * Main method.
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        PApplet.main("ghost.App");
    }

    /**
     * Called whenever a key is pressed.
     * Passes the keyCode to the GameManager to handle.
     */
    public void keyPressed(){
        manager.action(keyCode);
    }
}
