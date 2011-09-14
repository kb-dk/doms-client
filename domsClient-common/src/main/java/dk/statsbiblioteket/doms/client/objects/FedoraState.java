package dk.statsbiblioteket.doms.client.objects;

/**
 * Created by IntelliJ IDEA.
 * User: eab
 * Date: 8/15/11
 * Time: 1:53 PM
 * To change this template use File | Settings | File Templates.
 */
public enum FedoraState {
    Active("A", "Published"), Inactive("I", "InProgress"), Deleted("D", "Deleted");
    private String shorthand;
    private String editorState;

    FedoraState(String shorthand, String editorState) {
        this.shorthand = shorthand;
        this.editorState = editorState;
    }





    public static FedoraState fromString(String state) {

        state = state.trim();
        for (FedoraState fedoraState : FedoraState.values()) {
            if (fedoraState.shorthand.equals(state)){
                return fedoraState;
            }
        }

        return valueOf(state);
    }

    public String getEditorState() {
        return editorState;
    }
}
