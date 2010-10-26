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

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.ws.BindingProvider;

import org.w3c.dom.Document;

import dk.statsbiblioteket.doms.centralWebservice.CentralWebservice;
import dk.statsbiblioteket.doms.centralWebservice.CentralWebserviceService;
import dk.statsbiblioteket.doms.centralWebservice.RecordDescription;
import dk.statsbiblioteket.doms.centralWebservice.ViewBundle;
import dk.statsbiblioteket.util.xml.DOM;

/**
 * Utility class for making it simple and easy to access the DOMS server main
 * web service.
 * 
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 */
public class DOMSWSClient {

    /**
     * Reference to the active DOMS webservice client instance.
     */
    private CentralWebservice domsAPI;

    /**
     * Login to the DOMS web service, using the end-point <code>URL</code>
     * specified by <code>domsWSAPIEndpoint</code> and the credentials given by
     * <code>userName</code> and <code>password</code>.
     * 
     * @param domsWSAPIEndpoint
     *            <code>URL</code> of the DOMS server web service end-point.
     * @param userName
     *            Name of the user to use for identification.
     * @param password
     *            Password of the user to use for identification.
     */
    public void login(URL domsWSAPIEndpoint, String userName, String password) {
        // TODO: QName parameters should probably be method parameters.
        domsAPI = new CentralWebserviceService(domsWSAPIEndpoint, new QName(
                "http://central.doms.statsbiblioteket.dk/",
                "CentralWebserviceService")).getCentralWebservicePort();

        Map<String, Object> domsAPILogin = ((BindingProvider) domsAPI)
                .getRequestContext();
        domsAPILogin.put(BindingProvider.USERNAME_PROPERTY, userName);
        domsAPILogin.put(BindingProvider.PASSWORD_PROPERTY, password);
    }

    /**
     * Create a new DOMS object from an object template already stored in the
     * DOMS.
     * 
     * @param templatePID
     *            PID identifying the template object to use.
     * @return PID of the created object.
     * @throws ServerOperationFailed
     *             if the object creation failed.
     */
    public String createObjectFromTemplate(String templatePID)
            throws ServerOperationFailed {
        try {
            return domsAPI.newObject(templatePID);
        } catch (Exception e) {
            throw new ServerOperationFailed(
                    "Failed creating a new object from template: "
                            + templatePID, e);
        }
    }

    /**
     * Create a new file object from an existing file object template, based on
     * the information provided by the <code>FileInfo</code> instance, in the
     * DOMS.
     * 
     * @param templatePID
     *            The PID of the file template object to use for creation of the
     *            new file object.
     * @param fileInfo
     *            File location, checksum and so on for the physical file
     *            associated with the object.
     * @return PID of the created file object in the DOMS.
     * @throws ServerOperationFailed
     *             if the object creation failed.
     * @see FileInfo
     */
    public String createFileObject(String templatePID, FileInfo fileInfo)
            throws ServerOperationFailed {

        try {
            final String fileObjectPID = createObjectFromTemplate(templatePID);

            domsAPI.addFileFromPermanentURL(fileObjectPID, fileInfo
                    .getFileName(), fileInfo.getMd5Sum(), fileInfo
                    .getFileLocation().toString(), fileInfo.getFileFormatURI()
                    .toString());

            return fileObjectPID;

        } catch (Exception e) {
            throw new ServerOperationFailed(
                    "Failed creating a new file object (template PID: "
                            + templatePID + ") from this file information: "
                            + fileInfo, e);
        }
    }

    /**
     * Get the PID of an existing file object in the DOMS which is associated
     * with the physical file specified by <code>fileURL</code>.
     * 
     * @param fileURL
     *            location of the physical file to find the corresponding DOMS
     *            file object for.
     * @return PID of the DOMS file object.
     * @throws NoObjectFound
     *             if there does not exist DOMS file object associated with
     *             <code>fileURL</code>.
     * @throws ServerOperationFailed
     *             if any errors are encountered while looking up the file
     *             object.
     */
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

