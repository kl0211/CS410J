package edu.pdx.cs410J.rwerf2;

import edu.pdx.cs410J.AbstractFlight;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Flight class inherits from AbstractFlight
 */
public class Flight extends AbstractFlight {
    private int flightNumber;
    private String src;
    private Date departTime;
    private String dest;
    private Date arriveTime;
    private DateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm a");

    /**
     * Sets the Flight variables to the given parameters.
     * @param flightNumber Flight Number
     * @param src Airport code that flight is departing from 
     * @param departTime Time and date the flight is departing
     * @param dest Airport code that flight is arriving to
     * @param arriveTime Time and date the flight is arriving
     */
    public Flight(int flightNumber, String src, String departTime,
                  String dest, String arriveTime) {
        format.setLenient(false);
        this.flightNumber = flightNumber;
        this.src = src;
        try {
            this.departTime = format.parse(departTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.dest = dest;
        try {
            this.arriveTime = format.parse(arriveTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * returns the flight number integer
     * @return returns the flight number
     */
    public int getNumber() {
        return flightNumber;
    }

    /**
     * returns the departing airport code String
     * @return returns the departing airport code
     */
    public String getSource() {
        return src;
    }

    @Override
    public Date getDeparture() {
        return departTime;
    }

    /**
     * returns the departing date and time String
     * @return returns the departing date and time
     */
    public String getDepartureString() {
        return format.format(departTime);
    }

    /**
     * returns the arriving airport code String
     * @return returns the arriving airport code
     */
    public String getDestination() {
        return dest;
    }

    @Override
    public Date getArrival() {
        return arriveTime;
    }

    /**
     * returns the arriving date and time String
     * @return returns the arriving date and time
     */
    public String getArrivalString() {

        return format.format(arriveTime);
    }
}
