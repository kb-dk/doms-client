package dk.statsbiblioteket.doms.client.objects;

import dk.statsbiblioteket.doms.client.sdo.SDOParsedXmlDocument;
import dk.statsbiblioteket.doms.client.sdo.SDOParsedXmlElement;
import dk.statsbiblioteket.util.Strings;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.fail;

/**
* Created by csr on 19/06/14.
*/
public class SdoUtils {

    protected static String parseDoc(SDOParsedXmlDocument doc) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("'" + doc.getRootSDOParsedXmlElement().getLabel() + "'\n");
        parseTree(doc.getRootSDOParsedXmlElement(), "", stringBuilder);
        return stringBuilder.toString();
    }

    protected static void parseTree(SDOParsedXmlElement doc, String indryk, StringBuilder stringBuilder) {
        indryk = indryk + "   ";
        ArrayList<SDOParsedXmlElement> children = doc.getChildren();
        for (SDOParsedXmlElement child : children) {
            if (child.isLeaf()) {
                stringBuilder.append(indryk + "'" + child.getLabel() + "': '" + child.getValue() + "'");
                if (child.getProperty() != null && child.getProperty().isMany()) {
                    if (child.getAddable()) {
                        stringBuilder.append(" (+)");
                    }
                    if (child.getRemovable()) {
                        stringBuilder.append("(-)");
                    }
                    if (child.isHasNonEmptyDescendant()) {
                        stringBuilder.append("(d)");
                    }
                }
                stringBuilder.append("  type=" + child.getGuiTypeAsString()+"\n");

            } else {
                stringBuilder.append(indryk + "'" + child.getLabel() + "'");
                if (child.getProperty() != null && child.getProperty().isMany()) {
                    if (child.getAddable()) {
                        stringBuilder.append(" (+)");
                    }
                    if (child.getRemovable()) {
                        stringBuilder.append("(-)");
                    }
                    if (child.isHasNonEmptyDescendant()) {
                        stringBuilder.append(" (d) ");
                    }
                }
                stringBuilder.append("\n");
                parseTree(child, indryk + "    ", stringBuilder);
            }
        }
    }

    public static String getStringFromFileOnClasspath(String filename) {
        try {
            return Strings.flush(Thread.currentThread().getContextClassLoader().getResourceAsStream(filename));
        } catch (IOException e) {
            fail(e.getMessage());
            return null;
        }
    }
}
