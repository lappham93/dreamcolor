package com.mit.luv.kafka.consumer;

import java.util.Properties;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.javaapi.consumer.ConsumerConnector;

import com.mit.utils.ConfigUtils;

public abstract class ConsumerConnect {
	private final ConsumerConnector consumer;

	public ConsumerConnect() {
		consumer = Consumer.createJavaConsumerConnector(createConsumerConfig());
	}

	public ConsumerConnect(String group) {
		consumer = Consumer.createJavaConsumerConnector(createConsumerConfig(group));
	}

	private ConsumerConfig createConsumerConfig() {
		Properties props = new Properties();
		props.put("zookeeper.connect", ConfigUtils.getConfig().getProperty("kafka.consumer.hosts"));
		props.put("zookeeper.session.timeout.ms", ConfigUtils.getConfig().getProperty("kafka.consumer.timeout"));
		props.put("zookeeper.sync.time.ms", ConfigUtils.getConfig().getProperty("kafka.consumer.synctime"));
		props.put("auto.commit.interval.ms", ConfigUtils.getConfig().getProperty("kafka.consumer.autocommit"));
		props.put("group.id", ConfigUtils.getConfig().getProperty("kafka.consumer.group"));
		//props.put("consumer.id", ConfigUtils.getConfig().getProperty("kafka.consumer.id"));
		props.put("rebalance.max.retries", ConfigUtils.getConfig().getProperty("kafka.consumer.retries"));

		return new ConsumerConfig(props);

	}

	private ConsumerConfig createConsumerConfig(String consumerGroup) {
		Properties props = new Properties();
		props.put("zookeeper.connect", ConfigUtils.getConfig().getProperty("kafka.consumer.hosts"));
		props.put("zookeeper.session.timeout.ms", ConfigUtils.getConfig().getProperty("kafka.consumer.timeout"));
		props.put("zookeeper.sync.time.ms", ConfigUtils.getConfig().getProperty("kafka.consumer.synctime"));
		props.put("auto.commit.interval.ms", ConfigUtils.getConfig().getProperty("kafka.consumer.autocommit"));
		props.put("group.id", consumerGroup);
		//props.put("consumer.id", ConfigUtils.getConfig().getProperty("kafka.consumer.id"));
		props.put("rebalance.max.retries", ConfigUtils.getConfig().getProperty("kafka.consumer.retries"));

		return new ConsumerConfig(props);

	}

	public ConsumerConnector getConsumer() {
		return consumer;
	}

	public abstract int start();
}
