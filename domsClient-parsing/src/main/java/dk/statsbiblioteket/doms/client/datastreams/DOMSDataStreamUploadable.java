package dk.statsbiblioteket.doms.client.datastreams;


import dk.statsbiblioteket.doms.client.datastreams.DOMSDataStream;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.objects.Component;
import dk.statsbiblioteket.doms.client.objects.RepositoryBean;

import java.rmi.RemoteException;

/**
 * TODO abr forgot to document this class
 */
public class DOMSDataStreamUploadable extends DOMSDataStream {

    private String url;

    private String temppath;

    public DOMSDataStreamUploadable(String dataobjectpid, String id) throws RemoteException, ServerOperationFailed {

        RepositoryBean repository = (RepositoryBean) Component.getInstance("repository");

        Datastream datastream = repository.getDataStream(dataobjectpid, id);
        if (datastream!=null) {
            String url = null; // datastream.getLocation();
            if (url!=null)
            {
                this.url = url;
            }
        }
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTemppath() {
        return temppath;
    }

    public void setTemppath(String temppath) {
        this.temppath = temppath;
    }
}



