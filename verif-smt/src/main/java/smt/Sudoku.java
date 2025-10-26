package smt;

public class Sudoku {

    public static void main(String[] args) {
        String inPath = args[0];
        String outPath = args[1];
        int[][] grid = readInput(inPath);
        printGrid(grid);
        
    }

    //helper function to read input in to a 2d array 
    static int[][] readInput(String filePath) {

        //extract all lines from the file
        java.util.List<String> lines;
        try {
            lines = java.nio.file.Files.readAllLines(java.nio.file.Paths.get(filePath));
        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to read input file: " + filePath, e);
        }
        
        java.util.List<int[]> rows = new java.util.ArrayList<>();
        //for each line (row), split them into seperate integers
        for (String line : lines) {
            String[] tokens = line.trim().split("\\s+");
            int[] row = new int[tokens.length];
            for (int i = 0; i < tokens.length; i++) {
                row[i] = Integer.parseInt(tokens[i]);
            }
            rows.add(row);
        }

        //conver the rows into the 2d array
        int[][] sudokuGrid = new int[rows.size()][];
        for (int i = 0; i < rows.size(); i++) {
            sudokuGrid[i] = rows.get(i);
        }

        //return final 2d array
        return sudokuGrid;
    }

    //helper function to test readInput to make sure we read correctly
    static void printGrid(int[][] grid) {
        for (int i = 0; i < grid.length; i++) {
            StringBuilder builder = new StringBuilder();
            for (int j = 0; j < grid[i].length; j++) {
                if (j > 0) {
                    builder.append(' ');
                }
                builder.append(grid[i][j]);
            }
            System.out.println(builder.toString());
        }
    }

}
