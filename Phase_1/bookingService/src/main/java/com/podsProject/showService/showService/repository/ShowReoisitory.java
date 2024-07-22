package com.podsProject.showService.showService.repository;

import com.podsProject.showService.showService.ShowServiceApplication;
import com.podsProject.showService.showService.entity.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShowReoisitory extends JpaRepository<Show,Integer> {
    public List<Show> findByTheatreId(Integer TheatreId);

}
