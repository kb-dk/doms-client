package dk.statsbiblioteket.doms.trialAndError;

import dk.statsbiblioteket.doms.client.*;
import java.net.URL;

public class Runner {


    public static void main(String args[]){
        DomsWSClient domswsclient = new DomsWSClientImpl();

        try {
            URL domsWSAPIEndpoint = new URL("http://alhena:7980/centralDomsWebservice/central/?wsdl");
            String username = "fedoraAdmin";
            String password = "fedoraAdminPass";
            domswsclient.login(domsWSAPIEndpoint, username, password);

        } catch (Exception e){
            System.err.println(e);
        }

        System.out.println(domswsclient.toString());

    }

}