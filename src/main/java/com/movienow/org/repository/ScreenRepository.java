package com.movienow.org.repository;

import com.movienow.org.dto.ScreenResponse;
import com.movienow.org.entity.Screen;
import com.movienow.org.entity.Theatre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScreenRepository extends JpaRepository<Screen, Long> {
    @Query(value = "select s.id as screenId,s.name as screenName from screen s " +
            "join screen_movie sm " +
            "on sm.screen_id = s.id " +
            "where " +
            "s.theatre_id = :theatreId " +
            "and sm.movie_id = :movieId"
            , nativeQuery = true)
    List<ScreenResponse> getScreens(@Param(value = "theatreId") Long theatreId, @Param(value = "movieId") Long movieId);

    List<Screen> findAllByTheatreAndNameIn(Theatre theatre, List<String> screenRequests);

    Optional<Screen> findByIdAndTheatreId(Long id, Long theatreId);

    List<Screen> findAllByTheatreId(Long theatreId);
}
