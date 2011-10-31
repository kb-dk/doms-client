package dk.statsbiblioteket.doms.client.impl.relations;

import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.objects.ContentModelObject;
import dk.statsbiblioteket.doms.client.objects.DigitalObject;
import dk.statsbiblioteket.doms.client.objects.DigitalObjectFactory;
import dk.statsbiblioteket.doms.client.ontology.OWLObjectProperty;
import dk.statsbiblioteket.doms.client.ontology.ParsedOwlOntology;
import dk.statsbiblioteket.doms.client.relations.Relation;
import dk.statsbiblioteket.doms.client.relations.RelationDeclaration;
import dk.statsbiblioteket.doms.client.relations.RelationModel;
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
    private String subjectPid;
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
        this.subjectPid = Constants.ensurePID(subjectPid);
    }


    @Override
    public DigitalObject getSubject() throws ServerOperationFailed {
        DigitalObject result = subject.get();
        if (result == null){
            result = factory.getDigitalObject(subjectPid);
            subject = new SoftReference<DigitalObject>(result);
        }
        return result;
    }

    @Override
    public String getSubjectPid() {
        return subjectPid;
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
    public Set<RelationDeclaration> getDeclarations() throws ServerOperationFailed {
        DigitalObject house = getSubject();
        List<ContentModelObject> types = house.getType();
        Set<RelationDeclaration> result = new HashSet<RelationDeclaration>();
        for (ContentModelObject type : types) {
            RelationModel relModel = type.getRelationModel();
            for (RelationDeclaration relationDeclaration : relModel.getRelationDeclarations()) {
                if (relationDeclaration.getPredicate().equals(this.getPredicate())){
                    result.add(relationDeclaration);
                }
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

        if (!subjectPid.equals(that.subjectPid)) {
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
        result = 31 * result + subjectPid.hashCode();
        return result;
    }

    @Override
    public int compareTo(Relation o) {
        return new Integer(hashCode()).compareTo(o.hashCode());
    }
}
