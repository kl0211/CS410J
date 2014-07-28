package edu.pdx.cs410J.rwerf2;

import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import edu.pdx.cs410J.InvokeMainTestCase;
import static junit.framework.Assert.assertEquals;

/**
 * Tests the functionality in the {@link Pacman} main class.
 */
public class PacmanTest extends InvokeMainTestCase {

    /**
     * Invokes the main method of {@link Pacman} with the given arguments.
     */
    private MainMethodResult invokeMain(String... args) {
        return invokeMain( Pacman.class, args );
    }

    /**
     * Tests that invoking the main method with no arguments issues an error
     */
    @Ignore
    @Test
    public void testNoCommandLineArguments() {
        MainMethodResult result = invokeMain();
        assertEquals(new Integer(1), result.getExitCode());
        assertTrue(result.getErr().contains( "Missing command line arguments" ));
    }

    @Test
    public void testCharacter() {
        Character test = new Character();
        assertTrue(test!=null);
        test.setFacing(Character.Direction.NORTH);
        assertEquals(Character.Direction.NORTH, test.getFacing());
    }

    @Test
    public void testPlayer() {
        Character test = new Player();
        test.setVulnerability(true);
        assertTrue(test.getVulnerability());
    }

    @Test
    public void testGhost() {
        Character test = new Ghost();
        test.setVulnerability(false);
        assertTrue(!test.getVulnerability());
    }

}
