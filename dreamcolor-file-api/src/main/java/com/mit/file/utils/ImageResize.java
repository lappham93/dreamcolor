package com.mit.file.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;
import org.im4java.process.Pipe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageResize {
	private static Logger logger = LoggerFactory.getLogger(ImageResize.class);
	public static ByteArrayOutputStream resizeImage(byte[] data, int width, int height) {
		ConvertCmd cmd = new ConvertCmd();

		// create the operation, add images and operators/options
		IMOperation op = new IMOperation();
		op.addImage("-");
		op.resize(width, height);
		op.addImage("jpg:-");
		try {
			Pipe pipeIn = new Pipe(new ByteArrayInputStream(data), null);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			Pipe pipeOut = new Pipe(null,out);
			cmd.setOutputConsumer(pipeOut);
			cmd.setInputProvider(pipeIn);
			cmd.run(op);
			return out;
		} catch (Exception e) {
			logger.error("resizeImage", e);
		}

		return null;
    }

	public static ByteArrayOutputStream resizeImage(byte[] data, int width) {
		ConvertCmd cmd = new ConvertCmd();

		// create the operation, add images and operators/options
		IMOperation op = new IMOperation();
		op.addImage("-");
		op.resize(width);
		op.addImage("jpg:-");
		try {
			Pipe pipeIn = new Pipe(new ByteArrayInputStream(data), null);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			Pipe pipeOut = new Pipe(null,out);
			cmd.setOutputConsumer(pipeOut);
			cmd.setInputProvider(pipeIn);
			cmd.run(op);

			return out;
		} catch (Exception e) {
			logger.error("resizeImage", e);
		}
		return null;
    }

}
