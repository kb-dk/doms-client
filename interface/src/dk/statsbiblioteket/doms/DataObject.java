package dk.statsbiblioteket.doms;

import dk.statsbiblioteket.doms.exceptions.FedoraConnectionException;
import dk.statsbiblioteket.doms.exceptions.FedoraIllegalContentException;

import java.util.List;

/**
 * TODO abr forgot to document this class
 */
public interface DataObject extends DigitalObject{

    public void markObjectAsTemplate(ContentModel cmpid)
            throws  FedoraConnectionException;


    public List<DataObject> getViewObjects(String viewAngle)
            throws FedoraConnectionException, FedoraIllegalContentException;



    public ValidationResult validate()
            throws FedoraConnectionException, FedoraIllegalContentException;

    
    public CompoundContentModel mergeContentModels();



}
