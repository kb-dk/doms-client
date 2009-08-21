package dk.statsbiblioteket.doms;

import dk.statsbiblioteket.doms.exceptions.FailedCharacterisationException;

import java.io.File;
import java.net.URL;

/**
 * TODO abr forgot to document this class
 */
public interface ExternalDatastream extends Datastream{

    public CharacterisationResult characterise(File file);
    public CharacterisationResult characterise(URL url);

    public CharacterisationResult upload(File file)
            throws FailedCharacterisationException;
    public CharacterisationResult upload(URL url)
            throws FailedCharacterisationException;

    public URL getExternalURL();

}
