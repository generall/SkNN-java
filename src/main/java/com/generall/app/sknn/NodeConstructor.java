package com.generall.app.sknn;

/**
 * Interface to init Node search measure and index
 * Created by generall on 19.01.16.
 */
public interface NodeConstructor {
    /**
     * Interface to init Node search measure and index
     * @param dataIndex index to init
     * @param node node, that contains index
     * @param model model, that contains node
     */
    void InitSearcher(SearchDataIndex dataIndex, Node node, Model model) throws Exception;

}
