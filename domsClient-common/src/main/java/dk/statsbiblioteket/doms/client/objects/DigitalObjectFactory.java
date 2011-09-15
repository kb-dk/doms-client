package dk.statsbiblioteket.doms.client.objects;

import dk.statsbiblioteket.doms.central.*;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.util.caching.TimeSensitiveCache;


import java.lang.String;

/**
 * This class retrieves and loads objects from the server. Use this class as the basis for requesting information from the server
 */
public class DigitalObjectFactory {

    private CentralWebservice api;

    private TimeSensitiveCache<String, DigitalObject> cache;


    /**
     * Constructor. Feed this a api, to talk to the Doms system.
     * @param api the api for DOMs.
     */
    public DigitalObjectFactory(CentralWebservice api) {
        this.api = api;
        cache = new TimeSensitiveCache<String, DigitalObject>(100000,true);//TODO
    }

    /**
     * Retrieve an object from the server.
     * @param pid the pid of the object to retrieve
     * @return an instance of the DigitalObject interface
     * @throws ServerOperationFailed in something failed in retrieving the object
     * @see DataObject
     * @see ContentModelObject
     * @see MissingObject
     */
    public synchronized DigitalObject getDigitalObject(String pid) throws ServerOperationFailed {

        if (pid.startsWith("info:fedora/")){
            pid = pid.substring("info:fedora/".length());
        }
        DigitalObject object = cache.get(pid);
        if (object == null){
            try {
                try {
                    object = retrieveObject(pid);
                } catch (InvalidResourceException e){
                    object = new MissingObject();
                }
                cache.put(pid,object);
            } catch (InvalidCredentialsException e) {
                throw new ServerOperationFailed("Invalid Credentials",e);
            } catch (MethodFailedException e) {
                throw new ServerOperationFailed("Failed to load object",e);
            }

        }
        return object;
    }



    private synchronized DigitalObject retrieveObject(String pid)
            throws InvalidCredentialsException, MethodFailedException, InvalidResourceException, ServerOperationFailed {
        ObjectProfile profile = api.getObjectProfile(pid);
        DigitalObject object;
        if ("ContentModel".equals(profile.getType())){
            object = new ContentModelObject(profile, api, this);
        } else if ("TemplateObject".equals(profile.getType())){
            object = new TemplateObject(profile, api, this);
        } else {
            object = new DataObject(profile,api,this);
        }
        return object;
    }


}
