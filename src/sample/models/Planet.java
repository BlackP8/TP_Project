package sample.models;

public class Planet extends SpaceObject {
    public int radius;
    public boolean atmosphere;
    public double gravity_force;

    public Planet() {};

    public Planet(double distance_to_Earth, int radius, boolean atmosphere, double gravity_force) {
        super(distance_to_Earth);
        this.radius = radius;
        this.atmosphere = atmosphere;
        this.gravity_force = gravity_force;
    }

    @Override
    public String getDescription () {
        String radiusString = "Радиус: "+ this.radius + " km.";
        String gravityString = "Сила притяжения: "+ this.gravity_force + " H";
        String atmoString = this.atmosphere ? "Есть атмосфера." : "Нет атмосферы.";
        return String.format("Планета. \n%s \n%s \n%s", radiusString, gravityString, atmoString);
    }

}
