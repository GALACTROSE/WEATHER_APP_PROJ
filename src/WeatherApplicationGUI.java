
import org.json.simple.JSONObject;

import javax.swing.*;
import java.awt.*;
import javax.imageio.ImageIO;
//import javax.swing.border.AbstractBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class WeatherApplicationGUI extends JFrame{
    private JSONObject weatherData;
    public WeatherApplicationGUI(){
        // app title :
        super("Weather App");

        // setting up a size ...
        setSize(450,650);

        //setting the app launching point...
        Dimension s_size=Toolkit.getDefaultToolkit().getScreenSize();
        int x=(s_size.width-getWidth())/2;
        int y=(s_size.height-getHeight())/2;
        setLocation(x,y);

        // setting up a layout....
        setLayout(null);

        // terminating operations ...
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        AddGuiComponents();
    }
    private void AddGuiComponents(){
        // creating a search field(text)....
        JTextField searchTextField=new JTextField();

        // giving co-ordinates and the size to our component...
        searchTextField.setBounds(15,15,351,45);

        // changing the fontstyle and size
        searchTextField.setFont(new Font("Dialog",Font.PLAIN,18));

        // applying the custom rounded border...
        searchTextField.setBorder(new RoundedBorder(11));

        searchTextField.setBackground(new Color(221, 248, 248));

        // creating a search button....

        // adding the search_text_field to the frame...
        add(searchTextField);

        // creating a label of weather image...
        JLabel weatherConditionImage=new JLabel(load_the_image("src/Assets/cloud.png"));
        weatherConditionImage.setBounds(0,125,450,217);
        // adding it...
        add(weatherConditionImage);

        // creating a label of temperature text ...
        JLabel tempText=new JLabel("10° C");
        tempText.setBounds(0,350,450,54);
        tempText.setFont(new Font("Dialog",Font.PLAIN,35));

        // centering the text ...
        tempText.setHorizontalAlignment(SwingConstants.CENTER);
        tempText.setVerticalAlignment(SwingConstants.TOP);
        // adding it...
        add(tempText);

        // Description of weather condition :
        JLabel weatherConditionDesc=new JLabel("Cloudy");
        weatherConditionDesc.setBounds(0,405,450,36);
        weatherConditionDesc.setFont(new Font("Dialog",Font.PLAIN,29));
        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
        // adding it...
        add(weatherConditionDesc);

        // Creating a humidity image label...
        JLabel humidityImg=new JLabel(load_the_image("src/Assets/humidityNew.png"));
        humidityImg.setBounds(15,500,74,66);
        // adding it..
        add(humidityImg);

        // creating a label of humidity text ...
        JLabel humidityText=new JLabel("<html><b>Humidity</b><p>100%</p></html>");
        humidityText.setBounds(90,500,85,55);
        humidityText.setFont(new Font("Dialog",Font.PLAIN,16));
        add(humidityText);

        // Creating a wind speed label...
        JLabel windSpeedImg=new JLabel(load_the_image("src/Assets/windSpeed.png"));
        windSpeedImg.setBounds(220,500,74,66);
        windSpeedImg.setIconTextGap(-10);
        // adding it..
        add(windSpeedImg);

        // creating a label of wind speed text ...
        JLabel windspeedText=new JLabel("<html><b>Windspeed</b><p>16Km/h</p></html>");
        windspeedText.setBounds(310,500,85,55);
        windspeedText.setFont(new Font("Dialog",Font.PLAIN,16));
        // adding it..
        add(windspeedText);


        // JButton searchButton=new RoundedButton("",15);
        JButton searchButton=new JButton(load_the_image("src/Assets/search_button.png"));
        // changing the cursor to a hand cursor when hovering over this button...
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375,13,47,45);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // get location from user :
                String userInput=searchTextField.getText();
                if(userInput.replaceAll("\\s","").length()<=0){
                    return;
                }
                // giving the input to retrieve the weather data
                weatherData=WeatherApp.getWeatherData(userInput);

                if (weatherData == null) {
                    JOptionPane.showMessageDialog(null, "Error fetching weather data", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String weatherCondition=(String) weatherData.get("weather-condition");
                switch(weatherCondition){
                    case "Clear" :
                        weatherConditionImage.setIcon(load_the_image("src/Assets/clear.png"));
                        break;
                    case "Cloudy" :
                        weatherConditionImage.setIcon(load_the_image("src/Assets/cloud.png"));
                        break;
                    case "Rain" :
                        weatherConditionImage.setIcon(load_the_image("src/Assets/rain.png"));
                        break;
                    case "Snow" :
                        weatherConditionImage.setIcon(load_the_image("src/Assets/snowflake.png"));
                        break;
                }
                // update temperature text
                double temperature=(double)weatherData.get("temperature");
                tempText.setText(temperature+"° C");

                // update weather condition text
                weatherConditionDesc.setText(weatherCondition);

                // update humidity text
                long humidity=(long)weatherData.get("humidity");
                humidityText.setText("<html><b>Humidity</b> " + humidity + "%</html>");

                // update windspeed text
                long windspeed=(long)weatherData.get("windspeed");
                windspeedText.setText("<html><b>Windspeed</b> " + windspeed + "km/h</html>");
            }
        });

        // adding it...
        add(searchButton);

        URL location=getClass().getResource("Assets/weather.png");
        if(location!=null){
            ImageIcon ii=new ImageIcon(location);
            this.setIconImage(ii.getImage());
        }
        else{
            System.err.println("Image resource not found!");
        }
    }
    private ImageIcon load_the_image(String resourcePath){
        try{
            // read the image file from the path given
            BufferedImage image=ImageIO.read(new File(resourcePath));

            // returns the image icon.
            return new ImageIcon(image);
        }
        catch(IOException ie) {
            ie.printStackTrace();
            System.out.println("Couldn't find any resource!");
            return null;
        }
    }
}
