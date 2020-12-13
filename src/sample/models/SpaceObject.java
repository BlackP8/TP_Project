package sample.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

// тут написано что создай свойство @class и пропиши в нем имя класса
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
@JsonIgnoreProperties({"description"}) // указали что свойство description нужно игнорировать
public class SpaceObject {
    private double distance_to_Earth;
    //Идентификатор объекта
    public Integer id = null;

    public SpaceObject() {};

    public SpaceObject (double distance_to_Earth) {
        this.setDistance(distance_to_Earth);
    }

    @Override
    public String toString() {
        return  String.format("%.2f km", this.getDistance());
    }

    public double getDistance() {
        return distance_to_Earth;
    }

    public void setDistance(double distance_to_Earth) {
        this.distance_to_Earth = distance_to_Earth;
    }

    public String getDescription() {
        return "";
    }
}
