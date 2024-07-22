package com.microservice.h2db.Repository;

import com.microservice.h2db.Models.Show;
import org.springframework.data.repository.Repository;

import java.util.List;


public interface ShowDBRepository extends Repository<Show,Integer> {
    Show save(Show show);
    Show findById(Integer id);
    List<Show> findByTheatreId(Integer id);
}
