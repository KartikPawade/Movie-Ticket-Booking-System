package com.movienow.org.repository;

import com.movienow.org.entity.ScreenMovie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScreenMovieRepository extends JpaRepository<ScreenMovie, Long> {
    Optional<ScreenMovie> findByMovieIdAndScreenId(Long movieId, Long screenId);
}
