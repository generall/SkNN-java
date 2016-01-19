package com.generall.app.sknn;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

/**
 * Created by generall on 06.01.16.
 */
public class WekaLoader implements Loader {

    public Instances loadARFF(String fname){
        try {
            DataSource source = new DataSource(fname);
            Instances data = source.getDataSet();
            if (data.classIndex() == -1)
                data.setClassIndex(data.numAttributes() - 1);
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
