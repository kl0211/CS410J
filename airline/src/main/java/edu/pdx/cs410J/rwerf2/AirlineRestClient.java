package edu.pdx.cs410J.rwerf2;

import edu.pdx.cs410J.web.HttpRequestHelper;

import java.io.IOException;

/**
 * A helper class for accessing the rest client. Supports post for adding an
 * airline/flight to the server and get for printing all flights in an
 * airline or searching for specific flights.
 */
public class AirlineRestClient extends HttpRequestHelper
{
    private static final String WEB_APP = "airline";
    private static final String SERVLET = "flights";

    private final String url;

    /**
     * Creates a client to the airline REST service running on the given host and port
     * @param hostName The name of the host
     * @param port The port
     */
    public AirlineRestClient(String hostName, int port) {
        this.url = String.format("http://%s:%d/%s/%s", hostName, port, WEB_APP, SERVLET);
    }

    /**
     * Sends a post request to the server with the information for a new flight
     * @param args
     *      A list of strings representing the new airline/flight
     * @return
     *      The response from the server
     * @throws IOException
     *      If the server cannot be reached
     */
    public Response addFlight(String [] args) throws IOException {
        return post(this.url, "name", args[0], "flightNumber", args[1], "src", args[2],
                    "departTime", args[3] + " " + args[4] + " " + args[5], "dest", args[6],
                    "arriveTime", args[7] + " " + args[8] + " " + args[9]);
    }

    /**
     * Sends a get request to the server for printing out all flights in an airline
     * @param name
     *      The name of the airline
     * @return
     *      The response from the server
     * @throws IOException
     *      If the server cannot be reached
     */
    public Response getFlights(String name) throws IOException {
        return get(this.url, "name", name);
    }

    /**
     * Sends a get request to the server for printing out flights from the airline name which
     * has flights from src to dest
     * @param name
     *      The name of the airline
     * @param src
     *      The name of the departing airport
     * @param dest
     *      The name of the arriving airport
     * @return
     *      The response from the server
     * @throws IOException
     *      If the server cannot be reached
     */
    public Response searchFlights(String name, String src, String dest) throws IOException {
        return get(this.url, "name", name, "src", src, "dest", dest);
    }
}
