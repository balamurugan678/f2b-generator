package com.gcp.poc.f2b.generator.helpers;

import com.sun.tools.internal.xjc.model.Model;
import org.junit.Test;

import java.io.IOException;

public class ModelHelperTest {
    @Test
    public void generateCSV() throws IOException {
        ModelHelper modelHelper = new ModelHelper();
        System.out.println(modelHelper.generatePartyCSV());
    }
}
