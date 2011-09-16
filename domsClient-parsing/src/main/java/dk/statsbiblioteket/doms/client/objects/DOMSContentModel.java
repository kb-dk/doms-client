package dk.statsbiblioteket.doms.client.objects;

import dk.statsbiblioteket.doms.client.datastreams.DOMSDataStreamCompositeModel;
import dk.statsbiblioteket.doms.client.datastreams.DOMSDataStreamOntology;
import dk.statsbiblioteket.doms.client.datastreams.DOMSDataStreamView;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.owl.OWLObjectProperties;
import dk.statsbiblioteket.doms.client.owl.OWLObjectProperty;
import dk.statsbiblioteket.doms.client.sdo.DOMSXMLDocumentOld;
import dk.statsbiblioteket.doms.client.sdo.DOMSXmlElement;
import dk.statsbiblioteket.doms.client.util.Constants;
import org.w3c.dom.Node;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.*;


/**
 * DOMS Content Model. A content model is an object, but each also represent
 * a class of data objects.
 *  //TODO document
 */
public class DOMSContentModel extends DOMSDigitalObject {

    private DOMSDataObject currentDataObject;

    /**
     * dataObjects are the instances created from this DOMSContentModel.
     */
    private Map<String, DOMSDataObject> dataObjects = new Hashtable<String, DOMSDataObject>();

    /**
     * The DOMSContentModel that this DOMSContentModel extends.
     *
     */
    private DOMSContentModel parentCM;

    /**
     * ownerCM is the DOMSContentModel that has this DOMSContentModel as a sub
     * DOMSContentModel, i.e. ownerCM has a relation, listed
     * in the GUI VIEW DataStream, to this DOMSContentModel.
     *
     * On the class level, certain object classes are marked as children of others.
     *
     *  @see #childrenCM
     * @see DOMSDataObject#children
     */
    private DOMSContentModel ownerCM;

    /**
     * childrenCM is a list of the DOMSContentModels that are related to this DOMSContentModel via the relations listed in the
     * GUI VIEW DataStream.
     *  @see #ownerCM
     */
    private Map<String,DOMSContentModel> childrenCM = new HashMap<String, DOMSContentModel>();


    /**
     * The "sub" objects that the user can create and/or create pointers to.
     */
    private OWLObjectProperties viewRelations;

    /**
     * The "sub" objects that the user can create pointers to, but cannot create
     */
    private OWLObjectProperties otherRelations;


    /**
     * The ontology datastream
     */
    private DOMSDataStreamOntology ontologyDataStream;


    private DOMSDataStreamCompositeModel compositeModelDS;

    /**
     * The view datastream
     */
    private DOMSDataStreamView viewDataStream;

    /**
     * Creates a new java object content model. The pid must exist as a
     * content model in the repository. The content model is not loaded
     * @param pid the pid of the content model
     * @throws RemoteException
     * @throws IOException
     */
    public DOMSContentModel(String pid) throws RemoteException, IOException, ServerOperationFailed {
        super(pid);
        parentCM = null;
        ownerCM = null;
    }


    /**
     * Loads this object, and all the parent content models from the repository
     * @param parentPids the parents to load as a well
     * @see #loadAncestors(java.util.List)
     * @throws IOException
     * @throws MyXMLWriteException
     * @throws DOMSIllegalStateException
     */
    public void load(List<String> parentPids) throws
            IOException,
            MyXMLWriteException,
            DOMSIllegalStateException, MyXMLParseException, ServerOperationFailed {

        if (isLoaded) {
            return;
        }

        loadAncestors(parentPids);
    }

