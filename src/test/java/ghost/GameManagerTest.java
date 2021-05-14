package ghost;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import processing.core.PApplet;
import processing.core.PImage;

class GameManagerTest {

    @Test 
    public void testInitialisation(){ 
        App app = new App();
        PApplet.runSketch(new String[] {"App"}, app);
        app.setup("src/test/resources/test1.json");
        GameManager manager = new GameManager(app, "src/test/resources/test1.json");

        assertNotNull(manager);
        assertNotNull(manager.getWaka());
        assertEquals(manager.getGhosts().size(), 4);
        assertTrue(manager.getGhosts().get(0) instanceof Ambusher);
        assertTrue(manager.getGhosts().get(1) instanceof Chaser);
        assertTrue(manager.getGhosts().get(2) instanceof Ignorant);
        assertTrue(manager.getGhosts().get(3) instanceof Whim);
    }

    @Test
    public void testActionDebug(){
        App app = new App();
        PApplet.runSketch(new String[] {"App"}, app);
        app.setup("src/test/resources/test1.json");
        GameManager manager = new GameManager(app, "src/test/resources/test1.json");

        assertFalse(manager.inDebugMode);
        manager.action(32);
        assertTrue(manager.inDebugMode);
        manager.action(32);
        assertFalse(manager.inDebugMode);
    }

    @Test
    public void deductLifeIcon(){
        App app = new App();
        PApplet.runSketch(new String[] {"App"}, app);
        app.setup("src/test/resources/test1.json");
        GameManager manager = new GameManager(app, "src/test/resources/test1.json");

        assertNotNull(manager.getMap().get(35).get(4).getSprite());
        manager.deductLifeIcon();
        assertNull(manager.getMap().get(35).get(4).getSprite());
    }

    @Test
    public void updateModeAndFruit(){
        App app = new App();
        PApplet.runSketch(new String[] {"App"}, app);
        app.setup("src/test/resources/test1.json");
        GameManager manager = new GameManager(app, "src/test/resources/test1.json");

        // Mode length counter increment
        assertEquals(manager.modeLengthCounter, 0);
        manager.updateMode();
        assertEquals(manager.modeLengthCounter, 0);
        manager.modeLengthCounter = 100;
        manager.updateMode();
        assertEquals(manager.modeLengthCounter, 0);
        manager.time = 10;
        manager.updateMode();
        assertEquals(manager.modeLengthCounter, 1);
        manager.time = 15;
        manager.updateMode();
        assertEquals(manager.modeLengthCounter, 2);
        

        assertFalse(Ghost.frightened);
        assertFalse(Ghost.isSoda);
        assertFalse(Waka.isBolting);
        assertEquals(manager.fruitLeft, 12);
        // Eating superfruit
        manager.pathBlocks.get(26).get(3).consumeItem(manager);

        assertEquals(manager.fruitLeft, 11);
        manager.updateMode();
        Ghost.frightenedTime = 3;
        manager.updateMode();
        assertFalse(Ghost.frightened);
        manager.updateMode();
        // Eating sodacan
        manager.pathBlocks.get(26).get(4).consumeItem(manager);
        assertFalse(Ghost.frightened);
        assertTrue(Ghost.isSoda);
        manager.updateMode();
        Ghost.sodaTime = 4;
        manager.updateMode();
        assertFalse(Ghost.isSoda);

        // Eating sodacan while frightened
        manager.updateMode();
        manager.pathBlocks.get(26).get(3).resetBlock(manager);
        manager.pathBlocks.get(26).get(3).consumeItem(manager);
        manager.updateMode();
        Ghost.frightenedTime = 999;
        assertTrue(Ghost.frightened);
        manager.pathBlocks.get(26).get(4).resetBlock(manager);
        manager.pathBlocks.get(26).get(4).consumeItem(manager);
        assertTrue(Ghost.isSoda);
        assertFalse(Ghost.frightened);
        manager.updateMode();
        assertFalse(Ghost.frightened);
        assertTrue(Ghost.isSoda);
        manager.updateMode();

        // Waka bolt
        assertEquals(manager.getWaka().speed, 1);
        assertFalse(Waka.isBolting);
        assertEquals(Waka.boltTime, 0);
        manager.pathBlocks.get(26).get(5).consumeItem(manager);
        assertEquals(manager.getWaka().speed, 4);
        manager.updateMode();
        assertTrue(Waka.isBolting);
        Waka.boltTime = 5;
        manager.updateMode();
        assertFalse(Waka.isBolting);
        assertEquals(manager.getWaka().speed, 1);


    }

