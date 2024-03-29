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
            "where ci.id = :cityId and cm.available_till_date > current_date"
            , nativeQuery = true)
    List<MovieResponse> getMovies(@Param(value = "cityId") Long cityId);
}