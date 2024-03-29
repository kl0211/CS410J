package edu.pdx.cs410J.rwerf2;

import edu.pdx.cs410J.AbstractAirline;
import edu.pdx.cs410J.AirlineDumper;

import java.io.*;
import java.util.Collection;

/**
 * TextDumper class implements AirlineDumper
 */
public class TextDumper implements AirlineDumper {

    private String file;

    /**
     * Sets the private field 'file'
     * @param file
     *        The name of the file to be written to
     */
    TextDumper(String file) {
        this.file = file;
    }

    /**
     * Returns the filename string
     * @return
     *        the name of the file to be written to
     */
    public String getFileName() {
        return this.file;
    }

    /**
     * Dumps an airline to some destination (file).
     *
     * @param airline
     *        The airline being written to a destination
     *
     * @throws IOException
     *         Something went wrong while writing the airline
     */
    @Override
    public void dump(AbstractAirline airline) throws IOException {
        Writer outFile;
        try {
            outFile = new BufferedWriter(new OutputStreamWriter(
                      new FileOutputStream(file), "UTF-8"));

            outFile.write(airline.getName() + "\n"); //first line in the file gets the airline name
            Collection<Flight> flights = airline.getFlights();
            for (Flight flight : flights) { //for each flight in the Collection, write each variable line by line
                outFile.write(flight.getNumber() + "\n");
                outFile.write(flight.getSource() + "\n");
                outFile.write(flight.getDepartureString() + "\n");
                outFile.write(flight.getDestination() + "\n");
                outFile.write(flight.getArrivalString() + "\n");
            }
        }
        catch(IOException e) {
            throw new IOException("Error writing to file!");
        }
        outFile.close();
    }
}
