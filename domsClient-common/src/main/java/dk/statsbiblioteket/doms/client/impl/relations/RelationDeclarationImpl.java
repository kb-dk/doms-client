package dk.statsbiblioteket.doms.client.impl.relations;

import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.objects.ContentModelObject;
import dk.statsbiblioteket.doms.client.objects.DigitalObject;
import dk.statsbiblioteket.doms.client.objects.DigitalObjectFactory;
import dk.statsbiblioteket.doms.client.ontology.OWLObjectProperty;
import dk.statsbiblioteket.doms.client.ontology.OWLRestriction;
import dk.statsbiblioteket.doms.client.relations.RelationDeclaration;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 10/31/11
 * Time: 4:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class RelationDeclarationImpl implements RelationDeclaration {


    private String predicate;
    private Set<String> viewAngles;
    private Set<String> inverseViewAngles;
    private DigitalObjectFactory factory;

    private Set<ContentModelObject> firstLevelModels;

    private int max = Integer.MAX_VALUE;
    private int exact = -1;
    private int min = -1;


    public RelationDeclarationImpl(String predicate,
                                   OWLObjectProperty owlObjectProperty, Set<String> viewAngles,
                                   Set<String> inverseViewAngles,
                                   DigitalObjectFactory factory) throws ServerOperationFailed {
        this.predicate = predicate;
        if (viewAngles == null){
            viewAngles = new HashSet<String>();
        }
        if (inverseViewAngles == null){
            inverseViewAngles = new HashSet<String>();
        }
        this.viewAngles = viewAngles;
        this.inverseViewAngles = inverseViewAngles;
        this.factory = factory;

        firstLevelModels = new HashSet<ContentModelObject>();
        for (OWLRestriction owlRestriction : owlObjectProperty.getOwlRestrictions()) {
            int tmp = owlRestriction.getMinCardinality();
            if (tmp > min){
                min = tmp;
            }
            tmp = owlRestriction.getMaxCardinality();
            if (tmp > 0 && tmp < max){
                max = tmp;
            }

            tmp = owlRestriction.getCardinality();
            if (tmp >= 0){
                if (exact < 0){
                    exact = tmp;
                } else {
                    //TODO we have a problem, cannot be satisfied
                }
            }
            String allValuesFrom = owlRestriction.getAllValuesFrom();
            if (allValuesFrom != null){
                allValuesFrom = allValuesFrom.replace("#class","");//Strip class decl
                DigitalObject object = factory.getDigitalObject(allValuesFrom);
                if (object instanceof ContentModelObject) {
                    ContentModelObject contentModelObject = (ContentModelObject) object;
                    firstLevelModels.add(contentModelObject);
                }

            }


        }
    }

    @Override
    public Set<String> getViewAngles() {
        return Collections.unmodifiableSet(viewAngles);
    }

    @Override
    public Set<String> getInverseViewAngles() {
        return Collections.unmodifiableSet(inverseViewAngles);
    }

    @Override
    public int getMinCardinality() {
        return min;
    }

    @Override
    public int getExactCardinality() {
        return exact;
    }

    @Override
    public int getMaxCardinality() {
        return max;
    }

    @Override
    public Set<ContentModelObject> getFirstLevelModels() {
        return Collections.unmodifiableSet(firstLevelModels);
    }

    @Override
    public String getPredicate() {
        return predicate;
    }

    @Override
    public String toString() {
        return predicate.toString();
    }
}