    /**
     * Get the XML content of the datastream identified by
     * <code>datastreamID</code> of the DOMS object identified by
     * <code>objectPID</code>.
     * 
     * @param objectPID
     *            ID of the DOMS object to retrieve the datastream contents of.
     * @param datastreamID
     *            ID of the datastream to get the contents of.
     * @return <code>Document</code> containing the datastream XML contents.
     * @throws ServerOperationFailed
     *             if the datastream contents cannot be retrieved.
     */
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

    /**
     * Replace any existing datastream contents of the datastream, identified by
     * <code>dataStreamID</code> of the DOMS object identified by
     * <code>objectPID</code>, with the contents provided by the
     * <code>newDataStreamContents</code> <code>Document</code>.
     * 
     * @param objectPID
     *            ID of the DOMS object containing the datastream to replace the
     *            contents of.
     * @param dataStreamID
     *            ID of the datastream to replace the contents of.
     * @param newDataStreamContents
     *            <code>Document</code> containing the new datastream XML
     *            contents.
     * @throws ServerOperationFailed
     *             if the datastream contents cannot be updated.
     */
    public void updateDataStream(String objectPID, String dataStreamID,
            Document newDataStreamContents) throws ServerOperationFailed {
        try {
            domsAPI.modifyDatastream(objectPID, dataStreamID, DOM
                    .domToString(newDataStreamContents));

        } catch (Exception exception) {
            throw new ServerOperationFailed("Failed updating datastream (ID: "
                    + dataStreamID + ") contents from object (PID: "
                    + objectPID + ")", exception);
        }

    }

    /**
     * Add a relation between the objects identified by <code>sourcePID</code>
     * and <code>targetPID</code>. The information about the relation will be
     * stored in the source object identified by <code>sourcePID</code> and the
     * type specified by <code>relationType</code> must be valid according to
     * the content model for the object.
     * 
     * @param sourcePID
     *            ID of the source object in the relation.
     * @param relationType
     *            Relation type ID which is valid according the the content
     *            model for the source object.
     * @param targetPID
     *            ID of the target ("to") object of the relation.
     * @throws ServerOperationFailed
     *             if the relation cannot be added.
     */
    public void addObjectRelation(String sourcePID, String relationType,
            String targetPID) throws ServerOperationFailed {
        try {
            domsAPI.addRelation(sourcePID, "info:fedora/" + sourcePID,
                    relationType, "info:fedora/" + targetPID);
        } catch (Exception exception) {
            throw new ServerOperationFailed(
                    "Failed creating object relation (type: " + relationType
                            + ") from the source object (PID: " + sourcePID
                            + ") to the target object (PID: " + targetPID + ")",
                    exception);
        }
    }

    /**
     * Mark the objects identified by the the PIDs in <code>pidsToPublish</code>
     * as published, and thus viewable from the DOMS.
     * 
     * @param pidsToPublish
     *            <code>List</code> of PIDs for the objects to publish.
     * @throws ServerOperationFailed
     *             if any errors are encountered while publishing the objects.
     */
    public void publishObjects(List<String> pidsToPublish)
            throws ServerOperationFailed {
        try {
            domsAPI.markPublishedObject(pidsToPublish);
        } catch (Exception exception) {
            throw new ServerOperationFailed(
                    "Failed marking objects as published. PIDs: "
                            + pidsToPublish, exception);
        }
    }

    /**
     * Get a time-stamp for when the latest change was made to any of the
     * objects which currently are in the state specified by <code>state</code>
     * and are associated with the view, specified by <code>viewID</code>, from
     * the collection identified by <code>collectionPID</code>.
     * 
     * TODO: can we say anything about the timezone of the time-stamp
     * 
     * @param collectionPID
     *            PID of the collection to get the modification time-stamp for.
     * @param viewID
     *            ID of the view which any modified object must be a part of.
     * @param state
     *            The state the objects must have in order to have its
     *            modification time inspected.
     * @return the time-stamp in milliseconds for the latest modification.
     * @throws ServerOperationFailed
     *             if the time-stamp cannot be retrieved.
     */
    public long getModificationTime(URI collectionPID, String viewID,
            String state) throws ServerOperationFailed {

        try {
            return domsAPI.getLatestModified(collectionPID.toString(), viewID,
                    state);

        } catch (Exception exception) {
            throw new ServerOperationFailed(
                    "Failed retrieving the modification time-stamp for the "
                            + "collection with this PID: '" + collectionPID
                            + "' and this viewID: '" + viewID + "'.", exception);
        }
    }

