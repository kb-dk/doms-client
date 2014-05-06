package dk.statsbiblioteket.doms.client.impl.relations;

import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.impl.ontology.OWLObjectProperty;
import dk.statsbiblioteket.doms.client.impl.ontology.ParsedOwlOntology;
import dk.statsbiblioteket.doms.client.objects.ContentModelObject;
import dk.statsbiblioteket.doms.client.objects.DigitalObjectFactory;
import dk.statsbiblioteket.doms.client.relations.RelationDeclaration;
import dk.statsbiblioteket.doms.client.relations.RelationModel;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 10/31/11
 * Time: 4:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class RelationModelImpl implements RelationModel {

    ContentModelObject myModel;
    Set<RelationDeclaration> declarations;
    private ParsedOwlOntology ontology;
    private DigitalObjectFactory factory;

    public RelationModelImpl(ContentModelObject myModel, ParsedOwlOntology ontology, DigitalObjectFactory factory) {
        this.myModel = myModel;
        this.ontology = ontology;
        this.factory = factory;
    }

    @Override
    public ContentModelObject getContentModel() {
        return myModel;
    }

    @Override
    public synchronized Set<RelationDeclaration> getRelationDeclarations() throws ServerOperationFailed {
        if (declarations != null) {
            return Collections.unmodifiableSet(declarations);
        }
        Map<String, Set<String>> predicateToAngle = new HashMap<String, Set<String>>();
        Map<String, Set<String>> predicateToAngleInverse = new HashMap<String, Set<String>>();
        for (String viewAngle : myModel.getDeclaredViewAngles()) {

            List<String> rels = myModel.getRelationsWithViewAngle(viewAngle);
            for (String rel : rels) {
                Set<String> set = predicateToAngle.get(rel);
                if (set == null) {
                    set = new HashSet<String>();
                    predicateToAngle.put(rel, set);
                }
                set.add(viewAngle);
            }

            List<String> invrels = myModel.getInverseRelationsWithViewAngle(viewAngle);
            for (String invrel : invrels) {
                Set<String> set = predicateToAngle.get(invrel);
                if (set == null) {
                    set = new HashSet<String>();
                    predicateToAngle.put(invrel, set);
                }
                set.add(viewAngle);
            }

        }
        declarations = new HashSet<RelationDeclaration>();
        for (OWLObjectProperty owlObjectProperty : ontology.getOwlObjectProperties()) {
            String predicate = owlObjectProperty.getMappingId();
            RelationDeclaration declaration = new RelationDeclarationImpl(
                    predicate,
                    owlObjectProperty,
                    predicateToAngle.get(predicate),
                    predicateToAngleInverse.get(predicate),
                    factory);
            declarations.add(declaration);
        }
        return Collections.unmodifiableSet(declarations);
    }
}
