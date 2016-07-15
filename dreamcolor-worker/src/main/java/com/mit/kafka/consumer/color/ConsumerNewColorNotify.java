package com.mit.kafka.consumer.color;

import java.util.List;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.luv.kafka.consumer.ConsumerService;
import com.mit.luv.kafka.producer.ProducerTopic;
import com.mit.utils.StringUtils;
import com.mit.worker.worker.ColorWorker;

public class ConsumerNewColorNotify extends ConsumerService {
	private final Logger _logger = LoggerFactory.getLogger(ConsumerNewColorNotify.class);
	private final static String topic = ProducerTopic.NEW_COLOR;

	public ConsumerNewColorNotify() {
		super(topic);
	}

	@Override
	public String getTopic() {
		return topic;
	}

	@Override
	public void execute(byte[] data) {
		String msg = new String(data);
		try {
			if (msg != null && !msg.isEmpty()) {
				String[] dataArr = msg.split("\t");
				if (dataArr.length >= 5) {
					String msgSend = dataArr[1];
					long thumb = NumberUtils.toLong(dataArr[2]);
					List<Long> colorIds = StringUtils.stringToLongList(dataArr[3], true);
					List<Integer> destIds = StringUtils.stringToList(dataArr[4], true);
					ColorWorker.notifyColor(colorIds, destIds, msgSend, thumb);
					_logger.debug("Notify color: " + msg);
				} else {
					_logger.error("Data format error", msg);
				}
			}
		} catch (Exception e) {
			_logger.error("consumer error " + topic, e);
		}
	}
}
