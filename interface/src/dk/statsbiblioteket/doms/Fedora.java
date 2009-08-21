package dk.statsbiblioteket.doms;

import dk.statsbiblioteket.doms.exceptions.FedoraConnectionException;
import dk.statsbiblioteket.doms.exceptions.FedoraIllegalContentException;
import dk.statsbiblioteket.doms.exceptions.ObjectNotFoundException;

import java.util.List;

/**
 * TODO abr forgot to document this class
 */
public interface Fedora {



    public <E extends DigitalObject> E getObject(String pid) throws
            FedoraConnectionException, ObjectNotFoundException;


    public List<ContentModel> getEntryCMsForAngle(
            String viewAngle) throws FedoraIllegalContentException,
                                     FedoraConnectionException;


    public <E extends DataObject> List<E> getEntriesForAngle(String viewAngle,String state)
            throws FedoraConnectionException,
                   FedoraIllegalContentException;



}
