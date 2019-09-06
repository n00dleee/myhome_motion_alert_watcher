package model.huesensordata.swupdate;

public class SwUpdate {
    public String state;
    public String lastinstall;

    public SwUpdate(String state, String lastinstall) {
        this.state = state;
        this.lastinstall = lastinstall;
    }

    public SwUpdate() {
        this.state = "noupdates";
        this.lastinstall = "2019-03-12T13:54:38";
    }
}
