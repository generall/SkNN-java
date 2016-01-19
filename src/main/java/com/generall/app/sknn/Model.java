package com.generall.app.sknn;

import weka.core.Instances;

import java.io.Serializable;
import java.util.*;

/**
 * Created by generall on 06.01.16.
 */
public class Model implements Serializable {

    private static final long serialVersionUID = 2098503218487873198L;

    public Map<String, Node> labelToNode;

    public Set<String> labels;

    public Set<String> outputs;

    public Node currentNode;

    public Node initNode;
    public Node endNode;

    public String attributeRange;

    /**
     * All nodes in current model
     */
    public List<Node> nodes;

    /**
     * Creates initial and end nodes
     */
    public Model(Instances data){
        String end  = ":end";
        String init = ":init";

        endNode = new Node(data).setLabel(end);
        initNode = new Node(data).setLabel(init);

        nodes = new ArrayList<Node>();
        labels = new HashSet<String>();
        outputs = new HashSet<String>();
        labelToNode = new HashMap<String, Node>();

        labels.add(end);
        labels.add(init);

        labelToNode.put(init, initNode);
        labelToNode.put(end , endNode);

        nodes.add(initNode);
        nodes.add(endNode);

        currentNode = initNode;

        attributeRange = "first-last";
    }

}
