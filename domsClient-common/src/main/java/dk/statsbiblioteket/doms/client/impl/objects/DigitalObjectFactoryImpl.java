package dk.statsbiblioteket.doms.client.impl.objects;

import dk.statsbiblioteket.doms.central.*;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.objects.DigitalObject;
import dk.statsbiblioteket.doms.client.objects.DigitalObjectFactory;
import dk.statsbiblioteket.doms.client.objects.MissingObject;
import dk.statsbiblioteket.util.caching.TimeSensitiveCache;


import java.lang.String;

/**
 * This class retrieves and loads objects from the server. Use this class as the basis for requesting information from the server
 */
public class DigitalObjectFactoryImpl extends DigitalObjectFactory {

    private TimeSensitiveCache<String, DigitalObject> cache;

    private final DigitalObject MISSING = new MissingObject();


    /**
     * Constructor. Feed this a api, to talk to the Doms system.
     * @param api the api for DOMs.
     */
    public DigitalObjectFactoryImpl(CentralWebservice api) {
        super(api);
        cache = new TimeSensitiveCache<String, DigitalObject>(100000,true);//TODO
    }

    /**
     * Retrieve an object from the server. If the object cannot be found, an instance of MissingObject is returned instead.
     * @param pid the pid of the object to retrieve
     * @return an instance of the DigitalObject interface
     * @throws ServerOperationFailed in something failed in retrieving the object
     * @see DataObjectImpl
     * @see ContentModelObjectImpl
     * @see dk.statsbiblioteket.doms.client.objects.MissingObject
     * @see TemplateObjectImpl
     */
    @Override
    public synchronized DigitalObject getDigitalObject(String pid) throws ServerOperationFailed {

        if (pid.startsWith("info:fedora/")){
            pid = pid.substring("info:fedora/".length());
        }
        DigitalObject object = cache.get(pid);
        if (object == null){
            try {
                try {
                    AbstractDigitalObject newobj = retrieveObject(pid);
                    cache.put(pid,newobj);
                    newobj.loadContentModels();
                    object = newobj;
                } catch (InvalidResourceException e){
                    object = MISSING;
                    cache.put(pid,object);
                }
            } catch (InvalidCredentialsException e) {
                throw new ServerOperationFailed("Invalid Credentials",e);
            } catch (MethodFailedException e) {
                throw new ServerOperationFailed("Failed to load object",e);
            }

        }
        return object;
    }

    private synchronized AbstractDigitalObject retrieveObject(String pid)
            throws InvalidCredentialsException, MethodFailedException, InvalidResourceException, ServerOperationFailed {
        ObjectProfile profile = getApi().getObjectProfile(pid);
        AbstractDigitalObject object;
        if ("ContentModel".equals(profile.getType())){
            object = new ContentModelObjectImpl(profile, getApi(), this);
        } else if ("TemplateObject".equals(profile.getType())){
            object = new TemplateObjectImpl(profile, getApi(), this);
        } else {
            object = new DataObjectImpl(profile,getApi(),this);
        }
        return object;
    }


}
