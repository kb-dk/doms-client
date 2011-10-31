package dk.statsbiblioteket.doms.client.objects;

import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;

import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 10/31/11
 * Time: 11:16 AM
 * To change this template use File | Settings | File Templates.
 */
public interface FileObject extends DataObject{

    public URL getFileUrl() throws ServerOperationFailed;

    public void setFileUrl() throws ServerOperationFailed;

    public void setFileUrl(String checksum, boolean validate) throws ServerOperationFailed;
}
