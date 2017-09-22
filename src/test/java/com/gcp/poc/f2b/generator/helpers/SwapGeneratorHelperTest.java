package com.gcp.poc.f2b.generator.helpers;

import freemarker.template.TemplateException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class SwapGeneratorHelperTest {
    private PubsubHelper pubsubHelper;
    private BigTableHelper bigTableHelper;
    private SwapGeneratorHelper swapGeneratorHelper;

    @Before
    public void init() throws IOException {
        pubsubHelper = new PubsubHelper();
        bigTableHelper = new BigTableHelper();
        swapGeneratorHelper = new SwapGeneratorHelper(pubsubHelper, bigTableHelper);
    }

    @Test
    public void generateMany() throws IOException, InterruptedException, ExecutionException, TemplateException {
        swapGeneratorHelper.generate(25000);
    }

    @Test
    public void generateFive() throws InterruptedException, TemplateException, ExecutionException, IOException {
        swapGeneratorHelper.generate(5);
    }
}
