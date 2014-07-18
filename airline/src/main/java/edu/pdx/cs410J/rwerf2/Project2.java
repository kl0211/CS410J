package edu.pdx.cs410J.rwerf2;

import edu.pdx.cs410J.AbstractAirline;
import edu.pdx.cs410J.ParserException;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;

/**
 * The main class for the CS410J airline Project
 * Contains main(), parseArgs(), printAirline(), displayReadme() and displayUsage()
 */
public class Project2 {
    //Regular Expression patterns for time and date was learned from mkyong at
    //http://www.mkyong.com/regular-expressions/how-to-validate-time-in-24-hours-format-with-regular-expression/
    //and
    //http://www.mkyong.com/regular-expressions/how-to-validate-date-with-regular-expression/
    static final String AIRPORT_CODE_PATTERN = "[A-Z]{3}";
    static final String TIME_PATTERN = "([01]?[0-9]|2[0-3]):[0-5][0-9]";
    static final String DATE_PATTERN = "^(0?[1-9]|1[012])[- /.](0?[1-9]|[12][0-9]|3[01])[- /.](19|20)\\d\\d$";

    /**
     * Creates an instance of one Airline. Depending on the options supplied, it can print to standard out
     * and/or read/write to a text file.
     * First checks if there are any arguments, and exits if there are none. Then checks the arguments for
     * -README and displays and exits the readme if present, regardless if there are any other arguments.
     * if -README is not present, -print and -textFile are then checked. If -textFile is present, it will
     * attempt to read from the file specified and check if the airline being entered in matches the Airline
     * the file is associated with or will create a new file. After options have been discovered, the function
     * will then check for correct number of arguments and then parse them into either the Airline that
     * was read from the file, or add them to a new Airline. The airline, with its flights will then be
     * printed and/or written to file or the program will just simply exit.
     *
     * @param args
     *        Command-line arguments
     */
    public static void main(String[] args) {
        AbstractAirline airline = null; //airline will either be new or from parsed text file
        TextParser parser;              //for reading in from file
        TextDumper dumper = null;       //for possible writing out
        int offset = 0; //for determining position in args which starts the flight information
        boolean willPrint = false;      //for printing to standard out

        if (args.length == 0) { //check if there are any arguments at all
            System.err.println("Missing command line arguments. Add the argument \"-README\"" +
                               " to display program help");
            displayUsage();
        }

        for (String arg: args) { //check for -README argument
            if (arg.contains("-README")) { //and if supplied, print README and exit
                displayReadme();
            }
        }

        for (int i = 0; i < args.length; ++i) { //check for other options supplied and calculate offset
            if (args[i].contains("-print")) {
                ++offset;
                willPrint = true;
            }
            if (args[i].contains("-textFile")) {
                try {
                    parser = new TextParser(args[i + 1]);         //set TextParser with name from next argument
                    airline = parser.parse();                     //parse file and return airline
                }
                catch (ParserException e) { //catch if filename does not exist or is missing flight information
                    if (e.getMessage().contains("File '" + args[i + 1] + "' not found!")) { //if file is missing,
                        File file = new File(args[i + 1]);                                  //create a new one
                        try {
                            file.createNewFile();
                        } catch (IOException e1) { //For out-of-space issues
                            System.err.println("ERROR CREATING NEW FILE");
                            e1.printStackTrace();
                            System.exit(2);
                        }
                    }
                    else { //if the thrown ParserException was an error in reading in the existing file
                           //than the file was malformed
                        System.err.println(e.getMessage());
                        System.exit(2);
                    }
                }
                catch (ArrayIndexOutOfBoundsException e) { //catch if -textfile was last argument
                    System.err.println("Missing file name after -textFile argument");
                    displayUsage();
                }
                offset = offset + 2; //offset + 2 for "-textFile name"
                dumper = new TextDumper(args[i + 1]); //create new TextDumper for later writing out
            }
        }
        if (args.length - offset < 8) { //if there are not enough arguments
            System.err.println("Missing command line arguments. Add the argument \"-README\"" +
                               " to display program help");
            displayUsage();
        }
        else if (args.length - offset > 8) { //if there are extraneous arguments
            System.err.println("Too many arguments. Add the argument \"-README\"" +
                               " to display program help");
            displayUsage();
        }
        else {
            if (airline != null && !airline.getName().equals(args[offset])) { //If the name of the airline in the file
                                                                              //does not match what is being supplied
                System.err.println("Filename is already associated with airline '" + airline.getName() + "'");
                displayUsage();
            }
            if (airline == null) { //if airline was not already created by the file read in
                airline = new Airline(args[offset]); //create a new one
            }
            airline.addFlight(parseArgs(args, offset+1)); //Add a flight from the arguments
            if (willPrint) { //if -print is given
                printAirline((Airline) airline);
            }
            if (dumper != null) { //if -textFile was given, write airline to specified file.
                try {
                    dumper.dump(airline);
                } catch (IOException e) { //For the event that the file could not be written to
                    System.err.println("ERROR WRITING TO FILE");
                    e.printStackTrace();
                    System.exit(3);
                }
            }
        }
        System.exit(0);
    }

