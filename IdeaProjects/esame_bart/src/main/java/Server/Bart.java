package Server;

import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.gson.GsonFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class Bart {

    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static HttpRequestFactory requestFactory;
    private static final JsonFactory JSON_FACTORY = new GsonFactory();

    public Bart() {
        try {
            requestFactory = HTTP_TRANSPORT.createRequestFactory((HttpRequest request) -> {
                request.setParser(new JsonObjectParser(JSON_FACTORY));
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setStation() throws IOException {

        GenericUrl url = new GenericUrl("http://api.bart.gov/api/stn.aspx?cmd=stninfo&orig=daly&key=MW9S-E7SL-26DU-VV8V&json=y");
        HttpRequest request = requestFactory.buildGetRequest(url);

        HttpResponse httpResponse = request.execute();

        String jsonResponse = ((HttpResponse)httpResponse).parseAsString();

        JSONObject obj = new JSONObject(jsonResponse);
        JSONObject root = obj.getJSONObject("root");
        JSONObject stations = root.getJSONObject("stations");
        JSONObject station = stations.getJSONObject("station");

        String name = station.getString("name");
        String address = station.getString("address");
        String city = station.getString("city");
        String state = station.getString("state");

        //prima di aggiungere una nuova stazione cancello quella presente nel database. volendo si può togliere per aggiungere stazioni
        Dao.deleteStation();

        Dao.addStation(name, address , city, state);
    }

    public static void setSchedulesAndReservations() throws IOException {

        GenericUrl url = new GenericUrl("http://api.bart.gov/api/sched.aspx?cmd=stnsched&orig=daly&key=MW9S-E7SL-26DU-VV8V&json=y");
        HttpRequest request = requestFactory.buildGetRequest(url);
        String jsonResponse = request.execute().parseAsString();

        JSONObject obj = new JSONObject(jsonResponse);
        JSONObject root = obj.getJSONObject("root");
        JSONObject station = root.getJSONObject("station");
        JSONArray item = station.getJSONArray("item");

        //prima di creare una nuova tabella cancello tutto per evitare problemi
        Dao.deleteAllSchedules();
        Dao.deleteAllReservations();

        for(int i = 0 ; i < item.length(); i++) { //per ogni schedule
            JSONObject oneObject = item.getJSONObject(i);

            String line = oneObject.getString("@line");

            String origTime = oneObject.getString("@origTime");
            String trainId = oneObject.getString("@trainId");
            String trainHeadStation = oneObject.getString("@trainHeadStation");

            if( (line.equals("ROUTE 1"))||(line.equals("ROUTE 7"))) {
                Dao.addSchedule(origTime, "Southbound Platform", trainId, trainHeadStation);
            } else {
                Dao.addSchedule(origTime, "Northbound Platform", trainId, trainHeadStation);
            }
            Dao.initReservationTable(trainId);
        }
    }
}