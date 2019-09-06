package model.huesensordata.config;

public class Config {
    Boolean on;
    Integer battery;
    Boolean reachable;
    String alert;
    Boolean ledindication;
    Boolean usertest;
    Integer sensitivity;
    Integer sensitivitymax;
    String[] pending;

    public Config(Boolean on, Integer battery, Boolean reachable, String alert, Boolean ledindication, Boolean usertest, Integer sensitivity, Integer sensitivitymax, String[] pending) {
        this.on = on;
        this.battery = battery;
        this.reachable = reachable;
        this.alert = alert;
        this.ledindication = ledindication;
        this.usertest = usertest;
        this.sensitivity = sensitivity;
        this.sensitivitymax = sensitivitymax;
        this.pending = pending;
    }

    public Config() {
        this.on = true;
        this.battery = 100;
        this.reachable = true;
        this.alert = "none";
        this.ledindication = false;
        this.usertest = false;
        this.sensitivity = 1;
        this.sensitivitymax = 2;
        this.pending = new String[]{};
    }
}
