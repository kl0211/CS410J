package edu.pdx.cs410J.rwerf2.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import edu.pdx.cs410J.AirportNames;
import edu.pdx.cs410J.rwerf2.client.Airline;
import edu.pdx.cs410J.rwerf2.client.Flight;
import edu.pdx.cs410J.rwerf2.client.FlightService;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;

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
    public ArrayList<Airline> updateFlights(String name, Flight flight) {
        ArrayList<Airline> list = new ArrayList<>();
        Airline airline = airlines.get(name);
        if (airline == null) {
            airline = new Airline(name);
        }
        for (Flight flights : airline.getFlights()) {
            if (airline.getName().equals(name) && flight.getNumber() == flights.getNumber()) {
                return null;
            }
        }
        airline.addFlight(flight);
        airlines.put(name, airline);
        list.addAll(airlines.values());
        return list;
    }

    public String searchFlights(String name, String src, String dest) {
        Airline airline = airlines.get(name);
        if (airline == null)
            return "Airline \"" + name + "\"does not exist";
        else
            return prettyPrint(airline, src, dest);
    }

    /**
     * Prints all flights from the given airline which originate at src and arrive at dest to a String
     * @param airline
     *      The airline which flights to print out
     * @param src
     *      The departing airport code
     * @param dest
     *      The arriving airport code
     * @return
     *      The String containing the pretty printed flights
     */
    public String prettyPrint(Airline airline, String src, String dest) {
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.LONG,
                DateFormat.LONG, Locale.US);
        String str = "";
        long duration;  //for calculating the time difference between source and destination
        int i = 1;      //for numbering each flight in output

        str += "************** Flights of " + airline.getName() + " From " + src + " to " + dest + " **************\n";
        Collection<Flight> flights = airline.getFlights();
        for (Flight flight : flights) { //for each flight in the Collection, write each variable line by line
            if (flight.getSource().equals(src) && flight.getDestination().equals(dest)) {
                duration = (flight.getArrival().getTime() - flight.getDeparture().getTime()) / (60 * 1000);
                str += i + ". Flight Number " + flight.getNumber() + " Leaves ";
                str += AirportNames.getName(flight.getSource()) + " on ";
                str += format.format(flight.getDeparture()) + "\n   and Arrives at ";
                str += AirportNames.getName(flight.getDestination()) + " on ";
                str += format.format(flight.getArrival()) + "\n";
                str += "   Flight duration is " + duration + " minutes.\n\n";
                ++i;
            }
        }
        if (i == 1) str += "No flights found\n";
        str += "************************************************************************\n";

        return str;
    }
}
