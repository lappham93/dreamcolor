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

package com.mit.upload.photo.client;

import com.mit.mphoto.thrift.TMPhoto;
import com.mit.mphoto.thrift.TMPhotoResult;
import com.mit.utils.MIMETypeUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author nghiatc
 * @since Sep 23, 2015
 */
public class TestPhoto {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedException {
        File file  = new File("1-th.jpg");
        if(file.exists()){
            FileInputStream fis = new FileInputStream(file);
            
            long id = 1;
            TMPhoto tmp = new TMPhoto();
            tmp.setId(id);
            String filename = file.getName();
            tmp.setFilename(filename);
            String ext = FilenameUtils.getExtension(filename);
            String ct = MIMETypeUtil.getInstance().getMIMETypeImage(ext);
            tmp.setContentType(ct);
            tmp.setData(IOUtils.toByteArray(fis));
            
            int err = MPhotoClient.getInstance().putMPhoto(tmp);
            System.out.println("err: " + err);
            if(err >= 0){
                Thread.sleep(2000);
                TMPhotoResult t1 = MPhotoClient.getInstance().getMPhoto(id);
                System.out.println("t1: " + t1);
            }
        }
    }

}
