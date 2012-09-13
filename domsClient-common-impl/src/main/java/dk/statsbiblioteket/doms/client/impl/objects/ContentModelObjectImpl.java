package dk.statsbiblioteket.doms.client.impl.objects;

import dk.statsbiblioteket.doms.central.*;
import dk.statsbiblioteket.doms.central.Parameter;
import dk.statsbiblioteket.doms.client.datastreams.Datastream;
import dk.statsbiblioteket.doms.client.datastreams.DatastreamModel;
import dk.statsbiblioteket.doms.client.exceptions.NotFoundException;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.impl.datastreams.DatastreamModelImpl;
import dk.statsbiblioteket.doms.client.impl.datastreams.ExternalDatastreamImpl;
import dk.statsbiblioteket.doms.client.impl.datastreams.InternalDatastreamImpl;
import dk.statsbiblioteket.doms.client.impl.methods.MethodImpl;
import dk.statsbiblioteket.doms.client.impl.methods.ParameterImpl;
import dk.statsbiblioteket.doms.client.impl.ontology.ParsedOwlOntology;
import dk.statsbiblioteket.doms.client.impl.relations.RelationModelImpl;
import dk.statsbiblioteket.doms.client.impl.methods.*;
import dk.statsbiblioteket.doms.client.methods.ParameterType;
import dk.statsbiblioteket.doms.client.objects.ContentModelObject;
import dk.statsbiblioteket.doms.client.objects.DigitalObject;
import dk.statsbiblioteket.doms.client.objects.DigitalObjectFactory;
import dk.statsbiblioteket.doms.client.objects.TemplateObject;
import dk.statsbiblioteket.doms.client.impl.ontology.ParsedOwlOntologyImpl;
import dk.statsbiblioteket.doms.client.relations.LiteralRelation;
import dk.statsbiblioteket.doms.client.relations.ObjectRelation;
import dk.statsbiblioteket.doms.client.relations.Relation;
import dk.statsbiblioteket.doms.client.relations.RelationModel;
import dk.statsbiblioteket.doms.client.utils.Constants;
import dk.statsbiblioteket.util.xml.DOM;
import dk.statsbiblioteket.util.xml.XPathSelector;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.lang.String;
import java.util.*;


/**
 * Content Model objects are the objects that holds the structure of the objects in doms. TODO implement
 */
