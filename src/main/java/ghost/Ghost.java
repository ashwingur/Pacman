package ghost;

import processing.core.PApplet;
import processing.core.PImage;

import java.util.concurrent.ThreadLocalRandom;

import java.util.*;

public abstract class Ghost extends Entity{

    private static boolean scatter = true;
    /**
     * Indicates whether frightened mode is active
     */
    protected static boolean frightened = false;
    /**
     * Indicates whether soda mode is active
     */
    protected static boolean isSoda = false;
    
    /**
     * Current amount of time frightened mode has been active.
     */
    protected static double frightenedTime = 0;
    /**
     * Length of time frightened mode should last.
     */
    protected static double frightenedLength;

    /**
     * Current amount of time soda mode has been active.
     */
    protected static double sodaTime = 0;
    /**
     * Length of time soda mode should last.
     */
    protected static double sodaLength;

    private int[] target;
    /**
     * [y,x] representing the target location when ghost is in scatter mode.
     */
    protected int[] scatterTarget;
    /**
     * GameManager reference.
     */
    protected GameManager manager;
    /**
     * Sprite to display when ghost is frightened.
     */
    protected static PImage frightenedSprite;
    /**
     * Sprite to display when ghost is in soda mode.
     */
    protected static PImage sodaSprite;

    /**
     * Constructor.
     * Sets the amount of frames each sprite will last before switching.
     * @param x x pixel coordinate.
     * @param y y pixel coordinate.
     * @param app App instance.
     * @param manager GameManager instance.
     * @param sprite1 Sprite 1.
     * @param sprite2 Sprite 2.
     */
    public Ghost(int x, int y, App app, GameManager manager, PImage sprite1, PImage sprite2){
        super(x, y, sprite1, manager);
        this.offSet = 14;
        this.changeSpriteFrame1 = 50;
        this.changeSpriteFrame2 = 10;
        this.changeSpriteCounter = ThreadLocalRandom.current().nextInt(0, changeSpriteFrame1 + 1);
        
        this.scatterTarget = new int[]{0,0};
        this.sprite1 = sprite1;
        this.sprite2 = sprite2;
    }

    /**
     * Sets scatter to true or false.
     * @param b Sets scatter to this value.
     */
    public static void setScatter(boolean b){
        if (b){
            scatter = true;
        } else {
            scatter = false;
        }
    }

    /**
     * Sets the direction of the ghost based on the shortest straight line distance to the target coordinates.
     * @param targetCoords Cell coordinates of the target location [y,x].
     * @param map Current map.
     */
    public void goTo(int[] targetCoords, List<List<GameObject>> map){
        // Keep at minus 1 if a turn in the corresponding direction is blocked by a wall
        double rightDistance = -1;
        double leftDistance = -1;
        double upDistance = -1;
        double downDistance = -1;


        if (!collide(map, 1)){
            int[] rightBlockCoords = new int[] {this.coordinates[0], this.coordinates[1] + 1};
            rightDistance = distance(rightBlockCoords, targetCoords);
        }
        if (!collide(map, 2)){
            int[] leftBlockCoords = new int[] {this.coordinates[0], this.coordinates[1] - 1};
            leftDistance = distance(leftBlockCoords, targetCoords);
        }
        if (!collide(map, 3)){
            int[] downBlockCoords = new int[] {this.coordinates[0] + 1, this.coordinates[1]};
            downDistance = distance(downBlockCoords, targetCoords);
        }
        if (!collide(map, 4)){
            int[] upBlockCoords = new int[] {this.coordinates[0] - 1, this.coordinates[1]};
            upDistance = distance(upBlockCoords, targetCoords);
        }

        double[] distances = new double[] {rightDistance, leftDistance, downDistance, upDistance};

        int shortestD = this.shortestDirection(distances);

        if (shortestD == 1){
            this.setXVel(true);
            this.setDirection(1);
        } else if (shortestD == 2){
            this.setXVel(false);
            this.setDirection(2);
        } else if (shortestD == 3){
            this.setYVel(false);
            this.setDirection(3);
        }else if (shortestD == 4){
            this.setYVel(true);
            this.setDirection(4);
        }
    }

    /**
     * Finds the closest corner of the screen to the ghost.
     * Note this function was only used in the milestone submission, it is no longer used.
     * @param map Current map.
     * @return Cell coordinates of the closest corner [y,x].
     */
    public int[] closestCorner(List<List<GameObject>> map){
        int mapWidth = map.get(0).size();
        int mapHeight = map.size();

        int[] corner = new int[2];

        if (coordinates[1] > mapWidth / 2){
            corner[1] = mapWidth - 1;
        } else {
            corner[1] = 0;
        }

        if (coordinates[0] > mapHeight / 2){
            corner[0] = mapHeight - 1;
        } else {
            corner[0] = 0;
        }
        return corner;
    }

    /**
     * Finds the straight line distance between 2 objects.
     * @param object1 [y,x] of object1.
     * @param object2 [y,x] of object2.
     * @return The straight line distance between the 2 objects.
     */
    public static double distance(int[] object1, int[] object2){
        double xDiff = (double) object1[1] - object2[1];
        double yDiff = (double) object1[0] - object2[0];
        // Pythagoras
        return Math.sqrt(Math.pow(xDiff, 2) + Math.pow(yDiff, 2));
    }

    /**
     * Finds the direction that the ghost should go based on the shortest distance that is not reverse of its current direction.
     * If the only possible direct ion is the reverse of the current direction, then return that.
     * @param distances Array of size 4 representing the distance from each direction. If the direction is not possible (wall), then the value is -1.
     * @return Integer representing the direction the ghost should go.
     */
    public int shortestDirection(double[] distances){
        int reverseD = this.getReverseDirection();
        int shortestD = -1;

        for (int i = 0; i < distances.length; i++){  
            if (i + 1 != reverseD && distances[i] >= 0){
                if (shortestD == -1){
                    shortestD = i + 1;
                } else if (distances[i] < distances[shortestD - 1]){
                    shortestD = i + 1;
                }
            }
        }

        if (shortestD == -1){
            return reverseD;
        }
        return shortestD;
    }