    @Test
    public void fruitFinished(){
        App app = new App();
        PApplet.runSketch(new String[] {"App"}, app);
        app.setup("src/test/resources/test1.json");
        GameManager manager = new GameManager(app, "src/test/resources/test1.json");

        assertEquals(manager.fruitLeft, 12);
        for (int i = 0; i < 12; i++){
            manager.deductFruit();
        }
        assertEquals(manager.fruitLeft, 0);
        manager.gameMove();
        assertEquals(manager.gameFinishedText, "YOU WIN");
    }

    @Test
    public void testReset(){
        App app = new App();
        PApplet.runSketch(new String[] {"App"}, app);
        app.setup("src/test/resources/test1.json");
        GameManager manager = new GameManager(app, "src/test/resources/test1.json");

        manager.time = 10;
        manager.modeLengthCounter = 5;
        Ghost.isSoda = true;
        Ghost.sodaTime = 7;
        Waka.isBolting = true;
        Waka.boltTime = 6;
        manager.getWaka().speed = Math.toIntExact(2);
        Ghost.setScatter(false);

        manager.reset();

        assertEquals(manager.time, 0);
        assertEquals(manager.modeLengthCounter, 0);
        assertFalse(Ghost.isSoda);
        assertEquals(Ghost.sodaTime, 0);
        assertEquals(Waka.boltTime,0);
        assertFalse(Waka.isBolting);
        assertTrue(Ghost.getScatter());

        Ghost.frightened = true;
        manager.getGhosts().get(0).setX(manager.getWaka().getX());
        manager.ghostWakaCollide(manager.getGhosts().get(0), manager.getWaka());
        assertEquals(manager.removedGhosts.get(0).getX(),manager.getWaka().getX());
        manager.reset();
        assertEquals(manager.removedGhosts.get(0).getX(), manager.removedGhosts.get(0).getXSpawn());
    }

    @Test
    public void testRestart(){
        App app = new App();
        PApplet.runSketch(new String[] {"App"}, app);
        app.setup("src/test/resources/test1.json");
        GameManager manager = new GameManager(app, "src/test/resources/test1.json");
        
        manager.gameFinishedText = "filler";
        assertFalse(manager.inRestartMode);
        assertEquals(manager.time, 0);

        manager.restart("a");
        assertTrue(manager.inRestartMode);

        manager.time = 5;
        manager.restart("a");
        manager.time += 5;
        manager.restart("a");

        assertFalse(manager.inRestartMode);
    }

    @Test
    public void ghostWakaCollideTest(){
        App app = new App();
        PApplet.runSketch(new String[] {"App"}, app);
        app.setup("src/test/resources/test1.json");
        GameManager manager = new GameManager(app, "src/test/resources/test1.json");
    
        assertEquals(manager.removedGhosts.size(), 0);
        assertEquals(manager.getWaka().getLives(), 3);
        manager.ghostWakaCollide(manager.getGhosts().get(0), manager.getWaka());
        assertEquals(manager.removedGhosts.size(), 0);
        assertEquals(manager.getWaka().getLives(), 3);


        // Put ghost on top of waka
        manager.getGhosts().get(0).setX(manager.getWaka().getX());
        manager.ghostWakaCollide(manager.getGhosts().get(0), manager.getWaka());
        
        assertEquals(manager.getWaka().getLives(), 2);
                
        manager.getGhosts().get(0).setX(manager.getWaka().getX());
        manager.getGhosts().get(0).setY(manager.getWaka().getY());

        assertEquals(manager.getGhosts().get(0).getX(), manager.getWaka().getX());
        assertEquals(manager.getGhosts().get(0).getY(), manager.getWaka().getY());


        manager.ghostWakaCollide(manager.getGhosts().get(0), manager.getWaka());

        assertEquals(manager.getWaka().getLives(), 1);
        assertEquals(manager.removedGhosts.size(), 0);
        

        // Ghost collide while frightened
        manager.getWaka().setLives(3);
        Ghost.frightened = true;
        manager.getGhosts().get(0).setX(manager.getWaka().getX());
        manager.getGhosts().get(0).setY(manager.getWaka().getY());
        manager.ghostWakaCollide(manager.getGhosts().get(0), manager.getWaka());
        assertEquals(manager.removedGhosts.size(), 1);
        assertEquals(manager.getWaka().getLives(), 3);
        manager.reset();

        // Test waka dying
        assertFalse(manager.inRestartMode);
        manager.getWaka().setLives(1);
        manager.getGhosts().get(0).setX(manager.getWaka().getX());
        manager.getGhosts().get(0).setY(manager.getWaka().getY());
        manager.ghostWakaCollide(manager.getGhosts().get(0), manager.getWaka());
        assertEquals(manager.getWaka().getLives(), 0);
        assertTrue(manager.inRestartMode);
        
    }
}
