package com.gcp.poc.f2b.generator.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.xml.XmlMapper;
import com.google.cloud.bigquery.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class BigQueryHelper {

    BigQuery bigquery;


    // note: run this first:  gcloud auth application-default login
    public BigQueryHelper() {
        bigquery = BigQueryOptions.getDefaultInstance().getService();

    }

    public Dataset createDataset(String datasetName) {
        DatasetInfo datasetInfo = DatasetInfo.newBuilder(datasetName).build();
        return bigquery.create(datasetInfo);
    }

    public void createTable() {
        TableId tableId = TableId.of("f2br", "vincenttest"+ThreadLocalRandom.current().nextInt());
        Field stringField = Field.of("StringField", Field.Type.string());
        Schema schema = Schema.of(stringField);
        StandardTableDefinition tableDefinition = StandardTableDefinition.of(schema);
        Table createdTable = bigquery.create(TableInfo.of(tableId, tableDefinition));

        //InsertAllRequest.RowToInsert.of()
        //createdTable.insert();

    }

    public void test(String x) throws IOException {



        TableId tableId = TableId.of("f2br", "vincenttest"+ThreadLocalRandom.current().nextInt());
        Field stringField = Field.of("StringField", Field.Type.string());
        Schema schema = Schema.of(stringField);
        StandardTableDefinition tableDefinition = StandardTableDefinition.of(schema);
        Table createdTable = bigquery.create(TableInfo.of(tableId, tableDefinition));

        //createdTable.getDefinition().getSchema();


        //InsertAllRequest.RowToInsert.of()
        //createdTable.insert();




        HashMap<String,Object> mapResult = new XmlMapper().readValue(x, HashMap.class);
        InsertAllResponse response = bigquery.insertAll(InsertAllRequest.newBuilder(tableId)
                .addRow(UUID.randomUUID().toString(), mapResult)
                .addRow(UUID.randomUUID().toString(), mapResult)
                .addRow(UUID.randomUUID().toString(), mapResult)
                .addRow(UUID.randomUUID().toString(), mapResult)
                .build());
    }
}
