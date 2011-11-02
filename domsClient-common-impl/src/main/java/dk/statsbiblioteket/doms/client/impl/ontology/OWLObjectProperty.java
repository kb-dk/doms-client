package dk.statsbiblioteket.doms.client.impl.ontology;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 10/25/11
 * Time: 11:36 AM
 * To change this template use File | Settings | File Templates.
 */
public interface OWLObjectProperty {
    String getId();

    String getAbout();

    int getMinCardinality();

    int getMaxCardinality();

    int getCardinality();

    String getMappingId();

    String getLabel();

    List<OWLRestriction> getOwlRestrictions();

    ParsedOwlOntology getMyDataStream();

    String getAllValuesFrom();

    void addOwlRestriction(OWLRestriction restriction);
}
