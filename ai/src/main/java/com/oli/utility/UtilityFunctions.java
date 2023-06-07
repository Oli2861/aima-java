package main.java.com.oli.utility;

import main.java.com.oli.vacuum.maze.FieldState;

public class UtilityFunctions {
    public static boolean decodeString(String s) {
        if (s.equals("True")) return true;
        else if (s.equals("False")) return false;
        throw new IllegalArgumentException("String expected to be True or False");
    }

    /**
     * Prints the map to the console.
     *
     * @param map the map to print.
     */
    public static void printMap(FieldState[][] map) {
        for (FieldState[] fieldStates : map) {
            for (FieldState fieldState : fieldStates) {
                System.out.printf("%10s\t", fieldState);
            }
            System.out.println();
        }
    }
}