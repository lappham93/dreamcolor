package com.mit.luv.kafka.consumer;
//package com.dkmobility.luv.kafka.consumer;
//
//import kafka.consumer.ConsumerIterator;
//import kafka.consumer.KafkaStream;
//
//public abstract class ConsumerProcess<T> implements Runnable {
//	KafkaStream<byte[], byte[]> stream;
//
//	public ConsumerProcess() {
//		super();
//	}
//
//	public ConsumerProcess(KafkaStream<byte[], byte[]> stream) {
//		this.stream = stream;
//	}
//
//	@Override
//	public void run() {
//		if(stream != null) {
//			ConsumerIterator<byte[], byte[]> it = stream.iterator();
//			while (it.hasNext()) {
//				process(it.next().message());
//			}
//		}
//
//	}
//
//	public abstract void process(byte[] msg);
//
//	public KafkaStream<byte[], byte[]> getStream() {
//		return stream;
//	}
//
//	public void setStream(KafkaStream<byte[], byte[]> stream) {
//		this.stream = stream;
//	}
//}
