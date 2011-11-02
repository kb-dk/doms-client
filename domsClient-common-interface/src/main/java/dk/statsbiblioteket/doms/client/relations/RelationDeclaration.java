package dk.statsbiblioteket.doms.client.relations;

import dk.statsbiblioteket.doms.client.objects.ContentModelObject;
import org.eclipse.emf.ecore.impl.EStructuralFeatureImpl;

import java.io.StringWriter;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 10/31/11
 * Time: 4:28 PM
 * To change this template use File | Settings | File Templates.
 */
public interface RelationDeclaration {

    Set<String> getViewAngles();

    Set<String> getInverseViewAngles();

    int getMinCardinality();

    int getExactCardinality();

    int getMaxCardinality();

    Set<ContentModelObject> getFirstLevelModels();

    String getPredicate();
}
