package ghost;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import processing.core.PApplet;
import processing.core.PImage;

class GhostTest{
    @Test
    public void goToTest(){
        App app = new App();
        PApplet.runSketch(new String[] {"App"}, app);
        app.setup("src/test/resources/test1.json");
        GameManager manager = new GameManager(app, "src/test/resources/test1.json");
        manager.initialMove = false;
        Ghost g = new Ambusher(56,56, app, manager, app.loadImage("src/main/resources/ambusher.png"));
        g.manager = manager;
        Ghost.frightened = true;
        g.tick(manager);
        g.setX(40);
        g.setY(40);
        Ghost.frightened = false;
        Ghost.setScatter(false);
        g.tick(manager);
        g.setX(39);
        g.setY(39);
        g.tick(manager);
        g.setDirection(2);
        g.setTarget(new int[] {3,10});
        assertArrayEquals(g.getTarget(), new int[] {3,10});
        assertEquals(g.getDirection(), 2);
        g.goTo(new int[] {0,100}, manager.getMap());
        assertEquals(g.getDirection(), 4);
        
    }

    @Test
    public void shortestDirectionTest(){
        App app = new App();
        PApplet.runSketch(new String[] {"App"}, app);
        app.setup("src/test/resources/test1.json");
        GameManager manager = new GameManager(app, "src/test/resources/test1.json");

        Ghost g = new Ambusher(100, 100, app, manager, null);
        // Going right
        g.setDirection(1);
        // First is if only the reverse direction is valid
        double[] distances = new double[] {-1, 10, -1, -1};
        int shortestD = g.shortestDirection(distances);
        assertEquals(shortestD, 2);

        // ONe direction other than the reverse is valid
        double[] distances1 = new double[] {-1, -1, 5, -1};
        assertEquals(g.shortestDirection(distances1), 3);

        // All directions are valid, reverse direction is shortest, but should not go that way
        // Because other directions are prioritised
        double[] distances2 = new double[] {89, 1, 55.7, 33.4};
        assertEquals(g.shortestDirection(distances2), 4);

        double[] distances3 = new double[] {0.1, 1, 5, 4};
        assertEquals(g.shortestDirection(distances3), 1);
    }

    @Test
    public void testClosestCorner(){
        App app = new App();
        PApplet.runSketch(new String[] {"App"}, app);
        app.setup("src/test/resources/test1.json");
        GameManager manager = new GameManager(app, "src/test/resources/test1.json");

        Ghost g = new Ambusher(100, 100, app, manager, null);

        // Top left
        g.coordinates[0] = 3;
        g.coordinates[1] = 3;
        assertArrayEquals(g.closestCorner(manager.getMap()), new int[] {0,0});

         // Top right
         g.coordinates[0] = 5;
         g.coordinates[1] = 27;
         assertArrayEquals(g.closestCorner(manager.getMap()), new int[] {0,27});

         // bottom right
         g.coordinates[0] = 34;
         g.coordinates[1] = 27;
         assertArrayEquals(g.closestCorner(manager.getMap()), new int[] {35,27});

         // bottom left
         g.coordinates[0] = 34;
         g.coordinates[1] = 6;
         assertArrayEquals(g.closestCorner(manager.getMap()), new int[] {35,0});
 
    }

    @Test
    public void reverseDirectionTest(){
        // Also testing set direction
        App app = new App();
        PApplet.runSketch(new String[] {"App"}, app);
        app.setup("src/test/resources/test1.json");
        GameManager manager = new GameManager(app, "src/test/resources/test1.json");

        Ghost g = new Ambusher(100, 100, app, manager, null);

        assertEquals(g.getDirection(), 2);
        assertEquals(g.getReverseDirection(), 1);

        g.setDirection(1);
        assertEquals(g.getDirection(), 1);
        assertEquals(g.getReverseDirection(), 2);

        g.setDirection(3);
        assertEquals(g.getDirection(), 3);
        assertEquals(g.getReverseDirection(), 4);
        
        g.setDirection(4);
        assertEquals(g.getDirection(), 4);
        assertEquals(g.getReverseDirection(), 3);
    }

