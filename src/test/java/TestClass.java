import java.awt.*;

public class TestClass {
    public static void main(String[] args) {
        //Color color = new Color.decode()

        System.out.println(getColor("29, 255, 0, 20"));




    }

    public static Color getColor(String color) {
        String[] rgba;
        rgba = color.split(",");
        int r = Integer.parseInt(rgba[0].trim());
        int g = Integer.parseInt(rgba[1].trim());
        int b = Integer.parseInt(rgba[2].trim());
        int a = Integer.parseInt(rgba[3].trim());
        return new Color(r,g,b,a);
    }
}
