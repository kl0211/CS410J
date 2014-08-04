package edu.pdx.cs410J.rwerf2.client;

import edu.pdx.cs410J.AbstractFlight;

import java.util.Date;

/**
* Flight class inherits from AbstractFlight
*/
public class Flight extends AbstractFlight implements Comparable {
    private int flightNumber;
    private String src;
    private Date departTime;
    private String dest;
    private Date arriveTime;

    /**
     * Default constructor
     */
    public Flight() {
        this.flightNumber = 0;
        this.src = null;
        this.departTime = null;
        this.dest = null;
        this.arriveTime = null;
    }

    /**
     * Sets the Flight variables to the given parameters.
     * @param flightNumber Flight Number
     * @param src Airport code that flight is departing from
     * @param departTime Time and date the flight is departing
     * @param dest Airport code that flight is arriving to
     * @param arriveTime Time and date the flight is arriving
     */
    public Flight(int flightNumber, String src, Date departTime,
                  String dest, Date arriveTime) {
        this.flightNumber = flightNumber;
        this.src = src;
        this.departTime = departTime;
        this.dest = dest;
        this.arriveTime = arriveTime;
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

    /**
     * returns the departing date and time
     * @return
     *        returns the departure time in DATE format
     */
    @Override
    public Date getDeparture() {
        return departTime;
    }

    /**
     * returns the departing date and time String
     * @return returns the departing date and time
     */
    public String getDepartureString() {
        return departTime.toString();
    }

    /**
     * returns the arriving airport code String
     * @return returns the arriving airport code
     */
    public String getDestination() {
        return dest;
    }

    /**
     * returns the arriving date and time
     * @return
     *        returns the arriving time in DATE format
     */
    @Override
    public Date getArrival() {
        return arriveTime;
    }

    /**
     * returns the arriving date and time String
     * @return returns the arriving date and time
     */
    public String getArrivalString() {
        return arriveTime.toString();
    }

    /**
     * Compares two Flight objects. First compares the src Strings,
     * if they are equal, return the result of comparing the departure times.
     * @param o The Flight object to compare with
     * @return
     *        returns 0 if both are equal, positive int if this is greater
     *        and negative int if o is greater
     */
    @Override
    public int compareTo(Object o) {
        int srcAirport = this.src.compareTo(((Flight) o).src);
        return srcAirport == 0 ? this.departTime.compareTo(((Flight) o).departTime) : srcAirport;
    }
}