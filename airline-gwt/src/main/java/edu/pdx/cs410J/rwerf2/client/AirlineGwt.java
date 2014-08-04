package edu.pdx.cs410J.rwerf2.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import edu.pdx.cs410J.AirportNames;

import java.util.ArrayList;
import java.util.Date;

/**
 * A basic GWT class that makes sure that we can send an airline back from the server
 */
public class AirlineGwt implements EntryPoint {

    private FlightServiceAsync async = GWT.create(FlightService.class);
    private Label airlineLabel = new Label("Airline Name"), flightNumberLabel = new Label("Flight Number"),
                  srcLabel = new Label("Departing Airport"), departTimeLabel = new Label("Departing Date and Time"),
                  destLabel = new Label("Arriving Airport"), arriveTimeLabel = new Label("Arriving Date and Time"),
                  airlineSearchLabel = new Label("Airline Name"), srcSearchLabel = new Label("Departing Airport"),
                  destSearchLabel = new Label("Arriving Airport"),
                  flightPanelLabel = new Label("Airlines and Flights"), addPanelLabel = new Label("Add a new Flight"),
                  searchPanelLabel = new Label("Search for flights"), helpLabel = new Label("Help");
    private HorizontalPanel mainPanel = new HorizontalPanel(), flightPanel = new HorizontalPanel();
    private VerticalPanel tablePanel = new VerticalPanel(), addPanel = new VerticalPanel(),
                          searchPanel = new VerticalPanel(), helpPanel = new VerticalPanel(),
                          tableColumn = new VerticalPanel(), addColumn = new VerticalPanel(),
                          searchColumn = new VerticalPanel();
    private FlexTable flightTable = new FlexTable();
    private TextBox airlineNameInput = new TextBox(), flightNumberInput = new TextBox(), srcInput = new TextBox(),
                    departTimeInput = new TextBox(), destInput = new TextBox(), arriveTimeInput = new TextBox(),
                    airlineNameSearch = new TextBox(), srcSearch = new TextBox(), destSearch = new TextBox();
    private Button addFlightButton = new Button("Add new Airline/Flight"),
                   searchFlightButton = new Button("Search for Flights"),
                   helpButton = new Button("README");
    private DateTimeFormat format = DateTimeFormat.getFormat("MM/dd/yyyy hh:mm a");

    public void onModuleLoad() {
        flightTable.setText(0, 0, "Airline");
        flightTable.setText(0, 1, "Flight Number");
        flightTable.setText(0, 2, "Departing Airport");
        flightTable.setText(0, 3, "Departing Date and Time");
        flightTable.setText(0, 4, "Arriving Airport");
        flightTable.setText(0, 5, "Arriving Date and Time");
        flightTable.getRowFormatter().addStyleName(0, "flightListHeader");
        flightTable.addStyleName("flightList");
        flightTable.getCellFormatter().addStyleName(0, 0, "flightListNumericColumn");
        flightTable.getCellFormatter().addStyleName(0, 1, "flightListNumericColumn");
        flightTable.getCellFormatter().addStyleName(0, 2, "flightListNumericColumn");
        flightTable.getCellFormatter().addStyleName(0, 3, "flightListNumericColumn");
        flightTable.getCellFormatter().addStyleName(0, 4, "flightListNumericColumn");
        flightTable.getCellFormatter().addStyleName(0, 5, "flightListNumericColumn");

        flightNumberInput.getElement().setPropertyString("placeholder", "#");
        srcInput.getElement().setPropertyString("placeholder", "XYZ");
        departTimeInput.getElement().setPropertyString("placeholder", "mm/dd/yyyy hh:mm am/pm");
        destInput.getElement().setPropertyString("placeholder", "XYZ");
        arriveTimeInput.getElement().setPropertyString("placeholder", "mm/dd/yyyy hh:mm am/pm");
        srcSearch.getElement().setPropertyString("placeholder", "XYZ");
        destSearch.getElement().setPropertyString("placeholder", "XYZ");

        addPanel.add(airlineLabel);
        addPanel.add(airlineNameInput);
        addPanel.add(flightNumberLabel);
        addPanel.add(flightNumberInput);
        addPanel.add(srcLabel);
        addPanel.add(srcInput);
        addPanel.add(departTimeLabel);
        addPanel.add(departTimeInput);
        addPanel.add(destLabel);
        addPanel.add(destInput);
        addPanel.add(arriveTimeLabel);
        addPanel.add(arriveTimeInput);
        addPanel.add(addFlightButton);
        addPanel.addStyleName("addPanel");

        searchPanel.add(airlineSearchLabel);
        searchPanel.add(airlineNameSearch);
        searchPanel.add(srcSearchLabel);
        searchPanel.add(srcSearch);
        searchPanel.add(destSearchLabel);
        searchPanel.add(destSearch);
        searchPanel.add(searchFlightButton);
        searchPanel.addStyleName("searchPanel");

        helpPanel.add(helpLabel);
        helpPanel.add(helpButton);
        helpPanel.addStyleName("helpPanel");

        flightPanel.add(flightTable);

        tablePanel.add(flightPanel);

        tableColumn.add(flightPanelLabel);
        tableColumn.add(flightPanel);

        addPanelLabel.addStyleName("addPanelLabel");
        addColumn.add(addPanelLabel);
        addColumn.add(addPanel);

        searchPanelLabel.addStyleName("searchPanelLabel");
        searchColumn.add(searchPanelLabel);
        searchColumn.add(searchPanel);
        searchColumn.add(helpPanel);

        mainPanel.add(tableColumn);
        mainPanel.add(addColumn);
        mainPanel.add(searchColumn);

        if (async == null)
            async = GWT.create(FlightService.class);
        AsyncCallback<ArrayList<Airline>> callback = new AsyncCallback<ArrayList<Airline>>() {
            @Override
            public void onFailure(Throwable caught) {
                Window.alert(caught.toString());
            }

            @Override
            public void onSuccess(ArrayList<Airline> result) {
                updateTable(result);
            }
        };
        async.updateFlights(callback);


        addFlightButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent clickEvent) {
                if (airlineNameInput.getText().isEmpty()) {
                    Window.alert("You must enter an airline name");
                    return;
                }
                if (!isValidFlightNumber(flightNumberInput.getText())) return;
                if (!isValidAirportCode(srcInput.getText())) return;
                Date departTime, arriveTime;
                try {
                    departTime = format.parseStrict(departTimeInput.getValue());
                } catch (IllegalArgumentException ex) {
                    Window.alert("Departing Date and Time must be entered in the format\n" +
                                 "\"mm/dd/yyyy hh:mm am/pm\" and must be a valid date");
                    return;
                }
                if (!isValidAirportCode(destInput.getText())) return;
                try {
                    arriveTime = format.parseStrict(arriveTimeInput.getValue());
                } catch (IllegalArgumentException ex) {
                    Window.alert("Arrival Date and Time must be entered in the format\n" +
                            "\"mm/dd/yyyy hh:mm am/pm\" and must be a valid date");
                    return;
                }
                Flight newFlight = new Flight(Integer.parseInt(flightNumberInput.getText()),
                                              srcInput.getText().toUpperCase(), departTime,
                                              destInput.getText().toUpperCase(), arriveTime);

                if (async == null)
                    async = GWT.create(FlightService.class);
                AsyncCallback<ArrayList<Airline>> callback = new AsyncCallback<ArrayList<Airline>>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        Window.alert(caught.toString());
                    }

