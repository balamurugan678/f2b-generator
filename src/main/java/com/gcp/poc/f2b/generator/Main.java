package com.gcp.poc.f2b.generator;

import com.gcp.poc.f2b.generator.helpers.BigQueryHelper;
import com.gcp.poc.f2b.generator.model.Book;
import com.gcp.poc.f2b.generator.model.Party;
import com.solacesystems.jcsmp.JCSMPException;
import freemarker.template.TemplateException;

import com.fasterxml.jackson.xml.XmlMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

public class Main {

    public static void main( String[] args ) throws JCSMPException, IOException, TemplateException, ParseException {



        // Read trade templates
        //List

        // Create solae helper
        //SolaceHelper solaceHelper = new SolaceHelper("","","","");
        //solaceHelper.init();




        Generator fxGenerator = new Generator("fx");


        //BigQueryHelper bigQueryHelper = new BigQueryHelper();
        //bigQueryHelper.createDataset("vincenttest");
        //bigQueryHelper.test(x);


        //for each day of last week, generate x trades
        ThreadLocalRandom random = ThreadLocalRandom.current();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        //todo: skip weekend days (and holidays?)
        LocalDate start = LocalDate.now().minusDays(5);
        LocalDate end = LocalDate.now();

        //Date startDate =  formatter.parse("2010-12-20");
        //Date endDate = formatter.parse("2010-12-26");
        //LocalDate start = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        //LocalDate end = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        int sequenceNumber = 1;
        for (LocalDate date = start; date.isBefore(end); date = date.plusDays(1)) {
            LocalTime startTime = LocalTime.of(07, 30);
            LocalTime endTime = LocalTime.of(05, 30);
            int messagesGenerated = 0;
            for (LocalTime time = startTime; time.isBefore(endTime) || messagesGenerated < 5; time = time.plusNanos(3000 + random.nextInt(4000))) {
                System.out.print(fxGenerator.next(date.atTime(time), sequenceNumber++));
                messagesGenerated++;
            }
        }



 /*       for (int i=0; i< 1000; i++) {
            XMLMessage message = solaceHelper.createMessage();
            message.writeAttachment(randomMessageStream);
            solaceHelper.sendMessage(message);
        }*/
    }


}