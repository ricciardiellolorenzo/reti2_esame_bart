package Server;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class PhilipsHue {

    private String lightsURL;
    private RestTemplate rest;

    private static String baseURL = "http://localhost:8000";
    //private static String baseURL = "http://172.30.1.138"; //URL lampadine laboratorio


    private static String username = "newdeveloper";
    //private static String username = "C0vPwqjJZo5Jt9Oe5HgO6sBFFMxgoR532IxFoGmx";

    private Map<String, ?> allLights;
    private HttpHeaders headers;

    public PhilipsHue(){
        this.lightsURL = baseURL + "/api/" + username + "/lights/";
        this.rest = new RestTemplate();
        this.allLights = rest.getForObject(lightsURL, Map.class);
        this.headers =  new HttpHeaders();
        this.headers.setContentType(MediaType.APPLICATION_JSON);
        turnOff();
    }

    public void turnOn() {
        String on = "{\"on\": true}";
        changeStatus(on);
    }

    public void turnOff() {
        String off = "{\"on\": false}";
        changeStatus(off);
    }

    private void turnGreen(){
        String green = "{\"hue\" : 25500}";
        changeStatus(green);
    }

    private void turnRed(){
        String red = "{\"hue\" : 0}";
        changeStatus(red);
    }

    private void brightness(int bri){
        String brightness = "{\"bri\" : "+ bri +"}";
        changeStatus(brightness);
    }

    public void reservationSuccessLights(int num) throws InterruptedException {
        brightness(num * 50);
        turnGreen();
        turnOn();
        waitSec(1);
        turnOff();
    }

    public void reservationErrorLights() throws InterruptedException {
        brightness(250);
        for (int i = 0; i < 3; i++){
            turnRed();
            turnOn();
            waitSec(1);
            turnOff();
            waitSec(1);
        }
    }

    private void changeStatus(String status){
        if (allLights != null)
        {
            for (String lightId : allLights.keySet()){
                HttpEntity<String> request = new HttpEntity<>(status, headers);
                String callURL = lightsURL + lightId + "/state";
                rest.put(callURL, request);
            }
        }
    }

    public void waitSec(int seconds) throws InterruptedException {
        Thread.sleep(1000*seconds);
    }
}
