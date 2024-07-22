package com.podsProject.showService.showService.repository;


import com.podsProject.showService.showService.entity.Theatre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(isolation = Isolation.SERIALIZABLE)
public interface TheatreRepository extends JpaRepository<Theatre,Integer> {


}







