package com.gcp.poc.f2b.generator.helpers;

import com.google.bigtable.repackaged.com.google.cloud.hbase.BigtableConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class BigTableHelper {

    Connection connection;
    byte[] DATA_FAMILY = Bytes.toBytes("data");
    byte[] PAYLOAD_QUALIFIER = Bytes.toBytes("payload");
    byte[] SYS_FAMILY = Bytes.toBytes("sys");
    byte[] USER_FAMILY = Bytes.toBytes("user");
    byte[] KEY_QUALIFIER = Bytes.toBytes("key");
    byte[] VALUE_QUALIFIER = Bytes.toBytes("value");

    public BigTableHelper() {
        this("gcp-f2brb","dist-l1-ssd");
    }

    public BigTableHelper(String projectId, String instanceId) {
        connection = BigtableConfiguration.connect(projectId, instanceId);
    }

    // Note: This method must synchronously write data to big table before returning so uuid sent in pubsub can be used to retrieve data immediately
    public String write(String messageBody, String messageType, String messageFormat, LocalDate date) throws IOException {
        String uuid = UUID.randomUUID().toString();

        // Write metadata
        Table table = connection.getTable(TableName.valueOf("l1.metadata"));
        Put put = new Put(Bytes.toBytes(uuid));
        put.addColumn(SYS_FAMILY, KEY_QUALIFIER, Bytes.toBytes("messageType"));
        put.addColumn(SYS_FAMILY, VALUE_QUALIFIER, Bytes.toBytes(messageType));
        put.addColumn(SYS_FAMILY, KEY_QUALIFIER, Bytes.toBytes("messageFormat"));
        put.addColumn(SYS_FAMILY, VALUE_QUALIFIER, Bytes.toBytes(messageFormat));
        put.addColumn(SYS_FAMILY, KEY_QUALIFIER, Bytes.toBytes("date"));
        put.addColumn(SYS_FAMILY, VALUE_QUALIFIER, Bytes.toBytes(date.toString().replaceAll("-","")));
        table.put(put);

        // Write message body
        table = connection.getTable(TableName.valueOf("l1.messages"));
        put = new Put(Bytes.toBytes(uuid));
        put.addColumn(DATA_FAMILY, PAYLOAD_QUALIFIER, Bytes.toBytes(messageBody));
        table.put(put);

        return uuid;
    }
}
