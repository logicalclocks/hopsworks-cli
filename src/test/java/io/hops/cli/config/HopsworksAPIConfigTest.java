package io.hops.cli.config;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HopsworksAPIConfigTest {


    @Test
    public void getPathTest() {
        HopsworksAPIConfig config = new HopsworksAPIConfig("admin@kth.se", "admin","http://bbc6.sics.se:8080/hopsworks-api/api", "test");
        assertEquals("should return the the path",config.getPath(),"/hopsworks-api/api");

    }



}
