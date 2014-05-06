package dk.statsbiblioteket.doms.client.impl.objects;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.InvalidCredentialsException;
import dk.statsbiblioteket.doms.central.InvalidResourceException;
import dk.statsbiblioteket.doms.central.MethodFailedException;
import dk.statsbiblioteket.doms.central.ObjectProfile;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.objects.DigitalObject;
import dk.statsbiblioteket.doms.client.objects.MissingObject;
import dk.statsbiblioteket.doms.client.utils.Constants;
import dk.statsbiblioteket.util.caching.TimeSensitiveCache;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

/**
 * This class retrieves and loads objects from the server. Use this class as the basis for requesting information from
 * the server
 */
public class DigitalObjectFactoryImpl extends AbstractDigitalObjectFactory {

    private final DigitalObject MISSING = new MissingObject();
    private Map<String, SoftReference<DigitalObject>> cache;
    private TimeSensitiveCache<String, DigitalObject> timeSensitiveCache;


    /**
     * Constructor. Feed this a api, to talk to the Doms system.
     *
     * @param api the api for DOMs.
     */
    public DigitalObjectFactoryImpl(CentralWebservice api) {
        super(api);
        cache = new HashMap<String, SoftReference<DigitalObject>>();
        timeSensitiveCache = new TimeSensitiveCache<String, DigitalObject>(10000, false);
    }

    /**
     * Retrieve an object from the server. If the object cannot be found, an instance of MissingObject is returned
     * instead.
     *
     * @param pid the pid of the object to retrieve
     *
     * @return an instance of the DigitalObject interface
     * @throws ServerOperationFailed in something failed in retrieving the object
     * @see DataObjectImpl
     * @see ContentModelObjectImpl
     * @see dk.statsbiblioteket.doms.client.objects.MissingObject
     * @see TemplateObjectImpl
     */
    @Override
    public synchronized DigitalObject getDigitalObject(String pid) throws ServerOperationFailed {

        pid = Constants.ensurePID(pid);

        DigitalObject object = timeSensitiveCache.get(pid);

        if (object == null) {
            try {
                try {
                    //System.out.println("Object "+pid+" not in cache, loading");
                    AbstractDigitalObject newobj = retrieveObject(pid);
                    timeSensitiveCache.put(pid, newobj);
                    newobj.loadContentModels();
                    object = newobj;
                } catch (InvalidResourceException e) {
                    object = MISSING;
                    timeSensitiveCache.put(pid, object);
                }
            } catch (InvalidCredentialsException e) {
                throw new ServerOperationFailed("Invalid Credentials", e);
            } catch (MethodFailedException e) {
                throw new ServerOperationFailed("Failed to load object", e);
            }

        }
        return object;
    }

    private synchronized AbstractDigitalObject retrieveObject(String pid) throws
                                                                          InvalidCredentialsException,
                                                                          MethodFailedException,
                                                                          InvalidResourceException,
                                                                          ServerOperationFailed {
        ObjectProfile profile = getApi().getObjectProfile(pid);
        AbstractDigitalObject object;
        if ("ContentModel".equals(profile.getType())) {
            object = new ContentModelObjectImpl(profile, getApi(), this);
        } else if ("TemplateObject".equals(profile.getType())) {
            object = new TemplateObjectImpl(profile, getApi(), this);
        } else if ("CollectionObject".equals(profile.getType())) {
            object = new CollectionObjectImpl(profile, getApi(), this);
        } else if ("FileObject".equals(profile.getType())) {
            object = new FileObjectImpl(profile, getApi(), this);

        } else {
            object = new DataObjectImpl(profile, getApi(), this);
        }
        return object;
    }


}
