public class MazeTraversalStep {

    private int stepNumber;
    private Coordinate location;
    private Coordinate parentLocation;
    private int initialCost;
    private int heuristicsCost;
    private Cell state;



    public MazeTraversalStep(int stepNumber, Coordinate location, Coordinate parentLocation, int initialCost, int heuristicsCost, Cell state) {
        this.stepNumber = stepNumber;
        this.location = location;
        this.parentLocation = parentLocation;
        this.initialCost = initialCost;
        this.heuristicsCost = heuristicsCost;
        this.state = state;
    }

    public MazeTraversalStep(Coordinate location, Cell state) {
        this.location = location;
        this.state = state;
    }

    public void setStepNumber(int stepNumber) {
        this.stepNumber = stepNumber;
    }

    public Cell getState() {
        return state;
    }

    public void setState(Cell state) {
        this.state = state;
    }

    public Coordinate getLocation() {
        return location;
    }

    public Coordinate getParentLocation() {
        return parentLocation;
    }

    public void setInitialCost(int initialCost) {
        this.initialCost = initialCost;
    }

    public int getInitialCost() {
        return initialCost;
    }

    public int getHeuristicsCost() {
        return heuristicsCost;
    }

    public int totalCost() {
        return this.initialCost + this.heuristicsCost;
    }

    public int getStepNumber() {
        return stepNumber;
    }
}
