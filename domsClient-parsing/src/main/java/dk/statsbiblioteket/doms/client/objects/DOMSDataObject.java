package dk.statsbiblioteket.doms.client.objects;

import dk.statsbiblioteket.doms.client.datastreams.*;
import dk.statsbiblioteket.doms.client.objects.FedoraState;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.owl.OWLObjectProperty;
import dk.statsbiblioteket.doms.client.owl.Relation;
import dk.statsbiblioteket.doms.client.sdo.DOMSXMLDocumentOld;
import dk.statsbiblioteket.doms.client.util.Constants;
import org.w3c.dom.Node;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.*;

/**
 * <p>This class models an instance of a given Doms Data object. The object is
 * not populated with information until one of the load methods have been
 * called.
 * <p>
 * Each data model has:
 * <ul>
 * <li> a title
 * <li> an unique pid
 * <li> a state
 * <li> a content model
 * <li> a list of children
 * <li> a list of child relations
 * <li> a list of other relations
 * <li> a set of datastreams
 * <li> a list of importmethods
 * <li> a list of export methods
 * </ul>
 *
 *<p>About children and child relations. A dataobject has a number of allowed
 * relations. Some of these are marked as child relations (view relations). Any
 * object connected to this, by one such relation is a child of this object.
 * Any relation not marked as a child relation is a otherrelation.
 *
 * <p>About datatreams. Each data object has a number of special datastreams.
 * <ul>
 * <li> The RELS-EXT datastream, denoting the relations from this object
 * <li> The POLICY datastream, denoting the license restricitons on this object
 * <li> May have a uploadable datastream. An uploadable datastream is a data
 * stream which content is an url that can be set upon upload of a file to
 * the bitstorage. TODO
 * </ul>
 *
 * @see #load
 *
 */
public class DOMSDataObject extends DOMSDigitalObject {

    /** The state of the data object */
    private FedoraState fedoraState;

    /**The schema for this DOMSDataObject.*/
    private DOMSContentModel contentModel;

    /**The DOMSDataObject that has this DOMSDataObject as a sub DOMSDataObject.*/
    private DOMSDataObject ownerDOMSDataObject;

    /**
     * children is a list of the DOMSDataObjects that are related to this DOMSDataObject via the
     * relations listed in the GUI VIEW DataStream.
     */
    private ArrayList<DOMSDataObject> children = new ArrayList<DOMSDataObject>();

    /**
     * The relations that are listed in the GUI VIEW datastream.
     */
    private ArrayList<Relation> childRelations = new ArrayList<Relation>();

    /**
     * The relations that are not listed in the GUI VIEW datastream
     */
    private ArrayList<Relation> otherRelations = new ArrayList<Relation>();


    private DOMSDataStreamPolicy policy;


    /**
     * The export methods defined for this object
     */
    private ObjectMethodsDef[] exportMethods = new ObjectMethodsDef[0];

    /**
     * The import methods defined for this object
     */
    private ObjectMethodsDef[] importMethods = new ObjectMethodsDef[0];


    private Map<String, DOMSDataStreamEditable> editableDatastreams = new TreeMap<String, DOMSDataStreamEditable>();
    private Map<String, DOMSDataStreamUploadable> uploadableDatastreams = new TreeMap<String, DOMSDataStreamUploadable>();
    private Map<String, DOMSDataStreamInvisible> invisibleDatastreams = new TreeMap<String, DOMSDataStreamInvisible>();
    private Map<String, DOMSDataStreamImportable> importableDatastreams = new TreeMap<String, DOMSDataStreamImportable>();
    private Map<String, DOMSDataStreamReadOnly> readonlyDatastreams = new TreeMap<String, DOMSDataStreamReadOnly>();

    // --------------------- CONSTRUCTOR ----------------------------
    /**
     * Create a new Dataobject for the given pid. The object is not loaded from
     * the repository before being called
     * @param pid
     * @throws RemoteException
     * @throws IOException
     * @throws ServiceException
     */
    public DOMSDataObject(String pid) throws RemoteException, IOException, ServiceException, ServerOperationFailed {
        super(pid);
        ownerDOMSDataObject = null;
        contentModel = null;

        fedoraState = repository().getDataObjectState(getPid());

        // TODO: Currently not in use, remove comment and implement client code when needed
        // exportMethods = repository().getExportMethods(pid);
        // importMethods = repository().getImportMethods(pid);
    }


