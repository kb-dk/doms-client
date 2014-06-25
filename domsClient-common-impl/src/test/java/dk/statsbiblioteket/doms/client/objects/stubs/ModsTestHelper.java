package dk.statsbiblioteket.doms.client.objects.stubs;


public class ModsTestHelper {

    private static final String ADDITIONAL_ATTRIBUTE_STRING = "---INVALID-ATTRIBUTE-PLACEHOLDER---";

    public String modsString = "<mods:mods xmlns:mods=\"http://www.loc.gov/mods/v3\">\n" +
            "    <mods:part>\n" +
            "        <mods:detail type=\"sectionLabel\">\n" +
            "            <mods:number>Sektion 1</mods:number>\n" +
            "        </mods:detail>\n" +
            "        <mods:extent unit=\"pages\">\n" +
            "            <mods:start>1</mods:start>\n" +
            "        </mods:extent>\n" +
            "    </mods:part>\n" +
            "    <mods:relatedItem type=\"original\">\n" +
            "        <mods:identifier type=\"reel number\" "+ ADDITIONAL_ATTRIBUTE_STRING + ">400022028241-1</mods:identifier>\n" +
            "        <mods:identifier type=\"reel sequence number\">6</mods:identifier>\n" +
            "        <mods:physicalDescription>\n" +
            "            <mods:form type=\"microfilm\"/>\n" +
            "            <mods:note type=\"pagecondition\">Not tested</mods:note>\n" +
            "            <mods:note type=\"photocondition\">Not tested</mods:note>\n" +
            "        </mods:physicalDescription>\n" +
            "        <mods:note type=\"noteAboutReproduction\" displayLabel=\"Hm nice\">present</mods:note>\n" +
            "    </mods:relatedItem>\n" +
            "    <mods:relatedItem type=\"host\">\n" +
            "        <mods:titleInfo type=\"uniform\" authority=\"Statens Avissamling\">\n" +
            "            <mods:title>adresseavisen1759</mods:title>\n" +
            "        </mods:titleInfo>\n" +
            "    </mods:relatedItem>\n" +
            "</mods:mods>";

     public String modsSimpleString = "<mods:mods xmlns:mods=\"http://www.loc.gov/mods/v3\">\n" +
            "    <mods:part>\n" +
            "        <mods:detail type=\"sectionLabel\">\n" +
            "            <mods:number>Sektion 1</mods:number>\n" +
             "           <mods:myType></mods:myType>\n" +
             "        </mods:detail>\n" +
             "        <mods:extent unit=\"pages\" " + ADDITIONAL_ATTRIBUTE_STRING +" >\n" +
             "            <mods:start>1</mods:start>\n" +
             "           <mods:end>2</mods:end>\n" +
             "        </mods:extent>\n" +
             "    </mods:part>\n" +
             "        <mods:titleInfo type=\"uniform\" >\n" +
             "            <mods:title>adresseavisen1759</mods:title>\n" +
             "        </mods:titleInfo>\n" +
             "</mods:mods>";

    public void setAdditionalAttributeString(String attributeString) {
        modsString = modsString.replaceAll(ADDITIONAL_ATTRIBUTE_STRING, attributeString);
        modsSimpleString = modsSimpleString.replaceAll(ADDITIONAL_ATTRIBUTE_STRING, attributeString);
    }

    public String getModsString() {
        setAdditionalAttributeString("");
        return modsString;
    }

    public String getModsSimpleString() {
         setAdditionalAttributeString("");
         return modsSimpleString;
    }

}
