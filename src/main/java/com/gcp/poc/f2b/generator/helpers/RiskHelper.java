package com.gcp.poc.f2b.generator.helpers;

import com.gcp.poc.f2b.generator.Main;
import com.gcp.poc.f2b.generator.model.Party;
import com.gcp.poc.f2b.generator.model.Trade;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class RiskHelper {
    private final Template template;
    private final RandomHelper randomHelper;

    public RiskHelper() throws IOException {
        // Start free marker
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_26);
        //configuration.setDirectoryForTemplateLoading(new File(this.getClass().getClassLoader().getResource("templates/"+templateFolder).getPath()));
        configuration.setDefaultEncoding("UTF-8");

        // Sets how errors will appear
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        // Don't log exceptions inside FreeMarker that it will thrown at you anyway
        configuration.setLogTemplateExceptions(false);

        template = loadTemplate(configuration);

        randomHelper = new RandomHelper(ThreadLocalRandom.current());
    }

    public List<String> getBasketEntries(Map<String, Object> data) throws IOException, TemplateException {
        List<String> basketEntries = new ArrayList<>();

        Trade[] trades = (Trade[])data.get("trades");
        for(Trade trade : trades) {
            Map<String, Object> root = new HashMap<>();
            root.put("random", randomHelper);
            root.put("dateHelper", new DateHelper());
            root.put("tradeId", trade.getTradeId());
            root.put("timestamp", data.get("timestamp"));
            root.put("exchangeRate", trade.getExchangeRate());
            root.put("book", data.get("book"));
            root.put("counterpartyId", trade.getParty().getPartyId());
            root.put("currency", trade.getExchangeRate().getCurrency1());
            root.put("amount", trade.getAmount1());

            Writer out = new StringWriter();
            template.process(root, out);
            basketEntries.add(out.toString());

            // Generate risk entry for reverse cashflow
            root.put("currency", trade.getExchangeRate().getCurrency2());
            root.put("amount", trade.getAmount2());
            out = new StringWriter();
            template.process(root, out);
            basketEntries.add(out.toString());
        }

        return basketEntries;
    }

    public String getSwapEntry(Map<String, Object> data) throws IOException, TemplateException {

        Map<String, Object> root = new HashMap<>();
        root.put("random", randomHelper);
        root.put("dateHelper", new DateHelper());
        root.put("tradeId", data.get("tradeId1"));
        root.put("timestamp", data.get("timestamp"));
        root.put("exchangeRate", data.get("exchangeRate"));
        root.put("book", data.get("book"));
        root.put("counterpartyId", ((Party)data.get("party")).getPartyId());
        root.put("amount", data.get("amount"));

        Writer out = new StringWriter();
        template.process(root, out);

        return out.toString();
    }

    private Template loadTemplate(Configuration configuration) throws IOException {
        String templatePath = Main.class.getClassLoader().getResource("templates/risk").getPath();
        configuration.setDirectoryForTemplateLoading(new File(templatePath));
        return configuration.getTemplate("risk.json");
    }
}
