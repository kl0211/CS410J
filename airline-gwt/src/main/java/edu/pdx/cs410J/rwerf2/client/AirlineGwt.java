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
        airlineNameInput.setMaxLength(24);
        
        addFlightButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent clickEvent) {
                final String airlineName = airlineNameInput.getText(), src = srcInput.getText().toUpperCase(),
                       dest = destInput.getText().toUpperCase();
                final int flightNumber;
                Date departTime, arriveTime;
                if (airlineName.isEmpty()) {
                    Window.alert("You must enter an airline name");
                    return;
                }
                if (!isValidFlightNumber(flightNumberInput.getText())) return;
                flightNumber = Integer.parseInt(flightNumberInput.getText());
                if (!isValidAirportCode(src)) return;
                try {
                    departTime = format.parseStrict(departTimeInput.getValue());
                } catch (IllegalArgumentException ex) {
                    Window.alert("Departing Date and Time must be entered in the format\n" +
                                 "\"mm/dd/yyyy hh:mm am/pm\" and must be a valid date");
                    return;
                }
                if (!isValidAirportCode(dest)) return;
                try {
                    arriveTime = format.parseStrict(arriveTimeInput.getValue());
                } catch (IllegalArgumentException ex) {
                    Window.alert("Arrival Date and Time must be entered in the format\n" +
                            "\"mm/dd/yyyy hh:mm am/pm\" and must be a valid date");
                    return;
                }
                Flight newFlight = new Flight(flightNumber, src, departTime, dest, arriveTime);

                if (async == null)
                    async = GWT.create(FlightService.class);
                AsyncCallback<ArrayList<Airline>> callback = new AsyncCallback<ArrayList<Airline>>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        Window.alert(caught.toString());
                    }

                    @Override
                    public void onSuccess(ArrayList<Airline> result) {
                        if (result == null) {
                            Window.alert("Airline \"" + airlineName + "\" already " +
                                    "contains flight " + flightNumber);
                            return;
                        }
                        updateTable(result);
                        airlineNameInput.setText("");
                        flightNumberInput.setText("");
                        srcInput.setText("");
                        departTimeInput.setText("");
                        destInput.setText("");
                        arriveTimeInput.setText("");
                    }
                };
                async.updateFlights(airlineName, newFlight, callback);
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
                async.searchFlights(airlineNameSearch.getText(), srcSearch.getText().toUpperCase(),
                                    destSearch.getText().toUpperCase(), callback);
                airlineNameSearch.setText("");
                srcSearch.setText("");
                destSearch.setText("");
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
        return "Welcome to the help file for this airline web application using" +
               " Google Web Toolkit. You've managed to click the help button, so" +
               " you probably know what that button does. The rest of the window" +
               " is divided into 3 parts:\n\n" +
               "Under Airlines and Flights is a table showing all flight" +
               " information for all airlines. The flights are sorted by the" +
               " departing airport code and then by departing time.\n\n" +
               "Under Add a new flight are input boxes to add info for a" +
               " new airline and/or flight. The Airline field must be a" +
               " non-empty String. The Flight Number must be filled with only" +
               " numbers. The departing and arriving airports must be a valid" +
               " three-letter airport code. The departing and arriving dates" +
               " must be in format mm/dd/yyyy hh:mm am/pm.\n\n" +
               "Under Search for flights are input boxes for search information" +
               " for existing flights. Searching requires the Airline name," +
               " departing airport and arriving airport codes. The format for" +
               " the airport codes follow the same requirements as for adding a" +
               " new flight. Successful searches will display the flights in a" +
               "pop-up window in a more human-readable display.\n\n\n" +
               "Copyright (C) 2014 Rob Werfelmann\n";
    }
}
