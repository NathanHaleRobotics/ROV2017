package nathanhale.robotics.ui;

import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public final class ROVControlPanel extends VBox {
	private static Font font;
	
	static {
		font = Font.loadFont(ROVControlPanel.class.getResourceAsStream("/nathanhale/robotics/resources/SourceCodePro-Regular.ttf"), 16);
	}
	
	/** Utility interface I used to make the chartMaker variable in the constructor **/
	private static interface QuadFunction<F, S, T, L, R> {
		 R apply(F f, S s, T t, L l);
	}
	
	private final VBox devices;
	
	public ROVControlPanel() {
		//the three tabs of the user interface
		Tab controls = new Tab("Controls");	controls.setClosable(false);
		Tab devList = new Tab("Device List"); devList.setClosable(false);
		Tab monitor = new Tab("Monitor"); monitor.setClosable(false);
		TabPane tabs = new TabPane(monitor, controls, devList);
		
		//the scroll pane (just a scrollbar and stuff) for the device list
		ScrollPane sp = new ScrollPane();
		devList.setContent(sp);
		
		//not really used, but a vbox to store each device, currently there is just one entry
		devices = new VBox();
		sp.setContent(devices);
		
		//a lambda function that creates charts with a title, axis labels, and a specified duration (last 10, 20, 30 seconds, etc.)
		QuadFunction<String, String, String, Integer, LineChart<Number, Number>> chartMaker = (title, horizontalLabel, verticalLabel, durationSeconds) -> {
			//axes
			ValueAxis<Number> horizontal = new NumberAxis(), vertical = new NumberAxis();
			
			//set some properties of the horizontal and vertical axes
			horizontal.setLabel(horizontalLabel);
			horizontal.setAutoRanging(false);
			horizontal.setMinorTickVisible(false);
			horizontal.setTickMarkVisible(false);
			horizontal.setTickLabelsVisible(false);
			vertical.setLabel(verticalLabel);
			
			//series of data to put in the chart, kind of a pointless object, but allows a chart to have multiple different lines
			Series<Number, Number> series = new Series<Number, Number>(FXCollections.observableArrayList());
			
			//create the actual chart using the axes we created
			LineChart<Number, Number> chart = new LineChart<Number, Number>(horizontal, vertical);
			
			//set properties of the line chart
			chart.setTitle(title);
			chart.setCreateSymbols(false);
			chart.setLegendVisible(false);
			chart.setAnimated(false);
			chart.getData().add(series);
			
			//a javafx animation timer, just a simple way for the graph to constantly update with the min and max x axis bounds
			new AnimationTimer() {
				@Override
				public void handle(long now) {
					//set graph view to last 'durationSeconds' seconds
					horizontal.setLowerBound(System.currentTimeMillis()/1000d - durationSeconds);
					horizontal.setUpperBound(System.currentTimeMillis()/1000d);
					
					//delete old non-visible values
					series.getData().removeIf(data -> data.getXValue().doubleValue() < System.currentTimeMillis()/1000 - durationSeconds ? true : false);
				}
			}.start();
			
			return chart;
		};
		
		//create some example charts for now
		LineChart<Number, Number> depthChart = chartMaker.apply("Cosine of Time Example", "Time (seconds)", "Cosine", 10);
		LineChart<Number, Number> secondChart = chartMaker.apply("Example #2", "Time (seconds)", "blerp", 10);
		
		//add some sinusoidal data to one of the charts
		new AnimationTimer() {
			public void handle(long now) {
				depthChart.getData().get(0).getData().add(new Data<>(System.currentTimeMillis()/1000d, Math.cos(System.currentTimeMillis()/1000d)));
			}
		}.start();
		
		//make some formatting stuff so that hopefully the charts will be on the right side of the window and we can put other stuff on the left
		HBox layout = new HBox();
		ScrollPane infoContainer = new ScrollPane();
		VBox infoPane = new VBox();
		infoContainer.prefWidthProperty().bind(layout.widthProperty().multiply(2/3)); //TODO figure out how to make scroll pane 2/3 width of window
		VBox graphContainer = new VBox(depthChart, secondChart);
		infoContainer.setContent(infoPane);
		layout.getChildren().add(infoContainer);
		layout.getChildren().add(graphContainer);
		
		monitor.setContent(layout);
		
		//add content to the ROVControlPanel
		this.getChildren().add(tabs);
	}
	
	/** show a String in the device list, this is a temporary method **/
	public void setDevicesContent(String deviceList) {
		Text text = new Text(deviceList);
		text.setFont(font);
		devices.getChildren().add(text);
	}
}
