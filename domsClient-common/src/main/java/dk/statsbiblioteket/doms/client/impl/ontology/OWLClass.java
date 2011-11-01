package dk.statsbiblioteket.doms.client.impl.ontology;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 10/25/11
 * Time: 11:35 AM
 * To change this template use File | Settings | File Templates.
 */
public interface OWLClass {
    List<OWLRestriction> getOwlRestrictions();

    String getName();
}
