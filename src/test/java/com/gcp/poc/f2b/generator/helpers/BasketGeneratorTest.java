package com.gcp.poc.f2b.generator.helpers;

import com.gcp.poc.f2b.generator.BasketGenerator;
import freemarker.template.TemplateException;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

public class BasketGeneratorTest {
    @Test
    public void generateOnePrintToConsole() throws IOException, TemplateException {
        BasketGenerator basketGenerator = new BasketGenerator();
        Map<String, Object> data = basketGenerator.nextData(LocalDateTime.now());
        String xml = basketGenerator.next(data);

        System.out.println(xml);
    }
}
