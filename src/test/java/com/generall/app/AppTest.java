package com.generall.app;

import com.generall.app.sknn.*;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import weka.core.Instances;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }


    public void testLoader(){
        WekaLoader wl = new WekaLoader();
        Instances insts = wl.loadARFF("test.arff");
        ModelBuilder mb = new ModelBuilder();
        try {
            Model model = mb.construct(insts, "class", "sequence_id", null, null, null);
            assertTrue(model.nodes.size() == 5);
            mb.constructSearchers(model, 1, new SimpleNodeConstructor());
            assertTrue(model.attributeRange.equals("2-3"));
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testViterbi(){
        WekaLoader wl = new WekaLoader();
        Instances train = wl.loadARFF("test2_train.arff");
        Instances test  = wl.loadARFF("test2_test.arff");

        ModelBuilder mb = new ModelBuilder();
        try {
            Model model = mb.construct(train, "class", "sequence_id", null, null, null);
            mb.constructSearchers(model, 1, new SimpleNodeConstructor());
            Tagger tagger = new Tagger().setModel(model);
            Tagger.ViterbiResult vr = tagger.viterbi(test);
            tagger.tagg(test);
            assertTrue(vr.distances.size() == 4);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }
}
