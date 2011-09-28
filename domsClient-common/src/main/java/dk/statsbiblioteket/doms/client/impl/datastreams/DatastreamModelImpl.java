package dk.statsbiblioteket.doms.client.impl.datastreams;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.DatastreamProfile;
import dk.statsbiblioteket.doms.client.datastreams.Datastream;
import dk.statsbiblioteket.doms.client.datastreams.DatastreamDeclaration;
import dk.statsbiblioteket.doms.client.datastreams.DatastreamModel;
import dk.statsbiblioteket.doms.client.exceptions.NotFoundException;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.objects.DigitalObject;
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
 * Created by IntelliJ IDEA.
 * User: eab
 * Date: 9/27/11
 * Time: 9:11 AM
 * To change this template use File | Settings | File Templates.
 */
public class DatastreamModelImpl extends InternalDatastreamImpl
        implements DatastreamModel {

    private boolean parsed = false;
    private HashMap<String, Datastream> compositeSchemas;
    private List<DatastreamDeclaration> datastreamDeclarations;
    private String pid;
    private CentralWebservice api;
    private DigitalObjectFactory factory;
    private DigitalObject digitalObject;


    public DatastreamModelImpl(DatastreamProfile datastreamProfile,
                               DigitalObject digitalObject, CentralWebservice api) {
        super(datastreamProfile, digitalObject, api);
        this.digitalObject = digitalObject;

        this.pid = pid;
        this.api = api;
        this.factory = factory;


    }


    public List<DatastreamDeclaration> getDatastreamDeclarations() throws ServerOperationFailed {
        try {
            parseDs();
        } catch (NotFoundException e) {
            throw new ServerOperationFailed(
                    "Failed when trying to locate 'DS-COMPOSITE-MODEL'", e);
        }
        return this.datastreamDeclarations;
    }

    private synchronized void parseDs() throws  ServerOperationFailed,
            NotFoundException {

        if (parsed){
            return;
        }
        datastreamDeclarations = new ArrayList<DatastreamDeclaration>();
        compositeSchemas = new HashMap<String, Datastream>();
        Document dsDoc = DOM.stringToDOM(getContents(), true);

        XPathSelector pathSelector = DOM.createXPathSelector("ds",
                "info:fedora/fedora-system:def/dsCompositeModel#");

        NodeList allDSReferences = pathSelector.selectNodeList(dsDoc,
                "//ds:dsTypeModel[@ID]");
//        NodeList allNamedDatastreams = pathSelector.selectNodeList(dsDoc,
//                "//*[@name]/*[@type='datastream']/@value");

        // For each dsTypeModel
        for(int i = 0; i < allDSReferences.getLength(); i++){
            Node item = allDSReferences.item(i);
            String name = pathSelector.selectString(item, "//ds:reference/@value");
            String dsName = pathSelector.selectString(item,
                    "//ds:extension[@name='SCHEMA']/*/@value");
            Datastream componentDs = this.digitalObject.getDatastream(dsName);
            compositeSchemas.put(name, componentDs);

            // Vilkårligt mange (0..*) form elementer
            List<String> dsMimeTypes = new ArrayList<String>();
            NodeList dsMimeTypeList = pathSelector.selectNodeList(item, "//*[@MIME]");
            for(int j = 0; j < dsMimeTypeList.getLength(); j++){
                dsMimeTypes.add(dsMimeTypeList.item(i).getAttributes()
                        .getNamedItem("MIME").getNodeValue());
            }

            List<String> dsFormatUris = new ArrayList<String>();
            NodeList dsFormatUriList = pathSelector.selectNodeList(item, "//*[@FORMAT_URI]");
            for(int j = 0; j < dsFormatUriList.getLength(); j++){
                dsFormatUris.add(dsFormatUriList.item(i).getAttributes()
                        .getNamedItem("FORMAT_URI").getNodeValue());
            }

            // Her håndteres at der er viewAngles for Gui
            NodeList guiPresentAs = pathSelector.selectNodeList(item,
                    "//*[@name='GUI']/presentAs");
            List<String> guiPresentation = new ArrayList<String>();
            for(int j = 0; j < guiPresentAs.getLength(); j++){
                guiPresentation.add(guiPresentAs.item(j).getAttributes().
                        getNamedItem("type").getNodeValue());
            }


            DatastreamDeclarationImpl dsDecl = new DatastreamDeclarationImpl(name,
                    componentDs);
            dsDecl.addMimeTypes(dsMimeTypes);
            dsDecl.addFormatUris(dsFormatUris);
            dsDecl.addCompositeSchemas(compositeSchemas);
            dsDecl.addPresentation("GUI", guiPresentation);
            datastreamDeclarations.add(dsDecl);
        }
    }
}
