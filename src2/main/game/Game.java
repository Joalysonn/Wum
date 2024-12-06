package main.game;

import main.game.map.Map;
import main.game.map.Point;
import main.game.map.TreasureChest;
import main.strategies.BinaryTreeDFS;
import main.strategies.FewerObstacles;
import main.strategies.Rollback;
import main.strategies.ShortestDistance;
import main.strategies.Sort;
import main.strategies.Votacao;

public class Game {
	/*private BinaryTreeMap map;*/
    private Map map;
    private Player player;
    private boolean gameOver;

    public Game() {
        this.map = new Map(8, 8);
        this.player = new Player(new Votacao());
        this.gameOver = false;
    }
    
    public void run() {
        this.map.print();
        System.out.println();
        while(!gameOver) {
            Point nextPoint = this.player.evaluatePossbileNextStep(map);
            if (nextPoint == null) {
                break;
            } else {
                String space = this.map.get(nextPoint);
                if (space != null && space.equals(TreasureChest.CHARACTER)) {
                    boolean shouldEnd = this.map.openTreasureChest(nextPoint);
                    this.map.print();
                    if (shouldEnd) {
                        gameOver = true;
                    } else {
                        // Se o baú estiver vazio, continua movendo o robô
                        this.map.moveRobot(nextPoint);
                    }
                } else {
                    this.map.moveRobot(nextPoint);                    
                }
            }
            this.map.print();
            System.out.println();
        }
    }
}