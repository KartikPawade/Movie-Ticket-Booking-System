package com.movienow.org.repository;

import com.movienow.org.entity.Movie;
import com.movienow.org.entity.Theatre;
import com.movienow.org.entity.TheatreMovie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TheatreMovieRepository extends JpaRepository<TheatreMovie, Long> {
//    List<TheatreMovie> findByMovieAndTheatresIn(Movie movie, List<Theatre> theatres);
}