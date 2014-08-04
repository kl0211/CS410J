package edu.pdx.cs410J.rwerf2.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.ArrayList;

/**
 * The client-side interface to the updateFlights service
 */
public interface FlightServiceAsync {

    /**
     *
     * @param async
     */
    void updateFlights(AsyncCallback<ArrayList<Airline>> async);

    /**
     *
     * @param name
     * @param flight
     * @param async
     */
    void updateFlights(String name, Flight flight, AsyncCallback<ArrayList<Airline>> async);
}
