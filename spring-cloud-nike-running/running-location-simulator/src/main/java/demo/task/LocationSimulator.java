package demo.task;

import demo.model.*;
import demo.model.support.NavUtils;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;



public class LocationSimulator implements Runnable{

    private long id;

    private AtomicBoolean cancel = new AtomicBoolean();

    private double speedInMps;
    private boolean shouldMove;
    private boolean exportPositionsToMessaging = true;
    private Integer reportInterval = 500;

    private PositionInfo positionInfo = null;

    private List<Leg> legs;
    private RunnerStatus runnerStatus = RunnerStatus.NONE;
    private String runningId;

    private Point startPoint;
    private Date executionStartTime;

    private MedicalInfo medicalInfo;

    public LocationSimulator (GpsSimulatorRequest gpsSimulatorRequest) {
        this.shouldMove = gpsSimulatorRequest.isMove();
        this.exportPositionsToMessaging = gpsSimulatorRequest.isExportPositionsToMessaging();
        this.reportInterval = gpsSimulatorRequest.getReportInterval();
        this.runningId = gpsSimulatorRequest.getRunningId();
        this.runnerStatus = gpsSimulatorRequest.getRunnerStatus();
        this.medicalInfo = gpsSimulatorRequest.getMedicalInfo();

        this.setSpeed(gpsSimulatorRequest.getSpeed());
    };




    @Override
    public void run() {
        try {
            executionStartTime = new Date();
            if (cancel.get()) {
                destroy();
                return;
            }

            while (!Thread.interrupted()) {
                long startTime = new Date().getTime();
                if (positionInfo != null) {
                    if (shouldMove) {
                        moveRunningLocation();
                        positionInfo.setSpeed(speedInMps);
                    }
                    else {
                        positionInfo.setSpeed(0.0);
                    }

                    positionInfo.setRunnerStatus(this.runnerStatus);

                    final MedicalInfo medicalInfoToUse;

                    switch (this.runnerStatus) {
                        case SUPPLY_NOW:
                        case SUPPLY_SOON:
                        case STOP_NOW:
                            medicalInfoToUse = this.medicalInfo;
                            break;
                        default:
                            medicalInfoToUse = null;
                            break;
                    }

                    final CurrentPosition currentPosition = new CurrentPosition(
                            this.positionInfo.getRunningId(),
                            new Point(this.positionInfo.getPosition().getLatitude(),
                                    this.positionInfo.getPosition().getLongitude()),
                            this.positionInfo.getRunnerStatus(),
                            this.positionInfo.getSpeed(),
                            this.positionInfo.getLeg().getHeading(),
                            medicalInfoToUse);
                    //TODO: Need a service to publish currentPosition


                }

                sleep(startTime);
            }
        }
        catch (InterruptedException ie) {
            destroy();
            return;
        }
        destroy();
    }

    void destroy() {
        positionInfo = null;
    }

    public void setSpeed(double speed) {
        this.speedInMps = speed;
    }

    public void sleep (long startTime) throws InterruptedException {
        long endTime = new Date().getTime();

        long elapsedTime = endTime - startTime;

        long sleepTime = reportInterval - elapsedTime > 0 ? reportInterval - elapsedTime : 0;
        Thread.sleep(sleepTime);
    }

    //set new position of running based on current position and running speed
    private void moveRunningLocation() {
        double distance = speedInMps * reportInterval / 1000.0;
        double distanceFromStart = positionInfo.getDistanceFromStart() + distance;
        double excess = 0.0;

        for (int i=positionInfo.getLeg().getId();i<legs.size();i++) {
            Leg currentLeg = legs.get(i);
            excess = distanceFromStart > currentLeg.getLength() ? distanceFromStart - currentLeg.getLength() : 0;

            if (Double.doubleToRawLongBits(excess) == 0) {
                positionInfo.setDistanceFromStart(distanceFromStart);
                positionInfo.setLeg(currentLeg);

                Point newPosition = NavUtils.getPosition(currentLeg.getStartPosition(), distanceFromStart, currentLeg.getLength());
                //TODO: Google Map API

                positionInfo.setPosition(newPosition);
                return;

            }
            distanceFromStart = excess;
        }

        setStartPosition();
    }

    public void setStartPosition() {
        positionInfo = new PositionInfo();
        positionInfo.setRunningId(this.runningId);
        Leg leg = legs.get(0);
        positionInfo.setLeg(leg);
        positionInfo.setPosition(leg.getStartPosition());
        positionInfo.setDistanceFromStart(0.0);
    }

    public double getSpeed() {
        return this.speedInMps;
    }

    public synchronized void cancel() {
        this.cancel.set(true);
    }



}
