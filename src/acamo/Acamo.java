package acamo;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import de.saring.leafletmap.*;
import de.saring.leafletmap.events.MapClickEventListener;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.event.EventHandler;
import jsonstream.PlaneDataServer;
import messer.BasicAircraft;
import messer.*;
import senser.Senser;

public class Acamo extends Application implements Observer  {
	private ActiveAircrafts activeAircrafts;
    private TableView<BasicAircraft> table = new TableView<BasicAircraft>();
    private ObservableList<BasicAircraft> aircraftList = FXCollections.observableArrayList();
    private HashMap<String, Marker> aircraftMap = new HashMap<>();
	private HashMap<String, Label> aircraftLabelMap;
    private ArrayList<String> fields;
    private ArrayList<Label> aircraftLabelList;
    private int selectedIndex = 0;
    private double latitude = 48.7433425;
    private double longitude = 9.3201122;
    private static boolean haveConnection = false;

    //GUI elements
	private GridPane gridpane = new GridPane();
	private AnchorPane mainLayout = new AnchorPane();
	private TextField longitudeText = new TextField("Longitude");
	private TextField latitudeText = new TextField("Latitude");
	private Button submitButton = new Button("Submit");

	//Elements for Map
	private LeafletMapView mapView = new LeafletMapView();
	private List<MapLayer> config = new LinkedList<>();;
	private CompletableFuture<Worker.State> loadState;
	private Marker homeMarker = new Marker(new LatLong(latitude, longitude), "Home", "Home", 0);

    public static void main(String[] args) {
		launch(args);
    }

