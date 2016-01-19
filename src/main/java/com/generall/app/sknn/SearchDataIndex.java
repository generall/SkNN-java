package com.generall.app.sknn;

import weka.core.Instances;
import weka.core.neighboursearch.NearestNeighbourSearch;

/**
 * Class for map in SkNN
 */
public class SearchDataIndex {
    /**
     * Instances of the same type in single node.
     */
    public Instances data;

    /**
     * Search instance for data
     */
    public NearestNeighbourSearch search;
}
