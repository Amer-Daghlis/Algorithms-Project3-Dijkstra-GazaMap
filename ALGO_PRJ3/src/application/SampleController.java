package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class SampleController implements Initializable {
	int numCity;
	int numDij;
	ArrayList<Vertex> cities = new ArrayList<>();
	List<String> city = new ArrayList<>();
	static final double Xmin = 0.0;
	static final double Ymin = 0.7999999999999998;
	static final double Xmax = 794.4;
	static final double Ymax = 617.6;
	static final double MxMin = 34.090543;
	static final double MxMax = 34.579709;
	static final double MyMin = 31.600000;
	static final double MyMax = 31.264686;

	private Vertex selectedSourceCity = null;
	private Vertex selectedTargetCity = null;
	private List<Circle> cityCircles = new ArrayList<>();
	@FXML
	private ChoiceBox<String> sourceSelect;
	@FXML
	private ChoiceBox<String> TargetSelect;
	@FXML
	private TextArea pathTA;
	@FXML
	private TextField distancT;
	@FXML
	private AnchorPane bord;
	@FXML
	private ImageView MapImg;

	@FXML
	void Run(ActionEvent event) {
		distancT.clear();
		pathTA.clear();

		String sourceCityName = sourceSelect.getValue();
		String targetCityName = TargetSelect.getValue();

		int sourceIndex = getIndex(sourceCityName);
		int targetIndex = getIndex(targetCityName);

		// Check if source and target cities are valid
		if (sourceIndex < 0 || targetIndex < 0 || sourceIndex >= cities.size() || targetIndex >= cities.size()) {
			pathTA.setText("Invalid source or target");
			return;
		}

		Vertex sourceCity = cities.get(sourceIndex);
		Vertex targetCity = cities.get(targetIndex);

		computeShortestPaths(sourceCity); // Updated to match the new method signature

		List<Vertex> path = getShortestPathTo(targetCity);

		Circle sourceCircle = findCircleForCity(sourceCity);
        Circle targetCircle = findCircleForCity(targetCity);
        if (sourceCircle != null) {
            sourceCircle.setFill(Color.RED); // Set the source city circle to red
        }
        if (targetCircle != null) {
            targetCircle.setFill(Color.GREEN); // Set the target city circle to green
        }
     else {
        pathTA.setText("NO PATH FOUND");
    }
		System.out.println("Computed Path from " + sourceCity.getName() + " to " + targetCity.getName() + ": " + path);

		if (!path.isEmpty() && targetCity.getDistance() != Double.MAX_VALUE) {
			StringBuilder pathText = new StringBuilder();
			double totalDistance = 0;
			Vertex previousVertex = null;

			for (Vertex vertex : path) {
				if (previousVertex != null) {
					totalDistance += calculateDistance(previousVertex, vertex);
					drowline(previousVertex.getX(), previousVertex.getY(), vertex.getX(), vertex.getY());
				}
				pathText.append(vertex.getName()).append(" -> ");
				previousVertex = vertex;
			}

			pathText.append("End");
			pathTA.setText(pathText.toString());
			distancT.setText(String.format("%.2f", totalDistance));
		} else {
			pathTA.setText("NO PATH FOUND");
		}
		
	}

	@FXML
	void clear(ActionEvent event) {
	    cities.clear();
	    city.clear();
	    selectedSourceCity = null;
	    selectedTargetCity = null;

	    // Clear UI components
	    pathTA.clear();
	    distancT.clear();
	    sourceSelect.setItems(FXCollections.observableArrayList());
	    TargetSelect.setItems(FXCollections.observableArrayList());
	    clearLines(); // Clear lines

	    initialize(null, null);
	}
	
	void drowline(double statX, double stary, double endx, double endy) {
		Line line = new Line(statX, stary, endx, endy);
		line.setStroke(Color.BLACK);
		bord.getChildren().add(line);
	}

	void readFile() {
		Scanner sc = null;
		try {
			sc = new Scanner(
					new BufferedReader(new FileReader("C:\\Users\\amerg\\OneDrive\\Desktop\\ALGO_PRJ3\\Cities.txt")));
			numCity = sc.nextInt();
			numDij = sc.nextInt();
			sc.nextLine(); // Move to the next line after reading integers

			for (int i = 0; i < numCity && sc.hasNextLine(); i++) {
				String[] line = sc.nextLine().trim().split("\\s+");
				if (line.length >= 3) {
					String cityName = line[0];
					double latitude = Double.parseDouble(line[1]);
					double longitude = Double.parseDouble(line[2]);
					cities.add(new Vertex(cityName, getX(longitude), getY(latitude)));// هان بحط في السيتييز الفيرتكس تبع كل سيتي
					city.add(cityName);// هان بحط فيها بس الاسماء
				}
			}

			for (int i = 0; i < numDij && sc.hasNextLine(); i++) {
				String[] line = sc.nextLine().trim().split("\\s+");
				if (line.length >= 2) {
					int cityIndex1 = getIndex(line[0]);
					int cityIndex2 = getIndex(line[1]);
					if (cityIndex1 < cities.size() && cityIndex2 < cities.size()) {
						Vertex city1 = cities.get(cityIndex1);
						Vertex city2 = cities.get(cityIndex2);
						double distance = calculateDistance(city1, city2);

						city1.addNeighbour(new Edge(distance, city1, city2));
						city2.addNeighbour(new Edge(distance, city2, city1));
					}
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("File not found!");
		} finally {
			if (sc != null) {
				sc.close();
			}
		}
	}

	
	//ميثود بتحوللي اللونجيتيود ل اكس 
	public double getX(double value) {
		double x = ((((Xmax - Xmin) * (value - MxMin)) / (MxMax - MxMin))) + Xmin;
		System.out.println(x);
		return x;

	}
	// ميثود بتحوللي اللاتيتيود ل واي
	public double getY(double My) {
		double y = ((((Ymax - Ymin) * (My - MyMin)) / (MyMax - MyMin))) + Ymin;
		System.out.println(y);
		return y;

	}
	// الميثود الي بتحسبلي الدستانس عن طريق القانون المسافة بين نقطتين
	private static double calculateDistance(Vertex city1, Vertex city2) {
		double xDiff = city2.getX() - city1.getX();
		double yDiff = city2.getY() - city1.getY();
		return Math.sqrt(xDiff * xDiff + yDiff * yDiff) / 15.8;
	}
	// هاي الميود بترجعلي الانديكس تاعة السيتي الي انا بحثت عليها بالاسم عشان اعرف اقارنها فوق مع السيتي سايز واعرف انها فاليد او لا
	int getIndex(String name) {
		for (int i = 0; i < cities.size(); i++) {
			if (cities.get(i).getName().equals(name))
				return i;
		}
		return cities.size() + 1;

	}
// هاي الميثود بتعملي انشيلايز لكل الي بشوفو عالشاشة هي الي بتعبي التشويس بوكسس وبتعملي السيت ايتيم وكل وبترسملي السيتيز 
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	    readFile();
	    List<String> filteredCities = city.stream()
	                                      .filter(cityName -> !cityName.startsWith("sA"))
	                                      .collect(Collectors.toList());

	    sourceSelect.setItems(FXCollections.observableArrayList(filteredCities));
	    TargetSelect.setItems(FXCollections.observableArrayList(filteredCities));

	    //هاي الميثود بتطبعلي اسماء المودون والدوائر على الخريطة
	    for (Vertex cityVertex : cities) {//هان بلف على كل الاري تبعية السيتيز الي مخزنين فيها فيرتكس وبتتخزن في قيمة السيتي فيرتكس
	        if (!cityVertex.getName().startsWith("sA")) {// هان فلتر عشان ميطبعش الاس اا
	            Circle circle = new Circle(cityVertex.getX(), cityVertex.getY(), 4);// هان برسملي الدائر محل الاكس والواي تبعيات المدينة
	            circle.setOnMouseClicked(e -> handleCityClick(cityVertex, circle));// هان الاكشن تبع الكليك بوتون 

	            Text text = new Text(cityVertex.getX() + 5, cityVertex.getY() + 5, cityVertex.getName());
	            text.setFont(Font.font(10));// النصوص

	            bord.getChildren().addAll(circle, text);// اضيف كل ها الحكي على البورد تبعتي الي هي الخريطة 
	            cityCircles.add(circle);// بضيف السيتي على الليست تبعية السيركل عشان الماوس كليك اكشن 
	        }
	    }
	}

	
		
	//هاي الميثود بتحسبلي الشورت باث للفيرتكس الي عندي الموجودة في الغراف
	public void computeShortestPaths(Vertex sourceVertex) {
		sourceVertex.setDistance(0);//مش المفروض تبكا في عندي قيمة صفر هاي هي بعمل هاي القيمة فيرتكس وبحط الدستانس صفر 
		VertexPriorityQueue priorityQueue = new VertexPriorityQueue();// بعمل بروبياتي كيو للفيرتكس عشان ابلش اشوف منها اقصر باث 
		priorityQueue.add(sourceVertex);// بحط القيمة الي عرفتلها الدستانس صفر عشان ابللش منها 

		while (!priorityQueue.isEmpty()) {// اللوب بتظل شغالة طول ما الكيو ملانة 
			Vertex currentVertex = priorityQueue.poll();//    والي لس معاجناهوش بعمل فيرتكس لاول قيمة وبوخذ اول قيمة في الكيو والي هي رح تبكا اصغر قيمة
			currentVertex.setVisited(true);// هان عشان مردش اعمللها معالجة اخرى مرة وافوتها في اللوب بعملها انها فيزيتيد عشان مردش الف عليها 

			for (Edge edge : currentVertex.getAdjacenciesList()) {// هان رح اصير الف على جميع الادجيز الي عندي 
				Vertex targetVertex = edge.getTargetVertex();// هاي رح تعطيني الفيرتكس الي وقف عندو الايدج
				if (!targetVertex.isVisited()) {// طول ما هو مش فيزيتيد ظللك لف 
					double newDistance = currentVertex.getDistance() + edge.getWeight();// هان بعمل ديستانس وبوخذ الديستانس لكل الايدجيز الي مريت فيهم مع االفيرتكس الي وقفت عليهن 
					if (newDistance < targetVertex.getDistance()) {// اذا المسافة الي طلعناها اقل من المسافة الي عننا 
						targetVertex.setDistance(newDistance);// والقيمة الجديدة بتصير اقل دستانس 
						targetVertex.setPredecessor(currentVertex);// وبحط قيمة التارجيت هي قيمة الكررنت الاقل دستانس 
						priorityQueue.add(targetVertex);// وبضيف ها التارجيت للكيو 
					}
				}
			}
		}
	}

	public List<Vertex> getShortestPathTo(Vertex targetVertex) {
		List<Vertex> path = new ArrayList<>();// بعمل ليست من الفيرتكس
		for (Vertex vertex = targetVertex; vertex != null; vertex = vertex.getPredecessor()) {//بتتبع اللوب من الفيرتكس الاولى لعن الفيرتكس الي بدو اياها ومنها بجيب rdlm hgfhe 
			path.add(vertex);// بحط الفيرتكس الي مريت عليهم تا وصلت الباث في الباث ليست
		}

		Collections.reverse(path);//بجمع كل القيم الي مرريت فيها وصول للباث وبرجعها كلها مرة وحدة 
		return path;
	}

	//هاي الميثود بتعملي هاندل للكليلك تبعية الماوس
	private void handleCityClick(Vertex cityVertex, Circle circle) {
	    clearLines();
	    if (selectedSourceCity == null) {
	        selectedSourceCity = cityVertex;
	        circle.setFill(Color.RED); // Change to red for the source city
	    } else if (selectedTargetCity == null && !cityVertex.equals(selectedSourceCity)) {
	        selectedTargetCity = cityVertex;
	        circle.setFill(Color.GREEN); // Keep green for the target city
	        computeAndDisplayPath();
	    }
	    }		// بحط القيم الي كبس عليهن الماوس في قيم انا ببكا معرفها عشان استخدمهن في المثود الي بدها تحسبلي الباث فضغطة الكليك
	
	// ميثود بتعملي كلير للاينز عادي عن طريق تتبع النود من الانترفيس تبعية البورد واللاين
	private void clearLines() {
	    bord.getChildren().removeIf(node -> node instanceof Line);
	}
	
	private void computeAndDisplayPath() {
		if (selectedSourceCity != null && selectedTargetCity != null) {// اذا المدن الي اخترتها مش فاظية كممل 
			computeShortestPaths(selectedSourceCity);//اعملي الشورت باث تاع المدينة الاولى 

			List<Vertex> path = getShortestPathTo(selectedTargetCity);// اعطيني الشورت باث تاع المدينة الثانية 

			// Clear previous lines and reset colors of previously selected cities
			bord.getChildren().removeIf(node -> node instanceof Line);// قيم اي خط عالشاشة
			cityCircles.forEach(c -> c.setFill(Color.BLACK));// حط الدائرة لونها اخضر
			pathTA.clear();//

			double totalDistance = 0;
			StringBuilder pathText = new StringBuilder();

			  if (!path.isEmpty() && selectedTargetCity.getDistance() != Double.MAX_VALUE) {
		            Vertex previousVertex = null;
		            for (Vertex vertex : path) {
		                if (previousVertex != null) {
		                    drawLine(previousVertex, vertex);
		                    totalDistance += calculateDistance(previousVertex, vertex);

		                    // Find circles for the two connected cities and set their color to green
		                    Circle sourceCircle = findCircleForCity(selectedSourceCity);
		                    Circle targetCircle = findCircleForCity(selectedTargetCity);
		                    if (sourceCircle != null) {
		                        sourceCircle.setFill(Color.RED); // Set the source city circle to red
		                    }
		                    if (targetCircle != null) {
		                        targetCircle.setFill(Color.GREEN); // Set the target city circle to green
		                    }
		                } 

		                // Append the city name to the path
		                pathText.append(vertex.getName()).append(" -> ");
		                previousVertex = vertex;
		            }
		            pathText.append("End");

		            // Display the path and total distance
		            pathTA.setText(pathText.toString());
		            distancT.setText(String.format("%.2f", totalDistance));
		        } else {
		            pathTA.setText("No path found");
		            distancT.clear();
		        }
		    }
		}
	private void drawLine(Vertex from, Vertex to) {
		Line line = new Line(from.getX(), from.getY(), to.getX(), to.getY());
		line.setStroke(Color.BLACK); 
		bord.getChildren().add(line);
	}
	
	private Circle findCircleForCity(Vertex cityVertex) {
		for (Circle circle : cityCircles) {
			if (circle.getCenterX() == cityVertex.getX() && circle.getCenterY() == cityVertex.getY()) {
				return circle;
			}
		}
		return null;
	}
}
