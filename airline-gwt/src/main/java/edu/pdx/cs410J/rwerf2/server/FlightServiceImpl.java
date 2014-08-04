package edu.pdx.cs410J.rwerf2.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import edu.pdx.cs410J.rwerf2.client.Airline;
import edu.pdx.cs410J.rwerf2.client.Flight;
import edu.pdx.cs410J.rwerf2.client.FlightService;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The server-side implementation of the Airline service
 */
public class FlightServiceImpl extends RemoteServiceServlet implements FlightService
{
    static final HashMap<String, Airline> airlines = new HashMap<>();

    /**
     * Returns the list of Airlines
     * @return
     *      The list containing the airlines in a HashMap
     */
    public ArrayList<Airline> updateFlights() {
        ArrayList<Airline> list = new ArrayList<>();
        list.addAll(airlines.values());
        return list;
    }

    /**
     * Returns the list of Airlines after adding a new flight
     * @param name
     *      The name of the airline
     * @param flight
     *      The new flight being added to "name" Airline
     * @return
     *      The list containing the airlines in a HashMap
     */
    public ArrayList<Airline> updateFlights(String name, Flight flight)
    {
        ArrayList<Airline> list = new ArrayList<>();
        Airline airline = airlines.get(name);
        if (airline == null) {
            airline = new Airline(name);
        }
        airline.addFlight(flight);
        airlines.put(name, airline);
        list.addAll(airlines.values());
        return list;
    }
}
