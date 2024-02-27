package com.movienow.org.repository;

import com.movienow.org.entity.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScreenMovieRepository extends JpaRepository<Show, Long> {
    Optional<Show> findByMovieIdAndScreenId(Long movieId, Long screenId);
}
