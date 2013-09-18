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

import java.util.List;
import java.util.Properties;
import javax.persistence.EntityExistsException;
import javax.persistence.TransactionRequiredException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.apache.commons.configuration.ConfigurationException;

import org.hibernate.HibernateException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mousephenotype.dcc.exportlibrary.datastructure.converters.DatatypeConverter;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.CentreILARcode;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.Gender;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.StageUnit;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.Zygosity;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.CentreProcedure;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Experiment;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Procedure;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.SimpleParameter;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.*;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.submission.Submission;
import org.mousephenotype.dcc.exportlibrary.datastructure.tracker.submission.SubmissionSet;
import org.mousephenotype.dcc.utils.io.conf.Reader;


import org.slf4j.LoggerFactory;

/**
 *
 * @author julian
 */
public class HibernateManagerTest {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(HibernateManagerTest.class);
    private static HibernateManager hibernateManager;
    private static final String CONNECTION_PROPERTIES_FILENAME = "hibernate.test.common.properties";
    private static final String PERSISTENCE_UNITNAME = "org.mousephenotype.dcc.exportlibrary.datastructure";
    private static Reader reader;
    private static SubmissionSet centreSpecimenSubmissionSet;
    private static SubmissionSet centreProcedureSubmissionSet;

    public static SubmissionSet generateSubmissionSetExampleForCentreSpecimen() {

        SubmissionSet submissionSet = new SubmissionSet();
        Submission submission = new Submission();
        submissionSet.getSubmission().add(submission);


        //
        CentreSpecimen centreSpecimen = new CentreSpecimen();
        centreSpecimen.setCentreID(CentreILARcode.J);

        //
        Mouse mouse = new Mouse();
        mouse.setColonyID("colonyID");
        mouse.setIsBaseline(Boolean.FALSE);
        mouse.setStrainID("strainID");
        mouse.setSpecimenID("specimenID_00_at_Jacks");
        mouse.setGender(Gender.MALE);
        mouse.setZygosity(Zygosity.WILD_TYPE);
        mouse.setLitterId("litterID");
        mouse.setPipeline("IMPC_001");
        mouse.setProductionCentre(CentreILARcode.J);
        mouse.setPhenotypingCentre(CentreILARcode.J);
        mouse.setProject("IMPC");
        mouse.setDOB(DatatypeConverter.parseDate("2012-06-30"));
        //
        mouse.getRelatedSpecimen().add(new RelatedSpecimen());
        mouse.getRelatedSpecimen().get(0).setSpecimenID("specimenID_00_a");
        mouse.getRelatedSpecimen().get(0).setRelationship(Relationship.LITTERMATE);
        //
        mouse.getRelatedSpecimen().add(new RelatedSpecimen());
        mouse.getRelatedSpecimen().get(1).setSpecimenID("specimenID_00_m");
        mouse.getRelatedSpecimen().get(1).setRelationship(Relationship.MOTHER);
        //
        centreSpecimen.getMouseOrEmbryo().add(mouse);

        //
        Embryo embryo = new Embryo();

        embryo.setSpecimenID("ColonyName");




        embryo.setStage("34");
        embryo.setStageUnit(StageUnit.DPC);
        embryo.setGender(Gender.FEMALE);
        embryo.setZygosity(Zygosity.WILD_TYPE);
        embryo.setLitterId("litterId");
        embryo.setPipeline("Pipeline");
        embryo.setProductionCentre(CentreILARcode.ICS);
        embryo.setPhenotypingCentre(CentreILARcode.J);
        embryo.setProject("Project");
        embryo.setSpecimenID("embryoID_00");
        embryo.setColonyID("colonyID");


        RelatedSpecimen relatedSpecimen1 = new RelatedSpecimen();
        relatedSpecimen1.setSpecimenID("subjectname1");
        relatedSpecimen1.setRelationship(Relationship.LITTERMATE);

        RelatedSpecimen relatedSpecimen2 = new RelatedSpecimen();
        relatedSpecimen2.setSpecimenID("subjectname2");
        relatedSpecimen2.setRelationship(Relationship.LITTERMATE);

        centreSpecimen.getMouseOrEmbryo().add(embryo);

        return submissionSet;
    }

    public static SubmissionSet generateSubmissionSetExampleForCentreProcedure() {
        SubmissionSet submissionSet = new SubmissionSet();
        Submission submission = new Submission();
        submissionSet.getSubmission().add(submission);

        CentreProcedure centreProcedure = new CentreProcedure();
        submission.getCentreProcedure().add(centreProcedure);
        centreProcedure.setCentreID(CentreILARcode.J);
        centreProcedure.setProject("IMPC");
        Experiment experiment = new Experiment();
        centreProcedure.getExperiment().add(experiment);

        Procedure procedure = new Procedure();
        experiment.setProcedure(procedure);

        procedure.setProcedureID("IMPC_BWT_001"); //body weight

        SimpleParameter simpleParameter = new SimpleParameter();
        simpleParameter.setParameterID("IMPC_BWT_001_001"); //body weight
        simpleParameter.setUnit("g");
        simpleParameter.setValue("20");

        procedure.getSimpleParameter().add(simpleParameter);

        return submissionSet;
    }

