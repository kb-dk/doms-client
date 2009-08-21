package dk.statsbiblioteket.doms;

import dk.statsbiblioteket.doms.exceptions.FedoraConnectionException;

/**
 * TODO abr forgot to document this class
 */
public class FedoraFactory {

    public Fedora getInstance(FedoraUserToken token) throws
                                                     FedoraConnectionException {

        testFedoraConnection(token);

        return new FedoraImpl(token);

    }

    private void testFedoraConnection(FedoraUserToken token) throws
                                                             FedoraConnectionException{
        //To change body of created methods use File | Settings | File Templates.
    }
}