    @Test
    public void isAtIntersectionTest(){
        App app = new App();
        PApplet.runSketch(new String[] {"App"}, app);
        app.setup("src/test/resources/testIntersection.json");
        GameManager manager = new GameManager(app, "src/test/resources/testIntersection.json");

        Ghost g = new Ambusher(100, 100, app, manager, null);

        g.coordinates[0] = 1;
        g.coordinates[1] = 1;
        assertTrue(g.isAtIntersection(manager.pathBlocks));

        g.coordinates[0] = 1;
        g.coordinates[1] = 3;
        assertTrue(g.isAtIntersection(manager.pathBlocks));

        g.coordinates[0] = 1;
        g.coordinates[1] = 2;
        assertFalse(g.isAtIntersection(manager.pathBlocks));

        g.coordinates[0] = 2;
        g.coordinates[1] = 10;
        assertTrue(g.isAtIntersection(manager.pathBlocks));

        g.coordinates[0] = 2;
        g.coordinates[1] = 18;
        assertTrue(g.isAtIntersection(manager.pathBlocks));
    }

    @Test
    public void goToDirectionTest(){
        App app = new App();
        PApplet.runSketch(new String[] {"App"}, app);
        app.setup("src/test/resources/test1.json");
        GameManager manager = new GameManager(app, "src/test/resources/test1.json");

        Ghost g = new Ambusher(100, 100, app, manager, null);
        g.manager = manager;
        g.coordinates[0] = 4;
        g.coordinates[1] = 4;

        assertArrayEquals(g.goToDirection(1), new int[] {4, 5});
        assertArrayEquals(g.goToDirection(2), new int[] {4, 3});
        assertArrayEquals(g.goToDirection(3), new int[] {5, 4});
        assertArrayEquals(g.goToDirection(4), new int[] {3, 4});
        // For invalid direction
        assertArrayEquals(g.goToDirection(5), new int[] {0, 0});
    
    }

    @Test
    public void frightenedTest(){
        App app = new App();
        PApplet.runSketch(new String[] {"App"}, app);
        app.setup("src/test/resources/testIntersection.json");
        GameManager manager = new GameManager(app, "src/test/resources/testIntersection.json");

        Ghost g = new Ambusher(8, 8, app, manager, null);
        g.manager = manager;
        g.setDirection(2);
        g.coordinates[0] = 1;
        g.coordinates[1] = 1;
        assertTrue(g.collide(manager.getMap(), 3));
        g.frightened();
        int[] option1 = new int[] {1,2};

        assertTrue(Arrays.equals(g.getTarget(), option1));

        g.coordinates[0] = 1;
        g.coordinates[1] = 3;
        g.setDirection(2);
        assertTrue(Arrays.equals(g.getTarget(), new int[] {2, 3}) || Arrays.equals(g.getTarget(), option1));
    }

    @Test
    public void testWhim(){
        App app = new App();
        PApplet.runSketch(new String[] {"App"}, app);
        app.setup("src/test/resources/testIntersection.json");
        GameManager manager = new GameManager(app, "src/test/resources/test2.json");

        Ghost whim = new Whim(16, 16, app, manager, null);
        whim.manager = manager;
        Chaser chaser = new Chaser(8, 8, app, manager, null);
        Whim.chaser = chaser;

        manager.getWaka().coordinates = new int[] {1, 0};
        manager.getWaka().setDirection(1);
        chaser.coordinates = new int[] {0,0};
        whim.chase();
        assertArrayEquals(whim.getTarget(), new int[] {2, 4});

        manager.getWaka().setDirection(2);
        whim.chase();
        assertArrayEquals(whim.getTarget(), new int[] {2, 0});

        manager.getWaka().setDirection(3);
        whim.chase();
        assertArrayEquals(whim.getTarget(), new int[] {0, 0});

        manager.getWaka().setDirection(4);
        whim.chase();
        assertArrayEquals(whim.getTarget(), new int[] {6, 0});
    }

    @Test
    public void testAmbusher(){
        App app = new App();
        PApplet.runSketch(new String[] {"App"}, app);
        app.setup("src/test/resources/testIntersection.json");
        GameManager manager = new GameManager(app, "src/test/resources/test2.json");

        Ghost ambusher = new Ambusher(16, 16, app, manager, null);
        ambusher.manager = manager;

        manager.getWaka().coordinates = new int[] {10, 10};
        
        manager.getWaka().setDirection(1);
        ambusher.chase();
        assertArrayEquals(ambusher.getTarget(), new int[] {10, 18});

        manager.getWaka().setDirection(2);
        ambusher.chase();
        assertArrayEquals(ambusher.getTarget(), new int[] {10, 2});
        
        manager.getWaka().setDirection(3);
        ambusher.chase();
        assertArrayEquals(ambusher.getTarget(), new int[] {18, 10});
        
        manager.getWaka().setDirection(4);
        ambusher.chase();
        assertArrayEquals(ambusher.getTarget(), new int[] {2, 10});

    }

}