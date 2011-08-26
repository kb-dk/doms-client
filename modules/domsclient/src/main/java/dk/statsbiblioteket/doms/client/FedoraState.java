package dk.statsbiblioteket.doms.client;

/**
 * Created by IntelliJ IDEA.
 * User: eab
 * Date: 8/15/11
 * Time: 1:53 PM
 * To change this template use File | Settings | File Templates.
 */
public enum FedoraState {
    Active("A"), Inactive("I"), Deleted("D");
    private String shorthand;

    FedoraState(String shorthand) {
        this.shorthand = shorthand;
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
}
