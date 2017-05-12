package nathanhale.robotics.ui;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import net.java.games.input.Controller;

public final class ROVControlPanel extends VBox {
	private final VBox devices;
	private final LineChart<Number, Number> depthChart;
	
	private static Font font;
	
	static {
		try {
			font = Font.loadFont(new FileInputStream("res/SourceCodePro-Regular.ttf"), 16);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public ROVControlPanel() {
		Tab controls = new Tab("Controls");	controls.setClosable(false);
		Tab devList = new Tab("Device List"); devList.setClosable(false);
		Tab monitor = new Tab("Monitor"); monitor.setClosable(false);
		TabPane tabs = new TabPane(monitor, controls, devList);
		ScrollPane sp = new ScrollPane();
		devList.setContent(sp);
		devices = new VBox();
		sp.setContent(devices);
		ValueAxis<Number> horizontal = new NumberAxis(), vertical = new NumberAxis();
		horizontal.setLabel("Time (seconds)");
		horizontal.setAutoRanging(false);
		horizontal.setTickLabelsVisible(false);
		vertical.setLabel("Depth (meters)");
		monitor.setContent(depthChart = new LineChart<Number, Number>(horizontal, vertical));
		Series<Number, Number> series = new Series<Number, Number>(FXCollections.observableArrayList());
		series.setName("Main Set");
		depthChart.setTitle("ROV Depth");
		depthChart.getData().add(series);
		depthChart.setCreateSymbols(false);
		depthChart.setAnimated(false);
		
		new AnimationTimer() {
			@Override
			public void handle(long now) {
				//set graph view to last 10 seconds
				horizontal.setLowerBound(System.currentTimeMillis()/1000 - 10);
				horizontal.setUpperBound(System.currentTimeMillis()/1000);
				
				//add new data point
				series.getData().add(new Data<Number, Number>(System.currentTimeMillis()/1000, Math.cos(System.currentTimeMillis()/1000)));
				
				//delete old non-visible values
				series.getData().removeIf(data -> data.getXValue().longValue() < System.currentTimeMillis()/1000 - 10 ? true : false);
			}
		}.start();
		
		//add content to the ROVControlPanel
		this.getChildren().add(tabs);
	}
	
	/** temporary **/
	public void setDevicesContent(String deviceList) {
		Text text = new Text(deviceList);
		text.setFont(font);
		devices.getChildren().add(text);
	}
	
	public void addInputDevice(Controller controller) {
		Label label = new Label(controller.getName());
		label.setFont(font);
		devices.getChildren().add(label);
	}
}
