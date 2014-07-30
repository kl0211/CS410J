package edu.pdx.cs410J.rwerf2;

/**
 * Created by karl on 7/23/14.
 */
public class Character {
    enum Direction { NORTH, EAST, WEST, SOUTH }
    private Direction facing;
    private boolean vulnerability;

    public Direction getFacing() {
        return facing;
    }

    public void setFacing(Direction facing) {
        this.facing = facing;
    }

    public boolean getVulnerability() {
        return vulnerability;
    }

    public void setVulnerability(boolean vulnerability) {
        this.vulnerability = vulnerability;
    }
}
