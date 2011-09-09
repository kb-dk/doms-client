package dk.statsbiblioteket.doms.client;

import java.net.URI;

/**
 * Created by IntelliJ IDEA.
 * User: eab
 * Date: 9/8/11
 * Time: 1:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class DigitalObjectFactory {

    DigitalObject getDigitalObject(String pid){

        return new DataObject(pid);
    }
}
