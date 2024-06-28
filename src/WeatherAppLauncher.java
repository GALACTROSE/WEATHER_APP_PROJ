
import javax.swing.*;

public class WeatherAppLauncher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new WeatherApplicationGUI().setVisible(true);
//                System.out.println(WeatherApp.getWeatherData("India"));
            }
        });
    }
}
