package dk.statsbiblioteket.doms.client.sdo;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

/**
 * Base class for all XML objects.
 * <P>
 * Implements <code>ErrorHandler</code> 
 * <P>
 */

public class DOMSXMLData implements ErrorHandler {

    public DOMSXMLData(){}


    private String errorHandlerMsg;

    /**
     * Gives the message recieved by the ErrorHandler
     *
     * @return fejlbeskeden modtaget via ErrorHandler'en.
     */
    public String getErrorHandlerMsg(){
        return errorHandlerMsg;
    }


    /**
     * {@inheritDoc}
     * Invokes printError and rethrows ex
     * @see #printError
     */
    public void warning(SAXParseException ex) throws SAXParseException {
        printError("Warning", ex);
        //throw ex;
    }

    /**
     * {@inheritDoc}
     * Invokes printError and rethrows ex
     * @see #printError
     */
    public void error(SAXParseException ex) throws SAXParseException {
        printError("Error", ex);

    }

    /**
     * {@inheritDoc}
     * Invokes printError and rethrows ex
     * @see #printError
     */
    public void fatalError(SAXParseException ex) throws SAXParseException {
        printError("Fatal Error", ex);

    }

    /**
     * Formats a  <code>SAXParseException</code> as html, and rethrows it.
     *
     * @param type the type of exception. Just a string
     * @param ex the exception to format
     * @throws org.xml.sax.SAXParseException Always throw ex
     */
    protected void printError(String type, SAXParseException ex) throws SAXParseException
    {
        if (errorHandlerMsg == null) {
            errorHandlerMsg = "";
        }
        else {
            errorHandlerMsg += "\n<br>";
        }

        errorHandlerMsg += "[";
        errorHandlerMsg += type;
        errorHandlerMsg += "] ";
        if (ex== null) {
            errorHandlerMsg += "!!!";
        }
        String systemId = ex.getSystemId();
        if (systemId != null) {
            int index = systemId.lastIndexOf('/');
            if (index != -1)
                systemId = systemId.substring(index + 1);
            errorHandlerMsg += systemId;
        }
        errorHandlerMsg += ':';
        errorHandlerMsg += ex.getLineNumber();
        errorHandlerMsg += ':';
        errorHandlerMsg += ex.getColumnNumber();
        errorHandlerMsg += ": ";
        errorHandlerMsg += ex.getMessage();
        errorHandlerMsg += "\n";

        throw ex;
    } // printError(String,SAXParseException)

}
