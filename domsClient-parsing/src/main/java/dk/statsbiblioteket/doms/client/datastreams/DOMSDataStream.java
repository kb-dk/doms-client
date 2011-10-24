package dk.statsbiblioteket.doms.client.datastreams;

import dk.statsbiblioteket.doms.client.objects.DOMSContentModel;
import dk.statsbiblioteket.doms.client.objects.DOMSDigitalObject;
import dk.statsbiblioteket.doms.client.util.Constants;

/**
 * The abstract class for all datastreams
 */
public abstract class DOMSDataStream{


    /**
     * The name of the Datastream
     */
    private String id;

    /**
     * The type model for this datastream
     */
    private DOMSDataStreamCompositeModelTypeModel dsTypeModel;

    /**
     * The content model defining the existence of this datastream?
     */
    private DOMSContentModel myDomsContentModel;

    /**
     * The data object containing this datastream
     */
    private DOMSDigitalObject owningDigitalObject;



    //private SDOParsedXmlDocument domsXmlDocument;


    private Constants.DatastreamControlGroup controlGroup;


    protected DOMSDataStream(DOMSDigitalObject owningDigitalObject) {
        this.owningDigitalObject = owningDigitalObject;
    }

    public DOMSDataStream(DOMSContentModel myDomsContentModel){
        setMyDomsContentModel(myDomsContentModel);
    }

    public DOMSDataStream(DOMSContentModel myDomsContentModel, DOMSDigitalObject myDomsDataObject)
    {
        setMyDomsContentModel(myDomsContentModel);
        setOwningDigitalObject(myDomsDataObject);
    }

    public DOMSDataStream() {
    }

    //-----------------------------GETTERS/SETTERS-------------------------------------------------

    /**
     * @return id for this <code>DataStream</code> object.
     */
    public String getId() {
        if (id==null)
            id = "wasNull";
        if (id.length()==0)
            id = "initid";
        return id;
    }

    /**
     * @param id The new id.
     */
    public void setId(String id) {
        this.id = id;
    }



    /**
     * @param dsTypeModel the dsTypeModel to set
     */
    public void setDsTypeModel(DOMSDataStreamCompositeModelTypeModel dsTypeModel) {
        this.dsTypeModel = dsTypeModel;
    }

    /**
     * @return the dsTypeModel
     */
    public DOMSDataStreamCompositeModelTypeModel getDsTypeModel() {
        return dsTypeModel;
    }

    /**
     * @param myDomsContentModel the myDomsContentModel to set
     */
    public void setMyDomsContentModel(DOMSContentModel myDomsContentModel) {
        this.myDomsContentModel = myDomsContentModel;
    }


    /**
     * @return the myDomsContentModel
     */
    public DOMSContentModel getMyDomsContentModel() {
        return myDomsContentModel;
    }



    public String toString()
    {
        return id;
    }

    public DOMSDigitalObject getOwningDigitalObject() {
        return owningDigitalObject;
    }

    public void setOwningDigitalObject(DOMSDigitalObject owningDigitalObject) {
        this.owningDigitalObject = owningDigitalObject;
    }


    public Constants.DatastreamControlGroup getControlGroup() {
        return controlGroup;
    }

    public void setControlGroup(Constants.DatastreamControlGroup controlGroup) {
        this.controlGroup = controlGroup;
    }


}
