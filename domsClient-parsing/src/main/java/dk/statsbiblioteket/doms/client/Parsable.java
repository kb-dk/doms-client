package dk.statsbiblioteket.doms.client;


import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import org.w3c.dom.Node;

/**
 * Interface for all classes that represent structures parsed from the foxml.
 * A Parsable java object is not populated with the values from the
 * repository until it has been parsed.
 */
public abstract class Parsable {


    private boolean parsed;
    /**
     * Parse the structure, where the root element is node.
     * @param node The root element of the structure.
     */
    public abstract void parse(Node node) throws ServerOperationFailed;

    /**
     * Tells whether or not the object have already been parsed from the foxml. Do not use accessor methods
     * on this object until isParsed() returns true.
     * @return true if the object has been parsed
     */
    public boolean isParsed(){
        return parsed;
    }

    public void setParsed(boolean parsed) {
        this.parsed = parsed;
    }
}
