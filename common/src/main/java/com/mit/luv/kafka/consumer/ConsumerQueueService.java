package com.mit.luv.kafka.consumer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import kafka.consumer.KafkaStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ConsumerQueueService extends ConsumerConnect {
	private final Logger _logger = LoggerFactory.getLogger(getClass());
	private List<ConsumerService> consumers = new LinkedList<ConsumerService>();

	public ConsumerQueueService() {}
	
	public ConsumerQueueService(String group) {
		super(group);
	}

	@Override
	public int start() {
		try {
			Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
			for(ConsumerService consumer : consumers) {
				topicCountMap.put(consumer.getTopic(), consumer.getNumThread());
			}

		    Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = getConsumer().createMessageStreams(topicCountMap);

		    for(ConsumerService consumer : consumers) {
		    	List<KafkaStream<byte[], byte[]>> streams =  consumerMap.get(consumer.getTopic());
			    consumer.assignAndRunStream(streams);
			    System.out.println("Add stream " + consumer.getTopic() + " success!");
			}

		} catch (Exception e) {
			_logger.error("Worker start error ", e);
			System.out.println("Worker start error !!!");
		}

	    System.out.println("Worker started !!!");
		return 0;
	}

	public void add(ConsumerService service) {
		consumers.add(service);
	}

}
