package edu.pdx.cs410J.rwerf2;

import java.util.Collection;

/**
 * The main class for the CS410J airline Project
 * Contains main(), parseArgs(), printAirline(), displayReadme() and displayUsage()
 */
public class Project1 {
    //Regular Expression patterns for time and date was learned from mkyong at
    //http://www.mkyong.com/regular-expressions/how-to-validate-time-in-24-hours-format-with-regular-expression/
    //and
    //http://www.mkyong.com/regular-expressions/how-to-validate-date-with-regular-expression/
    static final String AIRPORT_CODE_PATTERN = "[A-Z]{3}";
    static final String TIME_PATTERN = "([01]?[0-9]|2[0-3]):[0-5][0-9]";
    static final String DATE_PATTERN = "^(0?[1-9]|1[012])[- /.](0?[1-9]|[12][0-9]|3[01])[- /.](19|20)\\d\\d$";

    /**
     * Creates an instance of only one Airline for now. Uses one Flight instance for adding to the Airline.
     * First checks if there are any arguments, and exits if there are none. Then checks the arguments for
     * -README and displays and exits the readme if present, regardless if there are any other arguments.
     * If -README is not present it will make sure that the correct number of arguments are supplied and
     * creates a new Airline and parses the arguments. If parseArgs() is successful, it will add the new
     * Flight to the Airline and display the the inserted Flight if the -print argument was supplied.
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        Airline airline;
        Flight flight;

        if (args.length == 0) { //check if there are any arguments at all
            System.err.println("Missing command line arguments. Add the argument \"-README\"" +
                               " to display program help");
            displayUsage();
        }

        for (String arg : args) { //check for -README argument
            if (arg.contains("-README")) {
                displayReadme();
            }
        }

        if (args.length < 8 || args.length < 9 && args[0].contains("-print")) { //if there are not enough arguments
            System.err.println("Missing command line arguments. Add the argument \"-README\"" +
                               " to display program help");
            displayUsage();
        }
        else if (args.length == 8 ) { //if the -print was not supplied
            airline = new Airline(args[0]);
            flight = parseArgs(args, 1);
            airline.addFlight(flight);
        }
        else if (args.length == 9 && args[0].contains("-print")) { //if -print is given
            airline = new Airline(args[1]);
            flight = parseArgs(args, 2);
            airline.addFlight(flight);
            printAirline(airline);
        }
        else { //There must be too many arguments at this point
            System.err.println("Too many Arguments. Add the argument \"-README\"" +
                               " to display program help");
            displayUsage();
        }
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
     * @param airline The airline in which flights will be printed
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
                           "edu.pdx.cs410J.rwerf2.Project1 \"Airline\" By Rob Werfelmann.\n" +
                           "Project1 is a small application which creates an Airline     \n" +
                           "object and adds an instance of a Flight to that Airline.     \n" +
                           "Currently it only supports adding one Flight to one Airline  \n" +
                           "and does not save any data.                                \n\n" +
                           "It supports the the following two options: -print and -README\n\n" +
                           "-print will print out the flights of an added airline after  \n" +
                           "creating the airline object and adding the flight information.\n\n" +
                           "-README will print this message and exit, regardless if there\n" +
                           "are other arguments.                                        \n\n" +
                           "Example Usage:\n java edu.pdx.cs410J.rwerf2.Project1 -print " +
                           "\"Alaska Airlines\" 101 PDX 7/4/2014 12:00 SEA 07/04/2014 12:40\n" +
                           "***************************************************************");
        System.exit(0);
    }

    /**
     * Displays the program usage in the case of a bad argument. Exits with code 1.
     */
    static void displayUsage() {
        System.out.println("\nusage: java edu.pdx.cs410J.rwerf2.Project1 [-print] [-README] name" +
                           " flightNumber src departTime dest arriveTime\n\n" +
                           "name - The name of the airline\n" +
                           "flightNumber - The flight number as an integer number\n" +
                           "src - Three-letter code of the departure airport\n" +
                           "departTime - Departure date and time (24-hour time)\n" +
                           "dest - Three-letter code of the arrival airport\n" +
                           "arriveTime - Arrival date and time (24-hour time)\n" +
                           "-print - Prints a description of the new flight\n" +
                           "-README - Prints the Readme of this program and exits\n\n" +
                           "Date and time must be entered in the format: mm/dd/yyyy hh:mm\n");
        System.exit(1);
    }
}
