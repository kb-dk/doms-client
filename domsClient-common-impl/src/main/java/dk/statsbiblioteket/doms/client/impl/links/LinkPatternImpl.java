package dk.statsbiblioteket.doms.client.impl.links;

import dk.statsbiblioteket.doms.client.links.LinkPattern;

/**
 * Created with IntelliJ IDEA.
 * User: abr
 * Date: 3/13/13
 * Time: 2:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class LinkPatternImpl implements LinkPattern {


    private String name;
    private String description;

    private String value;

    public LinkPatternImpl(String name, String description, String value) {
        this.name = name;
        this.description = description;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
