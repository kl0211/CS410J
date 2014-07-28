package edu.pdx.cs410J.rwerf2;

import edu.pdx.cs410J.AirportNames;
import edu.pdx.cs410J.web.HttpRequestHelper;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * The main class that parses the command line and communicates with the
 * Airline server using REST.
 */
public class Project4 {

    static final String DATE_PATTERN = "^(0?[1-9]|1[012])[- /.](0?[1-9]|[12][0-9]|3[01])[- /.](19|20)\\d\\d$";

    /**
     * Main Function for Project4. Parses options and arguments. Makes sure everything is good to
     * send data to the server and prints out the responses.
     * @param args
     *      Command-line Arguments
     */
    public static void main(String... args) {
        String hostName = null, portString = null;
        boolean willPrint = false, willSearch = false;
        AirlineRestClient client;
        ArrayList<String> argList;      //for converting args to ArrayList
        HttpRequestHelper.Response response;

        if (args.length == 0) { //check if there are any arguments at all
            displayUsage("Missing command line arguments. Add the argument \"-README\"" +
                    " to display program help");
        }

        for (String arg: args) { //check for -README argument
            if (arg.contains("-README")) { //and if supplied, print README and exit
                displayReadme();
            }
        }

        argList = new ArrayList<>(Arrays.asList(args)); //convert args to ArrayList for removing options
        for (int i = 0; i < argList.size(); ++i) { //check for other options supplied
            if (argList.get(i).equals("-host")) {
                try {
                    if (argList.get(i + 1).startsWith("-") && !argList.get(i + 1).endsWith("-"))
                        throw new IndexOutOfBoundsException();
                    hostName = argList.get(i + 1);
                }
                catch (IndexOutOfBoundsException e) {
                    displayUsage("Missing host name after -host argument");
                }
                argList.remove("-host");   //remove both option
                argList.remove(hostName);  //and hostname
                --i;
            }
            else if (argList.get(i).equals("-port")) {
                try {
                    if (argList.get(i + 1).startsWith("-") && !argList.get(i + 1).endsWith("-"))
                        throw new IndexOutOfBoundsException();
                    portString = argList.get(i + 1);
                    try {
                        Integer.parseInt(portString);
                    }
                    catch (NumberFormatException ex) {
                        displayUsage("Argument \"" + portString + "\": Port must be an integer");
                    }
                }
                catch (IndexOutOfBoundsException e) {
                    displayUsage("Missing port after -port argument");
                }
                argList.remove("-port");    //remove both option
                argList.remove(portString); //and port
                --i;
            }
            else if (argList.get(i).equals("-search")) {
                willSearch = true;
                argList.remove("-search");
                --i;
            }
            else if (argList.get(i).equals("-print")) {
                willPrint = true;
                argList.remove("-print"); //remove option from arg list
                --i; //decrement i for offset of removal
            }

        }
        args = argList.toArray(new String[argList.size()]); //convert arg list back to a String array
        if (hostName == null && portString == null) {
            hostName = "localhost";
            portString = "8080";
        }
        else if (hostName != null && portString == null)
            displayUsage("port must be specified when specifying hostname");
        else if (hostName == null)
            displayUsage("hostname must be specified when specifying port");

        client = new AirlineRestClient(hostName, Integer.parseInt(portString));

        if (willSearch && args.length == 3) { //If search option was supplied
            if (!isValidAirportCode(args[1]))
                displayUsage("Argument `" + args[1] + "': Airport code is not valid");
            if (!isValidAirportCode(args[2]))
                displayUsage("Argument `" + args[2] + "': Airport code is not valid");
            try {
                response = client.searchFlights(args[0], args[1].toUpperCase(), args[2].toUpperCase());
                System.out.println(response.getContent());
            } catch (IOException ex) {
                displayUsage("Connection Error with server " + hostName + ":" + portString + ": " + ex);
            }
            System.exit(0);
        }
        else if (willSearch) {
            displayUsage("only name, src and dest must be supplied when -search is supplied");
        }

        if (args.length < 10) { //if there are not enough arguments
            displayUsage("Missing command line arguments. Add the argument \"-README\"" +
                    " to display program help");
        }
        else if (args.length > 10) { //if there are extraneous arguments
            displayUsage("Too many arguments. Add the argument \"-README\"" +
                    " to display program help");
        }
        else {
            validateArgs(args); //make sure all arguments are valid for creating a new flight
            try {
                response = client.addFlight(args);
                System.out.println(response.getContent());
            } catch (IOException ex) {
                displayUsage("Connection Error with server " + hostName + ":" + portString + ": " + ex);
            }

            if (willPrint) {
                try {
                    response = client.getFlights(args[0]);
                    System.out.println(response.getContent());
                } catch (IOException ex) {
                    displayUsage("Connection Error with server " + hostName + ":" + portString + ": " + ex);
                }
            }
        }
        System.exit(0);
    }

