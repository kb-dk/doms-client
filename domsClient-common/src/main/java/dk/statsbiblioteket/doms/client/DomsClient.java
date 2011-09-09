package dk.statsbiblioteket.doms.client;

import dk.statsbiblioteket.doms.central.InvalidResourceException;
import dk.statsbiblioteket.doms.central.MethodFailedException;

import java.io.InputStream;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: eab
 * Date: 9/6/11
 * Time: 8:51 AM
 * To change this template use File | Settings | File Templates.
 */
public interface DomsClient {



    /**
     * Get the datastream <code>ds</code> from the object <code>pid</code> the stream can be parsed by <code>
     *     documentBuilder.parse(ds)</code> when <code>XML</code> is expected.
     *
     * @param pid the persistent identifier of the object of intrest.
     * @param ds identifies the datastream of intrest.
     * @return MIMETypeStream containing the datastream.
     * @throws dk.statsbiblioteket.doms.client.ServerOperationFailed
     *          If the object or datastream cannot be found.
     */
    InputStream getDatastreamContent(String pid, String ds) throws ServerOperationFailed,
                MethodFailedException, InvalidResourceException;

    /**
     *
     * @param uuids the list of uuid's for which to get the label
     * @return the labels of the given uuid's
     */
    List<String> getLabels(List<String> uuids);

    /**
     *
     * @param uuid the uuid for which to get the label
     * @return the label found for the uuid
     */
    String getLabel(String uuid);

    /**
     * Get the <code>FedoraState</code> for the DOMS object with the specified <code>PID</code>.
     *
     * @param pid       The PID identifying the object of intrest.
     * @return A FedoraState enum indicating the state of the object.
     * @throws dk.statsbiblioteket.doms.client.ServerOperationFailed
     *          If the object cannot be found.
     */
    FedoraState getState(String pid) throws ServerOperationFailed;

    /**
     * Add a relation between the objects identified by <code>sourcePID</code>
     * and <code>targetPID</code>. The information about the relation will be
     * stored in the source object identified by <code>sourcePID</code> and the
     * type specified by <code>relationType</code> must be valid according to
     * the content model for the object.
     *
     * @param objectPID    ID of the object housing the relation.
     * @param relationType AbstractRelation type ID which is valid according the the content
     *                     model for the source object.
     * @return a List of Relations matching the restrictions
     * @throws dk.statsbiblioteket.doms.client.ServerOperationFailed
     *          if the relation cannot be added.
     */
    List<Relation> listObjectRelations(String objectPID, String relationType
    ) throws ServerOperationFailed;



}
