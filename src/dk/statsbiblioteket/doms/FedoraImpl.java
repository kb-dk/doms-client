package dk.statsbiblioteket.doms;

import dk.statsbiblioteket.doms.exceptions.FedoraConnectionException;
import dk.statsbiblioteket.doms.exceptions.ObjectNotFoundException;
import dk.statsbiblioteket.doms.exceptions.FedoraIllegalContentException;

import java.util.List;

/**
 * TODO abr forgot to document this class
 */
public class FedoraImpl implements Fedora{

    private FedoraUserToken token;


    public FedoraImpl(FedoraUserToken token) {

        this.token = token;
    }


    public <E extends DigitalObject> E getObject(String pid)
            throws FedoraConnectionException, ObjectNotFoundException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<ContentModel> getEntryCMsForAngle(String viewAngle)
            throws FedoraIllegalContentException, FedoraConnectionException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public <E extends DataObject> List<E> getEntriesForAngle(String viewAngle,
                                                             String state)
            throws FedoraConnectionException, FedoraIllegalContentException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
