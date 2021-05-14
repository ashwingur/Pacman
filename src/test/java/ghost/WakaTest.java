package ghost;

import org.junit.jupiter.api.Test;

import ghost.GameManager;
import ghost.Waka;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import processing.core.PApplet;
import processing.core.PImage;

class WakaTest{
    @Test
    public void wakaActionTest(){
        App app = new App();
        PApplet.runSketch(new String[] {"App"}, app);
        app.setup("src/test/resources/test1.json");
        GameManager manager = new GameManager(app, "src/test/resources/test2.json");

        Waka waka = new Waka (manager.getWaka().getX(), manager.getWaka().getY(), app, manager);
        waka.wakaAction(39, manager);
        assertEquals(waka.getDirection(), 1);

        waka.wakaAction(38, manager);
        assertEquals(waka.getDirection(), 1);

        waka.wakaAction(40, manager);
        assertEquals(waka.getDirection(), 1);

        waka.wakaAction(37, manager);
        assertEquals(waka.getDirection(), 2);
        

        waka.coordinates[1] = 9;
        System.out.println(Arrays.toString(waka.coordinates));
    
        waka.setDirection(2);

        waka.wakaAction(38, manager);
        assertEquals(waka.getDirection(), 4);

        waka.wakaAction(39, manager);
        assertEquals(waka.getDirection(), 1);
        
        waka.wakaAction(40, manager);
        assertEquals(waka.getDirection(), 3);

        waka.setDirection(2);
        waka.setX(waka.getX() + 1);

        waka.wakaAction(38, manager);
        assertEquals(waka.getDirection(), 2);

        waka.wakaAction(40, manager);
        assertEquals(waka.getDirection(), 2);

        waka.setX(waka.getX() - 1);
        waka.setY(waka.getY() + 1);

        waka.wakaAction(37, manager);
        assertEquals(waka.getDirection(), 2);

        waka.wakaAction(39, manager);
        assertEquals(waka.getDirection(), 2);

        waka.tick(manager);
        manager.initialMove = false;
        waka.setNextMove(1);
        waka.tick(manager);
        waka.setNextMove(2);
        waka.tick(manager);
        waka.setNextMove(3);
        waka.tick(manager);
        waka.setNextMove(4);
        waka.tick(manager);
        assertEquals(waka.getDirection(), 2);
    }

    @Test
    public void eatFruitTest(){
        App app = new App();
        PApplet.runSketch(new String[] {"App"}, app);
        app.setup("src/test/resources/test2.json");
        GameManager manager = new GameManager(app, "src/test/resources/test2.json");

        Waka waka = new Waka (manager.getWaka().getX(), manager.getWaka().getY(), app, manager);
        waka.coordinates[1] -= 1;
        int x = manager.fruitLeft;
        waka.setX(waka.getX() + 1);
        waka.setY(waka.getY() + 1);
        waka.eatFruit(manager.pathBlocks, manager);
        assertTrue(x == manager.fruitLeft);
        
        waka.setX(waka.getX() - 1);
        waka.setY(waka.getY() - 1);
        waka.eatFruit(manager.pathBlocks, manager);
        assertFalse(x == manager.fruitLeft);
    }

}