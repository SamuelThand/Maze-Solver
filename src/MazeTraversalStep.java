public record MazeTraversalStep(Coordinate coordinate, Coordinate parent, int initialCost, int heuristicsCost, Cell newState) {

    public int totalCost() {
        return this.initialCost + this.heuristicsCost;
    }

}
