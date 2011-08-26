/*
 * $Id$
 * $Revision$
 * $Date$
 * $Author$
 *
 * The DOMS project.
 * Copyright (C) 2007-2010  The State and University Library
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package dk.statsbiblioteket.doms.client;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.CentralWebserviceService;
import dk.statsbiblioteket.doms.central.RecordDescription;
import dk.statsbiblioteket.doms.central.ViewBundle;
import dk.statsbiblioteket.util.xml.DOM;
import org.w3c.dom.Document;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.ws.BindingProvider;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.nio.charset.CoderResult;
import java.util.*;

/**
 * Utility class for making it simple and easy to access the DOMS server main
 * web service.
 *
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 * @author Esben Agerbæk Black &lt;eab@statsbiblioteket.dk&gt;
 */
public class DomsWSClientImpl implements DomsWSClient {

    private static final QName CENTRAL_WEBSERVICE_SERVICE = new QName(
            "http://central.doms.statsbiblioteket.dk/",
            "CentralWebserviceService");
    /**
     * Reference to the active DOMS webservice client instance.
     */
    private CentralWebservice domsAPI;

    @Deprecated
    public void login(URL domsWSAPIEndpoint, String userName, String password) {
        setCredentials(domsWSAPIEndpoint, userName, password);
    }

    @Override
    public List<String> getLabel(List<String> uuids) {
        // domsAPI.getLabels(uuids);
        // TODO Implement the get label method on the server side.
        return uuids;
    }

    @Override
    public String getLabel(String uuid) {
        ArrayList lst = new ArrayList();
        lst.add(uuid);
        return getLabel(lst).get(0);
    }

    @Override
    public List<SearchResult> search(String query, int offset, int pageLength) throws ServerOperationFailed {
        try {
            List<dk.statsbiblioteket.doms.central.SearchResult> wresults
                    =
                    domsAPI.findObjects(query, offset, pageLength);
            List<SearchResult> cresults = new ArrayList<SearchResult>();
            for (dk.statsbiblioteket.doms.central.SearchResult wresult : wresults) {
                SearchResult cresult = new SearchResult(wresult.getPid(),
                                                        wresult.getType(),
                                                        wresult.getTitle(),
                                                        FedoraState.fromString(wresult.getState()),
                                                        new Date(wresult.getModifiedDate()),
                                                        new Date(wresult.getCreatedDate()));
                cresults.add(cresult);
            }
            return cresults;
        } catch (Exception exception) {
            throw new ServerOperationFailed(
                    "Failed searching", exception);
        }
    }

    public void setCredentials(URL domsWSAPIEndpoint, String userName,
                               String password) {
        domsAPI = new CentralWebserviceService(domsWSAPIEndpoint,
                                               CENTRAL_WEBSERVICE_SERVICE).getCentralWebservicePort();

        Map<String, Object> domsAPILogin = ((BindingProvider) domsAPI)
                .getRequestContext();
        domsAPILogin.put(BindingProvider.USERNAME_PROPERTY, userName);
        domsAPILogin.put(BindingProvider.PASSWORD_PROPERTY, password);
    }

    public String createObjectFromTemplate(String templatePID, String comment)
            throws ServerOperationFailed {
        try {
            return domsAPI.newObject(templatePID, new ArrayList<String>(), comment);
        } catch (Exception exception) {
            throw new ServerOperationFailed(
                    "Failed creating a new object from template: "
                    + templatePID, exception);
        }
    }

    public String createObjectFromTemplate(String templatePID, List<String> oldIdentifiers, String comment)
            throws ServerOperationFailed {
        try {
            return domsAPI.newObject(templatePID, oldIdentifiers, comment);
        } catch (Exception exception) {
            throw new ServerOperationFailed(
                    "Failed creating a new object from template: "
                    + templatePID, exception);
        }
    }


    public String createFileObject(String templatePID, FileInfo fileInfo, String comment)
            throws ServerOperationFailed {

        try {
            final String fileObjectPID = createObjectFromTemplate(templatePID, comment);
            addFileToFileObject(fileObjectPID, fileInfo, comment);
            return fileObjectPID;

        } catch (Exception e) {
            throw new ServerOperationFailed(
                    "Failed creating a new file object (template PID: "
                    + templatePID + ") from this file information: "
                    + fileInfo, e);
        }
    }

