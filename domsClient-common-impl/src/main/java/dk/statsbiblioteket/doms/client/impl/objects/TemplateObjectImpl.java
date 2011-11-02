package dk.statsbiblioteket.doms.client.impl.objects;

import dk.statsbiblioteket.doms.central.*;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.objects.ContentModelObject;
import dk.statsbiblioteket.doms.client.objects.DigitalObject;
import dk.statsbiblioteket.doms.client.objects.DigitalObjectFactory;
import dk.statsbiblioteket.doms.client.objects.TemplateObject;
import dk.statsbiblioteket.doms.client.relations.ObjectRelation;
import dk.statsbiblioteket.doms.client.relations.Relation;
import dk.statsbiblioteket.doms.client.utils.Constants;

import java.lang.String;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Template objects are objects that can be cloned to make new objects.
 */
public class TemplateObjectImpl extends AbstractDigitalObject implements TemplateObject {

    public TemplateObjectImpl(ObjectProfile profile, CentralWebservice api, DigitalObjectFactory factory)
            throws ServerOperationFailed {
        super(profile, api, factory);
    }


    @Override
    public DigitalObject clone(String... oldIDs) throws ServerOperationFailed {

        try {
            String newpid = api.newObject(this.getPid(), Arrays.asList(oldIDs), "Created new Object from Client");
            return factory.getDigitalObject(newpid);
        } catch (InvalidCredentialsException e) {
            throw new ServerOperationFailed(e);
        } catch (InvalidResourceException e) {
            throw new ServerOperationFailed(e);
        } catch (MethodFailedException e) {
            throw new ServerOperationFailed(e);
        }
    }

    @Override
    public Set<ContentModelObject> getTemplatedClasses() throws ServerOperationFailed {
        List<Relation> rels = getRelations();
        Set<ContentModelObject> classes = new HashSet<ContentModelObject>();
        for (Relation rel : rels) {
            if (rel.getPredicate().equals(Constants.TEMPLATE_PREDICATE)){
                if (rel instanceof ObjectRelation) {
                    ObjectRelation objectRelation = (ObjectRelation) rel;
                    if (objectRelation.getObject() instanceof ContentModelObject) {
                        classes.add((ContentModelObject) objectRelation.getObject());
                    }
                }
            }
        }
        return classes;
    }

}
