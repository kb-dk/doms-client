package dk.statsbiblioteket.doms.guiclient;

import dk.statsbiblioteket.doms.client.exceptions.ServerOperationFailed;
import dk.statsbiblioteket.doms.client.impl.AbstractDomsClient;
import dk.statsbiblioteket.doms.client.objects.FedoraState;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 9/5/11
 * Time: 1:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class GuiClientImpl extends AbstractDomsClient implements GuiClient {


    public GuiClientImpl(URL url, String username, String password){
        super(url, username, password);

    }


    public List<SearchResult> search(String query, int offset, int pageLength) throws ServerOperationFailed {
        try {
            List<dk.statsbiblioteket.doms.central.SearchResult> wresults
                    =
                    domsAPI.findObjects(query, offset, pageLength);
            List<SearchResult> cresults = new ArrayList<SearchResult>();
            for (dk.statsbiblioteket.doms.central.SearchResult wresult : wresults) {
                SearchResult cresult = new SearchResult(wresult.getPid(),
                                                        wresult.getType(),
                                                        wresult.getTitle(),
                                                        FedoraState.fromString(wresult.getState()),
                                                        new Date(wresult.getModifiedDate()),
                                                        new Date(wresult.getCreatedDate()),
                                                        getFactory());
                cresults.add(cresult);
            }
            return cresults;
//        } catch (dk.statsbiblioteket.doms.central.InvalidCredentialsException invalidCredentials){
//            throw new InvalidCredentialsException("Authorization Failed", invalidCredentials);
        } catch (Exception exception) {
            throw new ServerOperationFailed(
                    "Failed searching", exception);
        }
    }

}
