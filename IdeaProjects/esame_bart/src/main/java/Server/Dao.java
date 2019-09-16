package Server;

import utils.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class Dao {

    public static void addStation(String name, String address, String city , String state) {
        final String sql = "INSERT INTO station(name, address, city, state) VALUES (?, ?, ?, ?)";

        try {
            Connection conn = DBConnect.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(sql);

            st.setString(1, name);
            st.setString(2, address);
            st.setString(3, city);
            st.setString(4, state);

            st.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public  static void addSchedule(String time, String platform, String id, String destination) {
        final String sql = "INSERT INTO schedules(origTime, platform, trainId, trainHeadStation) VALUES (?, ?, ?, ?)";

        try {
            Connection conn = DBConnect.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(sql);
            st.setString(1, time);
            st.setString(2, platform);
            st.setString(3, id);
            st.setString(4, destination);

            st.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void initReservationTable(String id) {
        final String sql = "INSERT INTO reservations(trainId, peopleCount, bikeCount) VALUES (?,?,?)";

        try {
            Connection conn = DBConnect.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(sql);
            st.setString(1, id);
            st.setInt(2, 0);
            st.setInt(3, 0);

            st.executeUpdate();

            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public int increasePeopleCount(String id) {
        int count = getCount(id, "SELECT peopleCount FROM reservations WHERE trainId = ?", "peopleCount");
        count = increaseCount(id, count, "UPDATE reservations SET peopleCount = ? WHERE trainId = ?");
        return count;
    }

    public int increaseBikeCount(String trainId) {
        int count = getCount(trainId, "SELECT bikeCount FROM reservations WHERE trainId = ?", "bikeCount");
        count = increaseCount(trainId, count, "UPDATE reservations SET bikeCount = ? WHERE trainId = ?");
        return count;
    }

    public static int getCount(String trainId, String sql, String bikeOrPeopleCount) {
        final String finalSql = sql;
        int count = -1;

        try {
            Connection conn = DBConnect.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(finalSql);

            st.setString(1, trainId);

            ResultSet rs = st.executeQuery();
            count = rs.getInt(bikeOrPeopleCount);
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return count;
    }

    private int increaseCount(String trainId, int count, String sql) { // aumentiamo di uno
        final String finalSql = sql;

        try {
            count++;

            Connection conn = DBConnect.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(finalSql);
            st.setInt(1, count);
            st.setString(2, trainId);

            st.executeUpdate();

            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }


    public List<Schedule> getSchedules() {
        final String sql = "SELECT * FROM schedules";

        List<Schedule> schedules = new LinkedList<>();

        try {
            Connection conn = DBConnect.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(sql);

            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                Schedule t = new Schedule(
                        rs.getString("origTime"),
                        rs.getString("platform"),
                        rs.getString("trainId"),
                        rs.getString("trainHeadStation"));
                schedules.add(t);
            }

            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return schedules;
    }

    public List<Station> getStation() {
        final String sql = "SELECT * FROM station";
        List<Station> stations = new LinkedList<>();

        try {
            Connection conn = DBConnect.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(sql);

            ResultSet rs = st.executeQuery();

            Station station = new Station(
                    rs.getString("name"),
                    rs.getString("address"),
                    rs.getString("city"),
                    rs.getString("state"));

            stations.add(station);

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stations;
    }

    //DELETE FROM cancella tutto!!!

    public static void deleteStation() {
        final String sql = "DELETE FROM station";
        execute(sql);
    }

    public static void deleteAllSchedules() {
        final String sql = "DELETE FROM schedules";
        execute(sql);
    }

    public static void deleteAllReservations() {
        final String sql = "DELETE FROM reservations";
        execute(sql);
    }

    public static void execute(String sql){
        try {
            Connection conn = DBConnect.getInstance().getConnection();
            PreparedStatement st = conn.prepareStatement(sql);
            st.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}