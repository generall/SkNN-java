package com.generall.app.sknn;

import weka.core.EuclideanDistance;
import weka.core.neighboursearch.NearestNeighbourSearch;
import weka.core.neighboursearch.VPTree;

/**
 * Example of simple node constructor with Euclidean Disatance and VP-Tree searcher
 * Created by generall on 19.01.16.
 */
public class SimpleNodeConstructor implements NodeConstructor {

    public void InitSearcher(SearchDataIndex dataIndex, Node node, Model model) throws Exception {
        EuclideanDistance df = new EuclideanDistance();
        NearestNeighbourSearch nns = new VPTree();

        df.setDontNormalize(true);
        // node.dataset; - to normalize across all instances (true way)
        df.setInstances(node.dataset);

        // setting attribute range
        df.setAttributeIndices(model.attributeRange);
        nns.setInstances(dataIndex.data);

        dataIndex.search = nns;
        dataIndex.search.setDistanceFunction(df);
    }
}
