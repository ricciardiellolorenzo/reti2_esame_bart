package Server;

public class Station {

    private String address;
    private String city;
    private String name;
    private String state;

    public Station(String name, String address, String city, String state) {
        this.name = name;
        this.address = address;
        this.city = city;
        this.state = state;
    }
}