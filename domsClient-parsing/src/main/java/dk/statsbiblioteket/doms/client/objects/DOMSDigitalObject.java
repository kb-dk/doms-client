package dk.statsbiblioteket.doms.client.objects;


import dk.statsbiblioteket.doms.client.ObjectProperties;
import dk.statsbiblioteket.doms.client.datastreams.DOMSDataStreamRelsExt;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;

import java.io.IOException;
import java.rmi.RemoteException;


/** 
 * <p>Baseclass for DOMS DigitalObject objects. There are two kinds of objects in
 * doms, data objects and content models. This is the common class.
 * <p>
 *
 * @see DOMSDataObject
 * @see DOMSContentModel
 */

public abstract class DOMSDigitalObject {

    /**
     * The connector to the repository. Should be accessed through the
     * {@link #repository()} method.
     */
	private RepositoryBean repository;


    private DOMSDataStreamRelsExt relsext;

    /**
     * Get the repository bean, for talking to the repository.
     * @return
     */
	public RepositoryBean repository()
    {
		if (repository == null)
    		repository = (RepositoryBean)Component.getInstance(RepositoryBean.class, ScopeType.SESSION);//"repository");
     	return repository;
    }
	
	private DOMSContentModelFactory contentModelFactory;

    public DOMSContentModelFactory contentModelFactory()
	{
		if (contentModelFactory==null)
			contentModelFactory = (DOMSContentModelFactory)Component.getInstance("contentModelFactory");
		return contentModelFactory;
	}

    /**
     * Boolean denoting if this object has been fully loaded from the repository
     */
	protected Boolean isLoaded;

    /**
     * The pid of this object
     */
    private String pid;

    /**
     * The title of this object
     */
	private String title = "";

    /**
     * The properties for this object
     */
	private ObjectProperties objectProperties;


    /**
     * Constructor. Creates a connection to the repository, and initalises the
     * title. Does not load the object. The object must exist in the repository
     * @see #repository()
     * @param pid The pid of the object.
     * @throws RemoteException
     * @throws IOException
     */
    public DOMSDigitalObject(String pid) throws RemoteException, IOException, ServerOperationFailed {
		isLoaded = false;
		this.pid = pid;
		repository = (RepositoryBean) Component.getInstance("repository");
		this.title = repository().getDigitalObjectDCTitle(pid);
	}
	
	public Boolean getIsLoaded()
	{
		return isLoaded;
	}

	/**
	 * @return the pid.
	 */
	public String getPid() {
 		return pid;
	}

    /**
     * The title of the object, from the DC datastream. Does not update the
     * object in the repository
     * @param title the new title
     */
	public void setTitle(String title)
	{
		this.title = title;
	}
	
	/**
	 * The title of the object, from the DC datastream.
	 * @return the title
	 */
	public String getTitle()
	{
		return title;
	}

	/**
	 * @param objectProperties the objectProperties to set
	 */
	public void setObjectProperties(ObjectProperties objectProperties) {
		this.objectProperties = objectProperties;
	}

	/**
	 * @return the objectProperties
	 */
	public ObjectProperties getObjectProperties() {
		return objectProperties;
	}

    public DOMSDataStreamRelsExt getRelsext() {
        return relsext;
    }

    public void setRelsext(DOMSDataStreamRelsExt relsext) {
        this.relsext = relsext;
    }

    /**
     * Two DOMSDigitalObjects are equal, if their pid is equal. False otherwise
     * @see #getPid()
     * @param obj the object to compare
     * @return true for equals
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DOMSDigitalObject){
            return this.getPid().equals(((DOMSDigitalObject)obj).getPid());
        }else{
            return false;
        }

    }

    public void clear(){
        relsext = null;
    }

}
