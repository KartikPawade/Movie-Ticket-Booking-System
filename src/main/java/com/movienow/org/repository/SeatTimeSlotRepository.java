package com.movienow.org.repository;

import com.movienow.org.entity.TimeSlotSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatTimeSlotRepository extends JpaRepository<TimeSlotSeat, Long> {
}
