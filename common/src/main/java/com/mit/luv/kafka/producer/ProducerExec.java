package com.mit.luv.kafka.producer;

import java.util.Properties;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import com.mit.luv.kafka.producer.ProducerExec;
import com.mit.utils.ConfigUtils;

public class ProducerExec {

	private Producer<byte[], byte[]> producer;
	public static ProducerExec Instance = new ProducerExec();

	public ProducerExec() {
		init();
	}

	private void init() {
		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, ConfigUtils.getConfig().getProperty("kafka.broker"));
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, ConfigUtils.getConfig().getProperty("kafka.serializer"));
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ConfigUtils.getConfig().getProperty("kafka.serializer"));
		props.put(ProducerConfig.ACKS_CONFIG, ConfigUtils.getConfig().getString("kafka.ack", "1"));
		props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, ConfigUtils.getConfig().getString("kafka.buffer", "33554432"));
		props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, ConfigUtils.getConfig().getString("kafka.compression", "none"));
		props.put(ProducerConfig.RETRIES_CONFIG, ConfigUtils.getConfig().getString("kafka.retries", "0"));
		props.put(ProducerConfig.CLIENT_ID_CONFIG, ConfigUtils.getConfig().getString("kafka.clientId", "socket"));

		producer = new KafkaProducer<byte[], byte[]>(props);
	}

	public void send(ProducerRecord<byte[], byte[]> record) {
		Future<RecordMetadata> result = producer.send(record);
	}

}
