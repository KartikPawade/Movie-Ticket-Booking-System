package com.movienow.org.repository;

import com.movienow.org.dto.AddScreenRequest;
import com.movienow.org.dto.ScreenResponse;
import com.movienow.org.entity.Screen;
import com.movienow.org.entity.Theatre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScreenRepository extends JpaRepository<Screen, Long> {
    @Query(value = "select s.id as screenId, s.name as screenName , t.id as theatreId, m.id as movieId from screen s " +
            "join theatre t " +
            "on s.theatre_id = theatre_id " +
            "join city_movie cm " +
            "on s.movie_city_id = cm.id " +
            "join movie m " +
            "on m.id = cm.movie_id " +
            "where t.id = :theatreId and cm.id = :cityMovieId "
            , nativeQuery = true)
    List<ScreenResponse> getScreens(@Param(value = "theatreId") Long theatreId, @Param(value = "cityMovieId") Long cityMovieId);

    List<Screen> findAllByTheatreAndNameIn(Theatre theatre, List<String> screenRequests);
}
