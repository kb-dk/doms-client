package dk.statsbiblioteket.doms.client.datastreams;


import dk.statsbiblioteket.doms.client.Parsable;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: Feb 7, 2009
 * Time: 3:13:30 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class DOMSDataStreamCompositeModelTypeModelExtension extends Parsable {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * The name of the extension
     */
    private String id;
}
