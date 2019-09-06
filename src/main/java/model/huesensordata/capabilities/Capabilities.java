package model.huesensordata.capabilities;

public class Capabilities {
    public Boolean certified;
    public Boolean primary;

    public Capabilities(Boolean certified, Boolean primary) {
        this.certified = certified;
        this.primary = primary;
    }

    public Capabilities() {
        this.certified = true;
        this.primary = true;
    }
}
