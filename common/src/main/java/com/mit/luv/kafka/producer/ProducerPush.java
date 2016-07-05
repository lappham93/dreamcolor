package com.mit.luv.kafka.producer;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.luv.kafka.producer.ProducerExec;
import com.mit.luv.kafka.producer.ProducerPush;

public class ProducerPush {
	private static Logger logger = LoggerFactory.getLogger(ProducerPush.class);

	public static void send(String topic, String msg) {
		try {
			ProducerRecord<byte[], byte[]> record = new ProducerRecord<byte[], byte[]>(topic, msg.getBytes());
			ProducerExec.Instance.send(record);
		} catch (Exception e) {
			logger.error("error ", e);
		}

	}

	public static void send(String topic, byte[] data) {
		try {
			ProducerRecord<byte[], byte[]> record = new ProducerRecord<byte[], byte[]>(topic, data);
			ProducerExec.Instance.send(record);
		} catch (Exception e) {
			logger.error("error ", e);
		}

	}
}
