package dk.statsbiblioteket.doms.client.objects;

import dk.statsbiblioteket.doms.Fixer;
import dk.statsbiblioteket.util.xml.DOM;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 11/10/11
 * Time: 6:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class FixerTest {

    String doc = "\n" +
                 "\n" +
                 "<PBCoreDescriptionDocument xmlns=\"http://www.pbcore.org/PBCore/PBCoreNamespace.html\">\n" +
                 "  <pbcoreIdentifier>\n" +
                 "    <identifier>307894RitzauProgram</identifier>\n" +
                 "    <identifierSource>id</identifierSource>\n" +
                 "  </pbcoreIdentifier>\n" +
                 "  <pbcoreTitle>\n" +
                 "    <title>Luksusfælden</title>\n" +
                 "    <titleType>titel</titleType>\n" +
                 "  </pbcoreTitle>\n" +
                 "  <pbcoreTitle>\n" +
                 "    <title></title>\n" +
                 "    <titleType>originaltitel</titleType>\n" +
                 "  </pbcoreTitle>\n" +
                 "  <pbcoreTitle>\n" +
                 "    <title></title>\n" +
                 "    <titleType>episodetitel</titleType>\n" +
                 "  </pbcoreTitle>\n" +
                 "  <pbcoreDescription>\n" +
                 "    <description>Tine på 40 og Kenneth på 46 har tilsammen syv børn, tre af dem bor sammen med parret i Bredebro i Sønderjylland. Tine arbejder som socialrådgiver, og Kenneth er på førtidspension pga. en arbejdsskade i 2005. For fem år siden købte de et nedlagt landbrug, som de har renoveret for mere end 200.000 kr. Men husets tag er utæt, og Kenneth og Tine ved ikke, hvor de skal finde pengene til at få det lavet. Allerede nu drypper det ind, så Mette og Gustav bliver nødt til at få hjælp udefra til at vurdere, hvor skidt det står til med ejendommen.</description>\n" +
                 "    <descriptionType>langomtale1</descriptionType>\n" +
                 "  </pbcoreDescription>\n" +
                 "  <pbcoreDescription>\n" +
                 "    <description></description>\n" +
                 "    <descriptionType>langomtale2</descriptionType>\n" +
                 "  </pbcoreDescription>\n" +
                 "  <pbcoreDescription>\n" +
                 "    <description>Dansk livsstilsprogram.</description>\n" +
                 "    <descriptionType>kortomtale</descriptionType>\n" +
                 "  </pbcoreDescription>\n" +
                 "  <pbcoreGenre>\n" +
                 "    <genre>hovedgenre: Fritid &amp; Livsstil</genre>\n" +
                 "  </pbcoreGenre>\n" +
                 "  <pbcoreGenre>\n" +
                 "    <genre>undergenre: Alle</genre>\n" +
                 "  </pbcoreGenre>\n" +
                 "  <pbcoreGenre>\n" +
                 "    <genre>indhold_emne: </genre>\n" +
                 "  </pbcoreGenre>\n" +
                 "  <pbcoreCreator>\n" +
                 "    <creator></creator>\n" +
                 "    <creatorRole>forfatter</creatorRole>\n" +
                 "  </pbcoreCreator>\n" +
                 "  <pbcoreContributor>\n" +
                 "    <contributor></contributor>\n" +
                 "    <contributorRole>medvirkende</contributorRole>\n" +
                 "  </pbcoreContributor>\n" +
                 "  <pbcoreContributor>\n" +
                 "    <contributor></contributor>\n" +
                 "    <contributorRole>instruktion</contributorRole>\n" +
                 "  </pbcoreContributor>\n" +
                 "  <pbcorePublisher>\n" +
                 "    <publisher>tv3</publisher>\n" +
                 "    <publisherRole>channel_name</publisherRole>\n" +
                 "  </pbcorePublisher>\n" +
                 "  <pbcorePublisher>\n" +
                 "    <publisher>TV3</publisher>\n" +
                 "    <publisherRole>kanalnavn</publisherRole>\n" +
                 "  </pbcorePublisher>\n" +
                 "  <pbcoreInstantiation>\n" +
                 "    <dateCreated>0</dateCreated>\n" +
                 "    <dateIssued></dateIssued>\n" +
                 "    <formatLocation></formatLocation>\n" +
                 "    <formatMediaType>Moving Image</formatMediaType>\n" +
                 "    <formatStandard>ikke hd</formatStandard>\n" +
                 "    <formatDuration>0</formatDuration>\n" +
                 "    <formatAspectRatio>, </formatAspectRatio>\n" +
                 "    <formatColors>farve</formatColors>\n" +
                 "    <formatChannelConfiguration>ikke surround</formatChannelConfiguration>\n" +
                 "    <pbcoreDateAvailable>\n" +
                 "      <dateAvailableStart>2010-10-19T20:00:00+0200</dateAvailableStart>\n" +
                 "      <dateAvailableEnd>2010-10-19T21:00:00+0200</dateAvailableEnd>\n" +
                 "    </pbcoreDateAvailable>\n" +
                 "    <pbcoreFormatID>\n" +
                 "      <formatIdentifier>[INSERT_PBC_FORMAT_ID]</formatIdentifier>\n" +
                 "      <formatIdentifierSource>[INSERT_PBC_FORMAT_ID_SOURCE]</formatIdentifierSource>\n" +
                 "    </pbcoreFormatID>\n" +
                 "    <pbcoreAnnotation>\n" +
                 "      <annotation></annotation>\n" +
                 "    </pbcoreAnnotation>\n" +
                 "  </pbcoreInstantiation>\n" +
                 "  <pbcoreExtension>\n" +
                 "    <extension>antalepisoder:0</extension>\n" +
                 "  </pbcoreExtension>\n" +
                 "  <pbcoreExtension>\n" +
                 "    <extension>episodenr:6</extension>\n" +
                 "  </pbcoreExtension>\n" +
                 "  <pbcoreExtension>\n" +
                 "    <extension>premiere:ikke premiere</extension>\n" +
                 "  </pbcoreExtension>\n" +
                 "  <pbcoreExtension>\n" +
                 "    <extension>genudsendelse:ikke genudsendelse</extension>\n" +
                 "  </pbcoreExtension>\n" +
                 "  <pbcoreExtension>\n" +
                 "    <extension>hovedgenre_id:3</extension>\n" +
                 "  </pbcoreExtension>\n" +
                 "  <pbcoreExtension>\n" +
                 "    <extension>kanalid:5</extension>\n" +
                 "  </pbcoreExtension>\n" +
                 "  <pbcoreExtension>\n" +
                 "    <extension>live:ikke live</extension>\n" +
                 "  </pbcoreExtension>\n" +
                 "  <pbcoreExtension>\n" +
                 "    <extension>lydlink:</extension>\n" +
                 "  </pbcoreExtension>\n" +
                 "  <pbcoreExtension>\n" +
                 "    <extension>produktionsland:</extension>\n" +
                 "  </pbcoreExtension>\n" +
                 "  <pbcoreExtension>\n" +
                 "    <extension>produktionsland_id:0</extension>\n" +
                 "  </pbcoreExtension>\n" +
                 "  <pbcoreExtension>\n" +
                 "    <extension>program_id:14439699</extension>\n" +
                 "  </pbcoreExtension>\n" +
                 "  <pbcoreExtension>\n" +
                 "    <extension>program_ophold:ikke program ophold</extension>\n" +
                 "  </pbcoreExtension>\n" +
                 "  <pbcoreExtension>\n" +
                 "    <extension>undergenre_id:637</extension>\n" +
                 "  </pbcoreExtension>\n" +
                 "  <pbcoreExtension>\n" +
                 "    <extension>urllink:</extension>\n" +
                 "  </pbcoreExtension>\n" +
                 "  <pbcoreExtension>\n" +
                 "    <extension>afsnit_id:0</extension>\n" +
                 "  </pbcoreExtension>\n" +
                 "  <pbcoreExtension>\n" +
                 "    <extension>saeson_id:0</extension>\n" +
                 "  </pbcoreExtension>\n" +
                 "  <pbcoreExtension>\n" +
                 "    <extension>serie_id:0</extension>\n" +
                 "  </pbcoreExtension>\n" +
                 "  <pbcoreExtension>\n" +
                 "    <extension>tekstet:ikke tekstet</extension>\n" +
                 "  </pbcoreExtension>\n" +
                 "  <pbcoreExtension>\n" +
                 "    <extension>th:ikke tekstet for hørehæmmede</extension>\n" +
                 "  </pbcoreExtension>\n" +
                 "  <pbcoreExtension>\n" +
                 "    <extension>ttv:ikke tekst-tv</extension>\n" +
                 "  </pbcoreExtension>\n" +
                 "  <pbcoreExtension>\n" +
                 "    <extension>showviewcode:468797</extension>\n" +
                 "  </pbcoreExtension>\n" +
                 "</PBCoreDescriptionDocument>";
    @Test
    public void testRemoveOriginalTitel() throws Exception {


        Document document = DOM.stringToDOM(doc,true);
        Fixer fixer = new Fixer();
        fixer.fixAll(document);
        System.out.println(DOM.domToString(document, true));
    }
}