    // ----------------------- ADVANCED METHODS ------------------------
    /**
     * Gets the datastream with the uploadable guirepresentation
     * @return the uploadable datastream. null if none exists
     */
    public DOMSDataStreamUploadable getUploadableDataStream(){

        ArrayList<DOMSDataStreamUploadable> a = new ArrayList<DOMSDataStreamUploadable>(getUploadableDatastreams());
        if (a.size()==0){
            return null;
        } else{
            return a.get(0);
        }
    }

    /**
     * As the name says, gets the POLICY datastream
     * @return the XACML policy datastream java object
     */
    public DOMSDataStream getPolicyDataStream(){
        return policy;
    }

    /**
     * Get the title of the content model, ie the type of this object
     * @return The title
     */
    public String getTypeTitle()
    {
        return getContentModel().getTitle();
    }

    /**
     * @param fedoraState the fedoraState to set
     */
    public void setFedoraState(FedoraState fedoraState) {
        this.fedoraState = fedoraState;
    }

    /**
     * @return the fedoraState
     */
    public FedoraState getFedoraState() {
        return fedoraState;
    }

    public FedoraState getFedoraStateTrue(){
        return fedoraState;
    }


    /**
     * Get the value of the first isPartOfCollection relation
     * @return the pid of the collection
     */
    public String getPrimaryCollectionPid() {
        for (Relation relation: otherRelations) {
            if (relation.getObjProp().getMappingId().equals(Constants.IS_PART_OF_COLLECTION_RELATION)) {
                return relation.getPid();
            }
        }
        return null;
    }

    /**
     * Get the State of this object as a nice string.
     * If state is active, get "Active"
     * If state is inactive, get "Inactive"
     * If state is deleted, get ""
     * @return the state string
     */
    public String getStateDisplayStr() {
        String state;
        if (fedoraState.equals(Constants.FedoraState.A)) {
            state = "Active";
        }
        else if (fedoraState.equals(Constants.FedoraState.I)) {
            state = "Inactive";
        }
        else {
            state = "";
        }

        return state;
    }

    /**
     * Refreshes the title of the dataobject
     * @throws IOException
     */
    public void refresh() throws IOException, ServerOperationFailed {
        setTitle(repository().getDigitalObjectDCTitle(getPid()));
    }

    /**
     * If this object has a view relation the newPid, add the relation as a
     * child relation, load the newpid object and add it as a child of this
     * object
     * @param newPid
     * @throws IOException
     * @throws MyXMLWriteException
     * @throws ServiceException
     * @throws DOMSIllegalStateException
     */
    public void refreshRelations(String newPid) throws IOException, MyXMLWriteException, ServiceException, DOMSIllegalStateException, MyXMLParseException, ServerOperationFailed {
        InputStream is = repository().getDatastreamDissemination(getPid(), Constants.RELS_EXT_ID);
        if (is!=null)
        {
            DOMSXMLDocumentOld xmlSnippet = new DOMSXMLDocumentOld();
            xmlSnippet.Load(is);
            is.close();
            Node dsNode = xmlSnippet.getDocNode();
            setRelsext(new DOMSDataStreamRelsExt(this));
            getRelsext().parseRdf(dsNode);

        }
        //Get the relationships
        for (DOMSDataStreamRelsExtRelation relsExtelation : getRelsext().getRelsExtRelations())
        {
            OWLObjectProperty objProp;
            if ((objProp=isInViewRelations(relsExtelation))!=null)
            {
                String pid1 = Constants.ensurePID(relsExtelation.getResource());
                if (pid1.equals(newPid)) {
                    Relation relation = new Relation(objProp, pid1);
                    childRelations.add(relation);

                    DOMSDataObject myDo = new DOMSDataObject(relation.getPid());
                    myDo.load(false, true);
                    getChildren().add(myDo);

                    break;
                }
            }
        }
    }

