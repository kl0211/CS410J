package edu.pdx.cs410J.rwerf2;

import edu.pdx.cs410J.AbstractAirline;
import edu.pdx.cs410J.AbstractFlight;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Airline class inherits from AbstractAirline
 */
public class Airline extends AbstractAirline {
    private String name; //Name of the airline
    private ArrayList<Flight> flights; //List of flights from the airline

    /**
     * Initializes name of airline with the given string and initializes empty list of flights.
     * @param name Name of the Airline
     */
    public Airline(String name) {
        this.name = name;
        flights = new ArrayList<>();
    }

    /**
     * Returns the name of the airline
     * @return returns the name
     */
    public String getName() {
        return name;
    }

    /**
     * Adds the given flight to the list of flights
     * @param flight add a flight to the list of Flight
     */
    public void addFlight(AbstractFlight flight) {
        flights.add((Flight) flight);
        Collections.sort(flights);
    }

    /**
     * returns the list of flights as a Collection
     * @return returns the list of Flight as a Collection
     */
    public Collection getFlights() {
        return flights;
    }
}
