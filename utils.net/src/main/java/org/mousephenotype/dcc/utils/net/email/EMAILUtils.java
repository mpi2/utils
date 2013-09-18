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
package org.mousephenotype.dcc.utils.net.email;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

public class EMAILUtils {

    protected static final Logger logger = LoggerFactory.getLogger(EMAILUtils.class);
    private JavaMailSenderImpl mailSender;
    private MimeMessageHelper helper;

    /*dependency injection using src/main/resources/mailSender.xml*/
    public void setMailSender(JavaMailSenderImpl mailSender) {
        this.mailSender = mailSender;
    }

    public static  EMAILUtils getEMAILUtils(String configurationFilename) {
        ApplicationContext context = new ClassPathXmlApplicationContext(configurationFilename);
        EMAILUtils emailUtils = context.getBean(EMAILUtils.class);
        return emailUtils;
    }
    
    public static  EMAILUtils getEMAILUtils() {
        return EMAILUtils.getEMAILUtils("mailSender.xml");
    }
    
    public void sendEmail(String[] to, String[] cc, String[] bcc, String from, String subject, String text, String[] attachmentFilenames) throws MailException, MessagingException {
        MimeMessage message = this.mailSender.createMimeMessage();
        this.helper = new MimeMessageHelper(message,true);
        this.helper.setFrom(from);
        
        this.helper.setTo(to);
        if (cc != null) {
            this.helper.setCc(cc);
        }
        if (bcc != null) {
            this.helper.setBcc(bcc);
        }
        this.helper.setSubject(subject);
        this.helper.setText(text);
        if (attachmentFilenames != null) {
            for (String attachmentFilename : attachmentFilenames) {
                FileSystemResource file = new FileSystemResource(new File(attachmentFilename));
                this.helper.addAttachment(attachmentFilename, file);
            }
        }

        this.mailSender.send(message);
    }
}
