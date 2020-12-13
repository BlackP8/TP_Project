package sample.models;

import java.util.ArrayList;

public class Comet extends SpaceObject {
    public int period;
    public String name;

    public Comet() {};

    public Comet(double distance_to_Earth, int period, String name) {
        super(distance_to_Earth);
        this.period = period;
        this.name = name;
    }

    @Override
    public String getDescription() {
        String periodString = "Период прохождения через солнечную систему: "+ this.period + " сут.";
        String nameString = "Название: "+ this.name;
        return String.format("Комета. \n%s \n%s", periodString, nameString);
    }
}
