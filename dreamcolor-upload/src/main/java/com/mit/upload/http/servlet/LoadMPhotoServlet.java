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

package com.mit.upload.http.servlet;

import com.mit.api.ApiError;
import com.mit.api.ApiMessage;
import com.mit.entities.session.UserSessionEx;
import com.mit.midutil.MIdNoise;
import com.mit.mphoto.thrift.TMPhoto;
import com.mit.mphoto.thrift.TMPhotoResult;
import com.mit.upload.photo.client.MPhotoClient;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nghiatc
 * @since Sep 24, 2015
 */
public class LoadMPhotoServlet extends ServletWrapper {
    private static Logger logger = LoggerFactory.getLogger(LoadMPhotoServlet.class);
    
    @Override
	protected void doProcess(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //String action = req.getParameter("action");
        UserSessionEx userSession = (UserSessionEx)req.getAttribute("accountInfo");
        ApiMessage msg = new ApiMessage();
//        if (userSession == null) {
//            msg.setErr(ApiError.SESSION_TIMEOUT.getValue());
//        } else {
            String p = req.getParameter("p");
            if(p != null && !p.isEmpty()){
                long pid = MIdNoise.deNoiseLId(p);
                TMPhotoResult tptrs = MPhotoClient.getInstance().getMPhoto(pid);
                if(tptrs != null && tptrs.error >= 0 && tptrs.value != null){
                    TMPhoto tmp = tptrs.getValue();
                    String fileName = tmp.getFilename();
                    String mimeType = tmp.getContentType();
                    byte[] data = tmp.getData();
                    ByteArrayOutputStream outData = new ByteArrayOutputStream(data.length);
                    outData.write(data, 0, data.length);
                    printBigFile(resp, outData, fileName, mimeType);
                } else{
                    msg.setErr(ApiError.UNKNOWN.getValue());
                }
            } else{
                msg.setErr(ApiError.MISSING_PARAM.getValue());
            }
//        }
//        printJson(req, resp, msg.toString());
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