    /**
     * Gets the opposite direction of the object's current direction.
     * @return Integer representing direction (1: right, 2: left, 3: down, 4: up).
     */
    public int getReverseDirection(){
        if (this.getDirection() == 1){
            return 2;
        } else if (this.getDirection() == 2){
            return 1;
        } else if (this.getDirection() == 3){
            return 4;
        }
        return 3;
    }

    /**
     * Checks if the ghost is at an intersection.
     * An intersection is any path that is not a straight line only.
     * A dead end counts as an intersection, as the ghost must change its direction.
     * @param pathBlocks 2D list of pathBlocks.
     * @return true if at an intersection, otherwise false.
     */
    public boolean isAtIntersection(List<List<PathBlock>> pathBlocks){
        int turnCount = 0;
        int row = this.coordinates[0];
        int column = this.coordinates[1];

        if (pathBlocks.get(row).get(column + 1) != null){
            turnCount++;
        }
        if (pathBlocks.get(row).get(column - 1) != null){
            turnCount++;
        }

        if (pathBlocks.get(row + 1).get(column) != null){
            if (turnCount >= 1){
                return true;
            }
            turnCount++;
        }
        if (pathBlocks.get(row - 1).get(column) != null){
            turnCount++;
        }
        if (turnCount == 2){
            return false;
        }
        return true;
    }

    /**
     * Ghost logic that is performed every frame.
     * Updates coordinates.
     * Depending on the current state of the ghost, it goes to a certain target.
     * Moves the ghost and changes the sprite if it is not in frightened or soda mode.
     */
    public void tick(GameManager manager){
        this.updateCoords();
            if (this.getX() % 16 == 8 && this.getY() % 16 == 8){
                if (manager.initialMove){
                    // First ever move in the game, go left
                    this.scatter();
                    this.goTo(new int[] {this.coordinates[0], this.coordinates[1] - 1}, manager.getMap());
                } else {
                    if (this.isAtIntersection(manager.pathBlocks)){
                        if (frightened){
                            this.frightened();
                        } else if (isSoda){
                            this.frightened();
                        } else if (Ghost.getScatter()){
                            this.scatter();
                        } else {
                            this.chase();
                        }
                        
                    } else {
                        this.goToDirection(this.getDirection());
                    }
                    
                }      
            }
            this.collide(manager.getMap(), this.getDirection());
            this.move();
            if (!frightened && !isSoda){
                this.changeSprite();
            }
    }

    /**
     * Gets the value of scatter boolean.
     * @return scatter.
     */
    public static boolean getScatter(){
        return scatter;
    }

    /**
     * Sets the target of the ghost, given the target's cell coordinates.
     * @param arr Cell coordinates of the target location [y,x].
     */
    public void setTarget(int[] arr){
        this.target = arr;
    }

    /**
     * Gets the cell coordinates of the current target location.
     * @return Cell coordinates. 
     */
    public int[] getTarget(){
        return this.target;
    }

    /**
     * Sets and goes to the chase location of the ghost.
     */
    public abstract void chase();

    /**
     * Sets and goes to the scatter location of the ghost.
     */
    public abstract void scatter();

    /**
     * Choses a random neighbouring tile and goes to it. Only goes backward if that is the only choice.
     * Used when the ghost is at an intersection.
     */
    public void frightened(){
        int[] goToCoords = this.coordinates.clone();

        HashMap<Integer, Boolean> availableDirections = new HashMap<>();
        availableDirections.put(1, false);
        availableDirections.put(2, false);
        availableDirections.put(3, false);
        availableDirections.put(4, false);

        // After this, the hashmap contains only the valid directions it can go
        for (int i = 1; i <= 4; i++){
            if (this.collide(this.manager.getMap(), i)){
                availableDirections.remove(i);
            }
            else {
                availableDirections.put(i, true);
            }
        }

        // We can remove the reverse direction
        if (availableDirections.keySet().size() > 1){
            availableDirections.remove(this.getReverseDirection());
        }

        int randomDirectionIndex = ThreadLocalRandom.current().nextInt(0, availableDirections.keySet().size());

        int i = 0;
        for (int key : availableDirections.keySet()){
            if (i == randomDirectionIndex){
                this.setTarget(goToDirection(key));
            }
            i++;
        }
    }

    /**
     * Sets the target of the ghost to be the next tile in the chosen direction
     * @param direction int representing direction
     * @return map coordinates of the chosen target.
     */
    public int[] goToDirection(int direction){
        int[] goToCoords = new int[2];
        if (direction == 1){
            goToCoords[0] = this.coordinates[0];
            goToCoords[1] = this.coordinates[1] + 1;
            this.goTo(goToCoords, manager.getMap());
        } else if (direction == 2){
            goToCoords[0] = this.coordinates[0];
            goToCoords[1] = this.coordinates[1] - 1;
            this.goTo(goToCoords, manager.getMap());
        } else if (direction == 3){
            goToCoords[0] = this.coordinates[0] + 1;
            goToCoords[1] = this.coordinates[1];
            this.goTo(goToCoords, manager.getMap());
        } else if (direction == 4){
            goToCoords[0] = this.coordinates[0] - 1;
            goToCoords[1] = this.coordinates[1];
            this.goTo(goToCoords, manager.getMap());
        }
        return goToCoords;
    }

    /**
     * To set the scatter target of the ghost.
     */
    public abstract void setScatterTarget();

}