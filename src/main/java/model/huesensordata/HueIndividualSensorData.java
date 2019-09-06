package model.huesensordata;

import model.huesensordata.capabilities.Capabilities;
import model.huesensordata.config.Config;
import model.huesensordata.state.State;
import model.huesensordata.swupdate.SwUpdate;

public class HueIndividualSensorData {
    public State state;
    public Config config;
    public SwUpdate swupdate;
    public String name;
    public String type;
    public String modelid;
    public String manufacturername;
    public String productname;
    public String swversion;
    public String uniqueid;
    public Capabilities capabilities;

    public HueIndividualSensorData(State state, Config config, SwUpdate swupdate, String name, String type, String modelid, String manufacturername, String productname, String swversion, String uniqueid, Capabilities capabilities) {
        this.state = state;
        this.config = config;
        this.swupdate = swupdate;
        this.name = name;
        this.type = type;
        this.modelid = modelid;
        this.manufacturername = manufacturername;
        this.productname = productname;
        this.swversion = swversion;
        this.uniqueid = uniqueid;
        this.capabilities = capabilities;
    }

    public HueIndividualSensorData() {
        this.state = new State();
        this.config = new Config();
        this.swupdate = new SwUpdate();
        this.name = "capteur Entrée";
        this.type = "ZLLPresence";
        this.modelid = "SML001";
        this.manufacturername = "Philips";
        this.productname = "Hue motion sensor";
        this.swversion = "6.1.1.27575";
        this.uniqueid = "00:17:88:01:03:29:af:62-02-0406";
    }
}

