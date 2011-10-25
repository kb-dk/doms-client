package dk.statsbiblioteket.doms.client.ontology;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 10/25/11
 * Time: 11:34 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ParsedOwlOntology {
    String getRdfBase();

    List<OWLObjectProperty> getOwlObjectProperties();

    OWLClass getOwlClass();

    OWLObjectProperty getOWLObjectProperty(String id);
}
