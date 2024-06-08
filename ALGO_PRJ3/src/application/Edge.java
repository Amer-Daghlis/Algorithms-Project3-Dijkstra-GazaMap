package application;

public class Edge {

	private double distance;
	private Vertex startVertex;
	private Vertex targetVertex;

	public Edge(double distance, Vertex startVertex, Vertex targetVertex) {
		this.distance = distance;
		this.startVertex = startVertex;
		this.targetVertex = targetVertex;

	}

	public double getWeight() {
		return distance;
	}

	public void setWeight(double weight) {
		this.distance = weight;
	}

	public Vertex getStartVertex() {
		return startVertex;
	}

	public void setStartVertex(Vertex startVertex) {
		this.startVertex = startVertex;
	}

	public Vertex getTargetVertex() {
		return targetVertex;
	}

	public void setTargetVertex(Vertex targetVertex) {
		this.targetVertex = targetVertex;
	}
}