package dk.statsbiblioteket.doms;

import dk.statsbiblioteket.doms.exceptions.FedoraConnectionException;
import dk.statsbiblioteket.doms.exceptions.FedoraIllegalContentException;
import dk.statsbiblioteket.doms.exceptions.PIDGeneratorException;

/**
 * TODO abr forgot to document this class
 */
public interface Template extends DataObject{

    public DataObject cloneTemplate()
            throws FedoraIllegalContentException, FedoraConnectionException,
                   PIDGeneratorException;

    public ContentModel getMarkedContentModel();

}
