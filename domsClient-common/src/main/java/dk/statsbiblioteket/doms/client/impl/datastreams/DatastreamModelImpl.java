package dk.statsbiblioteket.doms.client.impl.datastreams;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.DatastreamProfile;
import dk.statsbiblioteket.doms.client.datastreams.Datastream;
import dk.statsbiblioteket.doms.client.datastreams.DatastreamDeclaration;
import dk.statsbiblioteket.doms.client.datastreams.DatastreamModel;
import dk.statsbiblioteket.doms.client.datastreams.Presentation;
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
    private List<DatastreamDeclaration> datastreamDeclarations;


    public DatastreamModelImpl(DatastreamProfile datastreamProfile,
                               DigitalObject digitalObject, CentralWebservice api) {
        super(datastreamProfile, digitalObject, api);
        datastreamDeclarations = new ArrayList<DatastreamDeclaration>();

    }


    public List<DatastreamDeclaration> getDatastreamDeclarations() throws ServerOperationFailed {
        try {
            parseDs();
        } catch (NotFoundException e) {
            //ignore this
        }
        return this.datastreamDeclarations;
    }

    private synchronized void parseDs() throws  ServerOperationFailed,
                                                NotFoundException {

        if (parsed){
            return;
        }


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
            String dsName = pathSelector.selectString(item,"@ID");
            String schemaDSname = pathSelector.selectString(item,
                                                            "ds:extension[@name='SCHEMA']/ds:reference[@type='datastream']/@value");
            Datastream compositeSchemas = null;
            if (schemaDSname != null && !schemaDSname.isEmpty()){
                compositeSchemas = getDigitalObject().getDatastream(schemaDSname);

            }

            // Vilkårligt mange (0..*) form elementer
            List<String> dsMimeTypes = new ArrayList<String>();
            NodeList dsMimeTypeList = pathSelector.selectNodeList(item, "ds:form/@MIME");
            for(int j = 0; j < dsMimeTypeList.getLength(); j++){
                dsMimeTypes.add(dsMimeTypeList.item(j).getNodeValue());
            }

            List<String> dsFormatUris = new ArrayList<String>();
            NodeList dsFormatUriList = pathSelector.selectNodeList(item, "ds:form/@FORMAT_URI");
            for(int j = 0; j < dsFormatUriList.getLength(); j++){
                dsFormatUris.add(dsFormatUriList.item(j).getNodeValue());
            }

            // Her håndteres at der er viewAngles for Gui
            Node guiPresentAs = pathSelector.selectNode(item,
                                                        "ds:extension[@name='GUI']/ds:presentAs/@type");
            Presentation presentation;
            try {
                if (guiPresentAs != null){
                    presentation = Presentation.valueOf(guiPresentAs.getNodeValue());
                } else {
                    presentation = Presentation.undefined;
                }
            } catch (IllegalArgumentException e){
                presentation = Presentation.undefined;
            }
            DatastreamDeclarationImpl dsDecl = new DatastreamDeclarationImpl(dsName, this);
            dsDecl.addMimeTypes(dsMimeTypes);
            dsDecl.addFormatUris(dsFormatUris);
            dsDecl.setSchema(compositeSchemas);
            dsDecl.setPresentation(presentation);
            datastreamDeclarations.add(dsDecl);
        }
    }
}