    public void addFileToFileObject(String fileObjectPID, FileInfo fileInfo, String comment)
            throws ServerOperationFailed {

        try {
            domsAPI.addFileFromPermanentURL(fileObjectPID, fileInfo
                    .getFileName(), fileInfo.getMd5Sum(), fileInfo
                    .getFileLocation().toString(), fileInfo.getFileFormatURI()
                    .toString(), comment);
        } catch (Exception e) {
            throw new ServerOperationFailed(
                    "Failed adding a file to a file object (file object PID: "
                    + fileObjectPID + ") from this file information: "
                    + fileInfo, e);
        }
    }

    public String getFileObjectPID(URL fileURL) throws NoObjectFound,
                                                       ServerOperationFailed {

        String pid = null;
        try {
            pid = domsAPI.getFileObjectWithURL(fileURL.toString());
        } catch (Exception exception) {
            throw new ServerOperationFailed(
                    "Unable to retrieve file object with URL: " + fileURL,
                    exception);
        }
        if (pid == null) {
            throw new NoObjectFound("Unable to retrieve file object with URL: "
                                    + fileURL);
        }
        return pid;
    }

    @Override
    public List<String> getPidFromOldIdentifier(String oldIdentifier) throws NoObjectFound, ServerOperationFailed {

        List<String> pids;
        try {
            pids = domsAPI.findObjectFromDCIdentifier(oldIdentifier);
        } catch (Exception exception) {
            throw new ServerOperationFailed(
                    "Unable to retrieve an object with this identifer: " + oldIdentifier,
                    exception);
        }
        if (pids == null || pids.isEmpty()) {
            throw new NoObjectFound("Unable to retrieve an object with this identifer: "
                                    + oldIdentifier);
        }
        return pids;

    }

    public Document getDataStream(String objectPID, String datastreamID)
            throws ServerOperationFailed {
        try {
            final String datastreamXML = domsAPI.getDatastreamContents(
                    objectPID, datastreamID);

            final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
                    .newInstance();

            final DocumentBuilder documentBuilder = documentBuilderFactory
                    .newDocumentBuilder();

            final ByteArrayInputStream datastreamBytes = new ByteArrayInputStream(
                    datastreamXML.getBytes());
            final Document dataStream = documentBuilder.parse(datastreamBytes);

            return dataStream;
        } catch (Exception exception) {
            throw new ServerOperationFailed("Failed getting datastream (ID: "
                                            + datastreamID + ") contents from object (PID: "
                                            + objectPID + ")", exception);
        }
    }

    public void updateDataStream(String objectPID, String dataStreamID,
                                 Document newDataStreamContents, String comment) throws ServerOperationFailed {
        try {
            domsAPI.modifyDatastream(objectPID, dataStreamID, DOM
                    .domToString(newDataStreamContents), comment);

        } catch (Exception exception) {
            throw new ServerOperationFailed("Failed updating datastream (ID: "
                                            + dataStreamID + ") contents from object (PID: "
                                            + objectPID + ")", exception);
        }

    }

    @Override
    public void addObjectRelation(Relation relation, String comment) throws ServerOperationFailed {
        try {
            dk.statsbiblioteket.doms.central.Relation domsrel = new dk.statsbiblioteket.doms.central.Relation();
            domsrel.setSubject(relation.getSubject());
            domsrel.setPredicate(relation.getPredicate());
            domsrel.setObject(relation.getObject());
            domsAPI.addRelation(relation.getSubject(), domsrel, comment);
        } catch (Exception exception) {
            throw new ServerOperationFailed(
                    "Failed creating object relation (type: " + relation.getPredicate()
                    + ") from the source object (PID: " + relation.getSubject()
                    + ") to the target object (PID: " + relation.getObject() + ")",
                    exception);
        }
    }

    @Override
    public void removeObjectRelation(Relation relation, String comment)
            throws ServerOperationFailed {
        try {
            dk.statsbiblioteket.doms.central.Relation domsrel = new dk.statsbiblioteket.doms.central.Relation();
            domsrel.setSubject(relation.getSubject());
            domsrel.setPredicate(relation.getPredicate());
            domsrel.setObject(relation.getObject());

            domsAPI.deleteRelation(relation.getSubject(),
                                   domsrel, comment);
        } catch (Exception exception) {
            throw new ServerOperationFailed(
                    "Failed removing object relation (type: " + relation.getPredicate()
                    + ") from the source object (PID: " + relation.getSubject()
                    + ") to the target object (PID: " + relation.getObject() + ")",
                    exception);
        }

    }

