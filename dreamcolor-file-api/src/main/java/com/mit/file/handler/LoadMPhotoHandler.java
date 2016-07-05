/*
 * Copyright 2015 nghiatc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mit.file.handler;

import com.mit.file.utils.ImageResize;
import com.mit.midutil.MIdNoise;
import com.mit.mphoto.thrift.TMPhoto;
import com.mit.mphoto.thrift.TMPhotoResult;
import com.mit.upload.photo.client.MPhotoClient;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nghiatc
 * @since Sep 24, 2015
 */
public class LoadMPhotoHandler extends HandlerWrapper {
    private static Logger logger = LoggerFactory.getLogger(LoadMPhotoHandler.class);
    private final int minSize = 20;
    
    @Override
	public void handle(String target, Request baseRequest,
			HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
        String uri = "/cyog/load/photo";
		if (target.startsWith(uri)) {
            long t = System.nanoTime();
            long fileZise = 0L;
            String p = req.getParameter("p");
            String ssize = req.getParameter("size");
            int size = 0;
            if(ssize != null && !ssize.isEmpty()){
                size = Integer.valueOf(ssize);
            }
            if(p != null && !p.isEmpty()){
                long pid = MIdNoise.deNoiseLId(p);
                TMPhotoResult tptrs = MPhotoClient.getInstance().getMPhoto(pid);
                if(tptrs != null && tptrs.error >= 0 && tptrs.value != null){
                    TMPhoto tmp = tptrs.getValue();
                    String fileName = tmp.getFilename();
                    String mimeType = tmp.getContentType();
                    byte[] data = tmp.getData();
                    if(data != null && size > 0){
                        size = size > minSize ? size : minSize;
                        ByteArrayOutputStream out = ImageResize.resizeImage(data, size);
                        if(out != null) {
                            data = out.toByteArray();
                            out.close();
                        }
                    }
                    fileZise = data.length;
                    ByteArrayOutputStream outData = new ByteArrayOutputStream(data.length);
                    outData.write(data, 0, data.length);
                    printBigFile(resp, outData, fileName, mimeType);
                } else{
                    resp.setStatus(HttpStatus.FORBIDDEN_403);
                }
            } else{
                resp.setStatus(HttpStatus.FORBIDDEN_403);
            }
            baseRequest.setHandled(true);
            logger.info("LoadMPhotoHandler - pid: " + p + " Size: " + size + " FileSize: " + FileUtils.byteCountToDisplaySize(fileZise) + " Time: " + (System.nanoTime() - t) + "ns ");
        }
    }
    
    protected void printBigFile(HttpServletResponse response, ByteArrayOutputStream bigfile, String fileName, String mimeType) throws IOException {
        ServletOutputStream stream = null;
        BufferedInputStream buf = null;
        try {
            stream = response.getOutputStream();

            //set response headers
            response.setBufferSize(1024 * 1024 * 6); // 6M
            response.setContentType(mimeType + ";charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setDateHeader("Expires", 0);
            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            response.setHeader("Pragma", "public");
            response.setHeader("Accept-Ranges", "bytes");
            //response.setHeader("Content-Length", String.valueOf(bigfile.size()));
            //response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
            // the contentlength
            response.setContentLength(bigfile.size());

            InputStream input = new ByteArrayInputStream(bigfile.toByteArray());
            buf = new BufferedInputStream(input);

            //read from the file; write to the ServletOutputStream
            byte[] bb = new byte[1024 * 1024 * 5]; // 5M
            int readByte;
            while ((readByte = buf.read(bb, 0, bb.length)) != -1) {
                stream.write(bb, 0, readByte);
                //System.out.println("Sending stream: " + readByte);
            }
            //System.out.println("Complete send file photo. Done!!!...");
        } catch (Exception e) {
            logger.error("printBigFile: ", e);
        } finally {
            if (stream != null) {
                stream.flush();
                stream.close();
            }
            if (buf != null) {
                buf.close();
            }
            if(bigfile != null){
                bigfile.close();
            }
        }
    }
}
