package com.generall.app.sknn;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.neighboursearch.NearestNeighbourSearch;

import java.util.*;

enum NearestMethod { DEFAULT, AVARAGE };

/**
 * SkNN tagger
 * Implementation of Viterbi algorithm
 */
public class Tagger {


    public Model getModel() {
        return model;
    }

    public Tagger setModel(Model model) {
        this.model = model;
        return this;
    }

    private Model model;

    NearestMethod dist_strategy = NearestMethod.DEFAULT;

    public class ViterbiResult {
        public Stack< Map< String, Double > > distances;
        public Stack< Map< String, Node > > path;
    }

    public Double get_distance(Instance inst, NearestNeighbourSearch search, Integer k) throws Exception {
        Instances nearest = search.kNearestNeighbours(inst, k);
        Double dist = search.getDistanceFunction().distance(nearest.firstInstance(), inst);

        double [] distances = search.getDistances();
        Double res = 0.0;
        switch (dist_strategy){
            case DEFAULT:
            case AVARAGE:
                for(double x: distances){
                    res += x;
                }
                res = res / distances.length;
                break;
            default:
                throw new Exception("Not implemented");
        }
        return res;
    }

    public ViterbiResult viterbi( Instances data ) throws Exception {
        ViterbiResult res = new ViterbiResult();
        res.distances = new Stack<Map<String, Double>>();
        res.path = new Stack<Map<String, Node>>();

        HashMap<String, Double> init_dists = new HashMap<String, Double>();
        HashMap<String, Node> init_path = new HashMap<String, Node>();

        init_dists.put(model.initNode.getLabel(), 0.0);
        init_path.put(model.initNode.getLabel(), model.initNode);

        res.distances.push(init_dists);
        //res.path.push(init_path);

        HashMap<String, Double> last_dists = init_dists;
        HashMap<String, Node> last_path = init_path;

        for(Instance inst : data){
            HashMap<String, Double> current_dists = new HashMap<String, Double>();
            HashMap<String, Node> current_path = new HashMap<String, Node>();
            // determine all accessible vertex on step idx
            for(Map.Entry<String, Double> distance : last_dists.entrySet()){
                String label = distance.getKey();
                Double prev_dist = distance.getValue();
                Node node = model.labelToNode.get(label);

                for(Map.Entry<String, Node> connection : node.connectedTo.entrySet()){
                    String connection_label = connection.getKey();
                    Double local_dist;
                    if( connection_label != model.endNode.getLabel() ) {
                        SearchDataIndex dataset = node.datasets.get(connection_label);
                        local_dist = get_distance(inst, dataset.search, node.getK());
                    }else{
                        local_dist = Double.POSITIVE_INFINITY;
                    }
                    System.out.println(local_dist);
                    Double sum_dist = local_dist + prev_dist;
                    Double current_min_dist = current_dists.get(connection_label);
                    if(current_min_dist == null || sum_dist < current_min_dist){
                        current_dists.put(connection_label, sum_dist);
                        current_path.put(connection_label, node);
                    }
                }
            }
            last_path = current_path;
            last_dists = current_dists;
            res.path.push(current_path);
            res.distances.push(current_dists);
        }

        // do not estimate path with final node not connected to the :end
        for(Map.Entry<String, Node> connection: last_path.entrySet()){
            String label = connection.getKey();
            Node node = model.labelToNode.get(label);
            if(node.connectedTo.get( model.endNode.getLabel() ) == null){
                last_dists.put(label, Double.POSITIVE_INFINITY);
            }
        }

        return res;
    }


    public void tagg(Instances data) throws Exception {
        ViterbiResult vr = viterbi(data);
        Map<String, Double> last_dists = vr.distances.pop();
        String min_key = Collections.min(last_dists.entrySet(), new Comparator<Map.Entry<String,Double>>(){
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                return o1.getValue() > o2.getValue()? 1:-1;
            }
        }).getKey();
        Node last_node = model.labelToNode.get(min_key);
        String min_output = last_node.getOutput();
        last_dists.values();
        int i = 0;
        while(!vr.path.empty()){
            Map<String, Node> path = vr.path.pop();
            Node node = path.get(min_key);
            data.instance(data.size() - 1 - i).setClassValue(min_output);
            min_key = node.getLabel();
            min_output = node.getOutput();
            i++;
        }
    }
}
