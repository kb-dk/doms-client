package dk.statsbiblioteket.doms.exceptions;

/**
 * This is the mother class of all exceptions for ECM. All exceptions will be
 * regarded as EcmExceptions for purposes of formatting them for user
 * comsumption.
 * <br/>
 * The class is abstract, as no code should ever throw just an EcmException.
 * If something fails, and you really do not know what to throw, throw an
 * UnknownException
 *
 * @see net.sourceforge.ecm.exceptions.UnknownException
 */
public abstract class DomsException
        extends Exception {

    public DomsException() {
    }

    public DomsException(String s) {
        super(s);
    }

    public DomsException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public DomsException(Throwable throwable) {
        super(throwable);
    }


}
