package dk.statsbiblioteket.doms.client.datastreams;

import dk.statsbiblioteket.doms.client.datastreams.Datastream;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 9/15/11
 * Time: 3:26 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ExternalDatastream extends Datastream {
    String getUrl();

}
