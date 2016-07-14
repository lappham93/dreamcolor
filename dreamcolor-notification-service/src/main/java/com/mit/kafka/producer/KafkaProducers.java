package com.mit.kafka.producer;

import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import com.mit.utils.ConfigUtils;

public class KafkaProducers{

	private Producer<String, String> producer;
	public static KafkaProducers Instance = new KafkaProducers();

	public KafkaProducers() {
		init();
	}

	private void init() {
		Properties props = new Properties();

		props.put("metadata.broker.list", ConfigUtils.getConfig().getProperty("kafka.broker"));
		props.put("serializer.class", ConfigUtils.getConfig().getProperty("kafka.serializer"));
		//props.put("partitioner.class", "kafka.producer.Partitioner");
		props.put("request.required.acks", "1");

		ProducerConfig config = new ProducerConfig(props);
		producer = new Producer<String, String>(config);
	}

	public void send(String topic, String msg) {
		KeyedMessage<String, String> data = new KeyedMessage<String, String>(topic, "", msg);
		producer.send(data);
	}
}
