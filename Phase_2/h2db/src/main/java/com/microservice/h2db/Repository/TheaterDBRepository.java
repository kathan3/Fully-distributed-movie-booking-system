package com.microservice.h2db.Repository;

import com.microservice.h2db.Models.Theatre;

import org.springframework.data.repository.Repository;

import java.util.List;

public interface TheaterDBRepository extends Repository<Theatre,Integer> {

    void save(Theatre theatre);
    List<Theatre> findAll();
    Theatre findById(Integer id);

}