    @BeforeClass
    public static void testConfiguration() {
        try {
            reader = new Reader(CONNECTION_PROPERTIES_FILENAME);
        } catch (ConfigurationException ex) {
            logger.error("", ex);
            Assert.fail();
        }
        Properties properties = reader.getProperties();

        Assert.assertTrue(properties.size() > 1);
        try {
            HibernateManagerTest.hibernateManager = new HibernateManager(properties, PERSISTENCE_UNITNAME);
        } catch (HibernateException ex) {
            logger.error("", ex);
            Assert.fail();
        }

        Assert.assertNotNull(HibernateManagerTest.hibernateManager);

        HibernateManagerTest.centreProcedureSubmissionSet = generateSubmissionSetExampleForCentreProcedure();
        HibernateManagerTest.centreSpecimenSubmissionSet = generateSubmissionSetExampleForCentreSpecimen();

        hibernateManager.persist(HibernateManagerTest.centreProcedureSubmissionSet);
        logger.info("centreProcedureSubmissionSet {} persisted", HibernateManagerTest.centreProcedureSubmissionSet.getHjid());

        hibernateManager.persist(HibernateManagerTest.centreSpecimenSubmissionSet);
        logger.info("centreSpecimenSubmissionSet {} persisted", HibernateManagerTest.centreSpecimenSubmissionSet.getHjid());

    }

    @AfterClass
    public static void teardown() {
        try {
            HibernateManagerTest.hibernateManager.close();
        } catch (HibernateException ex) {
            logger.error("", ex);
            Assert.fail();
        }
    }

    @Test
    public void testQuery() {
        logger.info("running testQuery");
        String query = "from CentreSpecimen";
        List<CentreSpecimen> centreSpecimens = null;
        try {
            centreSpecimens = hibernateManager.query(query);
        } catch (HibernateException ex) {
            logger.error("", ex);
            Assert.fail();
        }
        Assert.assertNotNull(centreSpecimens);
    }

    @Test
    public void testQuery2() {
        logger.info("running testQuery2");
        String query = "from Submission submission inner join fetch submission.centreProcedure ";
        List<Submission> submissions = null;
        try {
            submissions = hibernateManager.query(query);
        } catch (HibernateException ex) {
            logger.error("", ex);
            Assert.fail();
        }

        Assert.assertNotNull(submissions);
    }

    @Test
    public void testGetSubmission() {
        logger.info("running testGetSubmission");
        List<Submission> submissions = null;
        CriteriaQuery<Submission> criteriaQuery = hibernateManager.getCriteriaQuery(Submission.class);
        Root<Submission> queryRoot = criteriaQuery.from(Submission.class);
        criteriaQuery.select(queryRoot);
        try {
            submissions = hibernateManager.executeCriteriaQuery(criteriaQuery);
        } catch (IllegalStateException ex) {
            logger.error("", ex);
            Assert.fail();
        } catch (EntityExistsException ex) {
            logger.error("", ex);
            Assert.fail();
        } catch (IllegalArgumentException ex) {
            logger.error("", ex);
            Assert.fail();
        } catch (TransactionRequiredException ex) {
            logger.error("", ex);
            Assert.fail();
        } catch (RuntimeException ex) {
            logger.error("", ex);
            Assert.fail();
        }


        Assert.assertNotNull(submissions);
    }

    @Test
    public void testGetContainer() {
        logger.info("running testGetContainer");
        String query = "from CentreProcedure";
        List<CentreProcedure> centreProcedures = null;
        try {
            centreProcedures = hibernateManager.query(query);
        } catch (HibernateException ex) {
            logger.error("", ex);
            Assert.fail();
        }
        Assert.assertNotNull(centreProcedures);
        Assert.assertTrue(centreProcedures.size() > 0);


        Submission submission1 = null;

        submission1 = hibernateManager.getContainer(centreProcedures.get(0), Submission.class, "centreProcedure");

        Assert.assertNotNull(submission1);
        if (submission1 == null) {
            logger.error("cannot find submission1");
            Assert.fail();
        }
    }

    @Test
    public void testRemove() {
        logger.info("running testRemove");
        try {
            logger.info("trying to remove object {}", HibernateManagerTest.centreSpecimenSubmissionSet.getHjid());
            hibernateManager.remove(HibernateManagerTest.centreSpecimenSubmissionSet);
        } catch (Exception ex) {
            logger.error("error removing object", ex);
            Assert.fail();
        }
        try {
            hibernateManager.remove(SubmissionSet.class, HibernateManagerTest.centreProcedureSubmissionSet.getHjid());
        } catch (Exception ex) {
            logger.error("error removing object", ex);
            Assert.fail();
        }
    }
}
