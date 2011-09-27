package dk.statsbiblioteket.doms.client.datastreams.contentstreams;

import dk.statsbiblioteket.doms.client.datastreams.DOMSDataStream;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.objects.Component;
import dk.statsbiblioteket.doms.client.objects.RepositoryBean;
import dk.statsbiblioteket.doms.client.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * TODO abr forgot to document this class
 */
public class DOMSDataStreamReadOnly extends DOMSDataStream {

    private String contents;

    public DOMSDataStreamReadOnly(String dataobjectPid, String id) throws IOException, ServerOperationFailed {
        super();


        RepositoryBean repository = (RepositoryBean) Component.getInstance("repository");

        InputStream is = repository.getDatastreamDissemination(dataobjectPid, id);
        if (is != null){
            String foundDataStream = StringUtils.readStream(is);
            setContents(foundDataStream);

        }


    }


    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }
}