    /**
     * Get a <code>List</code> of <code>RecordDescription</code> instances
     * describing entry objects which have been modified later than time
     * specified by <code>timeStamp</code> in the collection specified by
     * <code>collectionPID</code>. An entry object is considered to be modified
     * if itself or any other object associated with the view specified by
     * <code>viewID</code> has been modified. Also, the entry objects affected
     * by modifications must be in the state described by
     * <code>objectState</code> in order to get included in the result set.
     * <p/>
     * The parameters <code>offsetIndex</code> and <code>maxRecordCount</code>
     * provides a means for retrieving a small number of result records at a
     * time. For example to get 10 records at a time, the first query will set
     * <code>offsetIndex</code> to 0 and <code>maxRecordCount</code> to 10. To
     * retrieve the next 10 records <code>offsetIndex</code> must be set to 10
     * and <code>maxRecordCount</code> to 10, and so on.
     * 
     * 
     * FIXME! objectState should probably not be a string. This class should
     * provide an Enum.
     * 
     * @param collectionPID
     *            the PID of the collection to make the query on.
     * @param viewID
     *            ID of the view to use when checking for modifications.
     * @param timeStamp
     *            Entry objects, matching the query, modified later than this
     *            time will be included in the result.
     * @param objectState
     *            The state an object must be in, in order to be a candidate for
     *            retrieval.
     * @param offsetIndex
     *            The index in the sequence of modified records to start
     *            retrieval from.
     * @param maxRecordCount
     *            The maximum number of records to retrieve.
     * @return A list of <code>RecordDescription</code> instances, containing
     *         information about all entry objects matching the query.
     * @throws ServerOperationFailed
     *             if the operation fails.
     */
    public List<RecordDescription> getModifiedEntryObjects(URI collectionPID,
            String viewID, long timeStamp, String objectState,
            long offsetIndex, long maxRecordCount) throws ServerOperationFailed {
        try {
            return domsAPI.getIDsModified(timeStamp, collectionPID.toString(),
                    viewID, objectState, (int) offsetIndex,
                    (int) maxRecordCount);
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

    /**
     * Get the view bundle for the view specified by <code>viewID</code> for the
     * DOMS object with the PID <code>entryObjectPID</code>. The returned bundle
     * contains all information from the object, and objects associated with it,
     * which is relevant for the specified view.
     * 
     * @param entryObjectPID
     *            The PID of the entry (i.e. root) object to fetch a view bundle
     *            for.
     * @param viewID
     *            ID of the view which the DOMS must use when building the
     *            bundle.
     * @return A <code>String</code> containing an XML document with all the
     *         information from the object and its associated objects which is
     *         relevant to the specified view.
     * @throws ServerOperationFailed
     *             if the view bundle cannot be retrieved from the DOMS.
     */
    public String getViewBundle(URI entryObjectPID, String viewID)
            throws ServerOperationFailed {
        try {
            final String entryObjectPIDString = entryObjectPID.toString();
            ViewBundle viewBundle = domsAPI.getViewBundle(entryObjectPIDString,
                    viewID);
            return viewBundle.getContents();
        } catch (Exception exception) {
            throw new ServerOperationFailed(
                    "Failed retrieving the view record (viewID=" + viewID
                            + ") containing the specified object (objectPID = "
                            + entryObjectPID + ").", exception);
        }
    }

    /**
     * Set the object label specified by <code>objectLabel</code> on the DOMS
     * object identified by the PID specified by <code>objectPID</code>.
     * 
     * @param objectPID
     *            The PID identifying the object to set the label on.
     * @param objectLabel
     *            The label to set on the object.
     * @throws ServerOperationFailed
     *             if the label could not be set on the object.
     */
    public void setObjectLabel(URI objectPID, String objectLabel)
            throws ServerOperationFailed {
        try {
            final String PIDString = objectPID.toString();
            domsAPI.setObjectLabel(PIDString, objectLabel);
        } catch (Exception exception) {
            throw new ServerOperationFailed("Failed setting label ('"
                    + objectLabel + "') on DOMS object (PID = " + objectPID
                    + ").");
        }
    }
}
