package dk.statsbiblioteket.doms.client.objects;


import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.ObjectProfile;
import dk.statsbiblioteket.doms.client.impl.objects.DigitalObjectFactoryImpl;

import org.junit.Before;
import org.junit.Test;

import java.net.MalformedURLException;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Test reading templates.
 */
public class TemplateTest {
    private DigitalObjectFactoryImpl factory;
    private CentralWebservice domsAPI;

    public TemplateTest() throws MalformedURLException {
        super();
    }

    @Before
    public void setUp() throws Exception {
        domsAPI = mock(CentralWebservice.class);
        when(domsAPI.getObjectProfile("doms:Template_Program")).thenReturn(createTemplateObjectProfile());
        when(domsAPI.getObjectProfile("uuid:XXX")).thenReturn(createNewObjectProfile());
        when(domsAPI.newObject(eq("doms:Template_Program"), eq(Collections.<String>emptyList()), anyString())).thenReturn(
                "uuid:XXX");
        factory = new DigitalObjectFactoryImpl(domsAPI);
    }

    /**
     * Test cloning a template.
     * @throws Exception
     */
    @Test
    public void testTemplateCreation() throws Exception {
        //Read template from doms.
        DigitalObject template = factory.getDigitalObject("doms:Template_Program");

        //Verify doms is called.
        verify(domsAPI).getObjectProfile("doms:Template_Program");
        verifyNoMoreInteractions(domsAPI);

        //Clone the template
        TemplateObject template1 = (TemplateObject) template;
        DigitalObject clone = template1.clone();

        //Verify that doms is called to clone from template, and reads the new object
        verify(domsAPI).newObject(eq("doms:Template_Program"), eq(Collections.<String>emptyList()), anyString());
        verify(domsAPI).getObjectProfile("uuid:XXX");
        verifyNoMoreInteractions(domsAPI);

        //Check result
        assertEquals("uuid:XXX", clone.getPid());
    }

    private ObjectProfile createTemplateObjectProfile() {
        ObjectProfile profile = new ObjectProfile();
        profile.setType("TemplateObject");
        profile.setPid("doms:Template_Program");
        profile.setState("A");
        return profile;
    }

    private ObjectProfile createNewObjectProfile() {
        ObjectProfile profile = new ObjectProfile();
        profile.setType("TemplateObject");
        profile.setPid("uuid:XXX");
        profile.setState("I");
        return profile;
    }
}
