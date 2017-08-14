package com.gcp.poc.f2b.generator.helpers;

import com.gcp.poc.f2b.generator.SwapGenerator;
import freemarker.template.TemplateException;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDateTime;

public class SwapGeneratorTest {
    @Test
    public void generateOnePrintToConsole() throws IOException, TemplateException {
        SwapGenerator swapGenerator = new SwapGenerator();
        String xml = swapGenerator.next(LocalDateTime.now());
        System.out.println(xml);
    }
}
