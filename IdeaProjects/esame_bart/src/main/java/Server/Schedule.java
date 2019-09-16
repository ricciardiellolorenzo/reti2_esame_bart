package Server;

public class Schedule {

    private String platform;
    private String origTime;
    private String trainId;
    private String trainHeadStation;

    public Schedule(String origTime, String platform, String trainId, String trainHeadStation) {
        this.origTime = origTime;
        this.platform = platform;
        this.trainId = trainId;
        this.trainHeadStation = trainHeadStation;
    }

}
