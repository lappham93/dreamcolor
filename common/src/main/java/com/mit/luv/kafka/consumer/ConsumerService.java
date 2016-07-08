package com.mit.luv.kafka.consumer;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ConsumerService {
	private final Logger _logger = LoggerFactory.getLogger(getClass());
	private int numThread = 1;
	private final String topic;

	public ConsumerService(String topic) {
		this.topic = topic;
	}

	public ConsumerService(String topic, int numThread) {
		this.topic = topic;
		this.numThread = numThread;
	}

	public String getTopic() {
		return topic;
	}

	public int getNumThread() {
		return numThread;
	}

	public void assignAndRunStream(List<KafkaStream<byte[], byte[]>> streams) {
		try {
			ExecutorService executor = Executors.newFixedThreadPool(numThread);
		    for(KafkaStream<byte[], byte[]> stream : streams) {
		    	executor.execute(new ConsumerProcess(stream));
		    }
		} catch(Exception e) {
			_logger.error("assignAndRunStream error ", e);
		}
	}

	public abstract void execute(byte[] data);

	public class ConsumerProcess implements Runnable {
		KafkaStream<byte[], byte[]> stream;

		public ConsumerProcess() {
			super();
		}

		public ConsumerProcess(KafkaStream<byte[], byte[]> stream) {
			this.stream = stream;
		}

		@Override
		public void run() {
			if(stream != null) {
				ConsumerIterator<byte[], byte[]> it = stream.iterator();
				while (it.hasNext()) {
					execute(it.next().message());
				}
			}

		}
		public KafkaStream<byte[], byte[]> getStream() {
			return stream;
		}

		public void setStream(KafkaStream<byte[], byte[]> stream) {
			this.stream = stream;
		}
	}

}
