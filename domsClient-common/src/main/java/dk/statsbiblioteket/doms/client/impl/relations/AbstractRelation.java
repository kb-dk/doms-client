package dk.statsbiblioteket.doms.client.impl.relations;

import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.objects.ContentModelObject;
import dk.statsbiblioteket.doms.client.objects.DigitalObject;
import dk.statsbiblioteket.doms.client.objects.DigitalObjectFactory;
import dk.statsbiblioteket.doms.client.ontology.OWLObjectProperty;
import dk.statsbiblioteket.doms.client.ontology.ParsedOwlOntology;
import dk.statsbiblioteket.doms.client.relations.Relation;

import java.lang.ref.SoftReference;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Barebones relation, predicate and object. No subject
 */
public abstract class AbstractRelation implements Relation {
    private String predicate;   // relation name
    private SoftReference<DigitalObject> object = new SoftReference<DigitalObject>(null);
    private String pid;
    private DigitalObjectFactory factory;


    /**
     * This constructor must be extended to complete the notion of triples
     * representing connections in the object graph.
     * @param predicate
     * @param objectPid
     */
    public AbstractRelation(String predicate, String objectPid, DigitalObjectFactory factory) {
        this.predicate = predicate;
        this.factory = factory;
        this.pid = objectPid;
    }


    @Override
    public DigitalObject getObject() throws ServerOperationFailed {
        DigitalObject result = object.get();
        if (result == null){
            result = factory.getDigitalObject(pid);
            object = new SoftReference<DigitalObject>(result);
        }
        return result;
    }

    @Override
    public String getObjectPid() {
        return pid;
    }

    @Override
    public String getPredicate() {
        return predicate;
    }

    @Override
    public void remove() throws ServerOperationFailed {
        getObject().removeRelation(this);
    }

    protected DigitalObjectFactory getFactory() {
        return factory;
    }

    @Override
    public Set<OWLObjectProperty> getOwlProperties() throws ServerOperationFailed {
        Set<OWLObjectProperty> result = new HashSet<OWLObjectProperty>();
        DigitalObject house = factory.getDigitalObject(pid);
        List<ContentModelObject> types = house.getType();
        for (ContentModelObject type : types) {
            ParsedOwlOntology ontology = type.getOntology();
            OWLObjectProperty property = ontology.getOWLObjectProperty(predicate);
            if (property != null){
                result.add(property);
            }
        }
        return result;
    }
}
