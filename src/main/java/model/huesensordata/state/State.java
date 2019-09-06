package model.huesensordata.state;

public class State {
    public Boolean presence;
    public String lastupdated;

    public State(Boolean presence, String lastupdated){
        this.presence = presence;
        this.lastupdated = lastupdated;
    }

    public State() {
        this.presence = true;
        this.lastupdated = "2019-09-06T15:29:58";
    }
}
