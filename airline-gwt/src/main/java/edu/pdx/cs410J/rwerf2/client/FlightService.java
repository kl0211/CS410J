package edu.pdx.cs410J.rwerf2.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import java.util.ArrayList;

/**
 * A GWT remote service that returns a list of Airline
 */
@RemoteServiceRelativePath("flights")
public interface FlightService extends RemoteService {

    /**
     * Returns the list of Airlines currently on the server
     * @return
     *      The list of airlines currently on the server
     */
    public ArrayList<Airline> updateFlights();

    /**
     * Returns the list of Airlines after adding a new flight
     * @param name
     *      The name of the airline
     * @param flight
     *      The new flight being added to "name" Airline
     * @return
     *      The list of airlines currently on the server
     */
    public ArrayList<Airline> updateFlights(String name, Flight flight);

    /**
     * Returns a string containing the PrettyPrinted flights that are being searched
     * @param name
     *      The name of the airline
     * @param src
     *      The name of the departing airport
     * @param dest
     *      The name of the arriving airport
     * @return
     *      The string with the PrettyPrinted flights
     */
    public String searchFlights(String name, String src, String dest);
}
