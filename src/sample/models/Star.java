package sample.models;

public class Star extends SpaceObject {
    public double density;
    public int temp;
    public enum Color {Red, Blue, Brown, White, Orange}
    public Color color;

    public Star() {};

    public Star(double distance_to_Earth, double density, int temp, Color color) {
        super(distance_to_Earth);
        this.density = density;
        this.temp = temp;
        this.color = color;
    }

    @Override
    public String getDescription() {
        String densityString = "Плотность: "+ this.density + " кг/м^3.";
        String tempString = "Температура: "+ this.temp + " градусов Цельсия.";
        String typeString = "";
        switch (this.color)
        {
            case Red:
                typeString = "красный карлик";
                break;
            case Blue:
                typeString = "голубой сверхгигант";
                break;
            case Brown:
                typeString = "коричневый карлик";
                break;
            case White:
                typeString = "белый карлик";
                break;
            case Orange:
                typeString = "оранжевый карлик";
                break;
        }
        return String.format("Звезда: %s \n%s \n%s", typeString, densityString, tempString);
    }
}
