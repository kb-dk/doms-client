package dk.statsbiblioteket.doms.client.impl.objects;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.ObjectProfile;
import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.objects.FileObject;

import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 10/31/11
 * Time: 11:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class FileObjectImpl extends DataObjectImpl implements FileObject{
    public FileObjectImpl(ObjectProfile profile, CentralWebservice api,
                          DigitalObjectFactoryImpl factory) throws ServerOperationFailed {
        super(profile,api,factory);
    }

    @Override
    public URL getFileUrl() throws ServerOperationFailed {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setFileUrl() throws ServerOperationFailed {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setFileUrl(String checksum, boolean validate) throws ServerOperationFailed {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
