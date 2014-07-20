package edu.pdx.cs410J.rwerf2;

import edu.pdx.cs410J.AbstractAirline;
import edu.pdx.cs410J.ParserException;
import edu.pdx.cs410J.AirportNames;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * The main class for the CS410J airline Project
 * Contains main(), parseArgs(), printAirline(), displayReadme() and displayUsage()
 */
public class Project3 {

    /**
     * Creates an instance of one Airline. Depending on the options supplied, it can print to standard out
     * and/or read/write to a text file.
     * First checks if there are any arguments, and exits if there are none. Then checks the arguments for
     * -README and displays and exits the readme if present, regardless if there are any other arguments.
     * if -README is not present, -print, -textFile and -pretty are then checked. If -textFile is present, it will
     * attempt to read from the file specified and check if the airline being entered in matches the Airline
     * the file is associated with or will create a new file. After options have been discovered, the function
     * will then check for correct number of arguments and then parse them into either the Airline that
     * was read from the file, or add them to a new Airline. If -pretty is supplied, a new PrettyPrinter object
     * is created for later writing out to file or standard out. The airline, with its flights will then be
     * printed and/or written to file or the program will just simply exit.
     *
     * @param args
     *        Command-line arguments
     */
    public static void main(String[] args) {
        AbstractAirline airline = null; //airline will either be new or from parsed text file
        TextParser parser;              //for reading in from file
        TextDumper dumper = null;       //for possible writing out
        PrettyPrinter printer = null;   //for printing the airline in a nice format to stdout or file
        boolean willPrint = false;      //for printing to standard out
        ArrayList<String> argList;      //for converting args to ArrayList

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

        argList = new ArrayList<>(Arrays.asList(args)); //convert args to ArrayList for removing options
        for (int i = 0; i < argList.size(); ++i) { //check for other options supplied
            if (argList.get(i).contains("-print")) {
                willPrint = true;
                argList.remove("-print"); //remove option from arg list
                --i; //decrement i for offset of removal
            }
            else if (argList.get(i).contains("-textFile")) {
                String fileName = null; //for the name of the file
                try {
                    if (argList.get(i + 1).startsWith("-")) //if the fileName is actually just another option
                        throw new IndexOutOfBoundsException();
                    fileName = argList.get(i + 1);     //set the next element in the list as the file name
                    parser = new TextParser(fileName); //set TextParser with name from next argument
                    airline = parser.parse();          //parse file and return airline
                }
                catch (ParserException e) { //catch if filename does not exist or is missing flight information
                    if (e.getMessage().contains("File '" + fileName + "' not found!")) { //if file is missing,
                        File file = new File(fileName);                                  //create a new one
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
                catch (IndexOutOfBoundsException e) { //for the case that -textFile is the last argument
                    System.err.println("Missing file name after -textFile argument");
                    displayUsage();
                }
                argList.remove("-textFile"); //remove both option
                argList.remove(fileName);    //and filename
                --i;
                dumper = new TextDumper(fileName); //create new TextDumper for later writing out
            }
            else if (argList.get(i).contains("-pretty")) {
                try {
                    if (argList.get(i + 1).startsWith("-")) //if the fileName is actually just another option
                        throw new IndexOutOfBoundsException();
                    String fileName = argList.get(i + 1);
                    printer = new PrettyPrinter(fileName);
                    argList.remove("-pretty");
                    argList.remove(fileName);
                    --i;
                } catch (IndexOutOfBoundsException e) {
                    System.err.println("Missing file name after -pretty argument");
                    displayUsage();
                }
            }
        }
        args = argList.toArray(new String[argList.size()]); //convert arg list back to a String array
        if (args.length < 10) { //if there are not enough arguments
            System.err.println("Missing command line arguments. Add the argument \"-README\"" +
                               " to display program help");
            displayUsage();
        }
        else if (args.length > 10) { //if there are extraneous arguments
            System.err.println("Too many arguments. Add the argument \"-README\"" +
                               " to display program help");
            displayUsage();
        }
        else {
            if (airline != null && !airline.getName().equals(args[0])) { //If the name of the airline in the file
                                                                              //does not match what is being supplied
                System.err.println("Filename is already associated with airline '" + airline.getName() + "'");
                displayUsage();
            }
            if (airline == null) { //if airline was not already created by the file read in
                airline = new Airline(args[0]); //create a new one
            }
            airline.addFlight(parseArgs(args)); //Add a flight from the arguments
            if (willPrint) { //if -print is given
                printAirline((Airline) airline);
            }
            if (printer != null) {
                try {
                    printer.dump(airline);
                } catch (IOException e) {
                    System.err.println("ERROR WRITING TO FILE");
                    e.printStackTrace();
                    System.exit(3);
                }
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
     * arguments start at (depending on which options were used and how many). For each argument that the
     * program requires, the String will be checked if they are entered in the correct format. For example,
     * the Flight number is testes if it can be parsed as an int. The Airport code is checked by looking in the
     * AirportNames map. The Date and Time are checked by attempting to convert them to a Date format. If all
     * arguments are valid, the function will return a new Flight object with the given arguments.
     *
     * @param args
     *        Command-line arguments
     * @return
     *        returns a new Flight
     */
    static Flight parseArgs(String[] args) {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        DateFormat timeFormat = new SimpleDateFormat("hh:mm");
        DateFormat AMPMFormat = new SimpleDateFormat("a");
        dateFormat.setLenient(false); //use strict formatting
        timeFormat.setLenient(false);
        AMPMFormat.setLenient(false);

        try { //Attempt to parse the flight number as an integer
            Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Argument `" + args[1] + "': Flight number must be" +
                               " represented as an integer number");
            displayUsage();
        }
        if (AirportNames.getName(args[2].toUpperCase()) == null) { //Departure airport code
            System.err.println("Argument `" + args[2] + "': Airport code is not valid");
            displayUsage();
        }
        try { //Departure date
            dateFormat.parse(args[3]);
        }
        catch (ParseException e) {
            System.err.println("Argument `" + args[3] + "': Date must be in the" +
                    " format mm/dd/yyyy and be a valid date");
            displayUsage();
        }
        try { //Departure time
            timeFormat.parse(args[4]);
        }
        catch (ParseException e) {
            System.err.println("Argument `" + args[4] + "': Time must be in the" +
                    " format hh:mm 12-hour time");
            displayUsage();
        }
        try { //Departure time
            AMPMFormat.parse(args[5]);
        }
        catch (ParseException e) {
            System.err.println("Argument `" + args[5] + "': Must be either am or pm");
            displayUsage();
        }
        if (AirportNames.getName(args[6].toUpperCase()) == null) { //Arrival airport code
            System.err.println("Argument `" + args[6] + "': Airport code is not valid");
            displayUsage();
        }
        try { //Arrival date
            dateFormat.parse(args[7]);
        }
        catch (ParseException e) {
            System.err.println("Argument `" + args[7] + "': Date must be in the" +
                    " format mm/dd/yyyy and be a valid date");
            displayUsage();
        }
        try { //Arrival time
            timeFormat.parse(args[8]);
        }
        catch (ParseException e) {
            System.err.println("Argument `" + args[8] + "': Time must be in the" +
                    " format hh:mm 12-hour time");
            displayUsage();
        }
        try { //Departure time
            AMPMFormat.parse(args[9]);
        }
        catch (ParseException e) {
            System.err.println("Argument `" + args[9] + "': Must be either am or pm");
            displayUsage();
        }
        //If all tests pass, return a new flight
        return new Flight(Integer.parseInt(args[1]), args[2],
                          args[3] + " " + args[4] + " " + args[5], args[6],
                          args[7] + " " + args[8] + " " + args[9]);
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
                           "edu.pdx.cs410J.rwerf2.Project3 \"Airline\" By Rob Werfelmann.\n" +
                           "Project3 is a small application which creates an Airline     \n" +
                           "object and adds an instance of a Flight to that Airline.     \n" +
                           "Currently it supports adding one Flight to one Airline which \n" +
                           "can printed to standard out and external files.              \n\n" +
                           "It supports the the following options:\n -print, -README," +
                           "-textFile, -pretty\n\n" +
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
                           "-pretty will print the added airline's flight information to \n" +
                           "a file or standard out in a more aesthetically pleasing format\n" +
                           "Example Usage:\n java edu.pdx.cs410J.rwerf2.Project3 -print " +
                           "\"Alaska Airlines\" \\\n 101 PDX 7/4/2014 12:00 pm SEA 07/04/2014 12:40 pm\n\n" +
                           " java edu.pdx.cs410J.rwerf2.Project3 -textFile united -print \\\n" +
                           " \"United Airlines\" 2453 LAX 12/12/2013 12:40 am PDX 12/12/2013 2:40 am\n\n" +
                           " java edu.pdx.cs410J.rwerf2.Project3 -pretty - -textFile alaska \\\n" +
                           " \"Alaska Airlines\" 463 DCA 2/28/2014 11:50 pm JFK 3/1/2014 2:20 am\n" +
                           "***************************************************************");
        System.exit(0);
    }

    /**
     * Displays the program usage in the case of a bad argument. Exits with code 1.
     */
    static void displayUsage() {
        System.out.println("\nusage: java edu.pdx.cs410J.rwerf2.Project3 [-print] [-README]" +
                           " [-textFile filename] [-pretty -|filename] name" +
                           " flightNumber src departTime dest arriveTime\n\n" +
                           "name - The name of the airline\n" +
                           "flightNumber - The flight number as an integer number\n" +
                           "src - Three-letter code of the departure airport\n" +
                           "departTime - Departure date and time and meridian (12-hour time)\n" +
                           "dest - Three-letter code of the arrival airport\n" +
                           "arriveTime - Arrival date and time and meridian (12-hour time)\n" +
                           "-pretty - Where to write the Airline's flights to\n" +
                           "          in a nice format (stdout with - for filename)\n" +
                           "-textFile - Where to read/write the airline info\n" +
                           "-print - Prints the airline's flight list information\n" +
                           "-README - Prints the Readme of this program and exits\n\n" +
                           "Date and time must be entered in the format: mm/dd/yyyy hh:mm am/pm\n" +
                           "If using -textFile, the Airline being added in through the argument\n" +
                           "list must match the Airline in the external file, if it exists\n");
        System.exit(1);
    }
}
