package dk.statsbiblioteket.doms.client;

import dk.statsbiblioteket.doms.centralWebservice.RecordDescription;
import org.w3c.dom.Document;

import javax.xml.namespace.QName;
import java.net.URL;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: Nov 3, 2010
 * Time: 4:53:19 PM
 * To change this template use File | Settings | File Templates.
 * @author Thomas Skou Hansen &lt;tsh@statsbiblioteket.dk&gt;
 * @author Esben Agerbæk Black &lt;eab@statsbiblioteket.dk&gt;
 */
public interface DomsWSClient {
    /**
     * @deprecated please use setCredentials(URL, String, String)
     * @see #setCredentials(URL, String, String)
     *
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
    @Deprecated
    void login(URL domsWSAPIEndpoint, String userName, String password);

     /**
     * User credentials to the DOMS web service, using the end-point <code>URL</code>
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
    void setCredentials(URL domsWSAPIEndpoint, String userName, String password);

    /**
     * Create a new DOMS object from an object template already stored in the
     * DOMS.
     *
     * @param templatePID
     *            PID identifying the template object to use.
     * @return PID of the created object.
     * @throws dk.statsbiblioteket.doms.client.ServerOperationFailed
     *             if the object creation failed.
     */
    String createObjectFromTemplate(String templatePID)
            throws ServerOperationFailed;

    /**
     * Create a new DOMS object from an object template already stored in the
     * DOMS. Set the old identifiers of the object to this list. The old
     * identifiers are whatever this object have been known as beforehand.
     *
     * @param templatePID
     *            PID identifying the template object to use.
     * @param oldIdentifiers the old identifiers of the object
     * @return PID of the created object.
     * @throws dk.statsbiblioteket.doms.client.ServerOperationFailed
     *             if the object creation failed.
     * @see #createObjectFromTemplate(String)
     */
    String createObjectFromTemplate(String templatePID, List<String> oldIdentifiers)
            throws ServerOperationFailed;

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
     * @throws dk.statsbiblioteket.doms.client.ServerOperationFailed
     *             if the object creation failed.
     * @see dk.statsbiblioteket.doms.client.FileInfo
     */
    String createFileObject(String templatePID, FileInfo fileInfo)
            throws ServerOperationFailed;

    /**
     * Add a physical file to an existing file object in the DOMS.
     * <p/>
     *
     * The existing file object in DOMS with <code>fileObjectPID</code> will be
     * associated with the physical file described by the <code>FileInfo</code>
     * instance.
     *
     * @param fileObjectPID
     *            The PID of the DOMS file object to associate the physical file
     *            with.
     * @param fileInfo
     *            File location, checksum and so on for the physical file
     *            associated with the file object.
     * @throws dk.statsbiblioteket.doms.client.ServerOperationFailed
     *             if the operation fails.
     * @see dk.statsbiblioteket.doms.client.FileInfo
     */
    void addFileToFileObject(String fileObjectPID, FileInfo fileInfo)
            throws ServerOperationFailed;

    /**
     * Get the PID of an existing file object in the DOMS which is associated
     * with the physical file specified by <code>fileURL</code>.
     *
     * @param fileURL
     *            location of the physical file to find the corresponding DOMS
     *            file object for.
     * @return PID of the DOMS file object.
     * @throws dk.statsbiblioteket.doms.client.NoObjectFound
     *             if there does not exist DOMS file object associated with
     *             <code>fileURL</code>.
     * @throws dk.statsbiblioteket.doms.client.ServerOperationFailed
     *             if any errors are encountered while looking up the file
     *             object.
     */
    String getFileObjectPID(URL fileURL) throws NoObjectFound,
                                                ServerOperationFailed;

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
     * @throws dk.statsbiblioteket.doms.client.ServerOperationFailed
     *             if the datastream contents cannot be retrieved.
     */
    Document getDataStream(String objectPID, String datastreamID)
            throws ServerOperationFailed;

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
     * @throws dk.statsbiblioteket.doms.client.ServerOperationFailed
     *             if the datastream contents cannot be updated.
     */
    void updateDataStream(String objectPID, String dataStreamID,
                          Document newDataStreamContents) throws ServerOperationFailed;

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
     * @throws dk.statsbiblioteket.doms.client.ServerOperationFailed
     *             if the relation cannot be added.
     */
    void addObjectRelation(String sourcePID, String relationType,
                           String targetPID) throws ServerOperationFailed;

    /**
     * Mark the objects identified by the the PIDs in <code>pidsToPublish</code>
     * as published, and thus viewable from the DOMS.
     *
     * @param pidsToPublish
     *            <code>List</code> of PIDs for the objects to publish.
     * @throws dk.statsbiblioteket.doms.client.ServerOperationFailed
     *             if any errors are encountered while publishing the objects.
     */
    void publishObjects(List<String> pidsToPublish)
            throws ServerOperationFailed;

    /**
     * Mark the objects identified by the the PIDs in <code>pidsToDelete</code>
     * as deleted, and thus invisible from the DOMS.
     *
     * @param pidsToDelete
     *            <code>List</code> of PIDs for the objects to delete.
     * @throws dk.statsbiblioteket.doms.client.ServerOperationFailed
     *             if any errors are encountered while deleting the objects.
     */
    void deleteObjects(List<String> pidsToDelete)
            throws ServerOperationFailed;

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
     * @throws dk.statsbiblioteket.doms.client.ServerOperationFailed
     *             if the time-stamp cannot be retrieved.
     */
    long getModificationTime(String collectionPID, String viewID,
                             String state) throws ServerOperationFailed;

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
     * @throws dk.statsbiblioteket.doms.client.ServerOperationFailed
     *             if the operation fails.
     */
    List<RecordDescription> getModifiedEntryObjects(
            String collectionPID, String viewID, long timeStamp,
            String objectState, long offsetIndex, long maxRecordCount)
            throws ServerOperationFailed;

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
     * @throws dk.statsbiblioteket.doms.client.ServerOperationFailed
     *             if the view bundle cannot be retrieved from the DOMS.
     */
    String getViewBundle(String entryObjectPID, String viewID)
            throws ServerOperationFailed;

    /**
     * Set the object label specified by <code>objectLabel</code> on the DOMS
     * object identified by the PID specified by <code>objectPID</code>.
     *
     * @param objectPID
     *            The PID identifying the object to set the label on.
     * @param objectLabel
     *            The label to set on the object.
     * @throws dk.statsbiblioteket.doms.client.ServerOperationFailed
     *             if the label could not be set on the object.
     */
    void setObjectLabel(String objectPID, String objectLabel)
            throws ServerOperationFailed;
}
