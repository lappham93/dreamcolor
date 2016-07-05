/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mit.kafka.consumer;

import java.util.Properties;

import kafka.consumer.ConsumerConfig;
import kafka.javaapi.consumer.ConsumerConnector;

import com.mit.utils.ConfigUtils;

public abstract class Consumer {
	private final ConsumerConnector consumer;
	private final String topic;

	public Consumer(String topic) {
		consumer = kafka.consumer.Consumer
				.createJavaConsumerConnector(createConsumerConfig(topic));
		this.topic = topic;
	}

	private static ConsumerConfig createConsumerConfig(String topic) {
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

	public ConsumerConnector getConsumer() {
		return consumer;
	}

	public String getTopic() {
		return topic;
	}

	public abstract int start();
}