public class ContentModelObjectImpl extends AbstractDigitalObject implements
        ContentModelObject {
    private boolean parsed = false;
    private HashMap<String, List<String>> relations;
    private HashMap<String, List<String>> inverseRelations;

    private DigitalObjectFactory factory;
    private DatastreamModel dsModel;
    private boolean datastreamsLoaded = false;
    private ParsedOwlOntology ontology;
    private boolean ontologyLoaded = false;
    private RelationModel relationModel;

    private boolean methodsParsed = false;
    private Set<dk.statsbiblioteket.doms.client.methods.Method> methods;

    public ContentModelObjectImpl(ObjectProfile profile, CentralWebservice api,
                                  DigitalObjectFactory factory)
            throws ServerOperationFailed {
        super(profile, api, factory);    //To change body of overridden methods use File | Settings | File Templates.
        this.profile = profile;
        this.factory = factory;
    }

    public List<String> getRelationsWithViewAngle(String viewAngle) throws ServerOperationFailed {
        parseView();
        return relations.get(viewAngle);
    }

    public List<String> getInverseRelationsWithViewAngle(String viewAngle) throws ServerOperationFailed {
        parseView();
        return inverseRelations.get(viewAngle);
    }

    @Override
    public Set<String> getDeclaredViewAngles() throws ServerOperationFailed {
        parseView();
        HashSet<String> result = new HashSet<String>(relations.keySet());
        result.addAll(inverseRelations.keySet());
        return result;
    }

    @Override
    public synchronized RelationModel getRelationModel() throws ServerOperationFailed {
        if (relationModel != null){
            return relationModel;
        }
        loadOntology();
        relationModel = new RelationModelImpl(this, ontology, factory);
        return relationModel;
    }

    private synchronized void parseView() throws ServerOperationFailed {
        if (parsed){
            return;
        }
        relations = new HashMap<String, List<String>>();
        inverseRelations = new HashMap<String, List<String>>();
        //parseView
        Datastream viewStream;
        Document viewDoc;
        try {
            viewStream = this.getDatastream(Constants.VIEW_ID);
        } catch (NotFoundException e) {
            parsed = true;
            return;
        }
        String contents = viewStream.getContents();
        viewDoc = DOM.stringToDOM(contents, true);
        XPathSelector xPathSelector = DOM.createXPathSelector("v",
                Constants.VIEWS_NAMESPACE);

        NodeList allViewAngles = xPathSelector.selectNodeList(viewDoc,
                "/v:views/v:viewangle/@name");
        for (int i = 0; i < allViewAngles.getLength(); i++){
            String viewAngleName = allViewAngles.item(i).getTextContent();
            NodeList namedViewAngles = xPathSelector.selectNodeList(viewDoc,
                    "/v:views/v:viewangle[@name = '" + viewAngleName +
                            "']/v:relations/*");
            List<String> relList = new ArrayList<String>();
            for (int j = 0; j < namedViewAngles.getLength(); j++){
                Node item = namedViewAngles.item(j);
                relList.add(item.getNamespaceURI()+item.getLocalName());
            }
            relations.put(viewAngleName, relList);

            namedViewAngles = xPathSelector.selectNodeList(viewDoc,
                    "/v:views/v:viewangle[@name = '" + viewAngleName +
                            "']/v:inverseRelations/*");
            List<String> iRelList = new ArrayList<String>();
            for (int j = 0; j < namedViewAngles.getLength(); j++){
                Node item = namedViewAngles.item(j);
                iRelList.add(item.getNamespaceURI()+item.getLocalName());
            }
            inverseRelations.put(viewAngleName, iRelList);
        }
        parsed = true;
        /*
            <v:views xmlns:v="http://ecm.sourceforge.net/types/view/0/2/#">
          <v:viewangle name="SummaVisible">
            <v:relations xmlns:doms="http://doms.statsbiblioteket.dk/relations/default/0/1/#">
              <doms:hasShard></doms:hasShard>
            </v:relations>
            <v:inverseRelations></v:inverseRelations>
          </v:viewangle>
        </v:views>
        */
    }

    private synchronized void parseMethods() throws ServerOperationFailed {
        if (methodsParsed) {
            return;
        }
        List<Method> methodsSoap;
        try {
            methodsSoap = api.getMethods(this.getPid());
        } catch (Exception e) {
            throw new ServerOperationFailed("Failed to parse Methods", e);
        }
        methods = new HashSet<dk.statsbiblioteket.doms.client.methods.Method>();
        for (dk.statsbiblioteket.doms.central.Method soapmethod : methodsSoap) {
            Set<dk.statsbiblioteket.doms.client.methods.Parameter> myparameters = new HashSet<dk.statsbiblioteket.doms.client.methods.Parameter>();
            for (dk.statsbiblioteket.doms.central.Parameter soapparameter : soapmethod.getParameters().getParameter()) {
                String parameterTypeString = soapparameter.getType();

                dk.statsbiblioteket.doms.client.methods.Parameter myparameter = new ParameterImpl(soapparameter.getName(),
                        ParameterType.valueOf(parameterTypeString),
                        "",
                        soapparameter.isRequired(),
                        soapparameter.isRepeatable(),
                        soapparameter.getConfig());
                myparameters.add(myparameter);
            }
            MethodImpl myMethod = new MethodImpl(api,this, soapmethod.getName(), myparameters);
            methods.add(myMethod);
        }
        methodsParsed = true;
    }

    public DatastreamModel getDsModel() throws ServerOperationFailed {
        loadDatastreams();
        return dsModel;
    }

    @Override
    protected synchronized void loadDatastreams() throws ServerOperationFailed {
        if(datastreamsLoaded){
            return;
        }
        //System.out.println("loading datastreams for "+this.getPid());
        for (DatastreamProfile datastreamProfile : profile.getDatastreams()) {
            if(datastreamProfile.getId().equals(Constants.DS_COMPOSITE_MODEL_ID)){
                dsModel = new DatastreamModelImpl(datastreamProfile, this,
                        this.api);
                datastreams.add(dsModel);
            }else if (datastreamProfile.isInternal()){
                datastreams.add(new InternalDatastreamImpl(datastreamProfile, this, api));
            } else {
                datastreams.add(new ExternalDatastreamImpl(datastreamProfile, this, api));
            }
        }
        datastreamsLoaded = true;
    }

    public ParsedOwlOntology getOntology() throws ServerOperationFailed {
        loadOntology();
        return ontology;
    }

    @Override
    public Set<TemplateObject> getTemplates() throws ServerOperationFailed {
        List<ObjectRelation> templateRels = getInverseRelations(Constants.TEMPLATE_PREDICATE);
        Set<TemplateObject> result = new HashSet<TemplateObject>();
        for (ObjectRelation templateRel : templateRels) {
            if (templateRel.getSubject() instanceof TemplateObject) {
                TemplateObject templateObject = (TemplateObject) templateRel.getSubject();
                result.add(templateObject);
            }
        }
        return result;
    }

    @Override
    public Set<String> getEntryViewAngles() throws ServerOperationFailed {
        Set<String> result = new HashSet<String>();
        List<Relation> rels = getRelations();
        for (Relation rel : rels) {
            if (rel instanceof LiteralRelation) {
                LiteralRelation literalRelation = (LiteralRelation) rel;
                if (literalRelation.getPredicate().equals(Constants.VIEWANGLE_PREDICATE)){
                    result.add(literalRelation.getObject());
                }
            }
        }
        return result;
    }

    @Override
    public Set<DigitalObject> getSubscribingObjects() throws ServerOperationFailed {
        List<ObjectRelation> objectRels = getInverseRelations(Constants.HASMODEL_PREDICATE);
        Set<DigitalObject> result = new HashSet<DigitalObject>();
        for (ObjectRelation objectRel : objectRels) {
            result.add(objectRel.getSubject());
        }
        return result;
    }

    @Override
    public Set<ContentModelObject> getParents() throws ServerOperationFailed {
        List<Relation> rels = getRelations();
        Set<ContentModelObject> result = new HashSet<ContentModelObject>();
        for (Relation rel : rels) {
            if (rel instanceof ObjectRelation) {
                ObjectRelation objectRelation = (ObjectRelation) rel;
                if (objectRelation.getPredicate().equals(Constants.EXTENDSMODEL_PREDICATE)){
                    if (objectRelation.getObject() instanceof ContentModelObject) {
                        ContentModelObject contentModelObject = (ContentModelObject) objectRelation.getObject();
                        result.add(contentModelObject);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public Set<ContentModelObject> getDescendants() throws ServerOperationFailed {
        List<ObjectRelation> rels = getInverseRelations(Constants.EXTENDSMODEL_PREDICATE);
        Set<ContentModelObject> result = new HashSet<ContentModelObject>();
        for (ObjectRelation rel : rels) {
            if (rel.getObject() instanceof ContentModelObject) {
                ContentModelObject contentModelObject = (ContentModelObject) rel.getSubject();
                result.add(contentModelObject);
            }
        }
        return result;
    }

    private synchronized void loadOntology() throws ServerOperationFailed {
        if (ontologyLoaded){
            return;
        }
        loadDatastreams();
        for (Datastream datastream : datastreams) {
            if (datastream.getId().equals(Constants.ONTOLOGY)){
                ontology = new ParsedOwlOntologyImpl(datastream);
            }
        }
        if (ontology != null){
            ontologyLoaded = true;
        }
    }

    @Override
    public Set<dk.statsbiblioteket.doms.client.methods.Method> getMethods() throws ServerOperationFailed {
        parseMethods();
        return Collections.unmodifiableSet(methods);

    }


}