    /**
     *
     * @param parentPids
     * @throws IOException
     * @throws MyXMLWriteException
     * @throws DOMSIllegalStateException
     */
    private void loadAncestors(List<String> parentPids) throws
            IOException,
            MyXMLWriteException,
            DOMSIllegalStateException, MyXMLParseException, ServerOperationFailed {
        InputStream is;
        DOMSXMLDocumentOld xmlSnippet;
        Node dsNode;

        isLoaded = true;

        //Get the VIEW DataStream.
        is = repository().getDatastreamDissemination(getPid(), Constants.VIEW_ID);
        if (is!=null)
        {
            xmlSnippet = new DOMSXMLDocumentOld();
            xmlSnippet.Load(is);
            is.close();
            dsNode = xmlSnippet.getDocNode();
            if (dsNode!=null)
            {
                DOMSDataStreamView viewDS = new DOMSDataStreamView(this);
                viewDS.parseGuiView(dsNode);
                setViewDataStream(viewDS);
            }
        }
        if (getViewDataStream()==null)
        {
            setViewDataStream(new DOMSDataStreamView());
        }

        //Get the DS_COMPOSITE_MODEL_ID DataStream
        is = repository().getDatastreamDissemination(getPid(), Constants.DS_COMPOSITE_MODEL_ID);
        if (is!=null)
        {
            xmlSnippet = new DOMSXMLDocumentOld();
            xmlSnippet.Load(is);
            is.close();
            dsNode = xmlSnippet.getDocNode();
            if (dsNode!=null)
            {
                DOMSDataStreamCompositeModel compositeModelDS = new DOMSDataStreamCompositeModel(this);
                compositeModelDS.parse(dsNode);
                setCompositeModelDS(compositeModelDS);
            }
        }
        if (getCompositeModelDS()==null) {
            //No DS-COMPOSITE-MODEL DataStream was found, so we create an empty DS_COMPOSITE_MODEL_ID DataStream
            //since we expect it to be there when we merge DS-COMPOSITE-MODEL DataStreams in the inheritance hierarchy.
            setCompositeModelDS(new DOMSDataStreamCompositeModel(this));

        }

        //Get the ONTOLOGY DataStream
        is = repository().getDatastreamDissemination(getPid(), Constants.ONTOLOGY);
        if (is!=null)
        {
            xmlSnippet = new DOMSXMLDocumentOld();
            xmlSnippet.Load(is);
            is.close();
            dsNode = xmlSnippet.getDocNode();
            if (dsNode!=null)
            {
                DOMSDataStreamOntology ontologyDS = new DOMSDataStreamOntology(this);
                ontologyDS.parseRdf(dsNode);
                setOntologyDataStream(ontologyDS);
            }
        }
        if (getOntologyDataStream()==null)
        {
            setOntologyDataStream(new DOMSDataStreamOntology());
        }


        //Load parent Content Model
        if (parentPids.size()>0)
        {
            String extendsModelPid = parentPids.remove(0);//Just get the first content model
            parentCM = contentModelFactory().getContentModel(extendsModelPid);
            if (!parentCM.getIsLoaded()) {
                parentCM.loadAncestors(parentPids);
            }
        }

        //This works because parents have already completed load, and thus have merged their own ancestors....

        //Merge the DS-COMPOSITE-MODEL DataStream from the parent content model with the DS-COMPOSITE-MODEL DataStream from this content model.
        if (parentCM != null){
            compositeModelDS.mergeCompositeModels(parentCM.getCompositeModelDS());
        }

        //Setup relations
        calculateRelations();


    }

    /**
     * Call this after load.
     * Finds all relations from the ONTOLOGY DataStream. Based on the relations listed in the GUI VIEW DataStream
     * relations are separated in to lists. Relations from parent ContentModels are included.
     */
    private void calculateRelations()
    {
        setViewRelations(new OWLObjectProperties());
        setOtherRelations(new OWLObjectProperties());

        if (parentCM!=null)
        {
            //We have a parent ContentModel (the DOMSContentModel that this DOMSContentModel extends.
            //Add relations from the parent.
            if (parentCM.getViewDataStream()!=null)
            {
                getViewDataStream().addRelationsDoNotOverwrite(parentCM.getViewDataStream());
            }
            if (parentCM.getOntologyDataStream()!=null)
            {
                getOntologyDataStream().addOWLObjectPropertiesDoNotOverwrite(parentCM.getOntologyDataStream());
            }
        }

        if (getOntologyDataStream()!=null)
        {
            //Split the relations into two lists.
            for (int i = 0; i < getOntologyDataStream().getOwlObjectProperties().size(); i++) {
                OWLObjectProperty objProp = getOntologyDataStream().getOwlObjectProperties().get(i);
                if (isInViewRelations(objProp))
                {
                    getViewRelations().add(objProp);
                }
                else
                {
                    getOtherRelations().add(objProp);
                }
            }
        }
    }


