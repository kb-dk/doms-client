package dk.statsbiblioteket.doms.client.impl.objects;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.ObjectProfile;
import dk.statsbiblioteket.doms.client.datastreams.Datastream;
import dk.statsbiblioteket.doms.client.exceptions.NotFoundException;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.links.LinkPattern;
import dk.statsbiblioteket.doms.client.objects.ContentModelObject;
import dk.statsbiblioteket.doms.client.objects.DataObject;
import dk.statsbiblioteket.doms.client.objects.DigitalObjectFactory;
import dk.statsbiblioteket.util.xml.DOM;
import dk.statsbiblioteket.util.xml.XPathSelector;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;

/**
 * Data objects are the objects that actually holds the data in DOMS. TODO implement
 */
public class DataObjectImpl extends AbstractDigitalObject implements DataObject {


    private String contentModelTitle;

    private List<LinkPattern> linkPatterns;

    public DataObjectImpl(ObjectProfile profile, CentralWebservice api, DigitalObjectFactory factory)
            throws ServerOperationFailed {
        super(profile, api, factory);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public synchronized String getContentmodelTitle() throws ServerOperationFailed {

        if (contentModelTitle != null){
            return contentModelTitle;
        }
        List<ContentModelObject> tmp = getType();
        List<ContentModelObject> contentModels = new ArrayList<ContentModelObject>();
        for (ContentModelObject contentModel : tmp) {
            if (!contentModel.getPid().equals("fedora-system:FedoraObject-3.0")){
                contentModels.add(contentModel);
            }
        }
        Map<ContentModelObject, Integer> extendsCount = new HashMap<ContentModelObject, Integer>();

        for (ContentModelObject contentModel : contentModels) {
            extendsCount.put(contentModel,0);
        }
        for (ContentModelObject contentModel : contentModels) {
            Set<ContentModelObject> childModels = contentModel.getDescendants();
            for (ContentModelObject childModel : childModels) {
                if (contentModels.contains(childModel)){
                    Integer extendscounter = extendsCount.get(childModel);
                    extendsCount.put(contentModel,extendscounter+1);
                }
            }
        }

        int bestCount = Integer.MAX_VALUE;
        ContentModelObject bestCM = null;
        for (ContentModelObject contentModelObject : extendsCount.keySet()) {
            Integer extendscounter = extendsCount.get(contentModelObject);
            if (extendscounter <= bestCount){
                bestCount = extendscounter;
                bestCM = contentModelObject;
            }
        }

        contentModelTitle = bestCM.getTitle();
        return contentModelTitle;
    }

    @Override
    public synchronized List<LinkPattern> getLinkPatterns() throws ServerOperationFailed {
        if (linkPatterns != null){
            return linkPatterns;
        }
        linkPatterns = new ArrayList<LinkPattern>();
        XPathSelector xpath = DOM.createXPathSelector("lp", "http://doms.statsbiblioteket.dk/types/linkpattern/0/1/#");

        List<ContentModelObject> contentmodels = getType();
        for (ContentModelObject contentmodel : contentmodels) {
            try {
                Datastream linkPatternStream = contentmodel.getDatastream("LINK_PATTERN");
                Document doc = DOM.stringToDOM(linkPatternStream.getContents(),true);
                NodeList linkPatternNodes = xpath.selectNodeList(doc, "/lp:linkPatterns/lp:linkPattern");
                for (int i = 0; i < linkPatternNodes.getLength(); i++) {
                    Node linkPatternNode = linkPatternNodes.item(i);
                    String name = xpath.selectString(linkPatternNode, "lp:name");
                    String alt_text = xpath.selectString(linkPatternNode, "lp:alt_text");
                    String value = xpath.selectString(linkPatternNode, "lp:value");

                    value = value.replaceAll("\\{objectPid\\}",this.getPid());
                    LinkPattern linkPattern = new LinkPatternImpl(name, alt_text, value);
                    linkPatterns.add(linkPattern);
                }
            } catch (NotFoundException e) {
                continue;
            }
        }
        return linkPatterns;
    }


}