    @Override
    public void start(Stage stage) {
 		String urlString = "https://opensky-network.org/api/states/all";
		PlaneDataServer server;

		if(haveConnection)
			server = new PlaneDataServer(urlString, latitude, longitude, 150);
		else
			server = new PlaneDataServer(latitude, longitude, 100);

		new Thread(server).start();

		Senser senser = new Senser(server);
		new Thread(senser).start();

		Messer messer = new Messer();
		senser.addObserver(messer);
		new Thread(messer).start();

		activeAircrafts = new ActiveAircrafts();

		messer.addObserver(activeAircrafts);
		messer.addObserver(this);

        fields = BasicAircraft.getAttributesNames();

        // Fill column header using the attribute names from BasicAircraft
		for(int i = 0;i < fields.size();i++) {
			TableColumn<BasicAircraft, String> columnHeader = new TableColumn<BasicAircraft, String>(fields.get(i));
			columnHeader.setCellValueFactory(new PropertyValueFactory<BasicAircraft, String>(fields.get(i)));
			table.getColumns().add(columnHeader);
		}
		table.setItems(aircraftList);
		table.setEditable(false);

		//creating labels for selection and text and set some style
	    Text myText = new Text("Henning Weise - Labor OOS2");
	    myText.setStyle("-fx-font-size: 2em");
	    myText.setFill(Color.RED);
	    Label title = new Label("Selected Aircraft");
	    title.setStyle("-fx-font-weight: bold");
	    title.setStyle("-fx-font-size: 2em");
	    Label icaoLabel = new Label("icao");
	    Label icaoInput = new Label("");
	    Label operator = new Label("operator");
	    Label operatorInput = new Label("");
	    Label posTime = new Label("posTime");
	    Label posTimeInput = new Label("");
	    Label coordinate = new Label("coordinate");
	    Label coordinateInpout = new Label("");
	    Label speed = new Label("speed");
	    Label speedInput = new Label("");
	    Label trak = new Label("trak");
	    Label trakInput = new Label("");

		//Gridpane for displaying selection
	    gridpane.setHgap(25);
	    gridpane.setVgap(15);
	    gridpane.add(title, 1, 0, 2, 1);
	    gridpane.add(icaoLabel, 1, 1);
	    gridpane.add(icaoInput, 2, 1);
	    gridpane.add(operator, 1, 2);
	    gridpane.add(operatorInput, 2, 2);
	    gridpane.add(posTime, 1, 3);
	    gridpane.add(posTimeInput, 2, 3);
	    gridpane.add(coordinate, 1, 4);
	    gridpane.add(coordinateInpout, 2, 4);
	    gridpane.add(speed, 1, 5);
	    gridpane.add(speedInput, 2, 5);
	    gridpane.add(trak, 1, 6);
	    gridpane.add(trakInput, 2, 6);

	    //Creating Map
	    mapView.setLayoutX(0);
	    mapView.setLayoutY(0);
	    mapView.setMaxWidth(640);
	    config.add(MapLayer.MAPBOX);
	    //Record the load state
	    loadState = mapView.displayMap(new MapConfig(config, new ZoomControlConfig(), new ScaleControlConfig(), new LatLong(latitude, longitude)));
	    //markers can only be added when the map is complete
	    loadState.whenComplete((state, throwable) -> {
		    //add custom home marker data
		    mapView.addCustomMarker("Home", "icons/basestation.png");
		    //add marker to display in map
		    mapView.addMarker(homeMarker);
		    //create plane icons
		    for (int i = 0; i <= 24;  i++) {
			    String number = String.format("%02d", i);
			    mapView.addCustomMarker("plane" + number, "icons/plane" + number + ".png");
		    }
		    //click event for map
		    mapView.onMapClick(new MapClickEventListener() {
			    @Override
			    public void onMapClick(LatLong latlong) {
				    resetLocation(latlong.getLatitude(), latlong.getLongitude(), 150, server);
			    }
		    });
	    });

	    //loop through the list of basic airplanes
	    for(BasicAircraft plane: activeAircrafts.values()){
		    int heading = plane.getTrak().intValue();
		    LatLong latlong = new LatLong(plane.getCoordinate().getLatitude(), plane.getCoordinate().getLongitude());
		    String icao = plane.getIcao();
		    String planeIcon = "plane" + String.format("%02d", heading / 15);
		    loadState.whenComplete((state, throwable) -> {
			    Marker planeMarker = new Marker(latlong, icao, planeIcon, 0);
			    mapView.addCustomMarker(planeIcon, "icons/" + planeIcon + ".png");
			    mapView.addMarker(planeMarker);
			    aircraftMap.put(icao, planeMarker);
		    });
	    }

		//Place text on anchorpane
	    AnchorPane.setTopAnchor(myText, 30.0);
	    AnchorPane.setLeftAnchor(myText, 550.0);
	    AnchorPane.setRightAnchor(myText, 70.0);
		//Place table on anchorpane
	    AnchorPane.setTopAnchor(table, 100.0);
	    AnchorPane.setLeftAnchor(table, 600.0);
	    AnchorPane.setRightAnchor(table, 290.0);
		//place gridpane on anchorpane
	    AnchorPane.setTopAnchor(gridpane, 100.0);
	    AnchorPane.setLeftAnchor(gridpane, 1100.0);
	    //place mapeView on achnorpane
	    AnchorPane.setTopAnchor(mapView, 100.0);
	    AnchorPane.setLeftAnchor(mapView, 30.0);
	    AnchorPane.setRightAnchor(mapView, 810.0);
	    //place textfields on anchorpane
	    AnchorPane.setTopAnchor(longitudeText, 710.0);
	    AnchorPane.setTopAnchor(latitudeText, 710.0);
	    AnchorPane.setLeftAnchor(longitudeText, 150.0);
	    AnchorPane.setLeftAnchor(latitudeText, 300.0);
	    //place Button on Acnhorpane
	    AnchorPane.setTopAnchor(submitButton, 740.0);
	    AnchorPane.setLeftAnchor(submitButton, 270.0);

	    //Set some style for our submit button
	    submitButton.setStyle("-fx-text-fill: black; -fx-background-color: lightgray; -fx-border-color: black;");
	    submitButton.setOnMouseEntered(e -> submitButton.setStyle("-fx-background-color: green; -fx-text-fill: white;"));
	    submitButton.setOnMouseExited(e -> submitButton.setStyle("-fx-text-fill: black; -fx-background-color: lightgray; -fx-border-color: black;"));
	    submitButton.setOnAction(e ->{
	    	resetLocation(Double.parseDouble(latitudeText.getText()), Double.parseDouble(longitudeText.getText()), 150, server);
	    });

        //Event handler for clicking on a row
	    table.setOnMousePressed(new EventHandler<MouseEvent>() {
		    @Override
		    public void handle(MouseEvent mouseEvent) {
			    if (mouseEvent.isPrimaryButtonDown()){
				    selectedIndex = table.getSelectionModel().getSelectedIndex();
				    table.getSelectionModel().select(selectedIndex);
				    BasicAircraft aircraft = table.getSelectionModel().getSelectedItem();
				    ArrayList<Object> aircraftValues = BasicAircraft.getAttributesValues(aircraft);

				    //Checking if data is icao, operator, posTime etc and set it to the correct Label
				    for (int i = 0; i < aircraftValues.size(); i++) {
					    System.out.println(aircraftValues.get(i).toString());
					    switch(i){
						    case 0:
						    	icaoInput.setText(aircraftValues.get(i).toString());
							    break;
						    case 1:
						    	operatorInput.setText(aircraftValues.get(i).toString());
							    break;
						    case 2:
						    	posTimeInput.setText(aircraftValues.get(i).toString());
							    break;
						    case 3:
						    	coordinateInpout.setText(aircraftValues.get(i).toString());
							    break;
						    case 4:
						    	speedInput.setText(aircraftValues.get(i).toString());
							    break;
						    case 5:
						    	trakInput.setText(aircraftValues.get(i).toString());
							    break;
					    }
				    }
			    }
		    }
	    });

	    //Adding content to the anchorpane
	    mainLayout.getChildren().addAll(myText, table, gridpane, mapView, longitudeText, latitudeText, submitButton);

		Scene scene = new Scene(mainLayout, 1400, 800);
        stage.setScene(scene);
        stage.setTitle("Acamo-Henning Weise");
        stage.sizeToScene();
        stage.setOnCloseRequest(e -> System.exit(0));
        stage.show();
    }