    /**
     * Clears and reloads childRelations and otherRelations.
     * @throws IOException
     * @throws MyXMLWriteException
     */
    public void refreshRelations() throws IOException, MyXMLWriteException, MyXMLParseException, ServerOperationFailed {

        childRelations = new ArrayList<Relation>();
        otherRelations = new ArrayList<Relation>();
        InputStream is = repository().getDatastreamDissemination(getPid(), Constants.RELS_EXT_ID);

        if (is!=null)
        {
            DOMSXMLDocumentOld xmlSnippet = new DOMSXMLDocumentOld();
            xmlSnippet.Load(is);
            is.close();
            Node dsNode = xmlSnippet.getDocNode();
            setRelsext(new DOMSDataStreamRelsExt(this));
            getRelsext().parseRdf(dsNode);

        }
        //Get the relationships
        for (DOMSDataStreamRelsExtRelation relsExtelation : getRelsext().getRelsExtRelations())
        {
            OWLObjectProperty objProp;
            if ((objProp=isInViewRelations(relsExtelation))!=null)
            {
                Relation relation = new Relation(objProp, Constants.ensurePID(relsExtelation.getResource()));
                childRelations.add(relation);
            }
            else if ((objProp=isInOtherRelations(relsExtelation))!=null)
            {
                Relation relation = new Relation(objProp, Constants.ensurePID(relsExtelation.getResource()));
                otherRelations.add(relation);
            }
        }
    }

    /**
     * Loads the dataobject from the repository, and loads the children data
     * objects recursively
     * @see #load(boolean, boolean)
     * @throws IOException
     * @throws MyXMLWriteException
     * @throws ServiceException
     * @throws DOMSIllegalStateException
     */
    public void loadAll() throws IOException, MyXMLWriteException, ServiceException, DOMSIllegalStateException, MyXMLParseException, ServerOperationFailed {
        this.load(false, true);
        loadChildren(this);

    }
    private void loadChildren(DOMSDataObject parentDataobject) throws IOException, MyXMLWriteException, ServiceException, DOMSIllegalStateException, MyXMLParseException, ServerOperationFailed {
        for (Relation relation : parentDataobject.getChildRelationships())
        {
            DOMSDataObject myDo = new DOMSDataObject(relation.getPid()); //cm.createDataObject(relation.getPid());
            myDo.load(false, true);
            parentDataobject.getChildren().add(myDo);

            loadChildren(myDo);
        }
    }

    /**
     * Calls load(false,true).
     * @see #load(boolean, boolean)
     * @throws IOException
     * @throws MyXMLWriteException
     * @throws DOMSIllegalStateException
     */
    public void load() throws IOException, MyXMLWriteException, DOMSIllegalStateException, MyXMLParseException, ServerOperationFailed {
        this.load(false, true);
    }

