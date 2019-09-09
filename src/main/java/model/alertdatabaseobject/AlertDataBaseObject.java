package model.alertdatabaseobject;

import java.util.Date;

public class AlertDataBaseObject {
    public Integer sensorId;
    public String name;
    public String productName;
    public String manufacturerName;
    public Date date;
    public String type;
    public boolean value;

    public AlertDataBaseObject(Integer sensorId, String name, String productName, String manufacturerName, Date date, String type, boolean value) {
        this.sensorId = sensorId;
        this.name = name;
        this.productName = productName;
        this.manufacturerName = manufacturerName;
        this.date = date;
        this.type = type;
        this.value = value;
    }

    public AlertDataBaseObject() {
        this.sensorId = 123;
        this.name = "mockSensor";
        this.productName = "defaultMockSensor";
        this.manufacturerName = "Philips";
        this.date = new Date();
        this.type = "presence";
        this.value = true;
    }
}
