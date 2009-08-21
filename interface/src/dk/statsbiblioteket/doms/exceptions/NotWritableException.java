package dk.statsbiblioteket.doms.exceptions;

/**
 * TODO abr forgot to document this class
 */
public class NotWritableException
        extends DomsException {

    public NotWritableException() {
    }

    public NotWritableException(String s) {
        super(s);
    }

    public NotWritableException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public NotWritableException(Throwable throwable) {
        super(throwable);
    }
}