                    @Override
                    public void onSuccess(ArrayList<Airline> result) {
                        updateTable(result);
                    }
                };
                async.updateFlights(airlineNameInput.getText(), newFlight, callback);
            }
        });
        searchFlightButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                if (airlineNameSearch.getText().isEmpty()) {
                    Window.alert("You must enter an airline name");
                    return;
                }
                if (!isValidAirportCode(srcSearch.getText())) return;
                if (!isValidAirportCode(destSearch.getText())) return;
                if (async == null)
                    async = GWT.create(FlightService.class);
                AsyncCallback<String> callback = new AsyncCallback<String>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        Window.alert(caught.toString());
                    }

                    @Override
                    public void onSuccess(String result) {
                        Window.alert(result);
                    }
                };
                async.searchFlights(airlineNameSearch.getText(), srcSearch.getText(), destSearch.getText(), callback);
            }
        });
        helpButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Window.alert(readMe());
            }
        });
        RootPanel rootPanel = RootPanel.get();
        rootPanel.add(mainPanel);
    }


    private void updateTable(ArrayList<Airline> result) {
        int rowCount = 0;
        for (Airline airline : result) {
            for (Flight flight : airline.getFlights()) {
                ++rowCount;
                flightTable.setText(rowCount, 0, airline.getName());
                flightTable.setText(rowCount, 1, String.valueOf(flight.getNumber()));
                flightTable.setText(rowCount, 2, flight.getSource());
                flightTable.setText(rowCount, 3, format.format(flight.getDeparture()));
                flightTable.setText(rowCount, 4, flight.getDestination());
                flightTable.setText(rowCount, 5, format.format(flight.getArrival()));
            }
        }
    }

    /**
     * Checks whether string can be converted to an integer
     * @param str
     *      string to check
     * @return
     *      true if it can converted, otherwise false
     */
    static boolean isValidFlightNumber(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            Window.alert("Flight number must only contain numbers");
            return false;
        }
    }

    /**
     * Checks whether string is a valid airport code
     * @param str
     *      string to check
     * @return
     *      true if valid, otherwise false
     */
    static boolean isValidAirportCode(String str) {
        if (AirportNames.getName(str.toUpperCase()) == null) {
            Window.alert("\"" + str + "\" is not a valid airport code");
            return false;
        }
        return true;
    }

    static String readMe() {
        return "*************************************************************\n" +
                "edu.pdx.cs410J.rwerf2.Project4 \"Airline\" By Rob Werfelmann.           \n" +
                "Project4 is an application which stores Airlines containing Flights     \n" +
                "on a server. It supports specifying host names and ports.               \n" +
                "It supports the the following options:\n -print, -README, " +
                "-host, -port, -search                                                 \n\n" +
                "-print will print out the flights of an added airline after             \n" +
                "adding a flight. This includes printing out flights which were          \n" +
                "written to the server.                                                  \n" +
                "-README will print this message and exit, regardless if there           \n" +
                "are other arguments.                                                    \n" +
                "-host and -port specify the host name on which the server is located    \n" +
                "and which port it is listening. If host and port are not specified,     \n" +
                "then host and port will default to \"localhost:8080\"                   \n" +
                "-search fetches all flights from airline \"name\" that Originate from   \n" +
                "airport \"src\" and arrive at airport \"dest\" that are on the server.\n\n" +
                "Example Usage:\n java edu.pdx.cs410J.rwerf2.Project4 -print " +
                "\"Alaska Airlines\" \\\n 101 PDX 7/4/2014 12:00 pm SEA 07/04/2014 12:40 pm\n\n" +
                " java edu.pdx.cs410J.rwerf2.Project4 -host cs.pdx.edu -port 2020 \\     \n" +
                " united \"United Airlines\" 2453 LAX 12/12/2013 12:40 am PDX \\         \n" +
                " 12/12/2013 2:40 am\n\n" +
                " java edu.pdx.cs410J.rwerf2.Project4 -search \"Alaska Airlines\" PDX SEA\n" +
                "***************************************************************";
    }
}
