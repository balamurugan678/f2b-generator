package com.gcp.poc.f2b.generator.helpers;

import com.gcp.poc.f2b.generator.SwapGenerator;
import freemarker.template.TemplateException;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

public class SwapGeneratorTest {
    @Test
    public void generateOnePrintToConsole() throws IOException, TemplateException {
        SwapGenerator swapGenerator = new SwapGenerator();
        Map<String, Object> data = swapGenerator.nextData(LocalDateTime.now());
        String xml = swapGenerator.next(data);
        System.out.println(xml);
    }
}
