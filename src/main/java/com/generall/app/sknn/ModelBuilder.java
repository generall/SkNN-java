package com.generall.app.sknn;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Range;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Model factory by instances.
 * Created by generall on 06.01.16.
 */
public class ModelBuilder {


    private void completeSeq(Model model){
        model.currentNode.connectedTo.put(model.endNode.getLabel(), model.endNode);
        model.currentNode = model.initNode;
    }

    private void makeLink(Model model, Node node) throws Exception {
        Node nextNode = model.currentNode.connectedTo.get(node.label);
        if((nextNode != null) && (nextNode != node)){
            throw new Exception("Incorrect model construction detected!");
        }
        model.currentNode.connectedTo.put(node.label, node);
        model.currentNode = node;
    }

    /**
     * Add instance to the appropriate dataset in node
     * @param node node to modify
     * @param inst instance to add
     * @param label label to use
     */
    private void addInstance(Node node, Instance inst, String label){
        node.dataset.add(inst);
        SearchDataIndex sdi = node.datasets.get(label);
        if (sdi == null){
            sdi = new SearchDataIndex();
            sdi.data = new Instances(node.dataset, 1);
            node.datasets.put(label, sdi);
        }
        sdi.data.add(inst);

    }

    public Model construct(Instances data, String label_attr_name, String seq_attr_name, String output_attr_name, Model oldModel, String attrRange) throws Exception {

        Model model = oldModel != null ? oldModel : new Model(data);

        Attribute label_attr    = label_attr_name  != null ? data.attribute(label_attr_name)  : data.classAttribute() ;
        Attribute output_attr   = output_attr_name != null ? data.attribute(output_attr_name) : label_attr;
        Attribute seq_attr      = seq_attr_name    != null ? data.attribute(seq_attr_name)    : data.attribute(0);


        Map<String, Node> labelToNode = model.labelToNode;
        Set<String> labels  = model.labels;
        Set<String> outputs = model.outputs;
        List<Node> allNodes = model.nodes;

        String prevSeq = "";
        boolean first = true;

        for(Instance ins : data){
            String label = ins.stringValue(label_attr);
            String output = ins.stringValue(output_attr);
            String seq = ins.stringValue(seq_attr);

            Node node = labelToNode.get(label);
            if(node == null){
                node = new Node(data)
                        .setLabel(label)
                        .setOutput(output);
                labels.add(label);
                allNodes.add(node);
                labelToNode.put(label, node);
            }else{
                if(!node.output.equals(output) || !node.label.equals(label)){
                    throw new Exception("Label may not have multiple outputs");
                }
            }
            outputs.add(output);

            // We will create link now.
            if(!seq.equals(prevSeq) && !first){
                // Starting new seq.
                completeSeq(model);
            }
            // Add instance to the current node
            addInstance(model.currentNode, ins, label);

            makeLink(model, node);

            prevSeq = seq;

            first = false;


        }

        completeSeq(model);

        // Exclude attribultes from distance calculation

        Integer numOfattributes = data.numAttributes();

        Set<Integer> selection = new HashSet<Integer>();



        if(attrRange == null){
            attrRange="first-last";
        }

        Range range = new Range(attrRange);
        range.setUpper(numOfattributes - 1);
        int[] custom_selection = range.getSelection();
        for(int i=0 ; i < custom_selection.length; i++ ){
            selection.add(custom_selection[i]);
        }

        selection.remove(label_attr.index());
        selection.remove(output_attr.index());
        selection.remove(seq_attr.index());

        int[] indices = new int[selection.size()];
        int i = 0;
        for(Integer sel : selection){
            indices[i] = sel;
            i++;
        }

        model.attributeRange = Range.indicesToRangeList(indices);

        return model;
    }


    public void constructSearchers(Model model, Integer k, NodeConstructor constructor ){
        try {
            for(Node node : model.nodes) {

                node.setK(k);
                for(SearchDataIndex sdi : node.datasets.values()){
                    constructor.InitSearcher(sdi, node, model);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
