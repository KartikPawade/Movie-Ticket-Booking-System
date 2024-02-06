package com.movienow.org.repository;

import com.movienow.org.dto.ScreenResponse;
import com.movienow.org.entity.Screen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScreenRepository extends JpaRepository<Screen, Long> {
    @Query(value = "select s.id as screenId, s.name as screenName , t.id as theatreId, t.name as theatreName, m.id as movieId, m.name as movieName from screen s " +
            "join theatre t " +
            "on s.theatre_id = theatre_id " +
            "join city_movie cm " +
            "on s.movie_city_id = cm.id " +
            "join movie m " +
            "on m.id = cm.movie_id " +
            "where t.id = :theatreId and cm.id = :cityMovieId "
            , nativeQuery = true)
    List<ScreenResponse> getScreens(@Param(value = "theatreId") Long theatreId, @Param(value = "cityMovieId") Long cityMovieId);
}
