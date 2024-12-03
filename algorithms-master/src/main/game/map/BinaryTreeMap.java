package main.game.map;

import java.util.HashMap;
import java.util.Random;
import java.util.LinkedList;
import java.util.List;
import main.game.Player;

class MapNode {
    private String content;
    private Point position;
    private MapNode left;
    private MapNode right;
    
    public MapNode(String content, Point position) {
        this.content = content;
        this.position = position;
        this.left = null;
        this.right = null;
    }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Point getPosition() { return position; }
    public MapNode getLeft() { return left; }
    public MapNode getRight() { return right; }
    public void setLeft(MapNode left) { this.left = left; }
    public void setRight(MapNode right) { this.right = right; }
}

public class BinaryTreeMap {
    private MapNode root;
    private int width;
    private int height;
    private Point robotLocation;
    private HashMap<String, Point> treasureChests;
    
    public BinaryTreeMap(int width, int height) {
        this.width = width;
        this.height = height;
        this.treasureChests = new HashMap<>();
        this.robotLocation = new Point(0, 0);
        initializeMap();
        generateMap();
    }
    
    private void initializeMap() {
        // Inicializa a árvore com espaços vazios "*"
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                insert(new Point(x, y), "*");
            }
        }
    }
    
    private void insert(Point position, String content) {
        root = insertRec(root, position, content);
    }
    
    private MapNode insertRec(MapNode node, Point position, String content) {
        if (node == null) {
            return new MapNode(content, position);
        }
        
        // Usa posição x e y para determinar a direção na árvore
        // Se x é maior ou igual, vai para direita, senão para esquerda
        // Em caso de x igual, usa y para decisão
        int compareResult = comparePositions(position, node.getPosition());
        
        if (compareResult < 0) {
            node.setLeft(insertRec(node.getLeft(), position, content));
        } else if (compareResult > 0) {
            node.setRight(insertRec(node.getRight(), position, content));
        } else {
            node.setContent(content);
        }
        
        return node;
    }
    
    private int comparePositions(Point p1, Point p2) {
        if (p1.getPositionX() != p2.getPositionX()) {
            return p1.getPositionX() - p2.getPositionX();
        }
        return p1.getPositionY() - p2.getPositionY();
    }
    
    public String get(Point position) {
        MapNode node = findNode(root, position);
        return node != null ? node.getContent() : null;
    }
    
    private MapNode findNode(MapNode node, Point position) {
        if (node == null) return null;
        
        int compareResult = comparePositions(position, node.getPosition());
        
        if (compareResult == 0) {
            return node;
        } else if (compareResult < 0) {
            return findNode(node.getLeft(), position);
        } else {
            return findNode(node.getRight(), position);
        }
    }
    
    public void moveRobot(Point nextPoint) {
        setContent(robotLocation, "*");
        setContent(nextPoint, Player.CHARACTER);
        robotLocation = nextPoint;
    }
    
    private void setContent(Point position, String content) {
        MapNode node = findNode(root, position);
        if (node != null) {
            node.setContent(content);
        }
    }
    
    public Point getRobotLocation() {
        return robotLocation;
    }
    
    public int[] getScenarioSize() {
        return new int[]{width, height};
    }
    
    private void generateMap() {
        // Coloca o robô na posição inicial
        setContent(robotLocation, Player.CHARACTER);
        
        // Gera os elementos do mapa
        generateRocks();
        generateTreasureChests();
        generateMapOfTreasure();
        generateMonsters();
    }
    
    public void print() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                String content = get(new Point(i, j));
                if (content == null) {
                    content = "*";
                }
                System.out.print(content + " ");
                if (j == height - 1) {
                    System.out.println();
                }
            }
        }
    }
    
    public boolean openTreasureChest(Point nextPoint) {
        for (String key : treasureChests.keySet()) {
            if (treasureChests.get(key).equals(nextPoint)) {
                setContent(nextPoint, key);
                
                if (key.equals(TreasureChest.CHEST_TRESURE_CHARACTER)) {
                    System.out.println("Parabéns você encontrou o tesouro!");
                    return true;
                } else if (key.equals(TreasureChest.CHEST_TRAP_CHARACTER)) {
                    System.out.println("O jogo acabou! Você morreu, caiu em uma armadilha");
                    return true;
                } else {
                    System.out.println("Aqui não tem nada");
                    return false;
                }
            }
        }
        return false;
    }
    
    // Métodos de geração do mapa (similar ao Map.java original)
    private void generateRocks() {
        Random random = new Random();
        int rockCount = 0;
        while (rockCount < 3) {
            int x = random.nextInt(width - 1);
            int y = random.nextInt(height - 1);
            
            // Verifica se podemos colocar uma rocha 2x2 nesta posição
            if (canPlaceRock(x, y)) {
                setContent(new Point(x, y), Rock.CHARACTER);
                setContent(new Point(x + 1, y), Rock.CHARACTER);
                setContent(new Point(x, y + 1), Rock.CHARACTER);
                setContent(new Point(x + 1, y + 1), Rock.CHARACTER);
                rockCount++;
            }
        }
    }
    
    private boolean canPlaceRock(int x, int y) {
        if (x + 1 >= width || y + 1 >= height) return false;
        
        // Verifica se todas as posições necessárias estão livres
        String pos1 = get(new Point(x, y));
        String pos2 = get(new Point(x + 1, y));
        String pos3 = get(new Point(x, y + 1));
        String pos4 = get(new Point(x + 1, y + 1));
        
        return pos1.equals("*") && pos2.equals("*") && 
               pos3.equals("*") && pos4.equals("*");
    }
    
    private void generateTreasureChests() {
        Random random = new Random();
        List<String> treasureTypes = new LinkedList<>();
        treasureTypes.add(TreasureChest.CHEST_EMPTY_CHARACTER);
        treasureTypes.add(TreasureChest.CHEST_TRAP_CHARACTER);
        treasureTypes.add(TreasureChest.CHEST_TRESURE_CHARACTER);
        
        int treasureCount = 0;
        while (treasureCount < 3) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            Point position = new Point(x, y);
            
            if (get(position).equals("*")) {
                setContent(position, TreasureChest.CHARACTER);
                String type = treasureTypes.remove(random.nextInt(treasureTypes.size()));
                treasureChests.put(type, position);
                treasureCount++;
            }
        }
    }
    
    private void generateMapOfTreasure() {
        Random random = new Random();
        boolean placed = false;
        
        while (!placed) {
            int x = random.nextInt(2, width);
            int y = random.nextInt(2, height);
            Point position = new Point(x, y);
            
            if (get(position).equals("*")) {
                setContent(position, MapOfTreasure.CHARACTER);
                placed = true;
            }
        }
    }
    
    private void generateMonsters() {
        Random random = new Random();
        int monsterCount = 0;
        
        while (monsterCount < 3) {
            int x = random.nextInt(2, width - 1);
            int y = random.nextInt(2, height - 1);
            Point position = new Point(x, y);
            
            if (get(position).equals("*")) {
                setContent(position, Monster.CHARACTER);
                monsterCount++;
            }
        }
    }
}