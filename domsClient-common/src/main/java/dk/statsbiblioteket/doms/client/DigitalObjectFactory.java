package dk.statsbiblioteket.doms.client;

import dk.statsbiblioteket.doms.central.*;
import dk.statsbiblioteket.util.caching.TimeSensitiveCache;


import java.lang.String;

/**
 * Created by IntelliJ IDEA.
 * User: eab
 * Date: 9/8/11
 * Time: 1:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class DigitalObjectFactory {

    private CentralWebservice api;

    private TimeSensitiveCache<String, DigitalObject> cache;


    public DigitalObjectFactory(CentralWebservice api) {
        this.api = api;
        cache = new TimeSensitiveCache<String, DigitalObject>(100000,true);//TODO
    }

    public synchronized DigitalObject getDigitalObject(String pid) throws ServerOperationFailed {

        DigitalObject object = cache.get(pid);
        if (object == null){
            try {
                try {
                    object = retrieveObject(pid);
                } catch (InvalidResourceException e){
                    object = new MissingObject();
                }
                cache.put(pid,object);
                object.load();
            } catch (InvalidCredentialsException e) {
                throw new ServerOperationFailed("Invalid Credentials",e);
            } catch (MethodFailedException e) {
                throw new ServerOperationFailed("Failed to load object",e);
            }

        }
        return object;

    }

    private synchronized DigitalObject retrieveObject(String pid)
            throws InvalidCredentialsException, MethodFailedException, InvalidResourceException {
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
