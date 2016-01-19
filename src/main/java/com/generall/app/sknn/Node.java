package com.generall.app.sknn;

import weka.core.DistanceFunction;
import weka.core.Instances;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Node of SkNN graph
 */
public class Node implements Serializable {

    private static final long serialVersionUID = 3071020034685572543L;

    public Instances dataset;

    public Map<String, Node> connectedTo;
    public Map<String, SearchDataIndex> datasets;

    /**
     * Unique identifier of state machine
     */
    public String label;

    /**
     * Producing value (other nodes may have same output)
     */
    public String output;

    protected DistanceFunction distanceFunction;

    public Integer k = 1;

    public Integer getK() {
        return k;
    }

    public Node setK(Integer k) {
        this.k = k;
        return this;
    }

    public String getOutput() {
        return output;
    }

    public Node setOutput(String _output) {
        output = _output;
        return this;
    }

    public String getLabel() {
        return label;
    }

    public Node setLabel(String label) {
        this.label = label;
        return this;
    }

    public Node(Instances init){
        dataset = new Instances(init, 0);
        connectedTo = new HashMap<String, Node>();
        datasets = new HashMap<String, SearchDataIndex>();
    }

}
