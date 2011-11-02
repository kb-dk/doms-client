package dk.statsbiblioteket.doms.client.impl.objects;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.client.objects.DigitalObjectFactory;


/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 9/15/11
 * Time: 3:18 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractDigitalObjectFactory implements DigitalObjectFactory {

    private CentralWebservice api;

    protected AbstractDigitalObjectFactory(CentralWebservice api) {
        this.api = api;
    }

    public CentralWebservice getApi() {
        return api;
    }
}
