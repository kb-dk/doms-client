package dk.statsbiblioteket.doms.client.objects;

import dk.statsbiblioteket.doms.client.sdo.SDOParsedXmlDocument;
import dk.statsbiblioteket.doms.client.sdo.SDOParsedXmlElement;

import java.util.ArrayList;

/**
* Created by csr on 19/06/14.
*/
public class SdoUtils {

    protected static void parseDoc(SDOParsedXmlDocument doc) {
        System.out.println("'" + doc.getRootSDOParsedXmlElement().getLabel() + "'");
        parseTree(doc.getRootSDOParsedXmlElement(), "");
    }

    protected static void parseTree(SDOParsedXmlElement doc, String indryk) {


        indryk = indryk + "   ";
        ArrayList<SDOParsedXmlElement> children = doc.getChildren();
        for (SDOParsedXmlElement child : children) {
            if (child.isLeaf()) {

                System.out.print(indryk + "'" + child.getLabel() + "': '" + child.getValue() + "'");
                if (child.getProperty().isMany()) {
                    if (child.getAddable()) {
                        System.out.print(" (+)");
                    }
                    if (child.getRemovable()) {
                        System.out.print("(-)");
                    }
                    if (child.hasNonEmptyDescendant()) {
                        System.out.print("(d)");
                    }
                }
                System.out.print("  type=" + child.getGuiTypeAsString());
                System.out.println();


            } else {


                System.out.print(indryk + "'" + child.getLabel() + "'");
                if (child.getProperty().isMany()) {
                    if (child.getAddable()) {
                        System.out.print(" (+)");
                    }
                    if (child.getRemovable()) {
                        System.out.print("(-)");
                    }
                    if (child.hasNonEmptyDescendant()) {
                        System.out.print(" (d) ");
                    }
                }
                System.out.println();

                parseTree(child, indryk + "    ");
            }
        }
    }
}
