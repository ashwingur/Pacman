package ghost;

import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;

import processing.core.PApplet;
import processing.core.PImage;

public class MapParser{

    public static List<List<GameObject>> parseMap(String fileName, App app, GameManager manager){
        File file = new File(fileName);

        List<List<GameObject>> map = new ArrayList<List<GameObject>>();
        List<List<PathBlock>> pathBlocks = new ArrayList<List<PathBlock>>();

        try {
            Scanner scan = new Scanner(file);
            int row = 0;
            while (scan.hasNextLine()){
                String line = scan.nextLine();
                map.add(new ArrayList<GameObject>());
                pathBlocks.add(new ArrayList<PathBlock>());

                // Loop through each character in the row
                for (int i = 0; i < line.length(); i++){

                    char c = line.charAt(i);
                    if (c == '1'){
                        // Horizontal Wall
                        PImage sprite = app.loadImage("src/main/resources/horizontal.png");
                        map.get(row).add(new Obstacle(i*16, row*16, sprite));
                        pathBlocks.get(row).add(null);
                        
                    } else if (c == '2'){
                        // Vertical Wall
                        PImage sprite = app.loadImage("src/main/resources/vertical.png");
                        map.get(row).add(new Obstacle(i*16, row*16, sprite));
                        pathBlocks.get(row).add(null);

                    } else if (c == '3'){
                        // Upleft
                        PImage sprite = app.loadImage("src/main/resources/upLeft.png");
                        map.get(row).add(new Obstacle(i*16, row*16, sprite));
                        pathBlocks.get(row).add(null);

                    } else if (c == '4'){
                        // Upright
                        PImage sprite = app.loadImage("src/main/resources/upRight.png");
                        map.get(row).add(new Obstacle(i*16, row*16, sprite));
                        pathBlocks.get(row).add(null);

                    } else if (c == '5'){
                        // Downleft
                        PImage sprite = app.loadImage("src/main/resources/downLeft.png");
                        map.get(row).add(new Obstacle(i*16, row*16, sprite));
                        pathBlocks.get(row).add(null);

                    } else if (c == '6'){
                        // Downright
                        PImage sprite = app.loadImage("src/main/resources/downRight.png");
                        map.get(row).add(new Obstacle(i*16, row*16, sprite));
                        pathBlocks.get(row).add(null);

                    } else if (c == '7'){
                        // Fruit
                        PImage sprite = app.loadImage("src/main/resources/fruit.png");
                        Fruit pb = new Fruit(i*16, row*16, sprite);
                        map.get(row).add(pb);
                        pathBlocks.get(row).add(pb);
                        manager.addFruit();
                        
                    } else if (c == '8'){
                        // SuperFruit
                        PImage sprite = app.loadImage("src/main/resources/superFruit.png");
                        SuperFruit pb = new SuperFruit(i*16, row*16, sprite);
                        map.get(row).add(pb);
                        pathBlocks.get(row).add(pb);
                        manager.addFruit();

                    } else if (c == '9'){
                        // sodaCan
                        PImage sprite = app.loadImage("src/main/resources/sodaCan.png");
                        SodaCan pb = new SodaCan(i*16, row*16, sprite);
                        map.get(row).add(pb);
                        pathBlocks.get(row).add(pb);
                        manager.addFruit();

                    } else if (c == 'l'){
                        // lightning bolt
                        PImage sprite = app.loadImage("src/main/resources/bolt.png");
                        Bolt pb = new Bolt(i*16, row*16, sprite);
                        map.get(row).add(pb);
                        pathBlocks.get(row).add(pb);
                        manager.addFruit();

                    }else if (c == 'p'){
                        // Waka start
                        PImage sprite = app.loadImage("src/main/resources/fruit.png");
                        PathBlock pb = new PathBlock(i*16, row*16, null);
                        map.get(row).add(pb);
                        pathBlocks.get(row).add(pb);
                        manager.setWaka(new Waka(i*16 + 8, row*16 + 8, app, manager));
                        

                    } else if (c == 'a'){
                        // Ambusher Ghost
                        PImage sprite = app.loadImage("src/main/resources/ambusher.png");
                        PathBlock pb = new PathBlock(i*16, row*16, null);
                        map.get(row).add(pb);
                        pathBlocks.get(row).add(pb);
                        manager.addGhost(new Ambusher(i*16 + 8, row*16 + 8, app, manager, sprite));

                    } else if (c == 'c'){
                        // Chaser Ghost
                        PImage sprite = app.loadImage("src/main/resources/chaser.png");
                        PathBlock pb = new PathBlock(i*16, row*16, null);
                        map.get(row).add(pb);
                        pathBlocks.get(row).add(pb);
                        manager.addGhost(new Chaser(i*16 + 8, row*16 + 8, app, manager, sprite));

                    } else if (c == 'i'){
                        // Ignorant Ghost
                        PImage sprite = app.loadImage("src/main/resources/ignorant.png");
                        PathBlock pb = new PathBlock(i*16, row*16, null);
                        map.get(row).add(pb);
                        pathBlocks.get(row).add(pb);
                        manager.addGhost(new Ignorant(i*16 + 8, row*16 + 8, app, manager, sprite));

                    } else if (c == 'w'){
                        // Whim Ghost
                        PImage sprite = app.loadImage("src/main/resources/whim.png");
                        PathBlock pb = new PathBlock(i*16, row*16, null);
                        map.get(row).add(pb);
                        pathBlocks.get(row).add(pb);
                        manager.addGhost(new Whim(i*16 + 8, row*16 + 8, app, manager, sprite));

                    } else {
                        // Empty Cell
                        PathBlock pb = new PathBlock(i*16, row*16, null);
                        map.get(row).add(pb);
                        pathBlocks.get(row).add(pb);
                    }
                }

                row++;
            }
            
            scan.close();
        } catch (FileNotFoundException e) {
            System.out.println("Map does not exist");
        }

        manager.setPathBlocks(pathBlocks);

        return map;

    }

}