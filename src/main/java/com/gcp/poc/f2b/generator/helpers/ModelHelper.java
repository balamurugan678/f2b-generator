package com.gcp.poc.f2b.generator.helpers;

import com.fasterxml.jackson.xml.XmlMapper;
import com.gcp.poc.f2b.generator.Main;
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

    public String generatePartyCSV() throws IOException {
        StringBuilder csv = new StringBuilder();

        csv.append("PartyId,PartyBIC\n");
        List<Party> parties = loadModel("parties", Party.class);
        parties.forEach(party -> {
            csv.append(party.getBody().getMain().getPartyId()+","+party.getBody().getMain().getPartyBIC()+"\n");
        });

        return csv.toString();
    }
}
