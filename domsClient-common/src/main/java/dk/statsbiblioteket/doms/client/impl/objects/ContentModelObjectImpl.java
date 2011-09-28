package dk.statsbiblioteket.doms.client.impl.objects;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.DatastreamProfile;
import dk.statsbiblioteket.doms.central.ObjectProfile;
import dk.statsbiblioteket.doms.client.datastreams.Datastream;
import dk.statsbiblioteket.doms.client.datastreams.DatastreamModel;
import dk.statsbiblioteket.doms.client.exceptions.NotFoundException;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.impl.datastreams.DatastreamModelImpl;
import dk.statsbiblioteket.doms.client.impl.datastreams.ExternalDatastreamImpl;
import dk.statsbiblioteket.doms.client.impl.datastreams.InternalDatastreamImpl;
import dk.statsbiblioteket.doms.client.objects.ContentModelObject;
import dk.statsbiblioteket.doms.client.objects.DigitalObjectFactory;
import dk.statsbiblioteket.util.xml.DOM;
import dk.statsbiblioteket.util.xml.XPathSelector;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Content Model objects are the objects that holds the structure of the objects in doms. TODO implement
 */
public class ContentModelObjectImpl extends AbstractDigitalObject implements
                                                                  ContentModelObject {
    private boolean parsed = false;
    private HashMap<String, List<String>> relations;
    private HashMap<String, List<String>> inverseRelations;

    private DigitalObjectFactory factory;
    private DatastreamModel dsModel;
    private boolean datastreamsLoaded = false;


    public ContentModelObjectImpl(ObjectProfile profile, CentralWebservice api,
                                  DigitalObjectFactory factory)
            throws ServerOperationFailed {
        super(profile, api, factory);    //To change body of overridden methods use File | Settings | File Templates.
        this.profile = profile;
        this.factory = factory;
    }

    public List<String> getRelationsWithViewAngle(String viewAngle) throws ServerOperationFailed {
        parseView();
        return relations.get(viewAngle);
    }

    public List<String> getInverseRelationsWithViewAngle(String viewAngle) throws ServerOperationFailed {
        parseView();
        return inverseRelations.get(viewAngle);
    }

    private synchronized void parseView() throws ServerOperationFailed {
        if (parsed){
            return;
        }
        relations = new HashMap<String, List<String>>();
        inverseRelations = new HashMap<String, List<String>>();
        //parseView
        Datastream viewStream;
        Document viewDoc;
        try {
            viewStream = this.getDatastream("VIEW");
        } catch (NotFoundException e) {
            throw new ServerOperationFailed(
                    "'VIEW' datastream not found in object: " + this.getPid(), e);
        }
        String contents = viewStream.getContents();
        viewDoc = DOM.stringToDOM(contents, true);
        XPathSelector xPathSelector = DOM.createXPathSelector("v",
                                                              "http://ecm.sourceforge.net/types/view/0/2/#");
        HashMap<String, List<String>> viewNameRelations = new HashMap();

        NodeList allViewAngles = xPathSelector.selectNodeList(viewDoc,
                                                              "/v:views/v:viewangle/@name");
        for (int i = 0; i < allViewAngles.getLength(); i++){
            String viewAngleName = allViewAngles.item(i).getTextContent();
            NodeList namedViewAngles = xPathSelector.selectNodeList(viewDoc,
                                                                    "/v:views/v:viewangle[@name = '" + viewAngleName +
                                                                    "']/v:relations");
            List<String> relList = new ArrayList<String>();
            for (int j = 0; j < namedViewAngles.getLength(); j++){
                Node item = namedViewAngles.item(j);
                relList.add(item.getNamespaceURI()+item.getLocalName());
            }
            relations.put(viewAngleName, relList);

            namedViewAngles = xPathSelector.selectNodeList(viewDoc,
                                                           "/v:views/v:viewangle[@name = '" + viewAngleName +
                                                           "']/v:inverseRelations");
            List<String> iRelList = new ArrayList<String>();
            for (int j = 0; j < namedViewAngles.getLength(); j++){
                Node item = namedViewAngles.item(j);
                iRelList.add(item.getNamespaceURI()+item.getLocalName());
            }
            inverseRelations.put(viewAngleName, iRelList);
        }
        parsed = true;
        /*
            <v:views xmlns:v="http://ecm.sourceforge.net/types/view/0/2/#">
          <v:viewangle name="SummaVisible">
            <v:relations xmlns:doms="http://doms.statsbiblioteket.dk/relations/default/0/1/#">
              <doms:hasShard></doms:hasShard>
            </v:relations>
            <v:inverseRelations></v:inverseRelations>
          </v:viewangle>
        </v:views>
        */
    }

    public DatastreamModel getDsModel() throws ServerOperationFailed {
        loadDatastreams();
        return dsModel;
    }

    @Override
    protected synchronized void loadDatastreams() throws ServerOperationFailed {
        if(datastreamsLoaded){
            return;
        }
        for (DatastreamProfile datastreamProfile : profile.getDatastreams()) {
            if(datastreamProfile.getId().equals("DS-COMPOSITE-MODEL")){
                dsModel = new DatastreamModelImpl(datastreamProfile, this,
                                                  this.api);
                datastreams.add(dsModel);
            }else if (datastreamProfile.isInternal()){
                datastreams.add(new InternalDatastreamImpl(datastreamProfile, this, api));
            } else {
                datastreams.add(new ExternalDatastreamImpl(datastreamProfile, this, api));
            }
        }
        datastreamsLoaded = true;
    }
}

