package demo.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

public interface LocationRepository extends JpaRepository<Location, Long> {

    //SELECT * FROM Locations WHERE RunnerMovementType = movementType
    Page<Location> findByRunnerMovementType(@Param("movementType") Location.RunnerMovementType runnerMovementType, Pageable pageable);

    Page<Location> findByUnitInfoRunningId(@Param("runningId") String runningId, Pageable pageable);
}