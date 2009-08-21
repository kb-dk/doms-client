package dk.statsbiblioteket.doms.exceptions;

/**
 * TODO abr forgot to document this class
 */
public class DatastreamNotFoundException extends DomsException{
    public DatastreamNotFoundException() {
    }

    public DatastreamNotFoundException(String s) {
        super(s);
    }

    public DatastreamNotFoundException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public DatastreamNotFoundException(Throwable throwable) {
        super(throwable);
    }
}
