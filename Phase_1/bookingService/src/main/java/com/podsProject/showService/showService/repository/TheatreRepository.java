package com.podsProject.showService.showService.repository;


import com.podsProject.showService.showService.entity.Theatre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TheatreRepository extends JpaRepository<Theatre,Integer> {


}







