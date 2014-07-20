package edu.pdx.cs410J.rwerf2;

import edu.pdx.cs410J.AbstractAirline;
import edu.pdx.cs410J.InvokeMainTestCase;
import edu.pdx.cs410J.ParserException;
import org.junit.Test;

import java.io.*;
import java.util.Collection;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests the functionality in the {@link edu.pdx.cs410J.rwerf2.Project3} main class.
 */
public class Project3Test extends InvokeMainTestCase {

    private String fileName = "DoNotUseThisFile";
    private TextParser parser = new TextParser(fileName);
    private TextDumper dumper = new TextDumper(fileName);
    private Airline airline;

    /**
     * Invokes the main method of {@link Project1} with the given arguments.
     */
    private MainMethodResult invokeMain(String... args) {
        return invokeMain( Project3.class, args );
    }

    private void createFile(String str) {
        Writer outFile;
        try {
            outFile = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(fileName), "UTF-8"));
            outFile.write(str);
            outFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteFile() {
        File file = new File(fileName);
        if (file.exists())
            file.delete();
    }

    /**
     * Tests that invoking the main method with no arguments issues an error
     */
    @Test
    public void testNoCommandLineArguments() {
        MainMethodResult result = invokeMain();
        assertEquals(new Integer(1), result.getExitCode());
        assertTrue(result.getErr().contains( "Missing command line arguments" ));
    }

    @Test
    public void testMissingFile() {
        deleteFile();
        TextParser parser = new TextParser(fileName);
        try {
            AbstractAirline notUsed = parser.parse();
            assertTrue(false); //should not get here
        } catch (ParserException e) {
            assertEquals(e.getMessage(), "File 'DoNotUseThisFile' not found!");
        }
    }

    @Test
    public void testEmptyFile() {
        createFile("");
        try {
            AbstractAirline notUsed = parser.parse();
            assertTrue(false);
        } catch (ParserException e) {
            assertEquals(e.getMessage(), "FILE READ ERROR: 'DoNotUseThisFile' IS AN EMPTY FILE");
        }
        deleteFile();
    }

    @Test
    public void testFileAlreadyAssociated() {
        createFile("Foo Airlines");
        MainMethodResult result = invokeMain("-print", "-textFile", fileName, "Bar Airlines", "101", "PDX", "1/1/2000", "12:00", "aM", "SEA", "1/1/2000", "12:40", "Pm");
        deleteFile();
        assertTrue(result.getErr().contains("Filename is already associated with airline 'Foo Airlines'"));
        assertEquals(new Integer(1), result.getExitCode());
    }

    @Test
    public void testReadWithBadAirportCode() throws IOException {
        createFile("Foo Airlines\n101\napv\n01/01/2001 01:00 pm\nSEA\n01/01/2001 01:40 pm");
        try {
            AbstractAirline notUsed = parser.parse();
            assertTrue(false);
        } catch (ParserException e) {
            assertEquals(e.getMessage(), "FILE READ ERROR: 'apv' IS NOT A VALID AIRPORT CODE");
        }
        deleteFile();
    }

    @Test
    public void testReadWithBadDate() throws IOException {
        createFile("Foo Airlines\n101\nPDX\n1/0/2001 1:00\nSEA\n01/01/2001 01:40");
        try {
            AbstractAirline notUsed = parser.parse();
            assertTrue(false);
        } catch (ParserException e) {
            assertEquals(e.getMessage(), "FILE READ ERROR: '1/0/2001 1:00' IS NOT A VALID DATE AND TIME");
        }
        deleteFile();
    }

    @Test
    public void testReadWithBadDate2() throws IOException {
        createFile("Foo Airlines\n101\nPDX\n1/01/20/1 1:00\nSEA\n01/01/2001 01:40");
        try {
            AbstractAirline notUsed = parser.parse();
            Collection<Flight> flights = notUsed.getFlights();
            for (Flight flight : flights) {
                System.out.println(flight.toString());
            }
            assertTrue(false);
        } catch (ParserException e) {
            assertEquals(e.getMessage(), "FILE READ ERROR: '1/01/20/1 1:00' IS NOT A VALID DATE AND TIME");
        }
        deleteFile();
    }

    @Test
    public void testReadWithBadDate3() throws IOException {
        createFile("Foo Airlines\n101\nPDX\n1/01/2001 1:00 Pm\nSEA\n01/01/2/1 01:40 Am");
        try {
            AbstractAirline notUsed = parser.parse();
            assertTrue(false);
        } catch (ParserException e) {
            assertEquals(e.getMessage(), "FILE READ ERROR: '01/01/2/1 01:40 Am' IS NOT A VALID DATE AND TIME");
        }
        deleteFile();
    }

    @Test
    public void testReadWithBadDateWithExtraDepartureArgument() throws IOException {
        createFile("Foo Airlines\n101\nPDX\n1/01/2001 1:00 pm foo\nSEA\n01/01/2/1 01:40 am");
        try {
            AbstractAirline notUsed = parser.parse();
            assertTrue(false);
        } catch (ParserException e) {
            assertEquals(e.getMessage(), "FILE READ ERROR: '01/01/2/1 01:40 am' IS NOT A VALID DATE AND TIME");
        }
        deleteFile();
    }

