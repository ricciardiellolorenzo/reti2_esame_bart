package Client;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.Map;

import static spark.Spark.*;

public class BartClient {

    public static void main(String[] args) {

        port(5000);
        RestTemplate rest = new RestTemplate();
        final String url = "http://127.0.0.1:4567/api/v1.0";
        //final String url = "http://172.27.96.170:4567/api/v1.0";


        get("/", (req, res) -> {
            Map model = rest.getForObject(url+"/station", Map.class);
            return new HandlebarsTemplateEngine().render(new ModelAndView(model, "viewStations.hbs"));
        });

        get("/schedules", (req, res) -> {
            Map model2 = rest.getForObject(url+"/schedules", Map.class);
            return new HandlebarsTemplateEngine().render(new ModelAndView(model2, "viewSchedules.hbs"));
        });

        post("/newReservation/:id", "application/json", (req, res)->{

            String id = String.valueOf(req.params("id"));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String body = "";
            HttpEntity<String> entity = new HttpEntity<>(body, headers);

            rest.put(url + "/newReservation/" + id, entity);

            res.redirect("/schedules");
            return null;
        });

        post("/newReservationWithBike/:id", "application/json", (req, res)->{

            String id = String.valueOf(req.params("id"));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String body = "";
            HttpEntity<String> entity = new HttpEntity<>(body, headers);

            rest.put(url + "/newReservationWithBike/" + id, entity);

            res.redirect("/schedules");
            return null;
        });
    }
}

