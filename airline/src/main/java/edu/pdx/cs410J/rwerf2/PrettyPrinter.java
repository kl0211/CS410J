package edu.pdx.cs410J.rwerf2;

import edu.pdx.cs410J.AbstractAirline;
import edu.pdx.cs410J.AirlineDumper;
import edu.pdx.cs410J.AirportNames;

import java.io.*;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Locale;

/**
 * TextDumper class implements AirlineDumper
 */
public class PrettyPrinter implements AirlineDumper {

    private String file;
    private DateFormat format = DateFormat.getDateTimeInstance(DateFormat.LONG,
            DateFormat.LONG, Locale.US);

    /**
     * Sets the private field 'file'
     * @param file
     *        The name of the file to be written to
     */
    PrettyPrinter(String file) {
        this.file = file;
    }

    /**
     * Dumps an airline to some destination (file) or stdout if filename is "-".
     *
     * @param airline
     *        The airline being written to a destination
     *
     * @throws java.io.IOException
     *         Something went wrong while writing the airline
     */
    @Override
    public void dump(AbstractAirline airline) throws IOException {
        Writer outFile;
        long duration;  //for calculating the time difference between source and destination
        int i = 1;      //for numbering each flight in output

        try {
            //if target is "-", then print to stdout, otherwise, write to a file.
            outFile = file.equals("-") ? new BufferedWriter(new OutputStreamWriter(System.out)) :
                      new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));

            outFile.write("*********************** Flights of " + airline.getName() + " ***********************\n");
            Collection<Flight> flights = airline.getFlights();
            for (Flight flight : flights) { //for each flight in the Collection, write each variable line by line
                duration = (flight.getArrival().getTime() - flight.getDeparture().getTime()) / (60 * 1000);
                outFile.write(i + ". Flight Number " +flight.getNumber() + " Leaves ");
                outFile.write(AirportNames.getName(flight.getSource()) + " on ");
                outFile.write(format.format(flight.getDeparture()) + "\n   and Arrives at ");
                outFile.write(AirportNames.getName(flight.getDestination()) + " on ");
                outFile.write(format.format(flight.getArrival()) + "\n");
                outFile.write("   Flight duration is " + duration + " minutes.\n\n");
                ++i;
            }
            outFile.write("***********************************************************************\n");
        }
        catch(IOException e) {
            throw new IOException("Error writing to file!");
        }
        outFile.close();
    }
}
