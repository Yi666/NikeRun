package demo.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

// Json <--> object model <--> (ORM) relational model
// one to many, many to one, many to many, one to one, entity to entity
// map columns to one object

// Json to object, object to Json (JSON library Jackson library)

@JsonInclude(JsonInclude.Include.NON_NULL)      //only non-null field in Json from frontend will be mapped to object
@Entity
@Data
@Table(name = "LOCATIONS")
public class Location {

    enum GpsStatus{
        EXCELLENT, OK, UNRELIABLE, BAD, NOFIX, UNKNOW;
    }

    public enum RunnerMovementType{
        STOPPED, IN_MOTION;
    }

    @Id
    @GeneratedValue
    private long id;

    @Embedded
    @AttributeOverride(name = "bandMake", column = @Column(name = "Unit_band_make"))
    private UnitInfo unitInfo;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "fmi", column = @Column(name = "medical_fmi")),
            @AttributeOverride(name = "bfr", column = @Column(name = "medical_bfr"))
    })
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
    private String serviceType;

    public Location() { this.unitInfo = null; }

    @JsonCreator
    public Location(@JsonProperty("runningId") String runningId) { this.unitInfo = new UnitInfo(runningId); }

    public Location(UnitInfo unitInfo) { this.unitInfo = unitInfo; }

    public String getRunningId() { return this.unitInfo == null ? null : this.unitInfo.getRunningId(); }

}
