package com.gcp.poc.f2b.generator.helpers;

import freemarker.template.TemplateException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

/**
 * Created by vincentli on 14/08/2017.
 */
public class BasketGeneratorHelperTest {

    private PubsubHelper pubsubHelper;
    private BasketGeneratorHelper basketGeneratorHelper;

    @Before
    public void init() throws IOException {
        pubsubHelper = new PubsubHelper();
        basketGeneratorHelper = new BasketGeneratorHelper(pubsubHelper);
    }

    @Test
    public void generateMany() throws IOException, InterruptedException, ExecutionException, TemplateException {
        basketGeneratorHelper.generate(25000);
    }

    @Test
    public void generateFive() throws InterruptedException, TemplateException, ExecutionException, IOException {
        basketGeneratorHelper.generate(5);
    }

    @Test
    public void generateInvalidParty() throws InterruptedException, TemplateException, ExecutionException, IOException {
        basketGeneratorHelper.generateInvalidParty(false, true);
    }

    @Test
    public void generateDuplicatePair() throws InterruptedException, TemplateException, ExecutionException, IOException {
        basketGeneratorHelper.generateDuplicatePair(true, false);
    }
}
