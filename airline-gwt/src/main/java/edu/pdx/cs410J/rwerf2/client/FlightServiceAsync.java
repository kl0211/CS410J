package edu.pdx.cs410J.rwerf2.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.ArrayList;

/**
 * The client-side interface to the updateFlights service
 */
public interface FlightServiceAsync {

    /**
     * Gets back a list of airlines for first load
     * @param async
     *      returns a list of airlines
     */
    void updateFlights(AsyncCallback<ArrayList<Airline>> async);

    /**
     * Gets back a list of airlines for updating the table of flights
     * @param name
     *      The name of the airline
     * @param flight
     *      The flight being added
     * @param async
     *      returns a list of airlines
     */
    void updateFlights(String name, Flight flight, AsyncCallback<ArrayList<Airline>> async);

    /**
     * Gets back a string containing the searched flights
     * @param name
     *      The name of the airline
     * @param src
     *      The name of the departing airport
     * @param dest
     *      The name of the arriving airport
     * @param async
     *      returns a string
     */
    void searchFlights(String name, String src, String dest, AsyncCallback<String> async);
}
