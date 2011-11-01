package dk.statsbiblioteket.doms.client.impl.ontology;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 10/25/11
 * Time: 11:37 AM
 * To change this template use File | Settings | File Templates.
 */
public interface OWLRestriction {
    int getMinCardinality();

    int getMaxCardinality();

    int getCardinality();

    String getOnProperty();

    String getAllValuesFrom();
}
