package ghost;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PFont;
import java.util.*;
import java.io.*;

import javafx.scene.input.KeyCode;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class GameManager{

    /**
     * Stores the current map in a 2D list.
     */
    private List<List<GameObject>> map;
    /**
     * Stores the walkable blocks in a 2D list.
     */
    protected List<List<PathBlock>> pathBlocks;
    /**
     * Stores the waka instance that is controlled by the player.
     */
    private Waka waka;
    /**
     * Stores all the ghost instances.
     */
    private List<Ghost> ghosts;
    /**
     * Reference to the App instance.
     */
    protected App app;
    /**
     * Total lives the player starts with. Specified in config.json.
     */
    private long totalLives;
    /**
     * Speed of the player and ghosts. Specified in config.json.
     */
    private long speed;
    /**
     * Length of time ghost is frightened for.
     */
    private double frightenedLength;
    /**
     * Length of the time the soda can lasts for.
     */
    private double sodaLength;
    /**
     * Name of the map file. Specified in config.json.
     */
    private String mapFile;
    /**
     * Stores the length of time each ghost mode will last.
     * Alternates between scatter and chase, starting with scatter.
     */
    private double[] modeLengths;
    /**
     * Index at which modeLengths is currently at.
     */
    protected int modeLengthCounter = 0;
    /**
     * True if this is the first move in the round (Start of game or respawn).
     */
    protected boolean initialMove = true;
    /**
     * Amount of fruit left on the map.
     */
    protected int fruitLeft = 0;
    /**
     * Sprite representing each life the player has left.
     */
    private PImage lifeIconImage;
    /**
     * True if the player enters debug mode.
     */
    protected boolean inDebugMode = false;
    /**
     * Name of the config file.
     */
    private String configFile;
    /**
     * Amount of time the current mode has been running for.
     */
    protected double time = 0;

    protected List<Ghost> removedGhosts;

    private PFont font;

    /**
     * Indicates whether or not the game is in restart mode
     */
    protected boolean inRestartMode = false;

    /**
     * The end screen text to be displayed
     */
    protected String gameFinishedText = "YOU WIN";

    private int textX;

    private int textY;

    /**
     * Constructor.
     * Intialises ghosts, calls parseConfigFile(), calls MapParser.parseMap(),
     * calls addLivesIcons().
     * @param app App instance.
     * @param configFile The configuration file that will specify the game settings and map.
     */
    public GameManager(App app, String configFile){
        this.configFile = configFile;
        this.ghosts = new ArrayList<Ghost>();
        this.removedGhosts = new ArrayList<Ghost>();
        this.app = app;
        this.lifeIconImage = app.loadImage("src/main/resources/playerRight.png");

        this.parseConfigFile();

        Ghost.frightenedLength = frightenedLength;
        Ghost.sodaLength = sodaLength;

        this.map = MapParser.parseMap(mapFile, app, this);

        Ghost.frightenedSprite = app.loadImage("src/main/resources/frightened.png");
        Ghost.sodaSprite = app.loadImage("src/main/resources/sodaGhost.png");

        for (Ghost g : this.ghosts){
            g.manager = this;
            g.setScatterTarget();
        }

        this.addLivesIcons(totalLives);

        // Load and set font
        this.font = app.createFont("src/main/resources/PressStart2P-Regular.ttf", 20);
        this.app.textFont(this.font);

    }


    
    /**
     * Reads the config.json file and extracts the relevant data.
     * Gets totalLives, mapFile, speed, modeLengths.
     */
    private void parseConfigFile(){
        JSONParser jsonParser = new JSONParser();

        try {

            Object configObj = jsonParser.parse(new FileReader(configFile));

            JSONObject configData = (JSONObject) configObj;

            this.totalLives = (long) configData.get("lives");
            this.mapFile = (String) configData.get("map");
            this.speed = (long) configData.get("speed");
            this.frightenedLength = (double) (long) configData.get("frightenedLength");
            this.sodaLength = (double) (long) configData.get("sodaLength");
            Waka.boltLength = (double) (long) configData.get("boltLength");
            JSONArray arr = (JSONArray) configData.get("modeLengths");
            modeLengths = new double[arr.size()];

            for (int i = 0; i < modeLengths.length; i++){
                long l = (long) arr.get(i);

                modeLengths[i] = (double) l;
            }
           
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParseException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        } 
    }


    /**
     * Handles the key press input.
     * Up, right, left, down controls the player. Space toggles on debug mode.
     * Otherwise, do nothing.
     * @param keyCode An integer representing the current key pressed.
     */
    public void action(int keyCode){
        // /*
        //     32 -- Space
        //     37 -- Left
        //     38 -- Up
        //     39 -- Right
        //     40 -- Down
        // */
    
        waka.wakaAction(keyCode, this);
        if (keyCode == 32){
            this.toggleDebugMode();
        }

    }

    /**
     * Handles all the logic for the current frame.
     * If there is no fruit left, player has won and application is closed.
     * Calls methods on the waka and ghost instances (to do with collision and movement).
     * Updates the sprites to be shown for each entity.
     * Updates the current mode depending on the time.
     * Checks if player has hit a ghost.
     */
    public void gameMove(){
        if (initialMove){
            this.ghosts.addAll(removedGhosts);
            this.removedGhosts = new ArrayList<>();
        }
        if (this.fruitLeft == 0){
            textX = 160;
            textY = 204;
            this.gameFinishedText = "YOU WIN";
            this.restart(this.gameFinishedText);
            return;
        }
        this.updateMode();
        waka.tick(this);

        // After waka, perform logic on the ghosts.
        ghosts.stream().forEach(g -> g.tick(this));

        initialMove = false;

        // Check for ghost on waka collision
        ghosts.stream().forEach(g -> this.ghostWakaCollide(g, this.waka));

        ghosts.removeAll(removedGhosts);
    }

    /**
     * Updates the current mode based on the current time.
     * The soda can and super fruit override the normal modes, and once they are finished
     * the mode goes back to what it was before the special fruit were picked up.
     */
    public void updateMode(){
        if (Waka.isBolting){
            Waka.boltTime += App.timePerFrame;
            if (Waka.boltTime >= Waka.boltLength){
                Waka.isBolting = false;
                waka.speed = Math.toIntExact(this.speed);
            }
        }
        if (Ghost.frightened){
            Ghost.frightenedTime += App.timePerFrame;
            if (Ghost.frightenedTime >= Ghost.frightenedLength){
                Ghost.frightened = false;
                Ghost.frightenedTime = 0;
                for (Ghost g : this.ghosts){
                    if (Ghost.isSoda){
                        g.setSprite(Ghost.sodaSprite);
                    } else {
                        g.setSprite(g.sprite1);
                    }
                }
            }
        } else if (Ghost.isSoda){
            Ghost.sodaTime += App.timePerFrame;
            if (Ghost.sodaTime >= Ghost.sodaLength){
                Ghost.isSoda = false;
                Ghost.sodaTime = 0;
                for (Ghost g : this.ghosts){
                    g.setSprite(g.sprite1);
                }
            }
        } else {
            incrementTime(App.timePerFrame);
            // If entire modelengths has been traversed, go back to start of array
            if (modeLengthCounter >= modeLengths.length){
                modeLengthCounter = 0;
            }

            // If the current mode has completed, go to next mode. Pause if in frightened mode.

            if (time >= modeLengths[modeLengthCounter]){
                modeLengthCounter++;
                time = 0;
                if (modeLengthCounter % 2 == 1){
                    Ghost.setScatter(false);
                } else {
                    Ghost.setScatter(true);
                }
            }
        }    
    }

    /**
     * Draws the current state of the map.
     * @param app App instance.
     */
    public void drawMap(App app){
        app.background(0, 0, 0);
        for (List<GameObject> row : app.manager.getMap()){
                for (GameObject g : row){
                if (g.getSprite() != null){
                    app.image(g.getSprite(), g.getX() - g.getOffSet(), g.getY() + g.getOffSet());
                }     
            }
        }
    }

    /**
     * Draws the current location and sprite of each entity.
     * @param app App instance.
     */
    public void drawEntities(App app){
        Waka waka = app.manager.waka;
        
        waka.drawEntity(app);
        
        for (Ghost g : app.manager.getGhosts()){
            g.drawEntity(app);
            if (inDebugMode){
                app.line(g.getX(),g.getY(),g.getTarget()[1] * 16 + 8,g.getTarget()[0] * 16 + 8);
            }
            
        }
    }

    /**
     * Determine whether or not the player has collided with a ghost.
     * Compares pixel coordinates between player and each ghost.
     * If a ghost is hit, deduct a life.
     * If there are no lives left, exit the application.
     * @param g A ghost object
     * @param waka A waka object
     */
    public void ghostWakaCollide(Ghost g, Waka waka){
        // The comparison value is the difference between the centre points of the two objects
        if (Math.abs(waka.getX() - g.getX()) <= 16){
            if (Math.abs(waka.getY() - g.getY()) <= 16){
                if (Ghost.frightened){
                    this.removedGhosts.add(g);
                } else {
                    
                    this.deductLifeIcon();
                    waka.deductLife();
                    if (waka.getLives() == 0){
                        textX = 140;
                        textY = 204;
                        this.gameFinishedText = "GAME OVER";
                        this.restart(this.gameFinishedText);
                        return;
                    }
                    this.reset();
                }
            }   
        }
    }

    /**
     * Resets all the modes and power ups, ghosts (including those that were killed) and Waka go back to starting location and left direction.
     */
    public void reset(){
        time = 0;
        modeLengthCounter = 0;
        Ghost.isSoda = false;
        Ghost.sodaTime = 0;
        Ghost.frightened = false;
        Ghost.frightenedTime = 0;
        Waka.isBolting = false;
        Waka.boltTime = 0;
        waka.speed = Math.toIntExact(this.speed);
        Ghost.setScatter(true);

        initialMove = true;
        // Reset ghosts and waka to their original location
        waka.setX(waka.getXSpawn());
        waka.setY(waka.getYSpawn());
        waka.setDirection(2);

        for (Ghost ghost : this.ghosts){
            ghost.setX(ghost.getXSpawn());
            ghost.setY(ghost.getYSpawn());
            ghost.setDirection(2);
        }
        for (Ghost ghost : this.removedGhosts){
            ghost.setX(ghost.getXSpawn());
            ghost.setY(ghost.getYSpawn());
            ghost.setDirection(2);
        }
    }

    /**
     * Display the game end screen with the specified message for 10 seconds and then completely restart the game.
     * @param message To be displayed on the screen
     */
    public void restart(String message){
        app.background(0,0,0);
        app.text(this.gameFinishedText, textX, textY);
        if (!inRestartMode){
            time = 0;
            inRestartMode = true;
            this.inDebugMode = false;
        } else {
            time += App.timePerFrame;
            if (time >= 10){
                inRestartMode = false;
                this.addLivesIcons(this.totalLives);
                this.waka.setLives(this.totalLives);
                this.fruitLeft = 0;
                this.pathBlocks.stream().forEach(lp -> {lp.stream().forEach(p -> 
                    {if (p != null){ p.resetBlock(this);}});});
                reset();
            }
        }
    }

    /**
     * Sets the sprites of the bottom left tiles of the window to be the lifeIconImage
     * depending on the number of total lives.
     * @param lives
     */
    private void addLivesIcons(long lives){
        for (int i = 0; i < lives; i++){
            this.map.get(map.size() - 1).get(2*i).setSprite(lifeIconImage);
            this.map.get(map.size() - 1).get(2*i).setOffSet(-12);
        }
    }

    /**
     * Removes the lifeIcon sprite from the right most tile that is currently showing the icon.
     */
    protected void deductLifeIcon(){
        this.map.get(map.size() - 1).get(2*(Math.toIntExact(this.waka.getLives()) - 1)).setSprite(null);
    }

    /**
     * Inverts the inDebugMode boolean.
     */
    private void toggleDebugMode(){
        if (this.inDebugMode){
            this.inDebugMode = false;
        } else {
            this.inDebugMode = true;
        }
    }


    // GETTERS AND SETTERS
    /**
     * Gets waka.
     * @return waka
     */
    public Waka getWaka(){
        return waka;
    }

    /**
     * Gets ghosts.
     * @return ghosts
     */

    public List<Ghost> getGhosts(){
        return ghosts;
    }

    /**
     * Adds a ghost object to ghosts.
     * @param ghost A Ghost object
     */
    public void addGhost(Ghost ghost){
        ghosts.add(ghost);
    }

    /**
     * Sets waka.
     * @param waka A Waka object
     */
    public void setWaka(Waka waka){
        this.waka = waka;
    }

    /**
     * Gets map.
     * @return map
     */
    public List<List<GameObject>> getMap(){
        return this.map;
    }

    /**
     * Gets totalLives.
     * @return totalLives
     */
    public long getTotalLives(){
        return this.totalLives;
    }

    /**
     * Gets speed.
     * @return speed.
     */
    public long getSpeed(){
        return this.speed;
    }

    /**
     * Sets pathBlocks.
     * @param pathBlocks Same size as map, value of a cell is null if it is not a PathBlock.
     */
    public void setPathBlocks(List<List<PathBlock>> pathBlocks){
        this.pathBlocks = pathBlocks;
    }

    /**
     * Increments fruitLeft.
     */
    public void addFruit(){
        this.fruitLeft++;
    }

    /**
     * Decrements fruitleft.
     */
    public void deductFruit(){
        this.fruitLeft--;
    }

    /**
     * Increments time.
     * @param time The amount of time a single frame lasts.
     */
    public void incrementTime(double time){
        this.time += time;
    }
}