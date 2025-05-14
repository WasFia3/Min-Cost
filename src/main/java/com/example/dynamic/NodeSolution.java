package com.example.dynamic;

public class NodeSolution {
    private int optimalCost; // Minimum or optimal cost to reach this cell
    private String optimalPath; // Description or identifier of the optimal path leading to this cell
    private NodeSolution[] nodeSolutions; // Array of NodeSolution objects, potentially used for tracking additional path details or branching decisions

    public NodeSolution(int optimalCost, String optimalPath) {
        this.optimalCost = optimalCost;
        this.optimalPath = optimalPath;
    }

    public int getOptimalCost() {
        return optimalCost;
    }

    public void setOptimalCost(int optimalCost) {
        this.optimalCost = optimalCost;
    }

    public String getOptimalPath() {
        return optimalPath;
    }

    public void setOptimalPath(String optimalPath) {
        this.optimalPath = optimalPath;
    }

    public NodeSolution[] getCellInfos() {
        return nodeSolutions;
    }

    public void setCellInfos(NodeSolution[] nodeSolutions) {
        this.nodeSolutions = nodeSolutions;
    }
}