    // When messer updates Acamo (and activeAircrafts) the aircraftList must be updated as well
    @Override
    public void update(Observable o, Object arg) {
    	Platform.runLater(new Runnable() {
		    @Override
		    public void run() {
			    aircraftList.clear();
			    aircraftList.addAll(activeAircrafts.values());
			    autoResizeColumns(table);

			    //loop through the list of basic airplanes
			    for(BasicAircraft plane: activeAircrafts.values()){
				    int heading = plane.getTrak().intValue();
				    LatLong latlong = new LatLong(plane.getCoordinate().getLatitude(), plane.getCoordinate().getLongitude());
				    String icao = plane.getIcao();
				    String planeIcon = "plane" + String.format("%02d", (int) (heading / 15));  //   360/24=15
				    if(aircraftMap.containsKey(icao)) {
					    Marker planeMarker = aircraftMap.get(icao);
					    planeMarker.move(latlong);
					    planeMarker.changeIcon(planeIcon);
				    } else {
					    loadState.whenComplete((state, throwable) -> {
						    Marker planeMarker = new Marker(latlong, icao, planeIcon, 0);
						    mapView.addMarker(planeMarker);
						    aircraftMap.put(icao, planeMarker);
					    });
				    }
			    }
		    }
	    });
    }

    /*
    Created Method to resize the columns after filling it with content, to display
    all the content in posTime column and coordinate column
     */
	public static void autoResizeColumns( TableView<?> table ) {
		//Set the right policy
		table.setColumnResizePolicy( TableView.UNCONSTRAINED_RESIZE_POLICY);
		table.getColumns().stream().forEach( (column) -> {
			//Minimal width = columnheader
			Text t = new Text( column.getText() );
			double max = t.getLayoutBounds().getWidth();

			for ( int i = 0; i < table.getItems().size(); i++ )  {
				//cell must not be empty
				if ( column.getCellData( i ) != null )  {
					t = new Text( column.getCellData( i ).toString() );
					double calcwidth = t.getLayoutBounds().getWidth();
					//remember new max-width
					if ( calcwidth > max )  {
						max = calcwidth;
					}
				}
			}
			//set the new max-width with some extra space
			column.setPrefWidth( max + 10.0d );
		} );
	}

	private void resetLocation(double lat, double lng, int distance, PlaneDataServer server){
		server.resetLocation(lat, lng, distance);
		//loop through the hashtable of markers
		for(Marker anMarker : aircraftMap.values()){
			mapView.removeMarker(anMarker);
		}
		//clear hashtable
		aircraftMap.clear();
		//move the home marker and the map
		homeMarker.move(new LatLong(lat, lng));
		mapView.panTo(new LatLong(lat, lng));
		//clear the active aircrafts
		activeAircrafts.clear();
	}
}