    /**
     * Loads the DataObject from the repository. This populates this objects
     * with the correct information on the data object.
     * @param forceReload force the DataObject
     * @throws IOException
     * @throws MyXMLWriteException
     * @throws DOMSIllegalStateException
     */
    public void load(boolean forceReload, boolean clearChildren) throws IOException, MyXMLWriteException, DOMSIllegalStateException, MyXMLParseException, ServerOperationFailed {

        //Check if we should load at all
        if (isLoaded && !forceReload)
        {
            return;
        }
        //If we are loaded already, we need to clear the lists
        if(isLoaded) {
            clearDataObject(clearChildren);
        }

        InputStream is = repository().getDatastreamDissemination(getPid(), Constants.RELS_EXT_ID);
        if (is!=null)
        {
            DOMSXMLDocumentOld xmlSnippet = new DOMSXMLDocumentOld();
            xmlSnippet.Load(is);
            is.close();
            Node dsNode = xmlSnippet.getDocNode();
            setRelsext(new DOMSDataStreamRelsExt(this));
            getRelsext().parseRdf(dsNode);

        }

        if ((getContentModel()==null) || (!getContentModel().getIsLoaded()))
        {
            List<String> cmPids = getContentModelPids();

            String myCMPid = cmPids.remove(0);
            if ((getContentModel()==null))
            {
                setContentModel(contentModelFactory().getContentModel(myCMPid));
                getContentModel().addDataObject(this);
            }
            if (!getContentModel().getIsLoaded())
            {
                getContentModel().load(cmPids);
            }
        }
        if(getContentModel() == null) {
            throw new DOMSIllegalStateException("The dataobject with pid: '" + getPid() +
                    "' and title: '" + getTitle()+ "' has a contentmodel that is null");
        }
//		if(getContentModel().getViewDataStreams() == null) {
//			throw new DOMSIllegalStateException("The dataobject with pid: '" + getPid() +
//					"' and title: '" + getTitle()+ "' has a contentmodel with pid: '"+getContentModel().getPid()+
//					"' and title: '" + getContentModel().getTitle() + "' with no view data streams. (null)");
//		}


        for (DOMSDataStreamCompositeModelTypeModel dst : getContentModel().getCompositeModelDS().values()){
            DOMSDataStreamCompositeModelTypeModelExtensionGui gui =
                    (DOMSDataStreamCompositeModelTypeModelExtensionGui)
                            dst.getExtensionByID(Constants.EXTENSIONS_GUI);
            if (gui == null){
                continue;
            }
            DOMSDataStream newDs = null;


            switch (gui.getPresentAs()){
                case editable:{
                    DOMSDataStreamCompositeModelTypeModelExtensionSchema schema =
                            (DOMSDataStreamCompositeModelTypeModelExtensionSchema)
                                    dst.getExtensionByID(Constants.EXTENSIONS_SCHEMA);
                    if (schema == null){
                        //TODO the content model declared the datastream as editable but did not specify a schema
                        continue;
                    }
                    if (schema.getDomsXmlDocument()!=null){
                        if (schema.getDomsXmlDocument().isValid()){
                            //parse the content of all datastreams with a valid schema
                            DOMSDataStreamEditable newDs1 = new DOMSDataStreamEditable(getPid(), dst);
                            editableDatastreams.put(dst.getId(),newDs1);
                            newDs = newDs1;
                        } else {
                            //TODO LOG THIS, invalid schema
                            continue;
                        }
                    }  else {
                        //TODO LOG THIS, no schema datastream
                        continue;
                    }
                    break;
                }
                case invisible:{
                    DOMSDataStreamInvisible newDs2 = new DOMSDataStreamInvisible(getPid(),dst.getId());
                    invisibleDatastreams.put(dst.getId(),newDs2);
                    newDs = newDs2;
                    break;
                }
                case importable:{
                    DOMSDataStreamImportable newDs3 = new DOMSDataStreamImportable(getPid(),dst.getId());
                    importableDatastreams.put(dst.getId(),newDs3);
                    newDs = newDs3;
                    break;
                }
                case readonly:{
                    DOMSDataStreamReadOnly newDs4 = new DOMSDataStreamReadOnly(getPid(),dst.getId() );
                    readonlyDatastreams.put(dst.getId(),newDs4);
                    newDs = newDs4;
                    break;

                }
                case uploadable:{
                    DOMSDataStreamUploadable newDs5 = new DOMSDataStreamUploadable(getPid(),dst.getId());

                    uploadableDatastreams.put(dst.getId(),newDs5);
                    newDs = newDs5;
                    break;
                }
            }
            newDs.setId(dst.getId());
            newDs.setOwningDigitalObject(this);
            newDs.setMyDomsContentModel(getContentModel());
            newDs.setDsTypeModel(dst);

            if (newDs == null){
                continue;
            }

        }

        //Get the relationships
        for (DOMSDataStreamRelsExtRelation relsExtRelation : getRelsext().getRelsExtRelations()) {
            OWLObjectProperty objProp;
            if ((objProp=isInViewRelations(relsExtRelation))!=null) {
                Relation relation = new Relation(objProp, Constants.ensurePID(relsExtRelation.getResource()));
                childRelations.add(relation);
            }
            else if ((objProp=isInOtherRelations(relsExtRelation))!=null) {
                Relation relation = new Relation(objProp, Constants.ensurePID(relsExtRelation.getResource()));
                otherRelations.add(relation);
            }
        }

        isLoaded = true;

    }






    public String toString() {
        return getTitle();
    }


//------------------- PRIVATE HELPERS ----------------------------------

    private void clearDataObject(boolean clearChildren) {
        if (clearChildren)
        {
            children = new ArrayList<DOMSDataObject>();
        }
        childRelations = new ArrayList<Relation>();
        otherRelations = new ArrayList<Relation>();
        editableDatastreams.clear();
        invisibleDatastreams.clear();
        readonlyDatastreams.clear();
        importableDatastreams.clear();
        uploadableDatastreams.clear();
        super.clear();
    }


    private OWLObjectProperty isInViewRelations(DOMSDataStreamRelsExtRelation relation)
    {
        for (OWLObjectProperty objProp : getContentModel().getViewRelations())
        {
            if (objProp.getMappingId().equals(relation.getNamespaceURI()+relation.getLocalname()))
            {
                return objProp;
            }
        }
        return null;
    }

    private OWLObjectProperty isInOtherRelations(DOMSDataStreamRelsExtRelation relation)
    {
        for (OWLObjectProperty objProp : getContentModel().getOtherRelations())
        {
            if (objProp.getMappingId().equals(relation.getNamespaceURI()+relation.getLocalname()))
            {
                return objProp;
            }
        }
        return null;
    }

