package Server;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

public class BartService {

    private final static int MAX = 4;
    private final static PhilipsHue lights = new PhilipsHue();

    public static void main(String[] args) throws IOException{

        Bart api1 = new Bart();
        api1.setStation();

        Bart.setSchedulesAndReservations();

        String baseURL = "/api/v1.0";
        Dao dao = new Dao();
        Gson gson = new Gson();

        get(baseURL + "/station", "application/json", (request, response) -> {
            response.type("application/json");
            response.status(200);

            List<Station> stations = dao.getStation();

            Map<String, List<Station>> finalJson = new HashMap<>();

            finalJson.put("Station", stations);
            return finalJson;
        }, gson::toJson);

        get(baseURL + "/schedules", "application/json", (request, response) -> {
            response.type("application/json");
            response.status(200);

            List<Schedule> allSchedules = dao.getSchedules();

            Map<String, List<Schedule>> finalJson = new HashMap<>();
            finalJson.put("Schedules", allSchedules);
            return finalJson;
        }, gson::toJson);

        put(baseURL + "/newReservation/:trainId", (request, response) -> {
            String trainId = String.valueOf(request.params("trainId"));
            checkAvailabilityPerson(trainId);
            return "";
        });

        put(baseURL + "/newReservationWithBike/:trainId", (request, response) -> {
            String trainId = String.valueOf(request.params("trainId"));
            checkAvailabilityPersonWithBike(trainId);
            return "";
        });
    }

    public static void checkAvailabilityPerson(String trainId) throws InterruptedException {
        int peopleCount = Dao.getCount(trainId, "SELECT peopleCount FROM reservations WHERE trainId = ?", "peopleCount");
        Dao dao = new Dao();

        if((peopleCount) > MAX) {
            lights.reservationErrorLights();
        }
        else {
            if(dao.increasePeopleCount(trainId) == -1){
                lights.reservationErrorLights();
            } else{
                lights.reservationSuccessLights(peopleCount);
            }
        }
    }

    public static void checkAvailabilityPersonWithBike(String trainId) throws InterruptedException {
        int bikeCount = Dao.getCount(trainId, "SELECT bikeCount FROM reservations WHERE trainId = ?", "bikeCount");
        int peopleCount = Dao.getCount(trainId, "SELECT peopleCount FROM reservations WHERE trainId = ?", "peopleCount");
        Dao dao = new Dao();

        if((bikeCount > MAX)||(peopleCount > MAX)) {
            lights.reservationErrorLights();
        }
        else {
            if((dao.increasePeopleCount(trainId) == -1)||(dao.increaseBikeCount(trainId) == -1)){
                lights.reservationErrorLights();
            } else {
                lights.reservationSuccessLights(peopleCount);
            }
        }
    }
}
