package main.strategies;

import java.util.List;
import java.util.Stack;
import main.game.map.Map;
import main.game.map.Point;

public class Rollback implements Strategy {
    private Stack<Point> visitedPoints;
    private Stack<Point> rollbackPoints;

    public Rollback() {
        this.visitedPoints = new Stack<>();
        this.rollbackPoints = new Stack<>();
    }

    @Override
    public Point evaluatePossbileNextStep(List<Point> possibleNextStep, Map map) {
        // Adiciona a posição atual à pilha de pontos visitados
        Point currentPosition = map.getRobotLocation();
        if (!visitedPoints.contains(currentPosition)) {
            visitedPoints.push(currentPosition);
        }

        // Procura por um baú de tesouro nos pontos adjacentes
        for (Point nextPoint : possibleNextStep) {
            String content = map.get(nextPoint);
            if (content.equals("T")) { // Se encontrou um baú
                return nextPoint;
            }
        }

        // Filtra pontos já visitados e obstáculos
        Point bestPoint = null;
        for (Point nextPoint : possibleNextStep) {
            String content = map.get(nextPoint);
            if (!visitedPoints.contains(nextPoint) && !isObstacle(content)) {
                bestPoint = nextPoint;
                break;
            }
        }

        // Se não encontrou caminho livre, faz rollback
        if (bestPoint == null && !rollbackPoints.isEmpty()) {
            bestPoint = rollbackPoints.pop();
            // Remove pontos visitados após o ponto de rollback
            while (!visitedPoints.isEmpty() && !visitedPoints.peek().equals(bestPoint)) {
                visitedPoints.pop();
            }
        } else if (bestPoint != null) {
            rollbackPoints.push(currentPosition);
        }

        return bestPoint;
    }

    private boolean isObstacle(String content) {
        return content.equals("R") || content.equals("M"); // Rochas ou Monstros são obstáculos
    }
}