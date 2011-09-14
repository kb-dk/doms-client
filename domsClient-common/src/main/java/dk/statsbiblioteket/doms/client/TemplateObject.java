package dk.statsbiblioteket.doms.client;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.ObjectProfile;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 9/14/11
 * Time: 12:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class TemplateObject extends AbstractDigitalObject {

    public TemplateObject(ObjectProfile profile, CentralWebservice api, DigitalObjectFactory factory) {
        super(profile, api, factory);
    }
}
