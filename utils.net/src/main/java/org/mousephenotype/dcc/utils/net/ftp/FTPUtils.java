/**
 * Copyright (C) 2013 Julian Atienza Herrero <j.atienza at har.mrc.ac.uk>
 *
 * MEDICAL RESEARCH COUNCIL UK MRC
 *
 * Harwell Mammalian Genetics Unit
 *
 * http://www.har.mrc.ac.uk
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.mousephenotype.dcc.utils.net.ftp;

import java.io.*;
import java.util.List;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 */
public class FTPUtils {

    protected static final Logger logger = LoggerFactory.getLogger(FTPUtils.class);

    public static enum fileTypes {

        ASCII_FILE_TYPE, EBCDIC_FILE_TYPE, BINARY_FILE_TYPE, LOCAL_FILE_TYPE
        
    };

    public static enum fileTransferModes {

        STREAM_TRANSFER_MODE, BLOCK_TRANSFER_MODE, COMPRESSED_TRANSFER_MODE;

        public int value() {
            return this.ordinal() + 10;
        }
    }
    
    private final FTPClient ftpClient;
    private boolean login = false;

    public FTPUtils(String hostname, int port, String username, String password, fileTypes fileType, fileTransferModes fileTransferMode) throws IOException {
        //
        this.ftpClient = new FTPClient();
        //
        this.ftpClient.connect(hostname, port);
        
        this.login = this.ftpClient.login(username, password);
        
        this.ftpClient.setFileType( fileType.ordinal());
        this.ftpClient.setFileTransferMode(fileTransferMode.value());

        if (!this.login) {
            this.ftpClient.disconnect();
            throw new IOException("invalid login credentials");
        }
    }

    public List<Boolean> download(List<String> filenames) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    public boolean download(String filename, String destFilename) throws IOException {
        boolean success = false;
        InputStream retrieveFileStream = this.ftpClient.retrieveFileStream(filename);
        //
        if (!FTPReply.isPositiveIntermediate(this.ftpClient.getReplyCode())) {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(retrieveFileStream);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(destFilename, false));
            try {
                int c;
                while ((c = bufferedInputStream.read()) != -1) {
                    bufferedOutputStream.write(c);
                }
            } finally {
                success = this.ftpClient.completePendingCommand();
                if (bufferedInputStream != null) {
                    bufferedInputStream.close();
                }
                if (bufferedOutputStream != null) {
                    bufferedOutputStream.close();
                }
            }
        } else {
            retrieveFileStream.close();
        }
        return success;
    }

    public void close() throws IOException {
        if (!this.ftpClient.isConnected()) {
            this.ftpClient.logout();
            this.ftpClient.disconnect();
        }
    }
}
