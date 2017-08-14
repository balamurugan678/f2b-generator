package com.gcp.poc.f2b.generator.helpers;

import com.fasterxml.jackson.xml.XmlMapper;
import com.gcp.poc.f2b.generator.model.Book;
import com.gcp.poc.f2b.generator.model.Party;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ModelHelper {
    public <T> List<T> loadModel(String folder, Class<T> type) throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        List<T> model = new ArrayList<>();
        String modelPath = getClass().getClassLoader().getResource("model/"+folder).getPath();
        try (Stream<Path> paths = Files.walk(Paths.get(modelPath))) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach((Path path) -> {
                        File file = path.toFile();
                        try {
                            T value = xmlMapper.readValue(file, type);
                            model.add(value);
                        } catch (IOException e) {
                            System.out.println("Could not load model: "+path.toString());
                            e.printStackTrace();
                            System.exit(-1);
                        }
                    });
        }
        return model;
    }

    public String generateBookCSV() throws IOException {
        StringBuilder csv = new StringBuilder();

        csv.append("BookId,BookName,BusinessLine,Desk,PrimaryTrader,EntityId,Location,Region\n");
        List<Book> books = loadModel("books", Book.class);
        books.forEach(book -> {
            csv.append(String.format("%s,%s,%s,%s,%s,%s\n",
                    book.getBookId(),
                    book.getBookName(),
                    book.getBusinessLine(),
                    book.getDesk(),
                    book.getPrimaryTrader(),
                    book.getEntityId(),
                    book.getTradingLocation(),
                    book.getTradingRegion()));
        });

        return csv.toString();
    }

    public String generatePartyCSV() throws IOException {
        StringBuilder csv = new StringBuilder();

        csv.append("PartyId,PartyBIC,LegalName,VerificationStatus,EntityStatus,EffectiveFrom,CountryOfHeadOffice\n");
        List<Party> parties = loadModel("parties", Party.class);
        parties.forEach(party -> {
            csv.append(String.format("%s,%s,%s,%s,%s,%s,%s\n",
                    party.getBody().getMain().getPartyId(),
                    party.getBody().getMain().getPartyBIC(),
                    party.getBody().getLegalName(),
                    party.getBody().getMain().getVerificationStatus(),
                    party.getBody().getMain().getEntityStatus(),
                    party.getBody().getMain().getEffectiveFrom(),
                    party.getBody().getCountryOfHeadOffice()));
        });

        return csv.toString();
    }
}