    /**
     * Validates each argument before creating a new Flight. Each flight is created from 10 arguments.
     * Each argument is passed to a validator function to make sure each argument is in the correct format.
     * For example, argument 1 should be a string that can be converted to an integer and argument 5 should
     * be a string that represents am or pm. If any argument fails to pass these checks, the program will error
     * and display the usage and exit with code 1.
     *
     * @param args
     *        Command-line arguments
     */
    static void validateArgs(String[] args) {
        if (!isValidFlightNumber(args[1])) {
            displayUsage("Argument `" + args[1] + "': Flight number must be" +
                    " represented as an integer number");
        }
        if (!isValidAirportCode(args[2])) { //Departure airport code
            displayUsage("Argument `" + args[2] + "': Airport code is not valid");
        }
        if (!isValidDate(args[3])) { //Departure date
            displayUsage("Argument `" + args[3] + "': Date must be in the" +
                    " format mm/dd/yyyy and be a valid date");
        }
        if (!isValidTime(args[4])) { //Departure time
            displayUsage("Argument `" + args[4] + "': Time must be in the" +
                    " format hh:mm 12-hour time");
        }
        if (!isValidMeridian(args[5])) { //Departure time
            displayUsage("Argument `" + args[5] + "': Must be either am or pm");
        }
        if (!isValidAirportCode(args[6])) { //Arrival airport code
            displayUsage("Argument `" + args[6] + "': Airport code is not valid");
        }
        if (!isValidDate(args[7])){ //Arrival date
            displayUsage("Argument `" + args[7] + "': Date must be in the" +
                    " format mm/dd/yyyy and be a valid date");
        }
        if (!isValidTime(args[8])) { //Arrival time
            displayUsage("Argument `" + args[8] + "': Time must be in the" +
                    " format hh:mm 12-hour time");
        }
        if (!isValidMeridian(args[9])){ //Departure time
            displayUsage("Argument `" + args[9] + "': Must be either am or pm");
        }
    }

    /**
     * Checks whether string can be converted to an integer
     * @param str
     *      string to check
     * @return
     *      true if it can converted, otherwise false
     */
    static boolean isValidFlightNumber(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Checks whether string is a valid airport code
     * @param str
     *      string to check
     * @return
     *      true if valid, otherwise false
     */
    static boolean isValidAirportCode(String str) {
        return AirportNames.getName(str.toUpperCase()) != null;
    }

    /**
     * Checks whether string is a valid date
     * @param str
     *      string to check
     * @return
     *      true if valid, otherwise false
     */
    static boolean isValidDate(String str) {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        dateFormat.setLenient(false);
        if (!str.matches(DATE_PATTERN))
            return false;
        try {
            dateFormat.parse(str);
            return true;
        }
        catch (ParseException e) {
            return false;
        }
    }

    /**
     * Checks whether string is a valid 12-hour time
     * @param str
     *      string to check
     * @return
     *      true if valid, otherwise false
     */
    static boolean isValidTime(String str) {
        DateFormat dateFormat = new SimpleDateFormat("hh:mm");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(str);
            return true;
        }
        catch (ParseException e) {
            return false;
        }
    }

