package com.gcp.poc.f2b.generator.helpers;

import freemarker.template.TemplateException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class SwapGeneratorHelperTest {
    private PubsubHelper pubsubHelper;
    private SwapGeneratorHelper swapGeneratorHelper;

    @Before
    public void init() throws IOException {
        pubsubHelper = new PubsubHelper();
        swapGeneratorHelper = new SwapGeneratorHelper(pubsubHelper);
    }

    @Test
    public void generateMany() throws IOException, InterruptedException, ExecutionException, TemplateException {
        swapGeneratorHelper.generate(2500);
    }

    @Test
    public void generateFive() throws InterruptedException, TemplateException, ExecutionException, IOException {
        swapGeneratorHelper.generate(5);
    }
}
