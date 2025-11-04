package uj.wmii.pwj.collections;

import java.util.Random;

public class SimpleBattleshipGenerator implements BattleshipGenerator {

    private static final int SIZE = 10;
    private static final char SHIP = '*';
    private static final char WATER = '.';

    @Override
    public String generateMap() {
        char[][] grid = new char[SIZE][SIZE];
        boolean[][] occupied = new boolean[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                occupied[i][j] = false;
            }
        }
        initializeGrid(grid);
        placeShips(grid);
        return convertGridToString(grid);
    }

    private void initializeGrid(char[][] grid) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                grid[i][j] = WATER;
            }
        }
    }

    private String convertGridToString(char[][] grid) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < SIZE; i++) {
            sb.append(grid[i]);
        }
        return sb.toString();
    }

    private void occupy(char[][] grid, boolean[][] occupied, int row, int col, int size) {
        // to do
    }

    private void placeShips(char[][] grid, boolean[][] occupied) {
        int[] shipSizes = { 4, 3, 3, 2, 2, 2, 1, 1, 1, 1 };
        for (int size : shipSizes) {
            boolean placed = false;
            int[] toOccupy = { -1, -1, -1, -1 };
            int rand = 0, row = 0, col = 0;
            while (!placed) {
                rand = getRandomPosition();
                row = rand / SIZE;
                col = rand % SIZE;
                if (!occupied[row][col]) {
                    toOccupy[0] = rand;
                    placed = true;
                }
            }
            while (size-- > 0) {
                int direction = getRandomDirection();
                while (!placed) {
                    row += (direction % 10);
                    col += (direction / 10);
                    if (!occupied[row][col]) {
                        toOccupy[0] = rand;
                        placed = true;
                    }
                    // finish and this logic seems like bs so fix it later
                }

            }
        }
    }

    private int getRandomDirection() {
        Random rand = new Random();
        int[] nums = { -10, -9, 1, 10 };
        return nums[rand.nextInt(4)];
    }

    private int getRandomPosition() {
        Random rand = new Random();
        return rand.nextInt(100);
    }

}