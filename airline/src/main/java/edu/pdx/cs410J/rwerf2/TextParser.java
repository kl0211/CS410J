package edu.pdx.cs410J.rwerf2;

import edu.pdx.cs410J.AbstractAirline;
import edu.pdx.cs410J.AirlineParser;
import edu.pdx.cs410J.AirportNames;
import edu.pdx.cs410J.ParserException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * TextParser class implements AirlineParser
 */
public class TextParser implements AirlineParser{

    private String file;

    /**
     * Sets the private field 'file'
     * @param file
     *        The name of the file to be read
     */
    TextParser(String file) {
        this.file = file;
    }

    /**
     * Parses some source (file) and returns an airline.
     *
     * @throws ParserException
     *         If the source is malformatted.
     */
    @Override
    public AbstractAirline parse() throws ParserException {
        BufferedReader br;
        Airline airline;
        int parsedFlightNumber;
        String flightNumber, src, departTime, dest, arriveTime; //String destinations for BufferedReader
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT,
                                                               Locale.US); //Date Format for date and time
        dateFormat.setLenient(false); //Follow format exactly

        try {
            br = new BufferedReader(new FileReader(file));
            airline = new Airline(br.readLine()); //read in Airline name
            if (airline.getName() == null) { //check if file is empty, airline's name will be empty
                throw new ParserException("FILE READ ERROR: '" + file + "' IS AN EMPTY FILE");
            }
            while ((flightNumber = br.readLine()) != null) { // while there are flights
                try {
                    parsedFlightNumber = Integer.parseInt(flightNumber); //try to parse flight number as an int
                } catch (NumberFormatException e) {
                    throw new ParserException("FILE READ ERROR: '" + flightNumber + "' CANNOT BE CONVERTED TO A NUMBER");
                } catch (NullPointerException e) {
                    throw new ParserException("FILE READ ERROR: MISSING FLIGHT NUMBER");
                }
                src = br.readLine(); //read in departing airport code
                if (src == null) {
                    throw new ParserException("FILE READ ERROR: MISSING DEPARTURE AIRPORT CODE");
                }
                if (AirportNames.getName(src.toUpperCase()) == null) {
                    throw new ParserException("FILE READ ERROR: '" + src + "' IS NOT A VALID AIRPORT CODE");
                }
                departTime = br.readLine(); //read in departing date and time
                if (departTime == null) {
                    throw new ParserException("FILE READ ERROR: MISSING DEPARTURE DATE AND TIME");
                }
                try {
                    dateFormat.parse(departTime);
                } catch (ParseException e) {
                    throw new ParserException("FILE READ ERROR: '" + departTime + "' IS NOT A VALID DATE AND TIME");
                }
                dest = br.readLine(); //read in arriving airport code
                if (dest == null) {
                    throw new ParserException("FILE READ ERROR: MISSING ARRIVAL AIRPORT CODE");
                }
                if (AirportNames.getName(dest) == null) {
                    throw new ParserException("FILE READ ERROR: '" + dest + "' IS NOT A VALID AIRPORT CODE");
                }
                arriveTime = br.readLine(); //read in arriving date and time
                if (arriveTime == null) {
                    throw new ParserException("FILE READ ERROR: MISSING ARRIVAL TIME");
                }
                try {
                    dateFormat.parse(arriveTime);
                } catch (ParseException e) {
                    throw new ParserException("FILE READ ERROR: '" + arriveTime + "' IS NOT A VALID DATE AND TIME");
                }
                //arguments are valid, so add them to the airline
                airline.addFlight(new Flight(parsedFlightNumber, src, departTime, dest, arriveTime));
            }
        }
        catch (IOException e) { //catch if br can't open file
            throw new ParserException("File '" + file + "' not found!", e);
        }

        return airline;
    }
}
