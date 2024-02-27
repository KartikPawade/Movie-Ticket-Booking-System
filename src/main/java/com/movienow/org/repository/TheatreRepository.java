package com.movienow.org.repository;

import com.movienow.org.dto.TheatreDetails;
import com.movienow.org.entity.Theatre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TheatreRepository extends JpaRepository<Theatre, Long> {

    @Query(value = "select t.id, t.name, t.address from theatre t " +
            "join city c " +
            "on t.city_id = c.id " +
            " where t.city_id = :cityId "
            , nativeQuery = true)
    List<TheatreDetails> getTheatres(@Param(value = "cityId") Long cityId);
}