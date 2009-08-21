package dk.statsbiblioteket.doms;

import java.net.URI;
import java.util.List;

/**
 * TODO abr forgot to document this class
 */
public interface RelationDefinition {

    /**
     * Get the name of the relation.
     * @return
     */
    public String getName();

    /**
     * Gives the list of content models that the objects of this relation
     * must subscribe to.
     * @return the list of content models
     */
    public List<URI> getAllValuesFrom();

    /**
     * Gives the maximum number of relations with this name there must
     * exist in an object. In case no max have been defined, return Integer.Max
     * @return the max cardinality
     */
    public int getMaxCardinality();

    /**
     * Gives the minimum number of relations with this name there must
     * exist in an object. In case no min has been defined, return 0
     * @return the min cardinality
     */
    public int getMinCardinality();
}
