package dk.statsbiblioteket.doms.client.impl.relations;

import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.objects.ContentModelObject;
import dk.statsbiblioteket.doms.client.objects.DigitalObject;
import dk.statsbiblioteket.doms.client.objects.DigitalObjectFactory;
import dk.statsbiblioteket.doms.client.ontology.OWLObjectProperty;
import dk.statsbiblioteket.doms.client.ontology.ParsedOwlOntology;
import dk.statsbiblioteket.doms.client.relations.Relation;
import dk.statsbiblioteket.doms.client.utils.Constants;

import java.lang.ref.SoftReference;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Barebones relation, predicate and subject. No subject
 */
public abstract class AbstractRelation implements Relation, Comparable<Relation>{
    private String predicate;   // relation name
    private SoftReference<DigitalObject> subject = new SoftReference<DigitalObject>(null);
    private String pid;
    private DigitalObjectFactory factory;


    /**
     * This constructor must be extended to complete the notion of triples
     * representing connections in the subject graph.
     * @param predicate
     * @param subjectPid
     */
    public AbstractRelation(String predicate, String subjectPid, DigitalObjectFactory factory) {
        this.predicate = predicate;
        this.factory = factory;
        this.pid = Constants.ensurePID(subjectPid);
    }


    @Override
    public DigitalObject getSubject() throws ServerOperationFailed {
        DigitalObject result = subject.get();
        if (result == null){
            result = factory.getDigitalObject(pid);
            subject = new SoftReference<DigitalObject>(result);
        }
        return result;
    }

    @Override
    public String getSubjectPid() {
        return pid;
    }

    @Override
    public String getPredicate() {
        return predicate;
    }

    @Override
    public void remove() throws ServerOperationFailed {
        getSubject().removeRelation(this);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AbstractRelation)) {
            return false;
        }

        AbstractRelation that = (AbstractRelation) o;

        if (!pid.equals(that.pid)) {
            return false;
        }
        if (!predicate.equals(that.predicate)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = predicate.hashCode();
        result = 31 * result + pid.hashCode();
        return result;
    }

    @Override
    public int compareTo(Relation o) {
        return new Integer(hashCode()).compareTo(o.hashCode());
    }
}
