package edu.pdx.cs410J.rwerf2;

import edu.pdx.cs410J.AirportNames;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.DateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;

/**
 * The Server for Airline. Stores Airlines inside a HashMap with Airline name mapping to Airline Object.
 * Get requests print out the airline's flights.
 */
public class AirlineServlet extends HttpServlet
{
    private final HashMap<String, Airline> data = new HashMap<>();

    /**
     * Prints out the requested airline's flights using prettyPrint. Will display message if airline cannot be found.
     * @param request
     *      The request from the client
     * @param response
     *      The response to the client
     * @throws ServletException
     *      If there is an error with the connection to the client
     * @throws IOException
     *      If there is an error with PrintWriter
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/plain");

        String name = getParameter("name", request);
        String src = getParameter("src", request);
        String dest = getParameter("dest", request);
        PrintWriter pw = response.getWriter();
        if (data.get(name) == null)
            pw.println("Airline \"" + name + "\" not found");
        else if (src != null && dest != null) {
            pw.println(prettyPrint(this.data.get(name), src, dest));
            pw.flush();
        }
        else {
            Airline airline = this.data.get(name);
            pw.println(prettyPrint(airline));
            pw.flush();
        }
        response.setStatus (HttpServletResponse.SC_OK);
    }

    /**
     * Stores the received airline information in a new Flight and stores the airline in to the HashMap.
     * @param request
     *      The request from the client
     * @param response
     *      The response to the client
     * @throws ServletException
     *      If there is an error with the connection to the client
     * @throws IOException
     *      If there is an error with PrintWriter
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/plain");

        String airlineName = getParameter("name", request);
        Airline airline = data.get(airlineName);
        if (airline == null)
            airline = new Airline(airlineName);
        airline.addFlight(new Flight(Integer.parseInt(getParameter("flightNumber", request)),
                                     getParameter("src", request).toUpperCase(), getParameter("departTime", request),
                                     getParameter("dest", request).toUpperCase(), getParameter("arriveTime", request)));

        this.data.put(airlineName, airline);

        PrintWriter pw = response.getWriter();
        pw.println("Added flight " + getParameter("flightNumber", request) + " to \"" + airlineName + "\"");
        pw.flush();

        response.setStatus( HttpServletResponse.SC_OK);
    }

    /**
     * Prints all flights from the given airline object to a String
     * @param airline
     *      The airline which flights to print out
     * @return
     *      The String containing the pretty printed flights
     */
    public String prettyPrint(Airline airline) {
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.LONG,
                            DateFormat.LONG, Locale.US);
        String str = "";
        long duration;  //for calculating the time difference between source and destination
        int i = 1;      //for numbering each flight in output

        str += "*********************** Flights of " + airline.getName() + " ***********************\n";
        Collection<Flight> flights = airline.getFlights();
        for (Flight flight : flights) { //for each flight in the Collection, write each variable line by line
            duration = (flight.getArrival().getTime() - flight.getDeparture().getTime()) / (60 * 1000);
            str += i + ". Flight Number " +flight.getNumber() + " Leaves ";
            str += AirportNames.getName(flight.getSource()) + " on ";
            str += format.format(flight.getDeparture()) + "\n   and Arrives at ";
            str += AirportNames.getName(flight.getDestination()) + " on ";
            str += format.format(flight.getArrival()) + "\n";
            str += "   Flight duration is " + duration + " minutes.\n\n";
            ++i;
        }
        if (i == 1) str += "No flights found\n";
        str += "***********************************************************************\n";

        return str;
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

    /**
     * Returns the string value of the request parameter
     * @param name
     *      The keyword of the request
     * @param request
     *      The actual request
     * @return
     *      null if the request is empty, or the string value of the request
     */
    private String getParameter(String name, HttpServletRequest request) {
      String value = request.getParameter(name);
      if (value == null || "".equals(value)) {
        return null;

      } else {
        return value;
      }
    }

}
