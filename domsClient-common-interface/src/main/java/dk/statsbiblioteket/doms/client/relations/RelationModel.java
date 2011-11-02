package dk.statsbiblioteket.doms.client.relations;

import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.objects.ContentModelObject;

import java.util.Set;


/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 10/31/11
 * Time: 4:27 PM
 * To change this template use File | Settings | File Templates.
 */
public interface RelationModel {

    ContentModelObject getContentModel();

    Set<RelationDeclaration> getRelationDeclarations() throws ServerOperationFailed;
}
