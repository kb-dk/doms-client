package dk.statsbiblioteket.doms.client.util;

/**
 * TODO abr forgot to document this class
 */
public class Constants {
    static public final String DC_ID = "DC";
    static public final String ONTOLOGY = "ONTOLOGY";
    static public final String RELS_EXT_ID = "RELS-EXT";
    static public final String DS_COMPOSITE_MODEL_ID = "DS-COMPOSITE-MODEL";
    static public final String POLICY_ID = "POLICY";
    static public final String AUDIT_ID = "AUDIT";
    static public final String VIEW_ID = "VIEW";
    static public final String DS_COMPOSITE_SCHEMA_NAMESPACE = "http://doms.statsbiblioteket.dk/types/dscompositeschema/0/1/#";
    static public final String GUI_REPRESENTATION_NAMESPACE = "http://doms.statsbiblioteket.dk/types/dscompositeschema/guirepresentation/0/1/#";
    static public final String DOMS_RELATIONS_NAMESPACE = "http://doms.statsbiblioteket.dk/relations/default/0/1/#";
    static public final String VIEWS_NAMESPACE = "http://ecm.sourceforge.net/types/view/0/2/#";
    static public final String RDF_NAMESPACE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    static public final String RDFS_NAMESPACE = "http://www.w3.org/2000/01/rdf-schema#";
    static public final String OWL_NAMESPACE = "http://www.w3.org/2002/07/owl#";
    static public final String FEDORA_MODEL_NAMESPACE = "info:fedora/fedora-system:def/model#";
    static public final String INFO_FEDORA_URI_SCHEME = "info:fedora/";
    static public final String IS_PART_OF_COLLECTION_RELATION = DOMS_RELATIONS_NAMESPACE+"isPartOfCollection";
    static public final String EXTENSIONS_SCHEMA = "SCHEMA";
    static public final String EXTENSIONS_GUI = "GUI";

    /**                         guestplanets
     * If the given string starts with "info:fedora/", remove it.
     *
     * @param pid A pid, possibly as a URI
     * @return The pid, with the possible URI prefix removed.
     */
    public static String ensurePID(String pid) {
        if (pid.startsWith(INFO_FEDORA_URI_SCHEME)) {
            pid = pid.substring(INFO_FEDORA_URI_SCHEME.length());
        }
        return pid;
    }

    /**
     * If the given string does not start with "info:fedora/", remove it.
     *
     * @param uri An URI, possibly as a PID
     * @return The uri, with the possible URI prefix prepended.
     */
    public static String ensureURI(String uri) {
        if (!uri.startsWith(INFO_FEDORA_URI_SCHEME)) {
            uri = INFO_FEDORA_URI_SCHEME + uri;
        }
        return uri;
    }

    public static enum FedoraState {
        A, I, D
    }

    public static enum GuiRepresentation {
        importable,editable,uploadable,readonly,invisible;

        public static GuiRepresentation fromString(String a){
            return GuiRepresentation.valueOf(a);
        }
    }

    public static enum DatastreamControlGroup {
        E, M, R, X, B;
        public static DatastreamControlGroup fromString(String a){
            return DatastreamControlGroup.valueOf(a);
        }
        
    }
}