    /**
     *
     * @param objProp
     * @return true if objProp has the id of a relation listed in the GUI VIEW DataStream.
     */
    private boolean isInViewRelations(OWLObjectProperty objProp)
    {
        if ((getViewDataStream()!=null) && (getViewDataStream().guiView!=null))
        {
            if (getViewDataStream().guiView.relations!=null)
            {
                for (Node rNode : getViewDataStream().guiView.relations)
                {
                    String namespaceAlias = rNode.getPrefix();
                    String namespace = rNode.lookupNamespaceURI(namespaceAlias);
                    String rId = namespace + rNode.getLocalName();

                    if (objProp.getMappingId()!=null)
                    {
                        if (objProp.getMappingId().equals(rId))
                        {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


/*
    //Utility function that returns a HTML representation of this Media.
    public String toHTML() {
        StringBuffer resultHTML = new StringBuffer();

        resultHTML.append("<h1>" + getTitle() + "</h1>");

        resultHTML.append("<p>PID: " + getPid() + "</p>");

        if (getOwnerCM()!=null)
        {
            resultHTML.append("<p><a href=\"" + getOwnerCM().getPid() + "\">Owner: " + getOwnerCM().getPid() + "</a></p>");
        }

        */
/*
           * View DataStreams
           */
/*
        resultHTML.append("<h2>View DataStreams</h2>");
        resultHTML.append("<ul>");
        for (DOMSDataStream ds: viewDataStreams.values()){
            resultHTML.append("<li>");
            resultHTML.append(ds.getId());
            if (ds.getDomsXmlDocument().getRootDomsXmlElement()!=null)
            {
                resultHTML.append("<ul>");
                sdoLeafToHtml(ds.getDomsXmlDocument().getRootDomsXmlElement(), resultHTML);
                resultHTML.append("</ul>");
            }
            resultHTML.append("</li>");
        }
        resultHTML.append("</ul>");

        */
/*
           * Sub Content Models
           */
/*
        resultHTML.append("<h2>Sub Content Models</h2>");
        resultHTML.append("<ul>");
        for (int i = 0; i < getViewRelations().size(); i++) {
            OWLObjectProperty objProp = getViewRelations().get(i);
            if (objProp!=null)
            {
                if (objProp.getAllValuesFrom()!=null)
                {
                    String filename = objProp.getAllValuesFrom().substring((Constants.INFO_FEDORA_URI_SCHEME+"doms:").length(), objProp.getAllValuesFrom().length()) + ".html";
                    resultHTML.append("<li><a href=\"" + filename + "\">");
                    resultHTML.append(objProp.getAllValuesFrom());
                    resultHTML.append("</a></li>");
                }
            }
        }
        resultHTML.append("</ul>");

        */
/*
           * Other relations
           */
/*
        resultHTML.append("<h2>Other relations</h2>");
        resultHTML.append("<table border=\"1px\" cellpadding=\"4px\">");
        resultHTML.append("<tr><th>Id</th><th>Resource</th></tr>");
        for (int i = 0; i < getOtherRelations().size(); i++) {
            OWLObjectProperty objProp = getOtherRelations().get(i);
            if (objProp!=null)
            {
                String id;
                if (objProp.getMappingId()!=null)
                {
                    id = objProp.getMappingId();
                }
                else
                {
                    id = objProp.getAbout();
                }
                if (objProp.getAllValuesFrom()!=null)
                {
                    resultHTML.append("<tr>");
                    resultHTML.append("<td>" + id + "</td><td>" + objProp.getAllValuesFrom() + "</td>");
                    resultHTML.append("</tr>");
                }
            }
        }
        resultHTML.append("</table>");

        return resultHTML.toString();
    }

*/
    private void sdoLeafToHtml(DOMSXmlElement xmlElement, StringBuffer resultHTML) {
        if (!xmlElement.isLeaf()) {
            for (DOMSXmlElement child : xmlElement.getChildren()) {
                sdoLeafToHtml(child, resultHTML);
            }
        } else {
            DOMSXmlElement leaf = (DOMSXmlElement) xmlElement;
            resultHTML.append("<li>");
            resultHTML.append(leaf.getProperty().getName());
            resultHTML.append("</li>");
        }
    }


    //----------------------GETTER/SETTER--------------------------------











    /**
     * @param ownerCM the ownerCM to set
     */
    public void setOwnerCM(DOMSContentModel ownerCM) {
        this.ownerCM = ownerCM;
    }

    /**
     * @return the ownerCM
     */
    public DOMSContentModel getOwnerCM() {
        return ownerCM;
    }

    public ArrayList<DOMSContentModel> getChildrenCM() {
        return new ArrayList<DOMSContentModel>(childrenCM.values());
    }

    public void setChildrenCM(Map<String, DOMSContentModel> childrenCM) {
        this.childrenCM = childrenCM;
    }

    public DOMSContentModel getChildContentModel(String pid)
    {
        return childrenCM.get(pid);
    }

    /**
     * @param viewRelations the viewRelations to set
     */
    public void setViewRelations(OWLObjectProperties viewRelations) {
        this.viewRelations = viewRelations;
    }

    /**
     * @return the viewRelations
     */
    public OWLObjectProperties getViewRelations() {
        return viewRelations;
    }

    /**
     * @param otherRelations the otherRelations to set
     */
    public void setOtherRelations(OWLObjectProperties otherRelations) {
        this.otherRelations = otherRelations;
    }

    /**
     * @return the otherRelations
     */
    public OWLObjectProperties getOtherRelations() {
        return otherRelations;
    }

    /**
     * @param viewDataStream the viewDataStream to set
     */
    public void setViewDataStream(DOMSDataStreamView viewDataStream) {
        this.viewDataStream = viewDataStream;
    }

    /**
     * @return the viewDataStream
     */
    public DOMSDataStreamView getViewDataStream() {
        return viewDataStream;
    }

    /**
     * @param ontologyDataStream the ontologyDataStream to set
     */
    public void setOntologyDataStream(DOMSDataStreamOntology ontologyDataStream) {
        this.ontologyDataStream = ontologyDataStream;
    }

    /**
     * @return the ontologyDataStream
     */
    public DOMSDataStreamOntology getOntologyDataStream() {
        return ontologyDataStream;
    }

    /**
     * Create a new data object with the given PID
     */
    public DOMSDataObject addDataObject(DOMSDataObject dataObject) throws RemoteException, IOException
    {
        dataObjects.put(dataObject.getPid(), dataObject);
        return dataObject;
    }

    /**
     * @return the data object with the given pid
     */
    public DOMSDataObject getDataObject(String pid) {
        return dataObjects.get(pid);
    }

    /**
     *
     * @return the list of data objects
     */
    public ArrayList<DOMSDataObject> getDataObjects()
    {
        return new ArrayList<DOMSDataObject>(dataObjects.values());
    }

    /**
     * @param currentDataObject the data object to set
     */
    public void setCurrentDataObject(DOMSDataObject currentDataObject) {
        this.currentDataObject = currentDataObject;
    }

    /**
     * @return the data object
     */
    public DOMSDataObject getCurrentDataObject() {
        return currentDataObject;
    }


    public DOMSDataStreamCompositeModel getCompositeModelDS() {
        return compositeModelDS;
    }

    public void setCompositeModelDS(DOMSDataStreamCompositeModel compositeModelDS) {
        this.compositeModelDS = compositeModelDS;
    }
}