    @Override
    public List<Relation> listObjectRelations(String objectPID, String relationType)
            throws ServerOperationFailed {
        try {
            List<dk.statsbiblioteket.doms.central.Relation> domsRelations =
                    domsAPI.getNamedRelations(objectPID, relationType);

            ArrayList<Relation> clientRelations = new ArrayList<Relation>();
            for (dk.statsbiblioteket.doms.central.Relation domsRelation : domsRelations) {
                clientRelations.add(new Relation(domsRelation.getSubject(),
                                                 domsRelation.getPredicate(),
                                                 domsRelation.getObject()));
            }
            return clientRelations;

        } catch (Exception exception) {
            throw new ServerOperationFailed(
                    "Failed listing object relations (type: " + relationType
                    + ") from the source object (PID: " + objectPID
                    + ")",
                    exception);
        }
    }

    public void publishObjects(String comment, String... pidsToPublish)
            throws ServerOperationFailed {
        try {
            domsAPI.markPublishedObject(Arrays.asList(pidsToPublish), comment);
        } catch (Exception exception) {
            throw new ServerOperationFailed(
                    "Failed marking objects as published. PIDs: "
                    + pidsToPublish, exception);
        }
    }

    @Override
    public void unpublishObjects(String comment, String... pidsToUnpublish) throws ServerOperationFailed {
        try {
            domsAPI.markInProgressObject(Arrays.asList(pidsToUnpublish), comment);
        } catch (Exception exception) {
            throw new ServerOperationFailed(
                    "Failed marking objects as unpublished. PIDs: "
                    + pidsToUnpublish, exception);
        }

    }

    public void deleteObjects(String comment, String... pidsToDelete)
            throws ServerOperationFailed {
        try {
            domsAPI.deleteObject(Arrays.asList(pidsToDelete), comment);
        } catch (Exception exception) {
            throw new ServerOperationFailed(
                    "Failed marking objects as deleted. PIDs: "
                    + pidsToDelete, exception);
        }
    }


    public long getModificationTime(String collectionPID, String viewID,
                                    String state) throws ServerOperationFailed {

        try {
            return domsAPI.getLatestModified(collectionPID, viewID, state);

        } catch (Exception exception) {
            throw new ServerOperationFailed(
                    "Failed retrieving the modification time-stamp for the "
                    + "collection with this PID: '" + collectionPID
                    + "' and this viewID: '" + viewID + "'.", exception);
        }
    }

    public List<RecordDescription> getModifiedEntryObjects(
            String collectionPID, String viewID, long timeStamp,
            String objectState, long offsetIndex, long maxRecordCount)
            throws ServerOperationFailed {
        try {
            return domsAPI.getIDsModified(timeStamp, collectionPID, viewID,
                                          objectState, (int) offsetIndex, (int) maxRecordCount);
            // TODO: The casts to int should be removed once the DOMS web
            // service interface has been corrected to accept long!
        } catch (Exception exception) {
            throw new ServerOperationFailed(
                    "Failed retrieving objects (collectionPID = "
                    + collectionPID
                    + ") associated with the specified view "
                    + "(viewID = " + viewID
                    + ") and with the specified state (" + objectState
                    + "), modified later than the "
                    + "specified time-stamp (" + timeStamp
                    + "). The specified offset index was "
                    + offsetIndex
                    + " and the requested max. amount of records was "
                    + maxRecordCount + ".", exception);
        }
    }

    public String getViewBundle(String entryObjectPID, String viewID)
            throws ServerOperationFailed {
        try {
            ViewBundle viewBundle = domsAPI.getViewBundle(entryObjectPID,
                                                          viewID);
            return viewBundle.getContents();
        } catch (Exception exception) {
            throw new ServerOperationFailed(
                    "Failed retrieving the view record (viewID=" + viewID
                    + ") containing the specified object (objectPID = "
                    + entryObjectPID + ").", exception);
        }
    }

    public void setObjectLabel(String objectPID, String objectLabel, String comment)
            throws ServerOperationFailed {
        try {
            domsAPI.setObjectLabel(objectPID, objectLabel, comment);
        } catch (Exception exception) {
            throw new ServerOperationFailed("Failed setting label ('"
                                            + objectLabel + "') on DOMS object (PID = " + objectPID
                                            + ").");
        }
    }
}