    /**
     * Checks whether string is am or pm
     * @param str
     *      string to check
     * @return
     *      true if valid, otherwise false
     */
    static boolean isValidMeridian(String str) {
        DateFormat dateFormat = new SimpleDateFormat("a");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(str);
            return true;
        }
        catch (ParseException e) {
            return false;
        }
    }

    /**
     * Displays the README for this program and exits program with code 2.
     */
    static void displayReadme() {
        System.out.println("*************************************************************\n" +
                "edu.pdx.cs410J.rwerf2.Project4 \"Airline\" By Rob Werfelmann.           \n" +
                "Project4 is an application which stores Airlines containing Flights     \n" +
                "on a server. It supports specifying host names and ports.               \n" +
                "It supports the the following options:\n -print, -README, " +
                "-host, -port, -search                                                 \n\n" +
                "-print will print out the flights of an added airline after             \n" +
                "adding a flight. This includes printing out flights which were          \n" +
                "written to the server.                                                  \n" +
                "-README will print this message and exit, regardless if there           \n" +
                "are other arguments.                                                    \n" +
                "-host and -port specify the host name on which the server is located    \n" +
                "and which port it is listening. If host and port are not specified,     \n" +
                "then host and port will default to \"localhost:8080\"                   \n" +
                "-search fetches all flights from airline \"name\" that Originate from   \n" +
                "airport \"src\" and arrive at airport \"dest\" that are on the server.\n\n" +
                "Example Usage:\n java edu.pdx.cs410J.rwerf2.Project4 -print " +
                "\"Alaska Airlines\" \\\n 101 PDX 7/4/2014 12:00 pm SEA 07/04/2014 12:40 pm\n\n" +
                " java edu.pdx.cs410J.rwerf2.Project4 -host cs.pdx.edu -port 2020 \\     \n" +
                " united \"United Airlines\" 2453 LAX 12/12/2013 12:40 am PDX \\         \n" +
                " 12/12/2013 2:40 am\n\n" +
                " java edu.pdx.cs410J.rwerf2.Project4 -search \"Alaska Airlines\" PDX SEA\n" +
                "***************************************************************");
        System.exit(2);
    }

    /**
     * Displays the program usage in the case of a bad argument. Exits with code 1.
     */
    static void displayUsage(String errorMessage) {
        System.err.println(errorMessage);
        System.out.println("\nusage:\tjava edu.pdx.cs410J.rwerf2.Project4 [-README] [-host hostname] [-port port]\n" +
                "\t\t\t\t\t\t[-print] name flightNumber src departTime dest arriveTime\n" +
                "\t\tjava edu.pdx.cs410J.rwerf2.Project4 -search name src dest\n\n" +
                "name - The name of the airline\n" +
                "flightNumber - The flight number as an integer number\n" +
                "src - Three-letter code of the departure airport\n" +
                "departTime - Departure date and time and meridian (12-hour time)\n" +
                "dest - Three-letter code of the arrival airport\n" +
                "arriveTime - Arrival date and time and meridian (12-hour time)\n\n" +
                "options:\n" +
                "-host - Host computer on which the server runs\n" +
                "-port - Port on which server is listening\n" +
                "-search - Search for flights using name, src and dest\n" +
                "-print - Prints the airline's flight list information currently on the server\n" +
                "-README - Prints the Readme of this program and exits\n\n" +
                "Date and time must be entered in the format: mm/dd/yyyy hh:mm am/pm\n" +
                "If using -host or -port, both must be specified\n" +
                "if using -search, only name, src and dest should be supplied\n");
        System.exit(1);
    }
}