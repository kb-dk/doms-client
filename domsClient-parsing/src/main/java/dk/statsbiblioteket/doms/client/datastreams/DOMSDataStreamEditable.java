package dk.statsbiblioteket.doms.client.datastreams;

import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.objects.*;
import dk.statsbiblioteket.doms.client.sdo.DOMSXmlDocument;
import dk.statsbiblioteket.doms.client.util.Constants;

import java.io.IOException;
import java.io.InputStream;

/**
 * TODO abr forgot to document this class
 */
public class DOMSDataStreamEditable extends DOMSDataStream{
    private DOMSXmlDocument domsXmlDocument;
    private RepositoryBean repository;


    /**
     * Parse schema for the datastream.
     * @param dataobjectPid the datamodel containing the datastream. Null, if the parsing is done
     * on content model level
     * @throws dk.statsbiblioteket.doms.client.objects.MyXMLWriteException
     * @throws java.io.IOException
     * @throws dk.statsbiblioteket.doms.client.objects.DOMSIllegalStateException
     */
    public DOMSDataStreamEditable(String dataobjectPid, DOMSDataStreamCompositeModelTypeModel typemodel)
            throws MyXMLWriteException, IOException, DOMSIllegalStateException, MyXMLParseException, ServerOperationFailed {

        DOMSDataStreamCompositeModelTypeModelExtensionSchema schema =
                (DOMSDataStreamCompositeModelTypeModelExtensionSchema)
                        typemodel.getExtensionByID(Constants.EXTENSIONS_SCHEMA);


        if (schema!=null) {//if no schema, nothing to do

            setDomsXmlDocument(new DOMSXmlDocument());//okay, set the xml document to an empty document

            if (dataobjectPid !=null)//Do we know where the real datastream is?
            { //yes
                String fedoraUrl = Constants.ensureURI(dataobjectPid) + "/" + typemodel.getId();

                getDomsXmlDocument().setSdoContext(schema.getDomsXmlDocument().getSdoContext());
                getDomsXmlDocument().setSdoTypes(schema.getDomsXmlDocument().getSdoTypes());
                InputStream is = repository().getDatastreamDissemination(dataobjectPid,typemodel.getId());
                getDomsXmlDocument().load(schema, is);

            }
        }
    }

    public void setDomsXmlDocument(DOMSXmlDocument domsXmlDocument) {
        this.domsXmlDocument = domsXmlDocument;
    }

    public DOMSXmlDocument getDomsXmlDocument() {
        return domsXmlDocument;
    }

    public RepositoryBean repository()
    {
        if (repository == null)
            repository = (RepositoryBean) Component.getInstance("repository");
        return repository;
    }

}
