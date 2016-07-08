package com.mit.kafka.consumer;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;

public abstract class ConsumerProcess implements Runnable {
	KafkaStream<byte[], byte[]> stream;

	public ConsumerProcess(KafkaStream<byte[], byte[]> stream) {
		this.stream = stream;
	}

	@Override
	public void run() {
		ConsumerIterator<byte[], byte[]> it = stream.iterator();
		while (it.hasNext()) {
			process(new String(it.next().message()));
		}

	}

	public abstract void process(String msg);

}
