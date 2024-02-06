package com.movienow.org.repository;

import com.movienow.org.dto.TheatreMovieResponse;
import com.movienow.org.dto.TheatreResponse;
import com.movienow.org.entity.Theatre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TheatreRepository extends JpaRepository<Theatre, Long> {

    @Query(value = "select t.id, t.name, t.address_id as addressId, a.area, a.city_id as cityId, c.name as city from theatre t " +
            "join address a " +
            "on t.address_id = a.id " +
            "join city c " +
            "on a.city_id = c.id " +
            " where a.city_id = :cityId "
            , nativeQuery = true)
    List<TheatreResponse> getTheatres(@Param(value = "cityId") Long cityId);

    @Query(value = "select m.id as movieId, m.name as movieName, t.id as theatreId, t.name as theatreName from theatre t " +
            "join theatre_movie tm " +
            "on t.id = tm.theatre_id " +
            "join city_movie cm " +
            "on cm.id = tm.city_movie_id " +
            "join movie m " +
            "on m.id = cm.movie_id " +
            "where cm.id = :cityMovieId "
            , nativeQuery = true)
    List<TheatreMovieResponse> getTheatresForMovieInCity(@Param(value = "cityMovieId") Long cityMovieId);
}
