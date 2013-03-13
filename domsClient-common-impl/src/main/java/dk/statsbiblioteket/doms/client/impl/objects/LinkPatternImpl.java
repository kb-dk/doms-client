package dk.statsbiblioteket.doms.client.impl.objects;

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
    private String altText;

    private String value;

    public LinkPatternImpl(String name, String altText, String value) {
        this.name = name;
        this.altText = altText;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAltText() {
        return altText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
