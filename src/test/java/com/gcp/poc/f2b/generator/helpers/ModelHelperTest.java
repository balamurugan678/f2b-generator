package com.gcp.poc.f2b.generator.helpers;

import org.junit.Test;

import java.io.IOException;

public class ModelHelperTest {
    @Test
    public void generatePartyCSV() throws IOException {
        ModelHelper modelHelper = new ModelHelper();
        System.out.println(modelHelper.generatePartyCSV());
    }

    @Test
    public void generateBookCSV() throws IOException {
        ModelHelper modelHelper = new ModelHelper();
        System.out.println(modelHelper.generateBookCSV());
    }
}
