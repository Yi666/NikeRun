package demo.domain;

import java.util.Date;

public class Location {

    enum GpsStatus{
        EXCELLENT, OK, UNRELIABLE, BAD, NOFIX, UNKNOW;
    }

    enum RunnerMovementType{
        STOPPED, IN_MOTION;
    }

    private long id;

    private UnitInfo unitInfo;

    private MedicalInfo medicalInfo;

    private double latitude;
    private double longitude;
    private String heading;
    private double gpsSpeed;
    private GpsStatus gpsStatus;
    private double odometer;
    private double totalRunningTime;
    private double totalIdleTime;
    private double totalCalorieBurnt;
    private String address;
    private Date timestamp = new Date();
    private String gearProvide;
    private RunnerMovementType runnerMovementType;
    private String serviceTyper;
}