    /**
     * Get the list of content models for this data object
     * @return a list of pids
     * @throws RemoteException
     */
    private List<String> getContentModelPids() throws RemoteException
    {
        ArrayList<String> pids = new ArrayList<String>();

        for (DOMSDataStreamRelsExtRelation relsExtelation : getRelsext().getRelsExtRelations())
        {
            String relationship = relsExtelation.getNamespaceURI() + relsExtelation.getLocalname();

            if (relationship.equals(Constants.FEDORA_MODEL_NAMESPACE + "hasModel"))
            {
                pids.add(Constants.ensurePID(relsExtelation.getResource()));
            }
        }

        return pids;
    }



// ----------------- GETTERS AND SETTERS----------------------------------

    /**
     * Removes a DOMSDataobject from the children list given its pid
     * @param pid the pid of the child
     */
    public void removeChild(String pid) {
        DOMSDataObject remove = null;
        for(DOMSDataObject d : children) {
            if(d.getPid().equals(pid)) {
                remove = d;
                break;
            }
        }
        if(remove != null) {
            children.remove(remove);
        }
    }

    /**
     * Gets the child data object with the given pid
     * @param pid
     * @return Returns null, if the pid is not a child object
     */
    public DOMSDataObject getChild(String pid) {
        for(DOMSDataObject d : children) {
            if(d.getPid().equals(pid)) {
                return d;
            }
        }
        return null;
    }

    /**
     * @param contentModel the contentModel to set
     */
    public void setContentModel(DOMSContentModel contentModel) {
        this.contentModel = contentModel;
    }

    /**
     * @return the contentModel
     */
    public DOMSContentModel getContentModel() {
        return contentModel;
    }

    /**
     * @param ownerDOMSDataObject the ownerDOMSDataObject to set
     */
    public void setDOMSDataObject(DOMSDataObject ownerDOMSDataObject) {
        this.ownerDOMSDataObject = ownerDOMSDataObject;
    }

    /**
     * Returns the DOMSDataObject that has this DOMSDataObject as a sub DOMSDataObject.
     * @return the ownerDOMSDataObject
     */
    public DOMSDataObject getDOMSDataObject() {
        return ownerDOMSDataObject;
    }

    public void setChildren(ArrayList<DOMSDataObject> children) {
        this.children = children;
    }

    public ArrayList<DOMSDataObject> getChildren() {
        return children;
    }
    /**
     * @return the childPids
     */
    public ArrayList<Relation> getChildRelationships() {
        return childRelations;
    }

    /**
     * @return the otherPids
     */
    public ArrayList<Relation> getOtherPids() {
        return otherRelations;
    }

    public DOMSDataObject getOwnerDOMSDataObject() {
        return ownerDOMSDataObject;
    }



    public ArrayList<Relation> getChildRelations() {
        return childRelations;
    }

    public void setChildRelations(ArrayList<Relation> childRelations) {
        this.childRelations = childRelations;
    }

    public ArrayList<Relation> getOtherRelations() {
        return otherRelations;
    }

    public void setOtherRelations(ArrayList<Relation> otherRelations) {
        this.otherRelations = otherRelations;
    }


    public ObjectMethodsDef[] getExportMethods() {
        return exportMethods;
    }

    public void setExportMethods(ObjectMethodsDef[] exportMethods) {
        this.exportMethods = exportMethods;
    }

    public ObjectMethodsDef[] getImportMethods() {
        return importMethods;
    }

    public void setImportMethods(ObjectMethodsDef[] importMethods) {
        this.importMethods = importMethods;
    }


    public DOMSDataStreamPolicy getPolicy() {
        return policy;
    }

    public Collection<DOMSDataStreamEditable> getEditableDatastreams() {
        return editableDatastreams.values();
    }

    public Collection<DOMSDataStreamUploadable> getUploadableDatastreams() {
        return uploadableDatastreams.values();
    }

    public Collection<DOMSDataStreamInvisible> getInvisibleDatastreams() {
        return invisibleDatastreams.values();
    }

    public Collection<DOMSDataStreamImportable> getImportableDatastreams() {
        return importableDatastreams.values();
    }

    public Collection<DOMSDataStreamReadOnly> getReadonlyDatastreams() {
        return readonlyDatastreams.values();
    }
}