    @Test
    public void testReadWithBadDateWithMissingMeridiem() throws IOException {
        createFile("Foo Airlines\n101\nPDX\n1/01/2001 1:00\nSEA\n01/01/2001 01:40 Pm");
        try {
            AbstractAirline notUsed = parser.parse();
            assertTrue(false);
        } catch (ParserException e) {
            assertEquals(e.getMessage(), "FILE READ ERROR: '1/01/2001 1:00' IS NOT A VALID DATE AND TIME");
        }
        deleteFile();
    }

    @Test
    public void testReadWithBadTime() throws IOException {
        createFile("Foo Airlines\n101\nPDX\n1/01/2001 33:00\nSEA\n01/01/2001 01:40");
        try {
            AbstractAirline notUsed = parser.parse();
            assertTrue(false);
        } catch (ParserException e) {
            assertEquals(e.getMessage(), "FILE READ ERROR: '1/01/2001 33:00' IS NOT A VALID DATE AND TIME");
        }
        deleteFile();
    }

    @Test
    public void testSuccessfulRead() throws ParserException, IOException {
        airline = new Airline("Foo Airlines");
        airline.addFlight(new Flight(101, "PDX", "1/1/2001 12:00 am", "SEA", "1/1/2001 12:40 am"));
        dumper.dump(airline);
        AbstractAirline test = parser.parse();
        deleteFile();
        assertEquals(test.getName(), "Foo Airlines");
    }

    @Test
    public void canInvokeReadme() {
        MainMethodResult result = invokeMain("-README");
        assertEquals(new Integer(0), result.getExitCode());
    }

    @Test
    public void testValidArguments() {
        MainMethodResult result = invokeMain("Foo Airlines", "101", "PDX", "1/1/2000", "12:00", "AM", "SEA", "1/1/2000", "12:40", "pM");
        assertEquals(new Integer(0), result.getExitCode());
    }

    @Test
    public void testValidLowerCaseAirportCode() {
        MainMethodResult result = invokeMain("Foo Airlines", "101", "pdx", "1/1/2000", "12:00", "AM", "SEA", "1/1/2000", "12:40", "pM");
        assertEquals(new Integer(0), result.getExitCode());
    }

    @Test
    public void testValidMixedCaseAirportCode() {
        MainMethodResult result = invokeMain("Foo Airlines", "101", "Pdx", "1/1/2000", "12:00", "AM", "SEA", "1/1/2000", "12:40", "pM");
        assertEquals(new Integer(0), result.getExitCode());
    }

    @Test
    public void testValidArgumentsWithPrint() {
        MainMethodResult result = invokeMain("-print", "Foo Airlines", "101", "PDX", "1/1/2000", "12:00", "Am", "SEA", "1/1/2000", "12:40", "Pm");
        assertEquals(new Integer(0), result.getExitCode());
    }

    @Test
    public void testTooManyArguments() {
        MainMethodResult result = invokeMain("-print", "Foo Airlines", "101", "PDX", "1/1/2000", "12:00", "AM", "SEA", "1/1/2000", "12:40", "pm", "extra");
        assertEquals(new Integer(1), result.getExitCode());
        assertTrue(result.getErr().contains("Too many arguments"));
    }

    @Test
    public void canInvokeReadmeWithOtherArguments() {
        MainMethodResult result = invokeMain("-print", "Foo Airlines", "101", "PDX", "1/1/2000", "12:00", "am", "SEA", "1/1/2000", "12:40", "am", "-README");
        assertEquals(new Integer(0), result.getExitCode());
    }

    @Test
    public void CanPrintAndWriteValidArguments() {
        MainMethodResult result = invokeMain("-print", "-textFile", fileName, "Foo Airlines", "101", "PDX", "1/1/2000", "12:00", "aM", "SEA", "1/1/2000", "12:40", "pM");
        assertEquals(new Integer(0), result.getExitCode());
        deleteFile();
    }

    @Test
    public void CanPrintAndWriteWithSwappedOptions() {
        MainMethodResult result = invokeMain("-textFile", fileName, "-print", "Foo Airlines", "101", "PDX", "1/1/2000", "12:00", "pm", "SEA", "1/1/2000", "12:40", "PM");
        assertEquals(new Integer(0), result.getExitCode());
        deleteFile();
    }

    @Test
    public void testBadFlightNumber() {
        MainMethodResult result = invokeMain("-textFile", fileName, "-print", "Foo Airlines", "101a", "PDX", "1/1/2000", "12:00", "AM", "SEA", "1/1/2000", "12:40", "Am");
        assertEquals(new Integer(1), result.getExitCode());
        assertTrue(result.getErr().contains("Flight number must be represented as an integer number"));
        deleteFile();
    }

    @Test
    public void testBadAirportCodeArgument() {
        deleteFile();
        MainMethodResult result = invokeMain("-textFile", fileName, "-print", "Foo Airlines", "101", "APV", "1/1/2000", "12:00", "am", "SEA", "1/1/2000", "12:40", "aM");
        assertEquals(new Integer(1), result.getExitCode());
        assertTrue(result.getErr().contains("Argument `APV': Airport code is not valid"));
        deleteFile();
    }

