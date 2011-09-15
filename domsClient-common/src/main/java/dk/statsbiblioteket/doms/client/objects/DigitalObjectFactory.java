package dk.statsbiblioteket.doms.client.objects;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.InvalidCredentialsException;
import dk.statsbiblioteket.doms.central.InvalidResourceException;
import dk.statsbiblioteket.doms.central.MethodFailedException;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.impl.objects.AbstractDigitalObject;
import dk.statsbiblioteket.doms.client.impl.objects.DigitalObjectFactoryImpl;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 9/15/11
 * Time: 3:18 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class DigitalObjectFactory {

    private static DigitalObjectFactory instance;

    public abstract DigitalObject getDigitalObject(String pid) throws ServerOperationFailed;

    public static synchronized DigitalObjectFactory getInstance(CentralWebservice api){
        if (instance == null){
            instance = new DigitalObjectFactoryImpl(api);
        }
        return instance;
    }

}
