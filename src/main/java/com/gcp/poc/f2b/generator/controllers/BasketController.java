package com.gcp.poc.f2b.generator.controllers;

import com.gcp.poc.f2b.generator.helpers.BasketGeneratorHelper;
import com.gcp.poc.f2b.generator.helpers.SwapGeneratorHelper;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/basket")
public class BasketController {

    @Autowired
    BasketGeneratorHelper basketGeneratorHelper;

    @RequestMapping("/generate")
    public String generate() throws InterruptedException, TemplateException, ExecutionException, IOException {
        return basketGeneratorHelper.generate();
    }
}