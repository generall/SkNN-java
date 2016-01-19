package com.generall.app.sknn;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.neighboursearch.NearestNeighbourSearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        public List< Map< String, Double > > distances;
        public List< Map< String, Node > > path;
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
        res.distances = new ArrayList<Map<String, Double>>();
        res.path = new ArrayList<Map<String, Node>>();

        HashMap<String, Double> init_dists = new HashMap<String, Double>();
        HashMap<String, Node> init_path = new HashMap<String, Node>();

        init_dists.put(model.initNode.getLabel(), 0.0);
        init_path.put(model.initNode.getLabel(), model.initNode);

        res.distances.add(init_dists);
        res.path.add(init_path);

        HashMap<String, Double> last_dists = init_dists;
        HashMap<String, Node> last_path = init_path;

        for(Instance inst : data){
            HashMap<String, Double> current_dists = new HashMap<String, Double>();
            HashMap<String, Node> current_path = new HashMap<String, Node>();
            System.out.println("For " + inst.toString());
            // determine all accessible vertex on step idx
            for(Map.Entry<String, Double> distance : last_dists.entrySet()){
                String label = distance.getKey();
                Double prev_dist = distance.getValue();
                Node node = last_path.get(label);

                for(Map.Entry<String, Node> connection : node.connectedTo.entrySet()){
                    String connection_label = connection.getKey();
                    Double local_dist;
                    System.out.println("Match: " + label + " -> " + connection_label);
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
                        current_path.put(connection_label, connection.getValue());
                    }
                }
            }
            last_path = current_path;
            last_dists = current_dists;
            res.path.add(current_path);
            res.distances.add(current_dists);
        }

        // do not estimate path with final node not connected to the :end
        for(Map.Entry<String, Node> connection: last_path.entrySet()){
            String label = connection.getKey();
            Node node = connection.getValue();
            if(node.connectedTo.get( model.endNode.getLabel() ) == null){
                last_dists.put(label, Double.POSITIVE_INFINITY);
            }
        }
        return res;
    }


    public void tagg(Instances data){

    }
}
