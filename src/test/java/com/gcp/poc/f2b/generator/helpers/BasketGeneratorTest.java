package com.gcp.poc.f2b.generator.helpers;

import com.gcp.poc.f2b.generator.BasketGenerator;
import freemarker.template.TemplateException;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDateTime;

public class BasketGeneratorTest {
    @Test
    public void generateOnePrintToConsole() throws IOException, TemplateException {
        BasketGenerator basketGenerator = new BasketGenerator();
        String xml = basketGenerator.next(LocalDateTime.now());
        System.out.println(xml);
    }
}
