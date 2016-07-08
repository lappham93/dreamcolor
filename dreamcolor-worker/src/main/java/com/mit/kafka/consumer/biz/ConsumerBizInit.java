package com.mit.kafka.consumer.biz;
//package com.dkmobility.kafka.consumer.biz;
//
//import org.apache.commons.lang.math.NumberUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.dkmobility.kafka.consumer.ProducerTopic;
//import com.dkmobility.luv.kafka.consumer.ConsumerService;
//import com.dkmobility.worker.worker.BizWorker;
//
//public class ConsumerBizInit extends ConsumerService {
//	private final Logger _logger = LoggerFactory.getLogger(ConsumerBizInit.class);
//	private final static String topic = ProducerTopic.BIZ_INIT;
//
//	public ConsumerBizInit() {
//		super(topic);
//	}
//
//	@Override
//	public String getTopic() {
//		return topic;
//	}
//
//	@Override
//	public void execute(byte[] data) {
//		String msg = new String(data);		
//		try {
//			if (msg != null && !msg.isEmpty()) {
//				String[] dataArr = msg.split("\t");
//				if(dataArr.length >= 2) {
//					int bizId = NumberUtils.toInt(dataArr[1]);
//
//					if(bizId > 0) {
//						BizWorker.initializeBiz(bizId);
//					}
//				} else {
//					_logger.error("Data format error", msg);
//				}
//			}
//		} catch (Exception e) {
//			_logger.error("consumer error " + topic, e);
//		}
//	}
//}
