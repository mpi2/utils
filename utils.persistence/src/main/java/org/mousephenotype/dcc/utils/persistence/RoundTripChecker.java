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
package org.mousephenotype.dcc.utils.persistence;

import java.io.FileNotFoundException;
import java.io.IOException;
import javax.persistence.*;
import javax.xml.bind.JAXBException;
import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 */
public abstract class RoundTripChecker<T> {

    public static final org.slf4j.Logger logger = LoggerFactory.getLogger(RoundTripChecker.class);
    public final String filename;
    public final String contextPath;
    public final HibernateManager hibernateManager;
    public T fromFile;
    public T fromDatabase;

    public RoundTripChecker(String contextPath, String filename, HibernateManager persistenceManager) {
        this.contextPath = contextPath;
        this.filename = filename;
        this.hibernateManager = persistenceManager;
    }

    public boolean check() throws JAXBException, FileNotFoundException, IOException {
        logger.info("reading {}",this.filename);
        this.fromFile = this.readFromFile();
        logger.info("{} read. Persisting", this.filename);
        this.serialize2Database();
        logger.info("Persisted. Closing persistence Manager");
        this.hibernateManager.close();
        logger.info("Persistence manager closed. Renewing entity manager");
        
        logger.info("entitymanager renewed. Reading from database");
        this.fromDatabase = this.readFromDatabase();
        logger.info("submission read from database. Comparing objects");
        boolean equals = this.fromFile.equals(this.fromDatabase);
        logger.info(" are equal?: {}",equals);
        return equals;
    }

    public abstract <T> T readFromFile() throws JAXBException, FileNotFoundException, IOException;

    public <T> void serialize2Database() throws IllegalStateException, EntityExistsException, IllegalArgumentException, TransactionRequiredException {
        this.hibernateManager.persist(this.fromFile);
    }

    public abstract <T> T readFromDatabase() throws IllegalStateException, QueryTimeoutException, TransactionRequiredException, PessimisticLockException,
            LockTimeoutException, PersistenceException;

    public <T> boolean equals(T fromFile, T fromDatabase) {
        return fromFile.equals(fromDatabase);
    }
}
