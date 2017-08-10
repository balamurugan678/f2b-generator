package com.gcp.poc.f2b.generator;

import com.fasterxml.jackson.xml.XmlMapper;
import com.gcp.poc.f2b.generator.helpers.DateHelper;
import com.gcp.poc.f2b.generator.helpers.RandomHelper;
import com.gcp.poc.f2b.generator.model.Book;
import com.gcp.poc.f2b.generator.model.ExchangeRate;
import com.gcp.poc.f2b.generator.model.Party;
import com.gcp.poc.f2b.generator.model.Trade;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.codehaus.jackson.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SwapGenerator {

    private final Configuration configuration;

    private final List<Book> books;
    private final List<Party> parties;
    private final List<ExchangeRate> exchangeRates;

    private final List<Template> templates;

    private final ThreadLocalRandom random;

    public SwapGenerator(String templateFolder) throws IOException {
        random = ThreadLocalRandom.current();

        // Load models
        this.books = loadModel("books/"+templateFolder, Book.class);;
        this.parties = loadModel("parties", Party.class);
        IntStream.range(0,this.parties.size()).forEach(i ->
            parties.get(i).setPartyId("party"+(i+2))
        );
        this.exchangeRates = loadExchangeRates();

        // Start free marker
        configuration = new Configuration(Configuration.VERSION_2_3_26);
        //configuration.setDirectoryForTemplateLoading(new File(this.getClass().getClassLoader().getResource("templates/"+templateFolder).getPath()));
        configuration.setDefaultEncoding("UTF-8");

        // Sets how errors will appear
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        // Don't log exceptions inside FreeMarker that it will thrown at you anyway
        configuration.setLogTemplateExceptions(false);

        // Load templates
        templates = this.loadTemplates(configuration, templateFolder);
    }

    public String next(LocalDateTime dateTime) throws IOException, TemplateException {
        Map<String, Object> root = new HashMap<>();
        root.put("dateHelper", new DateHelper());
        root.put("timestamp", dateTime);

        // Add random helper and dealId to template
        RandomHelper randomHelper = new RandomHelper(random);
        root.put("random", randomHelper);
        root.put("dealId", randomHelper.numberDigits(12));

        // Select random book
        Book book = books.get(random.nextInt(books.size()));
        root.put("book", book);

        // Select id and parties for trades
        Set<Party> partiesUsed = new HashSet<>();

        // Select random counter party
        Party party = parties.get(random.nextInt(parties.size()));
        root.put("party", party);
        if (!partiesUsed.contains(party)) {
            partiesUsed.add(party);
        }

        // Select random exchange rate
        ExchangeRate exchangeRate = exchangeRates.get(random.nextInt(exchangeRates.size()));
        root.put("exchangeRate", exchangeRate);
        root.put("parties", partiesUsed);

        // Select template
        Template template = templates.get(random.nextInt(templates.size()));

        // todo: configurable: write output (converted to json) straight to big query or straight to solace
        Writer out = new StringWriter();//new OutputStreamWriter(System.out);
        template.process(root, out);

        return out.toString();
    }

    private List<ExchangeRate> loadExchangeRates() throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        String modelPath = Main.class.getClassLoader().getResource("model/other/exchangeRates.xml").getPath();
        return xmlMapper.readValue(new File(modelPath), new TypeReference<List<ExchangeRate>>() {});//;.getExchangeRate();
    }

    private <T> List<T> loadModel(String folder, Class<T> type) throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        List<T> model = new ArrayList<>();
        String modelPath = Main.class.getClassLoader().getResource("model/"+folder).getPath();
        try (Stream<Path> paths = Files.walk(Paths.get(modelPath))) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach((Path path) -> {
                        File bookFile = path.toFile();
                        try {
                            T value = xmlMapper.readValue(bookFile, type);
                            model.add(value);
                        } catch (IOException e) {
                            e.printStackTrace();
                            System.exit(-1);
                        }
                    });
        }
        return model;
    }

    private List<Template> loadTemplates(Configuration configuration, String folder) throws IOException {

        String templatesPath = this.getClass().getClassLoader().getResource("templates/"+folder).getPath();
        configuration.setDirectoryForTemplateLoading(new File(templatesPath));

        List<Template> templates = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(Paths.get(templatesPath))) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach((Path path) -> {
                        File templateFile = path.toFile();
                        try {
                            templates.add(configuration.getTemplate(path.getFileName().toString()));
                        } catch (IOException e) {
                            e.printStackTrace();
                            System.exit(-1);
                        }
                    });
        }
        return templates;
    }
}
