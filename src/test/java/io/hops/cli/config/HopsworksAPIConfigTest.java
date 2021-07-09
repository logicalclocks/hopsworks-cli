package io.hops.cli.config;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

@Ignore
public class HopsworksAPIConfigTest {


    @Test
    public void getPathTest() {
//        HopsworksAPIConfig config = new HopsworksAPIConfig("admin@kth.se", "admin",
        HopsworksAPIConfig config = new HopsworksAPIConfig("apikey",
          "http://bbc3.sics.se:21756/hopsworks-api/api", "demo_spark_admin000");
        assertEquals("should return the the path",config.getPath(),"/hopsworks-api/api");

    }



}
