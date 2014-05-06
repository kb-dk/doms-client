package dk.statsbiblioteket.doms;


import dk.statsbiblioteket.util.xml.DOM;
import dk.statsbiblioteket.util.xml.XPathSelector;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 11/10/11
 * Time: 6:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class Fixer {

    XPathSelector xpath = DOM.createXPathSelector("pb", "http://www.pbcore.org/PBCore/PBCoreNamespace.html");

    public void fixAll(Document document) {
        removeDateIssued(document);
        removeEpisodeTitel(document);
        removeForfatter(document);
        removeFormatAspectRatio(document);
        removeFormatDuration(document);
        removeInstruktion(document);
        removeInstruktion(document);
        removeLangOmtale1(document);
        removeLangOmtale2(document);
        removeMedvirkende(document);
        removeOriginalTitel(document);
        removeDateCreated(document);
        setFormatLocation(document);
    }


    public void removeOriginalTitel(Document doc) {
        Node badtitle = xpath.selectNode(
                doc, "/pb:PBCoreDescriptionDocument/pb:pbcoreTitle[pb:titleType='originaltitel']/pb:title");
        if (badtitle != null && badtitle.getFirstChild() == null) {
            Node parent = badtitle.getParentNode();
            parent.getParentNode().removeChild(parent);
        }
    }

    public void removeEpisodeTitel(Document doc) {
        Node badtitle = xpath.selectNode(
                doc, "/pb:PBCoreDescriptionDocument/pb:pbcoreTitle[pb:titleType='episodetitel']/pb:title");
        if (badtitle != null && badtitle.getFirstChild() == null) {
            Node parent = badtitle.getParentNode();
            parent.getParentNode().removeChild(parent);
        }
    }

    public void removeLangOmtale1(Document doc) {
        Node badtitle = xpath.selectNode(
                doc,
                "/pb:PBCoreDescriptionDocument/pb:pbcoreDescription[pb:descriptionType='langomtale1']/pb:description");
        if (badtitle != null && badtitle.getFirstChild() == null) {
            Node parent = badtitle.getParentNode();
            parent.getParentNode().removeChild(parent);
        }
    }


    public void removeLangOmtale2(Document doc) {
        Node badtitle = xpath.selectNode(
                doc,
                "/pb:PBCoreDescriptionDocument/pb:pbcoreDescription[pb:descriptionType='langomtale2']/pb:description");
        if (badtitle != null && badtitle.getFirstChild() == null) {
            Node parent = badtitle.getParentNode();
            parent.getParentNode().removeChild(parent);
        }
    }


    public void removeForfatter(Document doc) {
        Node badtitle = xpath.selectNode(
                doc, "/pb:PBCoreDescriptionDocument/pb:pbcoreCreator[pb:creatorRole='forfatter']/pb:creator");
        if (badtitle != null && badtitle.getFirstChild() == null) {
            Node parent = badtitle.getParentNode();
            parent.getParentNode().removeChild(parent);
        }
    }

    public void removeMedvirkende(Document doc) {
        Node badtitle = xpath.selectNode(
                doc,
                "/pb:PBCoreDescriptionDocument/pb:pbcoreContributor[pb:contributorRole='medvirkende']/pb:contributor");
        if (badtitle != null && badtitle.getFirstChild() == null) {
            Node parent = badtitle.getParentNode();
            parent.getParentNode().removeChild(parent);
        }
    }

    public void removeInstruktion(Document doc) {
        Node badtitle = xpath.selectNode(
                doc,
                "/pb:PBCoreDescriptionDocument/pb:pbcoreContributor[pb:contributorRole='instruktion']/pb:contributor");
        if (badtitle != null && badtitle.getFirstChild() == null) {
            Node parent = badtitle.getParentNode();
            parent.getParentNode().removeChild(parent);
        }
    }

    public void removeDateIssued(Document doc) {
        Node badnode = xpath.selectNode(
                doc, "/pb:PBCoreDescriptionDocument/pb:pbcoreInstantiation/pb:dateIssued");
        if (badnode != null && (badnode.getFirstChild() == null)) {
            badnode.getParentNode().removeChild(badnode);
        }
    }


    public void setFormatLocation(Document doc) {
        Node badnode = xpath.selectNode(
                doc, "/pb:PBCoreDescriptionDocument/pb:pbcoreInstantiation/pb:formatLocation");
        if (badnode != null && (badnode.getFirstChild() == null || badnode.getFirstChild().getNodeValue().equals(""))) {
            badnode.setTextContent("unspecified");
        }
    }

    public void removeFormatDuration(Document doc) {
        Node badnode = xpath.selectNode(
                doc, "/pb:PBCoreDescriptionDocument/pb:pbcoreInstantiation/pb:formatDuration");
        if (badnode != null && (badnode.getFirstChild() == null || badnode.getFirstChild()
                                                                          .getNodeValue()
                                                                          .equals("0"))) {
            badnode.getParentNode().removeChild(badnode);
        }
    }

    public void removeDateCreated(Document doc) {
        Node badnode = xpath.selectNode(
                doc, "/pb:PBCoreDescriptionDocument/pb:pbcoreInstantiation/pb:dateCreated");
        if (badnode != null && (badnode.getFirstChild() == null || badnode.getFirstChild()
                                                                          .getNodeValue()
                                                                          .equals("0"))) {
            badnode.getParentNode().removeChild(badnode);
        }
    }


    public void removeFormatAspectRatio(Document doc) {
        Node badnode = xpath.selectNode(
                doc, "/pb:PBCoreDescriptionDocument/pb:pbcoreInstantiation/pb:formatAspectRatio");
        if (badnode != null && (badnode.getFirstChild() == null || badnode.getFirstChild()
                                                                          .getNodeValue()
                                                                          .equals(", "))) {
            badnode.getParentNode().removeChild(badnode);
        }
    }


}