    @Test
    public void testMissingFileNameArgument() {
        MainMethodResult result = invokeMain("-textFile");
        assertEquals(new Integer(1), result.getExitCode());
        assertTrue(result.getErr().contains("Missing file name after -textFile argument"));
        deleteFile();
    }

    @Test
    public void testMissingArgumentsWithTextFileOption() {
        MainMethodResult result = invokeMain("-textFile", fileName);
        assertEquals(new Integer(1), result.getExitCode());
        assertTrue(result.getErr().contains("Missing command line arguments."));
        deleteFile();
    }

    @Test
    public void testMissingArgumentsWithTextFileAndPrintOption() {
        deleteFile();
        MainMethodResult result = invokeMain("-textFile", fileName, "-print");
        assertEquals(new Integer(1), result.getExitCode());
        assertTrue(result.getErr().contains("Missing command line arguments."));
        deleteFile();
    }

    @Test
    public void testMissingPrettyFileNameArgument() {
        MainMethodResult result = invokeMain("-pretty");
        assertEquals(new Integer(1), result.getExitCode());
        assertTrue(result.getErr().contains("Missing file name after -pretty argument"));
        deleteFile();
    }

    @Test
    public void testMissingArgumentsWithPrettyPrintOption() {
        MainMethodResult result = invokeMain("-pretty", fileName);
        assertEquals(new Integer(1), result.getExitCode());
        assertTrue(result.getErr().contains("Missing command line arguments."));
        deleteFile();
    }

    @Test
    public void testMissingArgumentsWithPrettyPrintAndPrintOption() {
        deleteFile();
        MainMethodResult result = invokeMain("-pretty", fileName, "-print");
        assertEquals(new Integer(1), result.getExitCode());
        assertTrue(result.getErr().contains("Missing command line arguments."));
        deleteFile();
    }

    @Test
    public void testMissingArgumentsWithPrettyPrintAndPrintOptionAndTextFileMissingFileName() {
        deleteFile();
        MainMethodResult result = invokeMain("-pretty", fileName, "-print", "-textFile");
        assertEquals(new Integer(1), result.getExitCode());
        assertTrue(result.getErr().contains("Missing file name after -textFile argument"));
        deleteFile();
    }

    @Test
    public void testMissingArgumentsWithPrettyPrintAndPrintOptionAndTextFile() {
        deleteFile();
        MainMethodResult result = invokeMain("-pretty", fileName, "-print", "-textFile", fileName);
        assertEquals(new Integer(1), result.getExitCode());
        assertTrue(result.getErr().contains("Missing command line arguments."));
        deleteFile();
    }

    @Test
    public void testMissingArgumentsWithPrettyPrintMissingFileNameAndTextFile() {
        deleteFile();
        MainMethodResult result = invokeMain("-pretty", fileName, "-textFile", fileName);
        assertEquals(new Integer(1), result.getExitCode());
        assertTrue(result.getErr().contains("Missing command line arguments."));
        deleteFile();
    }

    @Test
    public void testMissingFileNameAfterPretty() {
        deleteFile();
        MainMethodResult result = invokeMain("-pretty", "-textFile", fileName, "Foo Airlines", "101", "PDX", "1/1/2000", "11:50", "pm", "SEA", "01/02/2001", "12:30", "am");
        assertEquals(new Integer(1), result.getExitCode());
        assertTrue(result.getErr().contains("Missing file name after -pretty argument"));
        deleteFile();
    }

    @Test
    public void testMissingFileNameAfterTextFile() {
        deleteFile();
        MainMethodResult result = invokeMain("-textFile", "-pretty", fileName, "Foo Airlines", "101", "PDX", "1/1/2000", "11:50", "pm", "SEA", "01/02/2001", "12:30", "am");
        assertEquals(new Integer(1), result.getExitCode());
        assertTrue(result.getErr().contains("Missing file name after -textFile argument"));
        deleteFile();
    }

    @Test
    public void testPrettyAndTextFileToSameFileName() {
        deleteFile();
        MainMethodResult result = invokeMain("-textFile", fileName, "-pretty", fileName, "Foo Airlines", "101", "PDX", "1/1/2000", "11:50", "pm", "SEA", "01/02/2001", "12:30", "am");
        assertEquals(new Integer(1), result.getExitCode());
        assertTrue(result.getErr().contains("-pretty and -textFile cannot be written to the same file"));
        deleteFile();
    }

    @Test
    public void testSuccessfulParseArgs() {
        String [] args = {"Foo Airlines", "101", "PDX", "1/1/2000", "11:50", "pm", "SEA", "01/02/2001", "12:30", "am"};
        Flight flight = Project3.parseArgs(args);
        assertEquals(flight.getNumber(), 101);
        assertEquals(flight.getSource(), "PDX");
        assertEquals(flight.getDepartureString(), "1/1/00 11:50 PM");
        assertEquals(flight.getDestination(), "SEA");
        assertEquals(flight.getArrivalString(), "1/2/01 12:30 AM");
    }
}
