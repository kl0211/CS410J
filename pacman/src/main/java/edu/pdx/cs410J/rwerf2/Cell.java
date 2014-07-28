package edu.pdx.cs410J.rwerf2;

import java.util.ArrayList;

/**
 * Created by karl on 7/23/14.
 */
public class Cell {
    boolean [] walls = {true, false, true, false };
    // 0 is NORTH, 1 is EAST, 2 is WEST, 3 is SOUTH

    boolean dot;

    public ArrayList<Character> getOccupants() {
        return occupants;
    }

    public void setOccupants(ArrayList<Character> occupants) {
        this.occupants = occupants;
    }

    ArrayList<Character> occupants;

    public boolean canMove(Character.Direction direction) {
        switch(direction) {
            case NORTH: return !walls[0];
            case EAST: return !walls[1];
            case WEST: return !walls[2];
            case SOUTH: return !walls[3];
            default: System.exit(1);
        }
        return false;
    }

    public boolean hasDot() {
        return dot;
    }


}
