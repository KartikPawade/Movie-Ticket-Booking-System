package com.movienow.org.repository;

import com.movienow.org.dto.MovieResponse;
import com.movienow.org.dto.TheatreMovieResponse;
import com.movienow.org.entity.CityMovie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CityMovieRepository extends JpaRepository<CityMovie, Long> {

    @Query(value = "select mo.id, mo.name from city_movie cm " +
            "join city ci " +
            "on ci.id = cm.city_id " +
            "join movie mo " +
            "on mo.id = cm.movie_id " +
            "where ci.id = :cityId "
            , nativeQuery = true)
    List<MovieResponse> getMovies(@Param(value = "cityId") Long cityId);


    @Query(value = "select m.id as movieId, m.name as movieName from movie m " +
            "join city_movie cm " +
            "on cm.movie_id = m.id " +
            "join theatre_movie tm " +
            "on cm.id = tm.city_movie_id " +
            "join theatre t " +
            "on t.id = tm.theatre_id " +
            "where cm.city_id = t.city_id " +
            "and  t.id = :theatreId"
            , nativeQuery = true)
    List<TheatreMovieResponse> getAllMovies(@Param("theatreId") Long theatreId);
}
