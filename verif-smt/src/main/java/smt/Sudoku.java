package smt;

import com.microsoft.z3.*;

public class Sudoku {

    public static void main(String[] args) {
        String inPath = args[0];
        String outPath = args[1];
        int[][] grid = readInput(inPath);

        //what makes a sudoku solution valid?
        // - the provided numbers in the grid stay the same
        // - each row contains all the numbers 1-9 with no duplicates
        // - each column contains all the numbers 1-9 with no duplicates
        // - each 3x3 subgrid contains all the numbers 1-9 with no duplicates
        // so we have to make 4 things we need to make valid to check if SATISFIABLE

        Context ctx = new Context(new java.util.HashMap<>());
        try {
            Solver solver = ctx.mkSolver();

            // get grid dimentions
            int gLength = grid.length;
            int gWidth = grid[0].length;

            //we are going to store the z3 constraints in a 2d array
            IntExpr[][] d_i_j = new IntExpr[gLength][gWidth];

            //create the z3 variables for each cell in the grid
            for (int i = 0; i < gLength; i++) {
                for (int j = 0; j < gWidth; j++) { 
                    d_i_j[i][j] = (IntExpr) ctx.mkIntConst("d_" + i + "_" + j);
                }
            }

            //----- CONSTRAINT 1: PROVIDED NUMBERS -----

            // before we start, we store the provided numbers in the grid
            for (int i = 0; i < gLength; i++) {
                for (int j = 0; j < gWidth; j++) {
                    if (grid[i][j] != 0) {
                        //use make aqual to keep the provided values
                        solver.add(ctx.mkEq(d_i_j[i][j], ctx.mkInt(grid[i][j])));
                    }
                }
            }
            
            //----- CONSTRAINT 2: ROW CONSTRAINT -----

            //
            for (int i = 0; i < gLength; i++) {
                // for each row we store the chosen number then use mkDistinct to make sure they are unique
                Expr[] rowExpressions = new Expr[gWidth];
                for (int j = 0; j < gWidth; j++) {
                    // check the following in solver: (d_i_j >= 1) && (d_i_j <= 9) 
                    solver.add(ctx.mkAnd(ctx.mkGe(d_i_j[i][j], ctx.mkInt(1)), ctx.mkLe(d_i_j[i][j], ctx.mkInt(9))));
                    //store the chosen number in the rowExpressions array
                    rowExpressions[j] = d_i_j[i][j];
                }
                //use mkDistinct to make sure the numbers in the row are unique
                solver.add(ctx.mkDistinct(rowExpressions));
            }

            //----- CONSTRAINT 3: COLUMN CONSTRAINT -----
            

            //----- CONSTRAINT 4: 3x3 SUBGRID CONSTRAINT -----


            // ----- CHECKING AND PRINTING SAT OR NO SOLUTION -----
            //check if sat or unsat
            Status status = solver.check();
            //if sat, get the model and write to file
            if (status == Status.SATISFIABLE) {
                Model model = solver.getModel();
                // create a new 2d array to extract solution into
                int[][] result = new int[gLength][gWidth];
                //for each row and column, extract the solution
                for (int i = 0; i < gLength; i++) {
                    for (int j = 0; j < gWidth; j++) {
                        IntNum v = (IntNum) model.evaluate(d_i_j[i][j], false);
                        result[i][j] = v.getInt();
                    }
                }
                writeGridToFile(result, outPath);
            //if unsat, print no solution
            } else {
                System.out.println("No Solution");
            }
        //close the context
        } finally {
            ctx.close();
        }

    }

    //helper function to read input in to a 2d array 
    static int[][] readInput(String filePath) {

        //extract all lines from the file
        java.util.List<String> lines;
        try {
            lines = java.nio.file.Files.readAllLines(java.nio.file.Paths.get(filePath));
        } catch (java.io.IOException e) {
            throw new RuntimeException("Reading input failed!", e);
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

        //convert the rows into the 2d array
        int[][] sudokuGrid = new int[rows.size()][];
        for (int i = 0; i < rows.size(); i++) {
            sudokuGrid[i] = rows.get(i);
        }

        //return final 2d array
        return sudokuGrid;
    }

    //helper function to write the solved grid to output file
    static void writeGridToFile(int[][] grid, String filePath) {
        //create a list of strings to store the lines
        java.util.List<String> lines = new java.util.ArrayList<>();
        //for each row, create a string for that row and and too the lines
        for (int i = 0; i < grid.length; i++) {
            StringBuilder b = new StringBuilder();
            for (int j = 0; j < grid[i].length; j++) {
                if (j > 0) b.append(' ');
                b.append(grid[i][j]);
            }
            lines.add(b.toString());
        }
        //try to wtite in file
        try {
            java.nio.file.Files.write(java.nio.file.Paths.get(filePath), lines);
        } catch (java.io.IOException e) {
            throw new RuntimeException("Writing to output failed!", e);
        }
    }

}
