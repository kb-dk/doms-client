package dk.statsbiblioteket.doms.client.objects;

import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 9/15/11
 * Time: 3:24 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ContentModelObject extends DigitalObject {

    public List<String> getRelationsWithViewAngle(String viewAngle) throws ServerOperationFailed;

    public List<String> getInverseRelationsWithViewAngle(String viewAngle) throws ServerOperationFailed;


}