    /**
     * Parses the list of command-line arguments. The offset determines which element of the array the flight
     * arguments start at (if -print was supplied or not). For each argument that the program requires, the
     * String will be checked if they are entered in the correct format. For example, the Flight number is
     * testes if it can be parsed as an int. The rest of the checks use regular expressions to make sure
     * each argument is a valid airport code, date and time. If all arguments are valid, the function will
     * return a new Flight object with the given arguments.
     * @param args Command-line arguments
     * @param offset Array element offset determined by optional -print argument
     * @return returns a new Flight
     */
    static Flight parseArgs(String[] args, int offset) {
        try { //Attempt to parse the flight number as an integer
            int e = Integer.parseInt(args[offset]);
        } catch (NumberFormatException e) {
            System.err.println("Argument `" + args[offset] + "': Flight number must be" +
                    " represented as an integer number");
            displayUsage();
        }
        if (!args[offset+1].matches(AIRPORT_CODE_PATTERN)) { //Departure airport code
            System.err.println("Argument `" + args[offset+1] + "': Airport codes must be" +
                    " in the format of Three (3) upper-case letters");
            displayUsage();
        }
        if (!args[offset+2].matches(DATE_PATTERN)) { //Departure date
            System.err.println("Argument `" + args[offset+2] + "': Date must be in the" +
                    " format mm/dd/yyyy and be a valid date");
            displayUsage();
        }
        if (!args[offset+3].matches(TIME_PATTERN)) { //Departure time
            System.err.println("Argument `" + args[offset+3] + "': Time must be in the" +
                    " format hh:mm 24-hour time");
            displayUsage();
        }
        if (!args[offset+4].matches(AIRPORT_CODE_PATTERN)) { //Arrival airport code
            System.err.println("Argument `" + args[offset+4] + "': Airport codes must be" +
                    " in the format of Three (3) upper-case letters");
            displayUsage();
        }
        if (!args[offset+5].matches(DATE_PATTERN)) { //Arrival date
            System.err.println("Argument `" + args[offset+5] + "': Date must be in the" +
                    " format mm/dd/yyyy and be a valid date");
            displayUsage();
        }
        if (!args[offset+6].matches(TIME_PATTERN)) { //Arrival time
            System.err.println("Argument `" + args[offset+6] + "': Time must be in the" +
                    " format hh:mm 24-hour time");
            displayUsage();
        }
        //If all tests pass, return a new flight
        return new Flight(Integer.parseInt(args[offset]), args[offset+1],
                args[offset+2] + " " + args[offset+3], args[offset+4],
                args[offset+5] + " " + args[offset+6]);
    }

    /**
     * Prints the Collection (ArrayList) of Flights within the given Airline argument.
     * @param airline
     *        The airline in which flights will be printed
     */
    static void printAirline(Airline airline) {
        Collection<Flight>flights = airline.getFlights();
        System.out.println("Flights of airline \"" + airline.getName() + "\":\n");
        for (Flight flight : flights) { //Flight flight in flights... say that 5 times fast
            System.out.println(flight.toString());
        }
    }

    /**
     * Displays the README for this program and exits program with code 0.
     */
    static void displayReadme() {
        System.out.println("*************************************************************\n" +
                           "edu.pdx.cs410J.rwerf2.Project2 \"Airline\" By Rob Werfelmann.\n" +
                           "Project2 is a small application which creates an Airline     \n" +
                           "object and adds an instance of a Flight to that Airline.     \n" +
                           "Currently it supports adding one Flight to one Airline which \n" +
                           "can printed to standard out and external files.              \n\n" +
                           "It supports the the following options:\n -print, -README," +
                           "-textFile\n\n" +
                           "-print will print out the flights of an added airline after  \n" +
                           "creating the airline object and adding the flight information.\n" +
                           "This includes printing out flights which were written to file\n" +
                           "previously.\n" +
                           "-README will print this message and exit, regardless if there\n" +
                           "are other arguments.                                        \n" +
                           "-textFile will take the next argument and use that as the   \n" +
                           "file name from which to read/write. If the file does not exist,\n" +
                           "it will be created. If the file does exist, then the Airline\n" +
                           "that is read from the file must match the Airline that is being\n" +
                           "added. If it matches, the new flight will be added to the file.\n" +
                           "Example Usage:\n java edu.pdx.cs410J.rwerf2.Project2 -print " +
                           "\"Alaska Airlines\" \\\n 101 PDX 7/4/2014 12:00 SEA 07/04/2014 12:40\n\n" +
                           " java edu.pdx.cs410J.rwerf2.Project2 -textFile united -print \\\n" +
                           " \"United Airlines\" 2453 LAX 12/12/2013 00:40 PDX 12/12/2013 2:40\n" +
                           "***************************************************************");
        System.exit(0);
    }

    /**
     * Displays the program usage in the case of a bad argument. Exits with code 1.
     */
    static void displayUsage() {
        System.out.println("\nusage: java edu.pdx.cs410J.rwerf2.Project2 [-print] [-README]" +
                           " [-textFile filename] name" +
                           " flightNumber src departTime dest arriveTime\n\n" +
                           "name - The name of the airline\n" +
                           "flightNumber - The flight number as an integer number\n" +
                           "src - Three-letter code of the departure airport\n" +
                           "departTime - Departure date and time (24-hour time)\n" +
                           "dest - Three-letter code of the arrival airport\n" +
                           "arriveTime - Arrival date and time (24-hour time)\n" +
                           "-textFile - Where to read/write the airline info\n" +
                           "-print - Prints the airline's flight list information\n" +
                           "-README - Prints the Readme of this program and exits\n\n" +
                           "Date and time must be entered in the format: mm/dd/yyyy hh:mm\n" +
                           "If using -textFile, the Airline being added in through the argument\n" +
                           "list must match the Airline in the external file, if it exists\n");
        System.exit(1);
    }
}
